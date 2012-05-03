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


import com.unboundid.util.Validator;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides services used in validating whether an attribute is a valid
 * attribute.
 */
@SuppressWarnings("serial")
@Author("terry.gardner@unboundid.com")
@Since("Dec 4, 2011")
@CodeVersion("1.3")
public final class AttributeNotSupportedException
        extends Exception
{

  /**
   * Retrieve The attribute name that was found to be not supported in
   * the directory server to which the LDAP client is connected.
   * 
   * @return The attribute name.
   */
  public String getAttributeName()
  {
    return attributeName;
  }



  @Override
  public String toString()
  {
    return "AttributeNotSupportedException [" +
            (attributeName != null ? "attributeName=" + attributeName : "") + "]";
  }



  /**
   * Constructs a {@code AttributeNotSupportedException} object that has
   * been initialized with {@code attributeName}. The
   * {@code attributeName} presumably is not supported in the directory
   * server schema to which the LDAP client is connected.
   * 
   * @param attributeName
   *          The attribute name that is not supported by the directory
   *          server to which the LDAP client is connected.
   */
  public AttributeNotSupportedException(
          final String attributeName)
  {
    Validator.ensureNotNull(attributeName);
    this.attributeName = attributeName;
  }



  /**
   * The attribute name that was found to be not supported in the
   * directory server to which the LDAP client is connected.
   */
  private final String attributeName;
}
