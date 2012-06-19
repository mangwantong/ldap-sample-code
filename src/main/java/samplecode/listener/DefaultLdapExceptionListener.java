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


import com.unboundid.util.Validator;


import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * A minimal {@code LdapExceptionListener} that displays messages.
 */
public class DefaultLdapExceptionListener
        implements LdapExceptionListener
{


  /**
   * {@inheritDoc}
   */
  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    ldapRequestFailed(ldapExceptionEvent,Level.SEVERE);
  }



  private void ldapRequestFailed(final LdapExceptionEvent ev,final Level level)
  {
    logger.log(Level.SEVERE,ev.getLdapException().getMessage());
  }



  /**
   * Create the exception listener using the {@code logger} provided by
   * the client. The {@code logger} is not permitted to be {@code null}.
   */
  public DefaultLdapExceptionListener(
          final Logger logger)
  {
    Validator.ensureNotNull(logger);
    this.logger = logger;
  }



  /** logging facilities */
  private final Logger logger;

}
