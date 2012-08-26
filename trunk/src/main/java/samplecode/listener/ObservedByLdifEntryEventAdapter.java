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

import samplecode.util.SampleCodeCollectionUtils;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.Collections;
import java.util.List;

/**
 * A minimal, abstract implementation of
 * {@code ObservedByLdifEntryEventListener} supplying methods to add and
 * remove listeners.
 */
@Author("terry.gardner@unboundid.com") @Since("Jan 5, 2012") @CodeVersion("1.0")
public abstract class ObservedByLdifEntryEventAdapter
        implements ObservedByLdifEntryEventListener
{

  protected ObservedByLdifEntryEventAdapter()
  {
    ldifEventListeners =
            SampleCodeCollectionUtils.newArrayList();
  }

  @Override
  public synchronized void addLdifEventListener(final LdifEntryEventListener ldifEventListener)
  {
    if(ldifEventListener != null)
    {
      ldifEventListeners.add(ldifEventListener);
    }
  }

  @Override
  public synchronized void removeLdifEventListener(
          final LdifEntryEventListener ldifEventListener)
  {
    if(ldifEventListener != null)
    {
      ldifEventListeners.remove(ldifEventListener);
    }
  }

  /**
   * @return an unmodifiable list of event listeners
   */
  @Override public List<LdifEntryEventListener> getLdifEventListeners()
  {
    return Collections.unmodifiableList(ldifEventListeners);
  }

  private List<LdifEntryEventListener> ldifEventListeners;

}
