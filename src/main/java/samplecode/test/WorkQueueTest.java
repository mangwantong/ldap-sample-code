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


import java.util.List;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.SampleCodeCollectionUtils;
import samplecode.Since;
import samplecode.monitor.WorkQueue;


/**
 * Supports a set of tests for the {@code WorkQueue} class.
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
@Since("Dec 27, 2011")
@CodeVersion("1.2")
public final class WorkQueueTest
{


  // connection to directory server.
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
   * test the {@code WorkQueue}.
   * 
   * @throws LDAPSearchException
   * @throws LDAPException
   */
  @Test
  public void testGetWorkQueue() throws LDAPSearchException,LDAPException
  {
    final List<String> helpfulMessages =
        SampleCodeCollectionUtils.newArrayList();


    int value = WorkQueue.getInstance().getWorkQueueAverageSize(ldapConnection);
    String helpfulMessage = String.format("getWorkQueueAverageSize: %d",value);
    helpfulMessages.add(helpfulMessage);


    value =
        WorkQueue.getInstance().getWorkQueueAverageWorkerThreadPercentBusy(
            ldapConnection);
    helpfulMessage =
        String.format("getWorkQueueAverageWorkerThreadPercentBusy: %d",value);
    helpfulMessages.add(helpfulMessage);


    value =
        WorkQueue.getInstance().getWorkQueueCurrentAdministrativeQueueSize(
            ldapConnection);
    helpfulMessage =
        String.format("getWorkQueueCurrentAdministrativeQueueSize: %d",value);
    helpfulMessages.add(helpfulMessage);


    for(final String s : helpfulMessages)
    {
      TestUtils.displayHelpfulMessage(System.out,s);
    }
  }


}
