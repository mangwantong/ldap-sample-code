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


import com.unboundid.ldap.sdk.Control;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides services applicable to handling LDAP response controls that
 * an LDAP server may have included with a response.
 */
@Author("terry.gardner@unboundid.com")
@Since("01-Jan-2008")
@CodeVersion("1.3")
public interface ControlHandler
{

  /**
   * Do something with the specified control; if the {@code control}
   * parameter is {@code null}, no action is taken and no exception is
   * thrown.
   * 
   * @param object
   *          The client object.
   * @param control
   *          a response control.
   * @return The resulting ControlHandler object.
   */
  ControlHandler handleResponseControl(Object object,Control control);
}
