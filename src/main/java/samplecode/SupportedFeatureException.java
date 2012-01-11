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


/**
 * Thrown when a control OID is not supported by the server.
 */
@SuppressWarnings("serial")
@Author("terry.gardner@unboundid.com")
@Since("Nov 28, 2011")
@CodeVersion("1.0")
public final class SupportedFeatureException
  extends Exception
{


  private final String controlOID;


  /**
   * Retrieve the control OID (a dot-separated series of octets
   * represented as a string).
   * 
   * @return control OID.
   */
  public String getControlOID()
  {
    return this.controlOID;
  }


  /**
   * Prepares a {@code SupportedControlException} with the specified
   * detail message.
   * 
   * @param msg
   *          The detail message.
   * @param controlOID
   *          The OID that caused the exception.
   */
  public SupportedFeatureException(final String msg,final String controlOID)
  {
    super(msg);
    this.controlOID = controlOID;
  }


}
