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


import java.io.PrintStream;


import com.unboundid.util.CommandLineTool;


/**
 * Provides a method to display a message on one of two
 * {@link PrintStream} objects when a {@code CommandLineTool} has
 * completed processing. {@link CommandLineTool#doToolProcessing()} does
 * not throw an exception but provides a result code.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 24, 2011")
@CodeVersion("1.0")
public interface ToolCompletedProcessing
{


  /**
   * Transmits a message created at the discretion of the implementing
   * class to the output stream or error stream (errors only).
   * 
   * @param outStream
   * @param errStream
   */
  void displayMessage(PrintStream outStream,PrintStream errStream);

}
