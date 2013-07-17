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


import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * A minimal implementation of the {@code LdapLogRecord} interface. used
 * to create a {@code LogRecord} object suitable for formatting by the a
 * {@link Formatter}.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 24, 2011")
@CodeVersion("1.0")
@NotMutable
public class BasicLdapLogRecord
  implements LdapLogRecord {

  /**
   * {@inheritDoc}
   * <p/>
   * Creates a {@code LogRecord} object using the message with which
   * this object was created, which is known to be not {@code null}.
   * <p/>
   * Precondition: {@code level} is not permitted to be {@code null}.
   */
  @Override
  public LogRecord getLogRecord(final Level level) {
    Validator.ensureNotNull(level);
    return new LogRecord(level,logMessage);
  }



  /**
   * Creates a {@code BasicLdapLogRecord} initialized with a non-null
   * {@code logMessage} that can be used to create a new
   * {@code LogRecord} object.
   *
   * @param logMessage
   *   A message used to create a new {@code LogRecord}.
   *   Precondition: {@code logMessage} is not permitted to be
   *   {@code null}.
   */
  public BasicLdapLogRecord(
    final String logMessage) {
    Validator.ensureNotNull(logMessage);
    this.logMessage = logMessage;
  }



  // A message used to instantiate a LogRecord object.
  private final String logMessage;
}
