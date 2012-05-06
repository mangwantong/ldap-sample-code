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
package samplecode;


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.unboundidds.controls.AttributeRight;
import com.unboundid.ldap.sdk.unboundidds.controls.EffectiveRightsEntry;
import com.unboundid.ldap.sdk.unboundidds.controls.GetEffectiveRightsRequestControl;
import com.unboundid.util.Validator;


import java.util.Vector;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdapSearchExceptionEvent;
import samplecode.listener.LdapSearchExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.listener.ObservedByLdapSearchExceptionListener;


/**
 * Checks whether an authorization identity has effective {@code rights}
 * to an {@code attributeName}.
 * <p>
 * <b>Usage Example</b> <blockquote>
 * 
 * <pre>
 * 
 * public void check() throws CheckEffectiveRightsException,SupportedFeatureException
 * {
 *   CheckEffectiveRights ch = new CheckEffectiveRights(ldapConnection);
 *   ch.hasRight(searchRequest,attributeName,attributeRight,authZid);
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.1")
public final class CheckEffectiveRights
        implements ObservedByLdapExceptionListener,ObservedByLdapSearchExceptionListener
{

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
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapSearchExceptionListener(
          final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.add(ldapSearchExceptionListener);
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
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapSearchExceptionListener(final LDAPConnection ldapConnection,
          final LDAPSearchException ldapSearchException)
  {
    Vector<LdapSearchExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapSearchExceptionListener>)ldapSearchExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapSearchExceptionEvent ev =
            new LdapSearchExceptionEvent(this,ldapConnection,ldapSearchException);
    for(final LdapSearchExceptionListener l : copy)
    {
      l.searchRequestFailed(ev);
    }
  }



  /**
   * For each entry returned by the directory server from the
   * {@code searchRequest} check whether the authorization state of the
   * connection has the specified right to the attribute. Throw an
   * {@code EffectiveRightsException} if the right is not there for
   * attribute name. Do nothing (just return) if the right exists.
   * 
   * @param searchRequest
   *          transmitted to the server
   * @param attributeName
   *          the name of the attribute to check
   * @param attributeRight
   *          the right to check
   * @param authZid
   * @throws CheckEffectiveRightsException
   *           if the attribute name does not have the specified right.
   * @throws SupportedFeatureException
   */
  public void hasRight(final SearchRequest searchRequest,final String attributeName,
          final AttributeRight attributeRight,final String authZid)
          throws CheckEffectiveRightsException,SupportedFeatureException
  {
    Validator.ensureNotNull(searchRequest,attributeName,attributeRight);

    /*
     * Determine whether the GetEffectiveRightsRequestControl is
     * supported by the server to which the LDAP client is connected.
     */
    SupportedFeature supportedFeature;
    try
    {
      supportedFeature = SupportedFeature.newSupportedFeature(ldapConnection);
    }
    catch(final LDAPException exception)
    {
      fireLdapExceptionListener(ldapConnection,exception);
      return;
    }
    supportedFeature
            .isControlSupported(GetEffectiveRightsRequestControl.GET_EFFECTIVE_RIGHTS_REQUEST_OID);

    /*
     * Create the GetEffectiveRightsRequestControl using the authZid and
     * the attributeName.
     */
    final GetEffectiveRightsRequestControl getEffectiveRightsRequestControl =
            new GetEffectiveRightsRequestControl(authZid,attributeName);
    searchRequest.addControl(getEffectiveRightsRequestControl);

    /*
     * Transmit the search with the request control attached to the
     * server.
     */
    SearchResult searchResult;
    try
    {
      searchResult = ldapConnection.search(searchRequest);
    }
    catch(final LDAPSearchException exception)
    {
      fireLdapSearchExceptionListener(ldapConnection,exception);
      return;
    }

    /*
     * Check each entry.
     */
    if(searchResult.getEntryCount() > 0)
    {
      for(final SearchResultEntry entry : searchResult.getSearchEntries())
      {
        final EffectiveRightsEntry effectiveRightsEntry = new EffectiveRightsEntry(entry);
        if(effectiveRightsEntry.rightsInformationAvailable())
        {
          if(!effectiveRightsEntry.hasAttributeRight(attributeRight,attributeName))
          {
            throw new CheckEffectiveRightsException(attributeName,attributeRight);
          }
        }
      }
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



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapSearchExceptionListener(
          final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.remove(ldapSearchExceptionListener);
    }
  }



  /**
   * Creates a {@code CheckEffectiveRights} with default state.
   * 
   * @param ldapConnection
   */
  public CheckEffectiveRights(
          final LDAPConnection ldapConnection)
  {
    this.ldapConnection = ldapConnection;

  }



  private final LDAPConnection ldapConnection;



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
          new Vector<LdapExceptionListener>();



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapSearchExceptionListener> ldapSearchExceptionListeners =
          new Vector<LdapSearchExceptionListener>();

}
