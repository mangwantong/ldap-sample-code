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

import com.unboundid.ldap.sdk.Entry;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.EventObject;

/**
 * An event created from an entry that was read from an LDIF file. This
 * event is messaged to interested parties.
 */
@Author("terry.gardner@unboundid.com") @Since("Jan 5, 2012") @CodeVersion("1.0")
public class LdifEntryEvent extends EventObject
{

  private static final long serialVersionUID = -6982646541468330652L;

  /**
   * Creates a {@code LdifEvent} with default state.
   *
   * @param source
   * @param entry
   */
  public LdifEntryEvent(final Object source, final Entry entry)
  {
    super(source);
    this.entry = entry;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("LdifEvent [entry=%s]", entry);
  }

  // an entry read from a file or stream containing LDIF.
  private final Entry entry;

  /**
   * @return the entry
   */
  public Entry getEntry()
  {
    return entry;
  }

}
