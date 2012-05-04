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


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Represents a value associated with a key (a property).
 * 
 * @param <T>
 *          the type of the value.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 23, 2011")
@CodeVersion("1.0")
public interface PropertyValue<T>
{

  /**
   * Retrieves the value from the properties map, if present.
   * 
   * @return the value associated with a key.
   * @throws PropertyNotFoundException
   *           when a property cannot be found
   */
  T getValue() throws PropertyNotFoundException;



  /**
   * Invokes the setter from the {@link SetterAware} class.
   * 
   * @throws PropertyNotFoundException
   *           when a property cannot be found
   */
  void invokeSetter() throws PropertyNotFoundException;
}