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
package samplecode.test;


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;


import org.junit.Before;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;


/**
 * TODO
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.1")
public final class LdapConnectionUtils
{


  /**
   * Close the connection. If invoked from a method which is annotated
   * with {@code After}, this method will be invoked for each test. See
   * also: {@code commandLineOptions.properties}.
   * 
   * @param ldapConnection
   *          a connection to directory server.
   */
  public static void closeConnection(final LDAPConnection ldapConnection)
  {
    if(ldapConnection != null)
    {
      ldapConnection.close();
      TestUtils.displayHelpfulMessage(System.out,"ldapConnection closed");
    }
  }


  /**
   * Get connection to the server. If invoked from a method which is
   * annotated with {@code @Before}, this method will be invoked for
   * each test. See also: {@code commandLineOptions.properties}.
   * 
   * @return a connection to directory server.
   * @throws LDAPException
   *           if a connection cannot be established.
   */
  @Before
  public static LDAPConnection getConnection() throws LDAPException
  {
    final LdapServerConnectionData connectionData =
        DataUsedForTestingPurposesOnly.getLdapServerConnectionData();
    final String hostname = connectionData.getHostname();
    final int port = connectionData.getPort();
    final String bindDn = connectionData.getBindDn();
    final String bindPassword = connectionData.getBindPassword();
    final LDAPConnection ldapConnection =
        new LDAPConnection(hostname,port,bindDn,bindPassword);
    final String helpfulMessage =
        String.format("new LDAP Connection established "
            + "to server at %s on port %d",ldapConnection.getConnectTime(),
            ldapConnection.getConnectedPort());
    TestUtils.displayHelpfulMessage(System.out,helpfulMessage);
    return ldapConnection;
  }


  private LdapConnectionUtils()
  {
    // This block deliberately left empty.
  }
}
