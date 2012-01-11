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


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;


import samplecode.AttributeNotSupportedException;
import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;
import samplecode.SupportedUserAttribute;


/**
 * test the functionality of the {@code SupportedAttribute} class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 29, 2011")
@CodeVersion("1.0")
public final class SupportedAttributeTest
{ // connection to directory server.


  private static final String[] knownAttributes = new String[]
  {
      "cn",
      "commonName",
      "uid",
  };


  private static final String[] unknownAttributes = new String[]
  {
      "1.1",
      "abc",
      "&^&^^"
  };


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
   * tests that {@code SupportedAttribute} correctly handles a known
   * good attribute.
   * 
   * @throws AttributeNotSupportedException
   * @throws LDAPException
   */
  @Test
  public void testSupportedAttributeKnownGoodAttributeType()
      throws LDAPException,AttributeNotSupportedException
  {
    for(final String s : SupportedAttributeTest.knownAttributes)
    {
      TestUtils.displayHelpfulMessage(System.err,"testing " + s);
      SupportedUserAttribute.getInstance().supported(ldapConnection,s);
    }
  }


  /**
   * tests that {@code SupportedAttribute} correctly handles an unknown
   * attribute.
   * 
   * @throws LDAPException
   */
  @Test
  public void testSupportedAttributeUnknownAttributeType() throws LDAPException
  {
    for(final String s : SupportedAttributeTest.unknownAttributes)
    {
      TestUtils.displayHelpfulMessage(System.err,"testing " + s);
      try
      {
        SupportedUserAttribute.getInstance().supported(ldapConnection,s);
      }
      catch (final AttributeNotSupportedException ex)
      {
        Assert.assertTrue(true);
      }
    }
  }
}
