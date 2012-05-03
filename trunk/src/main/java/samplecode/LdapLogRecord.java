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


import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Defines services provided by classes that implement the construction
 * of LogRecord objects.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 6, 2011")
@CodeVersion("1.0")
public interface LdapLogRecord
{

  /**
   * Constructs a {@code LogRecord} suitable for use with the Java
   * logging framework.
   * 
   * @param level
   *          The severity of the message.
   * @return A LogRecord to use with the Java logging framework.
   */
  LogRecord getLogRecord(Level level);
}
