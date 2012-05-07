/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 */
package samplecode.delete;


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.DeleteRequest;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.controls.SubtreeDeleteRequestControl;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;


import java.util.Vector;


import samplecode.ControlHandler;
import samplecode.Singleton;
import samplecode.SupportedFeature;
import samplecode.SupportedFeatureException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;


/**
 * Provides the necessary services for demonstrating the user of the
 * tree delete control. The services provided are:
 * <ul>
 * <li>add all entries in a file containing LDIF</li>
 * <li>deletes a specified branch</li>
 * </ul>
 * Makes use of the services of the {@code LDAPCommandLineTool} and the
 * {@code CommandLineOptions} classes.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 24, 2011")
@CodeVersion("1.1")
@NotMutable
@Singleton
public final class LdapDeleteBranch
        implements ObservedByLdapExceptionListener
{

  // singleton instance
  private static LdapDeleteBranch instance = null;



  /**
   * get an instance of {@code LdapDeleteBranch}.
   * 
   * @return an instance of {@code LdapDeleteBranch}.
   */
  public static LdapDeleteBranch getInstance()
  {
    if(LdapDeleteBranch.instance == null)
    {
      LdapDeleteBranch.instance = new LdapDeleteBranch();
    }
    return LdapDeleteBranch.instance;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapExceptionListener(
          final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.add(ldapExceptionListener);
    }
  }



  /**
   * Deletes the branch specified by {@code dnToDelete}. if any response
   * controls are attached to the delete response, they are handled by
   * the {@code controlHandlers}. The {@code responseTimeout} specifies
   * the maximum time spent processing the delete.
   * 
   * @param ldapConnection
   *          connection to the LDAP server.
   * @param dnToDelete
   *          the branch to delete. {@code dnToDelete} is not permitted
   *          to be {@code null}.
   * @param responseTimeout
   *          the maximum time spent processing the request in
   *          milliseconds.
   * @param controlHandlers
   *          handles any response controls.
   * @throws SupportedFeatureException
   *           if the OID of the subtree delete request control is not
   *           supported by this server.
   */
  public void deleteTree(final LDAPConnection ldapConnection,final DN dnToDelete,
          final int responseTimeout,final ControlHandler[] controlHandlers)
          throws SupportedFeatureException
  {

    Validator.ensureNotNull(ldapConnection,dnToDelete);

    try
    {
      validateControls(ldapConnection,SubtreeDeleteRequestControl.SUBTREE_DELETE_REQUEST_OID);
    }
    catch(final LDAPException exception)
    {
      fireLdapExceptionListener(ldapConnection,exception);
      return;
    }

    /*
     * Check that the server supports the subtree delete request control
     */
    SupportedFeature supportedControlOrExtension;
    try
    {
      supportedControlOrExtension = SupportedFeature.newSupportedFeature(ldapConnection);
    }
    catch(final LDAPException exception)
    {
      fireLdapExceptionListener(ldapConnection,exception);
      return;
    }
    final String controlOID = SubtreeDeleteRequestControl.SUBTREE_DELETE_REQUEST_OID;
    supportedControlOrExtension.isControlSupported(controlOID);

    /*
     * Construct a delete request and add the subtree delete request
     * control to the request.
     */
    final DeleteRequest deleteRequest = new DeleteRequest(dnToDelete);
    final SubtreeDeleteRequestControl control = new SubtreeDeleteRequestControl();
    deleteRequest.addControl(control);
    deleteRequest.setResponseTimeoutMillis(responseTimeout);

    /*
     * Delete the specified request, and handle any response controls
     * that were included by the server in the response.
     */
    LDAPResult ldapResult;
    try
    {
      ldapResult = ldapConnection.delete(deleteRequest);
    }
    catch(final LDAPException exception)
    {
      fireLdapExceptionListener(ldapConnection,exception);
      return;
    }
    if(controlHandlers != null)
    {
      for(final Control responseControl : ldapResult.getResponseControls())
      {
        for(final ControlHandler h : controlHandlers)
        {
          h.handleResponseControl(this,responseControl);
        }
      }
    }
  }



  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
          final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapConnection,ldapException);
    Vector<LdapExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapExceptionListener>)ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev = new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapExceptionListener(
          final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.remove(ldapExceptionListener);
    }
  }



  private void validateControls(final LDAPConnection ldapConnection,
          final String... subtreeDeleteRequestOids) throws LDAPException,
          SupportedFeatureException
  {
    Validator.ensureNotNull(ldapConnection,subtreeDeleteRequestOids);
    for(final String controlOID : subtreeDeleteRequestOids)
    {
      final SupportedFeature supportedFeature =
              SupportedFeature.newSupportedFeature(ldapConnection);
      supportedFeature.isControlSupported(controlOID);
    }
  }



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
          new Vector<LdapExceptionListener>();

}
