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
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManager;
import javax.net.ssl.TrustManager;
import java.security.GeneralSecurityException;


/**
 * Demonstrates the {@link AddRequest}; this example uses a hard coded
 * hostname of {@code centos.example.com} and port {@code 1636} and
 * uses the test utilities class to generate an entry to add. The example
 * uses an unauthenticated connection to attempt the ADD. If an
 * unauthenticated connection does have sufficient access permissions to add
 * the entry, simply add some code to use an authenticated connection that
 * does have permission.
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
    connectionOptions.setConnectTimeoutMillis((int)AddExample.TIMEOUT_MILLIS);

    String host = AddExample.HOSTNAME;
    int port = AddExample.PORT;
    int result;
    LDAPResult ldapResult = null;

    KeyManager keyManager = null;
    TrustManager trustManager = new TrustAllTrustManager();
    SSLUtil sslUtil = new SSLUtil(keyManager,trustManager);

    try {

      SocketFactory socketFactory = sslUtil.createSSLSocketFactory();

      // Connect to the server.
      LDAPConnection ldapConnection =
        new LDAPConnection(socketFactory,connectionOptions,host,port);
      try {

        Entry entry = LDAPTestUtils.generateUserEntry(GIVEN_NAME,
          BASE_OBJECT,GIVEN_NAME,SURNAME,PASSWORD);

        // Add the entry (abandon the attempt if it does not succeed in the
        // specified number of milliseconds).
        AddRequest addRequest = new AddRequest(entry);
        addRequest.setResponseTimeoutMillis(AddExample.TIMEOUT_MILLIS);

        // Transmit the AddRequest to the server.
        ldapResult = ldapConnection.add(addRequest);

        System.out.println(ldapResult);
      } finally {
        ldapConnection.close();
      }
    } catch(final LDAPException e) {

      System.err.println(e);
      result = e.getResultCode().intValue();
      if(result != 0) {
        System.exit(result);
      }

    } catch(GeneralSecurityException e) {

      System.err.println(e);
      System.exit(1);

    }
  }


  /**
   * The object that is immediately superior to the user objects.
   */
  private static final String BASE_OBJECT = "ou=people,dc=example,dc=com";


  /**
   * Bab's given name.
   */
  private final static String GIVEN_NAME = "Babs";


  /**
   * The host where the LDAP Directory Server listens for client connections
   */
  private static final String HOSTNAME = "centos.example.com";


  /**
   * Bab's password
   */
  private static final String PASSWORD = "password";


  /**
   * The port on which the server at {@code HOSTNAME} listens for client
   * connections - this is expected to be th secure port.
   */
  private static final int PORT = 1636;


  /**
   * Bab's last name
   */
  private static final String SURNAME = "Jensen";


  /**
   * The maximum number of milliseconds to wait before abandoning the
   * attempt to connect or ADD.
   */
  private static final long TIMEOUT_MILLIS = 1000;


}
