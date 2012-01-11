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


import com.unboundid.ldap.sdk.unboundidds.controls.AttributeRight;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;


/**
 * This exception is thrown when an entry fails to pass the test for the
 * authorization identity's effective rights.
 */
@SuppressWarnings("serial")
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.0")
@NotMutable
public final class CheckEffectiveRightsException
  extends Exception
{


  // The attribute that was tested
  private final String attributeName;


  // The right to the attribute
  private final AttributeRight attributeRight;


  /**
   * Retrieve the name of the attribute that was tested.
   * 
   * @return the name of the attribute which was tested.
   */
  public String attributeName()
  {
    return this.attributeName;
  }


  /**
   * Retrieve the name of the right that was tested on the provided
   * attribute.
   * 
   * @return the name of the right that was tested on the provided
   *         attribute.
   */
  public AttributeRight attributeRight()
  {
    return this.attributeRight;
  }


  /**
   * Creates a {@code CheckEffectiveRightsException} initialized with
   * the {@code attribute} that was tested with respect to the
   * {@code attributeRight}.
   * 
   * @param attributeName
   *          name of the attribute that was tested - and failed.
   * @param attributeRight
   *          right against which the attribute was tested.
   */
  public CheckEffectiveRightsException(final String attributeName,
                                       final AttributeRight attributeRight)
  {
    Validator.ensureNotNull(attributeName,attributeRight);
    this.attributeName = attributeName;
    this.attributeRight = attributeRight;
  }

}
