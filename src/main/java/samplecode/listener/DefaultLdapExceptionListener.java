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

import org.apache.commons.logging.Log;

import java.util.logging.Level;

import static com.unboundid.util.Validator.ensureNotNull;

/**
 * A minimal {@code LdapExceptionListener} that displays messages.
 */
public class DefaultLdapExceptionListener implements LdapExceptionListener {

  private final Log logger;

  /**
   * Create the exception listener using the {@code logger} provided by
   * the client. The {@code logger} is not permitted to be {@code null}.
   */
  public DefaultLdapExceptionListener(final Log logger) {
    if (logger == null) {
      throw new IllegalArgumentException("logger must not be null.");
    }
    this.logger = logger;
  }

  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent) {
    ldapRequestFailed(ldapExceptionEvent,Level.SEVERE);
  }

  private void ldapRequestFailed(final LdapExceptionEvent ev, final Level level) {
    ensureNotNull(ev);

    logger.error(ev.getLdapException().getMessage());
  }

}
