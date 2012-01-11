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


import java.util.EventObject;


import com.unboundid.ldap.sdk.Entry;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;


/**
 * An event created from an entry that was read from an LDIF file. This
 * event is messaged to interested parties.
 */
@Author("terry.gardner@unboundid.com")
@Since("Jan 5, 2012")
@CodeVersion("1.0")
public class LdifEntryEvent
    extends EventObject
{


  private static final long serialVersionUID = -6982646541468330652L;


  // an entry read from a file or stream containing LDIF.
  private final Entry entry;


  /**
   * Creates a {@code LdifEvent} with default state.
   * 
   * @param source
   * @param entry
   */
  public LdifEntryEvent(
      final Object source,final Entry entry)
  {
    super(source);
    this.entry = entry;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj)
  {
    if(this == obj)
    {
      return true;
    }
    if(obj == null)
    {
      return false;
    }
    if(!(obj instanceof LdifEntryEvent))
    {
      return false;
    }
    final LdifEntryEvent other = (LdifEntryEvent)obj;
    if(entry == null)
    {
      if(other.entry != null)
      {
        return false;
      }
    }
    else if(!entry.equals(other.entry))
    {
      return false;
    }
    return true;
  }


  /**
   * @return the entry
   */
  public Entry getEntry()
  {
    return entry;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (entry == null ? 0 : entry.hashCode());
    return result;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("LdifEvent [entry=%s]",entry);
  }


}
