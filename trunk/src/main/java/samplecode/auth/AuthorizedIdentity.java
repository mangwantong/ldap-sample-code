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

import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityRequestControl;
import com.unboundid.ldap.sdk.controls.AuthorizationIdentityResponseControl;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedResult;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.ldap.SupportedFeature;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.util.SampleCodeCollectionUtils;

import java.util.List;

import static com.unboundid.util.Validator.ensureNotNull;
import static com.unboundid.util.Validator.ensureTrue;


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
 * AuthorizedIndentity authorizedIndentity =
 *     new AuthorizedIndentity(ldapConnection);
 * authorizedIndentity.addLdapExceptionListener(ldapExceptionListener);
 * </pre>
 * <p/>
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.3")
public final class AuthorizedIdentity
  implements ObservedByLdapExceptionListener {

  /**
   * Creates a new instance of {@code AuthorizedIdentity} that will use
   * the specified connection to a directory server.
   *
   * @param ldapConnection
   *   a connection to a directory server. {@code ldapConnection}
   *   is not permitted to be {@code null}.
   */
  public AuthorizedIdentity(final LDAPConnection ldapConnection) {
    ensureNotNull(ldapConnection);

    this.ldapConnection = ldapConnection;
  }



  @Override
  public synchronized void addLdapExceptionListener(
    final LdapExceptionListener ldapExceptionListener) {
    if(ldapExceptionListener != null) {
      ldapExceptionListeners.add(ldapExceptionListener);
    }
  }



  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
                                        final LDAPException ldapException) {
    ensureNotNull(ldapConnection,ldapException);

    final List<LdapExceptionListener> copy;
    synchronized(this) {
      copy = SampleCodeCollectionUtils.newArrayList(ldapExceptionListeners);
    }
    if(copy.size() == 0) {
      return;
    }
    final LdapExceptionEvent ev =
      new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy) {
      l.ldapRequestFailed(ev);
    }
  }



  @Override
  public synchronized void removeLdapExceptionListener(
    final LdapExceptionListener ldapExceptionListener) {
    if(ldapExceptionListener != null) {
      ldapExceptionListeners.remove(ldapExceptionListener);
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
   * @param bindDn
   *   the distinguished name with which to bind
   * @param bindPassword
   *   the password of the {@code bindDn}
   * @param responseTimeoutMillis
   *   the number of milliseconds that the client will wait for a
   *   response to the bind request before failing with a TIMEOUT
   *   condition. If a negative value is provided, 0 is used.
   *
   * @return the authorization identity from the response control
   *         associated with the
   *         {@code AuthorizationIdentityRequestControl}.
   */
  public String getAuthorizationIdentityFromBindRequest(
    final String bindDn, final String bindPassword,
    long responseTimeoutMillis) {
    ensureNotNull(bindDn,bindPassword);
    ensureTrue(responseTimeoutMillis > 0);

    /*
     * Create the request control and add it to a new SimpleBindRequest,
     * then set the maximum number of milliseconds the client will block
     * on the response to the bind request (response timeout).
     */
    final AuthorizationIdentityRequestControl control =
      new AuthorizationIdentityRequestControl();
    final SimpleBindRequest bindRequest =
      new SimpleBindRequest(bindDn,bindPassword,control);
    bindRequest.setResponseTimeoutMillis(responseTimeoutMillis);

    /*
     * Transmit the bind request to the server
     */
    BindResult bindResult;
    try {
      bindResult = ldapConnection.bind(bindRequest);
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return null;
    }

    /*
     * Extract the response control from the bind response.
     */
    AuthorizationIdentityResponseControl authorizationIdentityResponseControl;
    try {
      authorizationIdentityResponseControl =
        AuthorizationIdentityResponseControl.get(bindResult);
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return null;
    }
    return authorizationIdentityResponseControl.getAuthorizationID();
  }



  /**
   * Retrieves the authorization identity from an existing ldap
   * connection.
   *
   * @return the authorization identity from the response control
   *         associated with the
   *         {@code AuthorizationIdentityRequestControl}.
   */
  public String
  getAuthorizationIdentityWhoAmIExtendedOperation() {

    if(!checkSupportedFeature(ldapConnection,
      WhoAmIExtendedRequest.WHO_AM_I_REQUEST_OID)) {
      return null;
    }

    /*
     * Demonstrate the user of the Who Am I? extended operation. This
     * procedure requires creating a WhoAmIExtendedRequest object and
     * using processExtendedOperation to transmit it.
     */
    final WhoAmIExtendedRequest whoAmIExtendedRequest =
      new WhoAmIExtendedRequest();
    WhoAmIExtendedResult whoAmIExtendedResult;
    try {
      whoAmIExtendedResult =
        (WhoAmIExtendedResult) ldapConnection.processExtendedOperation
          (whoAmIExtendedRequest);
    } catch(final LDAPException exception) {
      fireLdapExceptionListener(ldapConnection,exception);
      return null;
    }
    return whoAmIExtendedResult.getAuthorizationID();

  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    final StringBuilder sb =
      new StringBuilder("samplecode.auth.AuthorizedIdentity{");
    sb.append("ldapConnection=").append(ldapConnection);
    sb.append(", ldapExceptionListeners=").append(ldapExceptionListeners);
    sb.append('}');
    return sb.toString();
  }



  private boolean checkSupportedFeature(final LDAPConnection ldapConnection,
                                        final String controlOID) {
    return SupportedFeature.isExtendedOperationSupported
      (ldapConnection,controlOID);
  }



  // a valid connection to a directory server.
  private final LDAPConnection ldapConnection;


  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile List<LdapExceptionListener> ldapExceptionListeners =
    SampleCodeCollectionUtils.newArrayList();

}
