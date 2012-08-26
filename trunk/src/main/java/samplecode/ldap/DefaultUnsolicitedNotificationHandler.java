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

import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.LDAPCommandLineTool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.logging.LogAware;

/**
 * A notification handler wherein the
 * {@code handleUnsolicitedNotification()} method logs a message using
 * {@link LDAPCommandLineTool#out(Object...)}.
 */
@Author("terry.gardner@unboundid.com") @Since("Dec 10, 2011") @CodeVersion("1.0")
public class DefaultUnsolicitedNotificationHandler
        implements UnsolicitedNotificationHandler, LogAware
{

  /**
   * Prepares this unsolicited notification handler for use by
   * initializing it using the provided {@code ldapCommandLineTool}.
   *
   * @param ldapCommandLineTool A tool which has connected to directory server and
   *                            requires this a notification handler to support
   *                            unsolicited extended results. {@code ldapCommandLineTool}
   *                            may not be {@code null}.
   */
  public DefaultUnsolicitedNotificationHandler(final LDAPCommandLineTool ldapCommandLineTool)
  {
    this.ldapCommandLineTool = ldapCommandLineTool;
  }

  /**
   * {@inheritDoc}
   * <p/>
   * Logs a message that the extended result was received as an
   * unsolicited notification. The message printed contains the returned
   * object from {@link ExtendedResult#getDiagnosticMessage()}.
   */
  @Override
  public void handleUnsolicitedNotification(final LDAPConnection ldapConnection,
          final ExtendedResult extendedResult)
  {
    if(ldapConnection == null)
    {
      throw new IllegalArgumentException("ldapConnection must not be null.");
    }
    if(extendedResult == null)
    {
      throw new IllegalArgumentException("extendedResult must not be null.");
    }
    if(getLogger().isWarnEnabled())
    {
      final String msg =
              String.format("the server to which the client is connected has sent an " +
                      "unsolicited notification including the following diagnostic message: " +
                      "%s", extendedResult.getDiagnosticMessage());
      getLogger().warn(msg);
    }
  }

  private final LDAPCommandLineTool ldapCommandLineTool;
  private Log logger;

  /**
   * @return the logger
   */
  @Override public Log getLogger()
  {
    if(logger == null)
    {
      logger = LogFactory.getLog(getClass());
    }
    return logger;
  }

}
