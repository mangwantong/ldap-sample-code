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

package samplecode.listener;

import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Describes services offered by listeners for exception conditions.
 *
 * @param <T>
 *   The type of exception
 */
@Author("terry.gardner@unboundID.com")
@Since("Oct 30, 2011")
@CodeVersion("1.0")
public interface ExceptionListener<T extends Exception> {

  /**
   * Whether to invoke
   * {@link ExceptionListener#processException(Exception)}.
   *
   * @return Whether the processException method should be invoked.
   */
  boolean invoke();

  /**
   * Process an exception.
   *
   * @param exception
   *   The exception.
   */
  void processException(T exception);

}
