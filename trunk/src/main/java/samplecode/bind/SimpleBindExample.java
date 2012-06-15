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
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;
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
   * @param args
   *          unused and ignored
   */
  public static final void main(final String... args)
  {
    try
    {
      final SimpleBindExample simpleBindExample = new SimpleBindExample();
      final LDAPConnection ldapConnection = new LDAPConnection("ldap.example.com",10389);
      final BindResult bindResult =
              simpleBindExample.authenticateUser(ldapConnection,"dc=example,dc=com","uid",
                      "user.0","password",10);
      System.out.println(bindResult);
    }
    catch(final LDAPException exception)
    {
      exception.printStackTrace();
    }

  }



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
   *          the password of the {@code dn} - {@code password} is not
   *          permitted to be {@code null}.
   * 
   * @param responseTimeoutMillis
   *          the time in milliseconds before the authentication attempt
   *          times out.
   * 
   * @return the result of the simple bind request.
   */
  public BindResult authenticate(final LDAPConnection ldapConnection,final DN dn,
          final String password,final int responseTimeoutMillis) throws LDAPException
  {
    Validator.ensureNotNull(ldapConnection,dn,password);
    final LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
    connectionOptions.setResponseTimeoutMillis(responseTimeoutMillis);
    ldapConnection.setConnectionOptions(connectionOptions);
    return ldapConnection.bind(new SimpleBindRequest(dn,password));
  }



  /**
   * Changes the authentication state of the connection specified by
   * {@code ldapConnection} to the authorization ID specified by the
   * {@code user} and {@code password}. Use this method when the
   * distinguished name is not known.
   * <p>
   * Usage example:<blockquote>
   * 
   * <pre>
   *  final SimpleBindExample simpleBindExample = new SimpleBindExample();
   *  final LDAPConnection ldapConnection = new LDAPConnection("ldap.example.com",10389);
   *  final BindResult bindResult =
   *          simpleBindExample.authenticateUser(ldapConnection,"dc=example,dc=com","uid",
   *                  "user.0","password",10);
   * </pre>
   * </blockquote>
   * 
   * @param ldapConnection
   *          an existing connection to a server -
   *          {@code ldapConnection} is not permitted to be {@code null}
   * 
   * @param baseObject
   *          the distinguished name at which the search should begin -
   *          {@code baseObject} is not permitted to be {@code null}
   * 
   * @param namingAttribute
   *          the attribute type
   * 
   * @param user
   *          the user-name to which the authorization identity of the
   *          connection will be set - {@code user} is not permitted to
   *          be {@code null}
   * 
   * @param password
   *          the password of the {@code user} - {@code password} is not
   *          permitted to be {@code null}.
   * 
   * @param responseTimeoutMillis
   *          the time in milliseconds before the authentication attempt
   *          times out.
   * 
   * @return the result of the simple bind request or {@code null} if
   *         the user does not exist, or if multiple entries match the
   *         search.
   */
  public BindResult authenticateUser(final LDAPConnection ldapConnection,
          final String baseObject,final String namingAttribute,final String user,
          final String password,final int responseTimeoutMillis) throws LDAPException
  {
    final String filter = String.format("(%s=%s)",namingAttribute,user);
    final SearchRequest searchRequest =
            new SearchRequest(baseObject,SearchScope.SUB,filter,"1.1");
    final SearchResult searchResult = ldapConnection.search(searchRequest);
    BindResult bindResult = null;
    if(searchResult.getSearchEntries().size() == 1)
    {
      final DN dn = new DN(searchResult.getSearchEntries().get(0).getDN());
      bindResult = authenticate(ldapConnection,dn,password,responseTimeoutMillis);
    }
    return bindResult;
  }
}
