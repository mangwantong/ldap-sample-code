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

package samplecode.add;

import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.LDAPTestUtils;


/**
 * Demonstrates the {@link AddRequest}; this example uses a hard coded
 * hostname of {@code ldap-server.ldap.com} and port {@code 389} and
 * uses the test utilities class to generate an entry to add.
 */
public final class AddExample {

  /**
   * demonstrate the {@link AddRequest}
   */
  public static void main(final String... args) {

    // Use connection options to specify that the connection attempt
    // should be 1 second and if the ADD request times out, the request
    // should be abandoned.
    LDAPConnectionOptions connectionOptions = new LDAPConnectionOptions();
    connectionOptions.setAbandonOnTimeout(true);
    connectionOptions.setConnectTimeoutMillis(AddExample.OP_TIMEOUT_MILLIS);

    String host = AddExample.HOSTNAME;
    int port = AddExample.PORT;
    int result;
    LDAPResult ldapResult = null;
    try {

      // Connect to the server.
      LDAPConnection ldapConnection =
        new LDAPConnection(connectionOptions,host,port);
      try {

        Entry entry = LDAPTestUtils.generateUserEntry(GIVEN_NAME,
          BASE_OBJECT,GIVEN_NAME,SURNAME,PASSWORD);

        AddRequest addRequest = new AddRequest(entry);

        // Transmit the AddRequest to the server.
        ldapResult = ldapConnection.add(addRequest);

        System.out.println(ldapResult);
      } finally {
        ldapConnection.close();

        // Convert the result code to an integer for use in the exit method.
        result = ldapResult == null ? 1 : ldapResult.getResultCode().intValue();
      }
    } catch(final LDAPException e) {
      System.err.println(String.valueOf(AddExample.class) + " is an example " +
        "of how to add an entry to an LDAP Directory Server. The example is " +
        "primitive and simple minded: it expects the LDAP Directory Server " +
        "to be listening on ldap://localhost:389 and hosting 'dc=example," +
        "dc=com'.");
      e.printStackTrace();
      result = e.getResultCode().intValue();
    }

    System.exit(result);
  }



  /**
   * The object that is immediately superior to the user objects.
   */
  private static final String BASE_OBJECT = "ou=people,c=us";


  /**
   * Bab's given name.
   */
  private final static String GIVEN_NAME = "Babs";


  /**
   * The host where the LDAP Directory Server listens for client connections
   */
  private static final String HOSTNAME = "ldap-server.ldap.com";


  /**
   * The maximum number of milliseconds to wait before abandoning the
   * AddRequest
   */
  private static final int OP_TIMEOUT_MILLIS = 1000;


  /**
   * Bab's password
   */
  private static final String PASSWORD = "password";


  /**
   * The port on which the server at {@code HOSTNAME} listens for client
   * connections
   */
  private static final int PORT = 389;


  /**
   * Bab's last name
   */
  private static final String SURNAME = "Jensen";


}
