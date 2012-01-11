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


import java.io.IOException;
import java.util.EventObject;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;


/**
 * An event reported to an object that has requested a service that
 * involves LDAP.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 31, 2011")
@CodeVersion("1.0")
public class IOExceptionEvent extends EventObject
{


  private static final long serialVersionUID = -7869888523368680001L;


  /**
   * The exception that caused this event.
   */
  private final IOException ioException;


  /**
   * @param source
   *          The object on which the Event initially occurred.
   * @param ioException
   *          the exception which caused this event.
   */
  public IOExceptionEvent(final Object source,final IOException ioException)
  {
    super(source);
    this.ioException = ioException;
  }


  protected IOExceptionEvent(final Object source)
  {
    super(source);
    ioException = null;
  }


  /**
   * @return the ioException
   */
  public final IOException getIoException()
  {
    return ioException;
  }

}
