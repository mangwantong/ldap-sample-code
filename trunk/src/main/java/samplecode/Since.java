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


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * Provides an annotation of a component indicating when the component
 * was first created, or first appeared in a particular version.
 */
@Author("terry.gardner@unboundid.com")
@Retention(RetentionPolicy.RUNTIME)
public @interface Since
{


  /**
   * The first appearance of the component.
   * 
   * @return A string representation of the first appearance, typically
   *         a date.
   */
  String value();
}
