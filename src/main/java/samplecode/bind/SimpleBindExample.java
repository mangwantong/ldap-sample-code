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
package samplecode.bind;


import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.util.Validator;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides a static method that changes the authentication state of an
 * existing connection to a server.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 11, 2011")
@CodeVersion("1.1")
public final class SimpleBindExample
{

  /**
   * Changes the authentication state of the connection specified by
   * {@code ldapConnection} to the authorization ID specified by the
   * {@code dn} and {@code password}.
   * 
   * @param ldapConnection
   *          an existing connection to a server -
   *          {@code ldapConnection} is not permitted to be {@code null}
   * 
   * @param dn
   *          the distinguished name to which the authorization identity
   *          of the connection will be set - {@code dn} is not
   *          permitted to be {@code null}
   * 
   * @param password
   *          the credentials of the {@code dn} - {@code password} is
   *          not permitted to be {@code null}.
   * 
   * @param responseTimeout
   *          the time in milliseconds before the authentication attempt
   *          times out.
   * 
   * @return the result of the simple bind request.
   */
  public static BindResult authenticate(final LDAPConnection ldapConnection,final DN dn,
          final String password,final int responseTimeout) throws LDAPException
  {
    Validator.ensureNotNull(ldapConnection,dn,password);
    final LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
    connectionOptions.setResponseTimeoutMillis(responseTimeout);
    ldapConnection.setConnectionOptions(connectionOptions);
    return ldapConnection.bind(new SimpleBindRequest(dn,password));
  }

}
