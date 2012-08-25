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

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPSearchException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.Vector;

/**
 * a minimal implementation of
 * {@code ObservedByLdapSearchExceptionListener} with support for add,
 * remove, and firing listeners.
 */
@Author("terry.gardner@unboundid.com") @Since("Jan 7, 2012") @CodeVersion("1.0")
public class AbstractObservedByLdapSearchExceptionListener
        implements ObservedByLdapSearchExceptionListener
{

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapSearchExceptionListener(
          final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.add(ldapSearchExceptionListener);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapSearchExceptionListener(
          final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.remove(ldapSearchExceptionListener);
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked") @Override
  public void fireLdapSearchExceptionListener(final LDAPConnection ldapConnection,
          final LDAPSearchException ldapSearchException)
  {
    Vector<LdapSearchExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapSearchExceptionListener>) ldapSearchExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapSearchExceptionEvent ev =
            new LdapSearchExceptionEvent(this, ldapConnection, ldapSearchException);
    for(final LdapSearchExceptionListener l : copy)
    {
      l.searchRequestFailed(ev);
    }
  }

  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapSearchExceptionListener> ldapSearchExceptionListeners =
          new Vector<LdapSearchExceptionListener>();

}
