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


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;


import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides LogRecord objects for clients that catch
 * {@code LDAPException} objects. Clients should create a new
 * {@code LdapLogRecord} object using the
 * {@code ExceptionLdapLogRecord.newExceptionLdapLogRecord(exception)}
 * and call get {@code getLogRecord} method on the {@code LdapLogRecord}
 * .
 * <p>
 * Usage Example:<blockquote>
 * 
 * <pre>
 * try
 * {
 *   LDAPConnection ldapConnection = getConnection();
 * }
 * catch(LDAPException ldapException)
 * {
 *   LdapLogRecord ldapLogRecord = new ExceptionLdapLogRecord(ldapException);
 *   LogRecord record = ldapLogRecord.getLogRecord(Level.SEVERE);
 *   displayErrorMsg(new MinimalLogFormatter().format(record));
 * }
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 6, 2011")
@CodeVersion("1.0")
class ExceptionLdapLogRecord
        implements LdapLogRecord
{

  static ExceptionLdapLogRecord newExceptionLdapLogRecord(final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapException);
    return new ExceptionLdapLogRecord(ldapException);
  }



  /**
   * {@inheritDoc}
   * <p>
   * The reasonably informative message is taken from the services
   * provided by an {@code LDAPException}.
   */
  @Override
  public LogRecord getLogRecord(final Level level)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append(String.format("LDAPException: %s",ldapException.getExceptionMessage()));
    return new LogRecord(level,builder.toString());
  }



  private ExceptionLdapLogRecord(
          final LDAPException ldapException)
  {
    this.ldapException = ldapException;
  }



  private final LDAPException ldapException;
}
