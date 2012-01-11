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


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldif.LDIFException;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.ControlHandler;
import samplecode.LdapDeleteBranch;
import samplecode.Since;
import samplecode.SupportedFeatureException;


/**
 * unit tests for LdapDeleteBranch.
 * <p>
 * The tests use a properties file for information concerning how to
 * connect to directory server, and for other purposes. The name of the
 * properties file is {@code commandLineOptions.properties}.
 * <p>
 * <b>example contents of the {@code commandLineOptions.properties}
 * file:</b><blockquote>
 * 
 * <pre>
 * #
 * # Properties used by the testing harness to connect to
 * # directory server.
 * #
 * # Supported keywords:
 * # hostname      - the hostname where the directory server runs.
 * # port          - the port upon which the directory server listens for connections.
 * # bindDn        - the distinguished name used to authenticate a connection.
 * # bindPassword  - the credentials of the bind DN.
 * #
 * samplecode.test.LdapServerConnectionData.hostname = ldap.example.com
 * samplecode.test.LdapServerConnectionData.port = 1389
 * samplecode.test.LdapServerConnectionData.bindDn = CN=RootDN
 * samplecode.test.LdapServerConnectionData.bindPassword = password
 * samplecode.test.LdapServerConnectionData.sizeLimit = 32
 * </pre>
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 24, 2011")
@CodeVersion("1.1")
public class LdapDeleteBranchTest
{


  // The branch to delete that might trigger admin_limit_exceeded
  private static final String BRANCH_TO_DELETE = "ou=people,dc=example,dc=com";


  // response control handler
  final ControlHandler notifyControlHandler = new ControlHandler()
  {


    @Override
    public ControlHandler handleResponseControl(final Object object,
        final Control control)
    {
      final String responseControlMessage =
          String.format("response control %s found with value: %s",
              control.getOID(),control.getValue());
      TestUtils.displayHelpfulMessage(System.out,responseControlMessage);
      return this;
    }

  };


  // connection an LDAP server
  private LDAPConnection ldapConnection;


  /**
   * Close the connection to the server.
   */
  @After
  public void closeConnection()
  {
    LdapConnectionUtils.closeConnection(ldapConnection);
  }


  /**
   * Get connection to the server.
   * 
   * @throws LDAPException
   */
  @Before
  public void getConnection() throws LDAPException
  {
    ldapConnection = LdapConnectionUtils.getConnection();
  }


  /**
   * tests deleting a branch.
   * 
   * @throws LDAPException
   * @throws SupportedFeatureException
   * @throws LDIFException
   */
  @Test
  public void testDeleteBranch() throws LDAPException,
      SupportedFeatureException,LDIFException
  {

    /*
     * Adds a tree to the server, then deletes it.
     */
    final String[] ldif = new String[]
    {
        "dn: cn=deleteme,dc=example,dc=com",
        "objectClass: top",
        "objectClass: inetOrgPerson",
        "cn: deleteme",
        "sn: deleteme-sn",
    };
    final String[] ldif2 = new String[]
    {
        "dn: uid=abc,cn=deleteme,dc=example,dc=com",
        "objectClass: top",
        "objectClass: inetOrgPerson",
        "uid: abc",
        "cn: uid-cn",
        "sn: deleteme-sn",
    };
    // add the entry
    ldapConnection.add(ldif);
    ldapConnection.add(ldif2);
    final LdapDeleteBranch deleter = LdapDeleteBranch.getInstance();
    // delete the entry
    final ControlHandler[] controlHandlers = new ControlHandler[]
    {
      notifyControlHandler
    };
    deleter.deleteTree(ldapConnection,new DN("cn=deleteme,dc=example,dc=com"),
        getResponseTimeout(),controlHandlers);
  }


  /**
   * tests deleting a branch where an administrative limit would be
   * exceeded.
   * 
   * @throws LDAPException
   * @throws SupportedFeatureException
   */
  @Test
  public void testDeleteBranchWithWhereAdminLimitWouldBeExceeded()
      throws LDAPException,SupportedFeatureException
  {
    final LdapDeleteBranch deleter = LdapDeleteBranch.getInstance();
    final ControlHandler[] controlHandlers = new ControlHandler[]
    {
      notifyControlHandler
    };
    try
    {
      deleter.deleteTree(ldapConnection,getBranchToDelete(),
          getResponseTimeout(),controlHandlers);
    }
    catch (final LDAPException ldapException)
    {
      if(ldapException.getResultCode().equals(ResultCode.ADMIN_LIMIT_EXCEEDED))
      {
        TestUtils.displayHelpfulMessage(System.err,
            "administrative limit exceeded.");
        Assert.assertTrue(true);
      }
      else if(ldapException.getResultCode().equals(ResultCode.TIMEOUT))
      {
        TestUtils.displayHelpfulMessage(System.err,"client-side timeout: " +
            ldapException.getExceptionMessage());
        Assert.assertTrue(true);
      }
      else
      {
        throw ldapException;
      }
    }
  }


  private DN getBranchToDelete() throws LDAPException
  {
    return new DN(LdapDeleteBranchTest.BRANCH_TO_DELETE);
  }


  private int getResponseTimeout()
  {
    return 1000;
  }

}
