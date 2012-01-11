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
 * test harness for the Who Am I? extended operation.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.0")
public class WhoAmITest
    implements LdapExceptionListener
{


  /**
   * Formats log records.
   */
  private final Formatter formatter = new MinimalLogFormatter();


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
   * tests retrieving the authorization identity using the Who am I?
   * extended request.
   * 
   * @throws LDAPException
   * @throws SupportedFeatureException
   */
  @Test
  public void testGetAuthorizationIdentityFromWhoAmIExtendedOperation()
      throws LDAPException,SupportedFeatureException
  {
    final AuthorizedIdentity authorizedIdentity =
        new AuthorizedIdentity(ldapConnection);
    final String authId =
        authorizedIdentity
            .getAuthorizationIdentityWhoAmIExtendedOperation(getResponseTimeout());
    Assert.assertNotNull(authId);
    final String helpfulMessage =
        String.format(
            "The AuthorizationIdentityRequestControl was transmitted to the server.\n"
                + "The authorization id from the "
                + "Who Am I? extended request was '%s'.",authId);
    TestUtils.displayHelpfulMessage(System.out,helpfulMessage);
  }


  private long getResponseTimeout()
  {
    return 10;
  }
}
