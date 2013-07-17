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

import java.util.EventListener;
import java.util.List;


/**
 * defines services for classes that are observed by another class which is
 * interested in LDIF events that occur.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 31, 2011")
@CodeVersion("1.0")
public interface ObservedByLdifEntryEventListener extends EventListener {

  /**
   * adds the specified {@code ldifEventListener} to receive LDAP
   * exception events from this class.
   *
   * @param ldifEventListener
   *   the LDIF Event listener. If {@code ldifEventListener} is
   *   {@code null}, no action is taken and no exception is
   *   thrown.
   */
  void addLdifEventListener(LdifEntryEventListener ldifEventListener);

  /**
   * notifies the listener that an {@code entry} has been read from a
   * file.
   *
   * @param entry
   */
  void fireLdifEventListener(Entry entry);

  /**
   * removes the specified {@code ldifEventListener} to receive LDAP
   * exception events from this class.
   *
   * @param ldifEventListener
   *   the LDIF Event listener. If {@code ldifEventListener} is
   *   {@code null}, no action is taken and no exception is
   *   thrown.
   */
  void removeLdifEventListener(LdifEntryEventListener ldifEventListener);

  /**
   * @return an unmodifiable list of event listeners
   */
  List<LdifEntryEventListener> getLdifEventListeners();
}
