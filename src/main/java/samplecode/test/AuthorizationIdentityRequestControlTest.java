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


import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import junit.framework.Assert;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.MinimalLogFormatter;


import samplecode.Author;
import samplecode.AuthorizedIdentity;
import samplecode.CodeVersion;
import samplecode.Since;
import samplecode.SupportedFeatureException;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;


/**
 * test case for the {@code AuthorizedIdentity} class.
 * <p>
 * The tests use a properties file for information concerning how to
 * connect to directory server, and for other purposes. The name of the
 * properties file is {@code commandLineOptions.properties}.
 * <p>
 * <b>example contents of the {@code commandLineOptions.properties}
 * file:</b><blockquote>
 * 
 * <pre>
 * # Used to provide default values for command line arguments in the
 * # event the command line argument is not provided.  Indicates whether
 * # the LDAP SDK should attempt to abandon any request for which no
 * # response is received in the maximum response timeout period.
 * abandonOnTimeout = true
 * 
 * 
 * # Despite its name, a comma-separated list of attribute names or
 * # object identifiers
 * attribute = cn
 * 
 * 
 * # Specifies whether associated connections should attempt to
 * # automatically reconnect to the target server if the connection is
 * # lost. Note that automatic reconnection will only be available for
 * # authenticated clients if the authentication mechanism used provides
 * # support for re-binding on a new connection. Also note that this
 * # option will not have any effect on pooled connections because
 * # defunct pooled connections will be replaced by newly-created
 * # connections rather than attempting to re-establish the existing
 * # connection.
 * autoReconnect = true
 * 
 * 
 * # Specifies the base DN for the search. Only entries at or below this
 * # location in the server (based on the scope) will be considered
 * # potential matches.
 * baseObject = dc=example,dc=com
 * 
 * 
 * # The filter to use in search
 * filter = (objectClass=*)
 * 
 * 
 * # The number of initial connections to establish in a pool of
 * # connections.
 * initialConnection = 4
 * 
 * 
 * # The maximum number of connections to establish in a pool of
 * # connections.
 * maxConnections = 8
 * 
 * 
 * # The connection time out in milliseconds.
 * connectTimeoutMillis = 1000
 * 
 * 
 * # The maximum operation reponse time in milliseconds.
 * maxResponseTimeMillis = 500
 * 
 * 
 * # The page size to use in a simple paged request control
 * pageSize = 1000
 * 
 * 
 * # The number of reports in tools that do repeated processing.
 * reportCount = 1
 * 
 * 
 * # The interval between reports.
 * reportInterval = 1000
 * 
 * 
 * # The search scope.
 * scope = BASE
 * 
 * 
 * # sizeLimit     - Specifies the maximum number of entries that should be returned from
 * #                 the search. A value of zero indicates that there should not be
 * #                 any limit enforced. Note that the directory server may also be
 * #                 configured with a server-side size limit which can also limit
 * #                 the number of entries that may be returned to the client and
 * #                 in that case the smaller of the client-side and server-side
 * #                 limits will be used. If no size limit is provided, then a default
 * #                 of zero (unlimited) will be used.
 * sizeLimit = 100
 * 
 * 
 * # timeLimit     - Specifies the maximum length of time in seconds that the server
 * #                 should spend processing the search. A value of zero
 * #                 indicates that there should not be any limit
 * #                 enforced. Note that the directory server may also be
 * #                 configured with a server-side time limit which can
 * #                 also limit the processing time, and in that case the
 * #                 smaller of the client-side and server-side limits
 * #                 will be used. If no time limit is provided, then a
 * #                 default of zero (unlimited) will be used.
 * #
 * timeLimit = 60
 * 
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.0")
public class AuthorizationIdentityRequestControlTest
    implements LdapExceptionListener
{


  /*
   * Formats log records.
   */
  private final Formatter formatter = new MinimalLogFormatter();


  // connection to the server.
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
   * {@inheritDoc}
   */
  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    final LogRecord record =
        new LogRecord(Level.SEVERE,ldapExceptionEvent.getLdapException()
            .getExceptionMessage());
    final String helpfulMessage = formatter.format(record);
    TestUtils.displayHelpfulMessage(System.err,helpfulMessage);
  }


  /**
   * tests retrieving the authorization identity from the bind request.
   * 
   * @throws LDAPException
   * @throws SupportedFeatureException
   */
  @Test
  public void testGetAuthorizedIdentity() throws LDAPException,
      SupportedFeatureException
  {
    final AuthorizedIdentity authorizedIdentity =
        new AuthorizedIdentity(ldapConnection);
    Assert.assertNotNull(authorizedIdentity);
    authorizedIdentity.addLdapExceptionListener(this);


    final LdapServerConnectionData data =
        DataUsedForTestingPurposesOnly.getLdapServerConnectionData();
    final String bindDn = data.getBindDn();
    final String bindPassword = data.getBindPassword();
    final String authId =
        authorizedIdentity.getAuthorizationIdentityFromBindRequest(bindDn,
            bindPassword,getResponseTimeout());
    Assert.assertNotNull(authId);
    final String helpfulMessage =
        String.format("The AuthorizationIdentityRequestControl "
            + "was transmitted to the server.\n"
            + "The authorization id from the "
            + "AuthorizationIdentityResponseControl was '%s'.",authId);
    TestUtils.displayHelpfulMessage(System.out,helpfulMessage);
  }


  private long getResponseTimeout()
  {
    return 10;
  }
}
