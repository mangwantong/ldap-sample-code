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


import java.util.EventListener;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provided to support notification that an entry has been read from an
 * LDIF file.
 */
@Author("terry.gardner@unboundid.com")
@Since("Jan 5, 2012")
@CodeVersion("1.0")
public interface LdifEntryEventListener
        extends EventListener
{

  /**
   * An entry has been read from an LDIF file.
   * 
   * @param ldifEvent
   *          the event that transpired
   */
  void entryReadFromLdifFile(LdifEntryEvent ldifEvent);
}
