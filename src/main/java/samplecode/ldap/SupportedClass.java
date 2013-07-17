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

package samplecode.ldap;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.lang.reflect.InvocationTargetException;


/**
 * Provides a method to control a new instance of a class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 10, 2011")
@CodeVersion("1.1")
public interface SupportedClass {

  /**
   * Creates a new instance of a class.
   *
   * @return a new instance of a class.
   *
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws NoSuchMethodException
   * @throws IllegalArgumentException
   * @throws InvocationTargetException
   */
  Object newInstance() throws InstantiationException, IllegalAccessException, SecurityException,
    NoSuchMethodException, IllegalArgumentException, InvocationTargetException;
}
