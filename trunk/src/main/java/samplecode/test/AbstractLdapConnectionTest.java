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
import com.unboundid.util.MinimalLogFormatter;


import java.util.logging.Formatter;


import org.junit.After;
import org.junit.Before;


/**
 * A minimal implementation of {@code LdapConnectionTest}.
 */
public abstract class AbstractLdapConnectionTest
{


  /**
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


  protected Formatter getFormatter()
  {
    return formatter;
  }


  protected LDAPConnection getLdapConnection()
  {
    return ldapConnection;
  }
}
