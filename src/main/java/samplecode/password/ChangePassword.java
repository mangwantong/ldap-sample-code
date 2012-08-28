/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2012 UnboundID Corp. This program is free
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
package samplecode.password;


import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedRequest;
import com.unboundid.ldap.sdk.extensions.PasswordModifyExtendedResult;
import com.unboundid.util.NotMutable;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.ldap.SupportedFeature;
import samplecode.ldap.SupportedFeatureException;


/**
 * Provide support for changing the authentication password of an entry.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.2")
@NotMutable
public class ChangePassword
{

  /**
   * Gets a new and distinct instance of the {@code ChangePassword}
   * object that will use the supplied {@code ldapConnection} for
   * changing passwords.
   * 
   * @param ldapConnection
   *          connection to LDAP server, {@code null} object not
   *          permitted.
   * 
   * @return a new and distinct instance of the {@code ChangePassword}
   *         object.
   */
  public static ChangePassword newChangePassword(final LDAPConnection ldapConnection)
  {
    if(ldapConnection == null)
    {
      throw new IllegalArgumentException("ldapConnection must not be null.");
    }
    return new ChangePassword(ldapConnection);
  }



  /**
   * Changes the password of the entry. Requires the existing password
   * and the new password.
   * 
   * @param distinguishedName
   *          the distinguished name of the entry whose password will be
   *          changed.
   * @param existingPassword
   *          the existing password of the entry, {@code null} is not
   *          permitted.
   * @param newPassword
   *          the new password of the entry, if {@code null}, the server
   *          will generate a new password and send it back to this
   *          client.
   * @param responseTimeoutMillis
   *          maximum time (ms) to wait for a response from the server.
   * @throws LDAPException
   * @throws SupportedFeatureException
   * @throws PasswordModifyExtendedOperationFailedException
   */
  public void changePassword(final DN distinguishedName,final String existingPassword,
          final String newPassword,final int responseTimeoutMillis) throws LDAPException,
          SupportedFeatureException,PasswordModifyExtendedOperationFailedException
  {
    if(distinguishedName == null)
    {
      throw new IllegalArgumentException("distinguishedName must not be null.");
    }
    if(existingPassword == null)
    {
      throw new IllegalArgumentException("existingPassword must not be null.");
    }
    if(newPassword == null)
    {
      throw new IllegalArgumentException("newPassword must not be null.");
    }

    /*
     * Check the the server supports the password modify extended
     * request.
     */
    final SupportedFeature supportedControl =
            SupportedFeature.newSupportedFeature(ldapConnection);
    final String oid = PasswordModifyExtendedRequest.PASSWORD_MODIFY_REQUEST_OID;
    supportedControl.isExtendedOperationSupported(oid);

    /*
     * Create the password modify extended request. The extended request
     * will operate on the authentication identity of the existing
     * connection.
     */
    final PasswordModifyExtendedRequest passwordModifyExtendedRequest =
            new PasswordModifyExtendedRequest(existingPassword,newPassword);
    passwordModifyExtendedRequest.setResponseTimeoutMillis(responseTimeoutMillis);

    /*
     * Send the request to the server:
     */
    final PasswordModifyExtendedResult extendedResult =
            (PasswordModifyExtendedResult)ldapConnection
                    .processExtendedOperation(passwordModifyExtendedRequest);

    /*
     * Examine the results:
     */
    final ResultCode resultCode = extendedResult.getResultCode();
    if(!resultCode.equals(ResultCode.SUCCESS))
    {
      throw new PasswordModifyExtendedOperationFailedException(resultCode);
    }

  }



  private ChangePassword(
          final LDAPConnection ldapConnection)
  {
    this.ldapConnection = ldapConnection;
  }



  /**
   * The LDAP connection supplied by the client
   */
  private final LDAPConnection ldapConnection;
}
