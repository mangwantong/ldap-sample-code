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


import java.io.PrintStream;
import java.util.EventListener;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;


/**
 * Notified when an error or exception occurs at the discretion of the
 * client and implementor.
 * 
 * @param <T>
 *          the type of data transmitted to {@ode displayError}.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 26, 2011")
@CodeVersion("1.0")
public interface ErrorListener<T>
    extends EventListener
{


  /**
   * Displays an error message in a manner decided by the implementor.
   * 
   * @param errStream
   *          a print stream to which the error message is transmitted.
   * @param object
   *          the object that generated the error.
   */
  void displayError(PrintStream errStream,T object);
}
