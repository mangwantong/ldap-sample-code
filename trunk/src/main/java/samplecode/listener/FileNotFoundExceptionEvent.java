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

import java.io.FileNotFoundException;


/**
 * An event reported to an object interested in knowing a file was not
 * found.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 31, 2011")
@CodeVersion("1.1")
public class FileNotFoundExceptionEvent
  extends IOExceptionEvent {

  private static final long serialVersionUID = -7869888523368680001L;



  /**
   * @param source
   *   The object on which the Event initially occurred.
   * @param filename
   *   the name of the file that was not found.
   * @param fileNotFoundException
   *   the exception which caused this event.
   */
  public FileNotFoundExceptionEvent(
    final Object source, final String filename,
    final FileNotFoundException fileNotFoundException) {
    super(source);
    this.filename = filename;
    this.fileNotFoundException = fileNotFoundException;
  }



  /**
   * The exception that caused this event.
   */
  private final FileNotFoundException fileNotFoundException;



  /**
   * @return the ioException
   */
  public final FileNotFoundException getFileNotFoundException() {
    return fileNotFoundException;
  }



  private final String filename;



  /**
   * @return the filename
   */
  public String getFilename() {
    return filename;
  }

}
