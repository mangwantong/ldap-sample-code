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
package samplecode;


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.util.Validator;


/**
 * Provides services used to determine whether an attribute is supported
 * by the directory server to which an LDAP client is connected.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 4, 2011")
@CodeVersion("1.1")
public final class SupportedUserAttribute
  extends AbstractSupportedAttribute
{


  // the singleton instance of SupportedAttribute
  private static SupportedUserAttribute instance = null;


  /**
   * Get an instance of the SupportedAttribute class.
   * 
   * @return an instance of SupportedAttribute
   */
  public static SupportedUserAttribute getInstance()
  {
    if(SupportedUserAttribute.instance == null)
    {
      SupportedUserAttribute.instance = new SupportedUserAttribute();
    }
    return SupportedUserAttribute.instance;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void supported(final LDAPConnection ldapConnection,
      final String attributeName) throws AttributeNotSupportedException,
      LDAPException
  {
    Validator.ensureNotNullWithMessage(ldapConnection,
        "SupportedAttribute requires a valid connection to an LDAP server.");


    Validator.ensureNotNullWithMessage(attributeName,
        "The attributeName to be checked cannot be null.");


    /**
     * Directory server subschema sub-entry. This includes information
     * about the attribute syntaxes, matching rules, attribute types,
     * object classes, name forms, DIT content rules, DIT structure
     * rules, and matching rule uses defined in the server schema.
     */
    final Schema schema = Schema.getSchema(ldapConnection);
    if(schema.getAttributeType(attributeName) == null)
    {
      throw new AttributeNotSupportedException(attributeName);
    }
  }


}
