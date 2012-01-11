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
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;


import java.util.Vector;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;


/**
 * a minimal implementation of {@code ObservedByLdapExceptionListener}
 * with support for add, remove, and firing listeners.
 */
@Author("terry.gardner@unboundid.com")
@Since("Jan 7, 2012")
@CodeVersion("1.0")
public abstract class AbstractObservedByLdapExceptionListener
    implements ObservedByLdapExceptionListener
{


  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
      new Vector<LdapExceptionListener>();


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapExceptionListener(
      final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.add(ldapExceptionListener);
    }
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
      final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapConnection,ldapException);
    Vector<LdapExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapExceptionListener>)ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev =
        new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapExceptionListener(
      final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.remove(ldapExceptionListener);
    }
  }


}
