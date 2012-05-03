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
 * Clients should use this interface when an exception (or error) has
 * occurred, and a de-coupled way to construct an error message is
 * required.
 * <p>
 * example usage:
 * </p>
 * 
 * <pre>
 * 
 * public final class NumberFormatExceptionMsg
 *         implements ExceptionMsg
 * {
 * 
 *   &#064;Override
 *   public String msg()
 *   {
 *     return String.format(&quot;There was an error trying to convert a string into an integer.&quot;);
 *   }
 * }
 * </pre>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 5, 2011")
@CodeVersion("1.1")
public interface ExceptionMsg
{

  /**
   * Retrieve the message associated with the error.
   * 
   * @return A helpful message.
   */
  String msg();
}
