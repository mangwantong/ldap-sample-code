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
package samplecode.auth;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityResponseControl;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedResult;
import samplecode.SupportedFeature;
import samplecode.SupportedFeatureException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;

import java.util.Vector;

/**
 * Provides clients with a method to get the authorization identity from
 * the result of a bind on a connection to directory server.
 * <p/>
 * This class can be observed by {@code LdapExceptionListener} objects
 * by adding a listener using {@code addLdapExceptionListener}.
 * <p/>
 * Usage Example: <blockquote>
 * <p/>
 * <pre>
 * AuthorizedIndentity authorizedIndentity = new AuthorizedIndentity(ldapConnection);
 * authorizedIndentity.addLdapExceptionListener(ldapExceptionListener);
 * </pre>
 * <p/>
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com") @Since("Dec 25, 2011") @CodeVersion("1.1")
public final class AuthorizedIdentity implements ObservedByLdapExceptionListener
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
  @SuppressWarnings("unchecked") @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
          final LDAPException ldapException)
  {
    Vector<LdapExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapExceptionListener>) ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev = new LdapExceptionEvent(this, ldapConnection, ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }

  /**
   * Binds to directory server using a simple bind request and returns
   * the authorization identity from the response.
   * <p/>
   * This method uses the authorization identity bind request control as
   * described in RFC 3829. It may be included in a bind request to
   * request that the server include the authorization identity
   * associated with the client connection in the bind response message,
   * in the form of an AuthorizationIdentityResponseControl. The
   * authorization identity request control is similar to the
   * "Who Am I?" extended request as implemented in the
   * WhoAmIExtendedRequest class. The primary difference between them is
   * that the "Who Am I?" extended request can be used at any time but
   * requires a separate operation, while the authorization identity
   * request control can be included only with a bind request but does
   * not require a separate operation.
   *
   * @param bindDn                the distinguished name with which to bind
   * @param bindPassword          the password of the {@code bindDn}
   * @param responseTimeoutMillis the number of milliseconds that the client will wait for a
   *                              response to the bind request before failing with a TIMEOUT
   *                              condition. If a negative value is provided, 0 is used.
   * @return the authorization identity from the response control
   *         associated with the
   *         {@code AuthorizationIdentityRequestControl}.
   * @throws SupportedFeatureException if the request control is not supported.
   */
  public String getAuthorizationIdentityFromBindRequest(final String bindDn,
          final String bindPassword, long responseTimeoutMillis)
          throws SupportedFeatureException
  {
    if(bindDn == null)
    {
      throw new IllegalArgumentException("bindDn must not be null.");
    }
    if(bindPassword == null)
    {
      throw new IllegalArgumentException("bindPassword must not be null.");
    }

    if(responseTimeoutMillis < 0)
    {
      responseTimeoutMillis = 0L;
    }

    final String oid = AuthorizationIdentityRequestControl.AUTHORIZATION_IDENTITY_REQUEST_OID;
    try
    {
      checkSupportedControl(ldapConnection, oid);
    }
    catch(LDAPException e)
    {
      throw new SupportedFeatureException(e,oid);
    }

    /*
     * Create the request control and add it to a new SimpleBindRequest,
     * then set the maximum number of milliseconds the client will block
     * on the response to the bind request (response timeout).
     */
    final AuthorizationIdentityRequestControl authorizationIdentityRequestControl =
            new AuthorizationIdentityRequestControl();
    final SimpleBindRequest bindRequest =
            new SimpleBindRequest(bindDn, bindPassword, authorizationIdentityRequestControl);
    bindRequest.setResponseTimeoutMillis(responseTimeoutMillis);

    /*
     * Transmit the bind request to the server
     */
    BindResult bindResult;
    try
    {
      bindResult = ldapConnection.bind(bindRequest);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection, ldapException);
      return null;
    }

    /*
     * Extract the response control from the bind response.
     */
    AuthorizationIdentityResponseControl authorizationIdentityResponseControl;
    try
    {
      authorizationIdentityResponseControl =
              AuthorizationIdentityResponseControl.get(bindResult);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection, ldapException);
      return null;
    }
    return authorizationIdentityResponseControl.getAuthorizationID();
  }

  private void checkSupportedControl(final LDAPConnection ldapConnection, final String oid)
          throws LDAPException
  {
    final RootDSE rootDSE = ldapConnection.getRootDSE();
  }

  /**
   * Retrieves the authorization identity from an existing ldap
   * connection.
   *
   * @param responseTimeout the number of milliseconds that the client will wait for a
   *                        response to the bind request before failing with a TIMEOUT
   *                        condition
   * @return the authorization identity from the response control
   *         associated with the
   *         {@code AuthorizationIdentityRequestControl}.
   * @throws SupportedFeatureException if the request control is not supported.
   */
  public String getAuthorizationIdentityWhoAmIExtendedOperation(final long responseTimeout)
          throws SupportedFeatureException
  {

    checkSupportedFeature(ldapConnection, WhoAmIExtendedRequest.WHO_AM_I_REQUEST_OID);

    /*
     * Demonstrate the user of the Who Am I? extended operation. This
     * procedure requires creating a WhoAmIExtendedRequest object and
     * using processExtendedOperation to transmit it.
     */
    final WhoAmIExtendedRequest whoAmIExtendedRequest = new WhoAmIExtendedRequest();
    WhoAmIExtendedResult whoAmIExtendedResult;
    try
    {
      whoAmIExtendedResult =
              (WhoAmIExtendedResult) ldapConnection.processExtendedOperation
                      (whoAmIExtendedRequest);
    }
    catch(final LDAPException exception)
    {
      fireLdapExceptionListener(ldapConnection, exception);
      return null;
    }
    return whoAmIExtendedResult.getAuthorizationID();

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
  public String toString()
  {
    return String.format("AuthorizedIdentity [ldapConnection=%s]", ldapConnection);
  }

  private void checkSupportedFeature(final LDAPConnection ldapConnection,
          final String controlOID) throws SupportedFeatureException
  {
    SupportedFeature supportedFeature;
    try
    {
      supportedFeature = SupportedFeature.newSupportedFeature(ldapConnection);
    }
    catch(final LDAPException exception)
    {
      fireLdapExceptionListener(ldapConnection, exception);
      return;
    }
    supportedFeature.isExtendedOperationSupported(controlOID);
  }

  /**
   * Creates a new instance of {@code AuthorizedIdentity} that will use
   * the specified connection to a directory server.
   *
   * @param ldapConnection a connection to a directory server. {@code ldapConnection}
   *                       is not permitted to be {@code null}.
   */
  public AuthorizedIdentity(final LDAPConnection ldapConnection)
  {
    if(ldapConnection == null)
    {
      throw new IllegalArgumentException("ldapConnection must not be null.");
    }
    this.ldapConnection = ldapConnection;
  }

  // a valid connection to a directory server.
  private final LDAPConnection ldapConnection;

  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
          new Vector<LdapExceptionListener>();

}
