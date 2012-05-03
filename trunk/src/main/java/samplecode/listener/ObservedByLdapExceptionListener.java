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


import java.util.EventListener;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Indicates that a class is observed by another class which is
 * interested in LDAP exceptions that occur.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 31, 2011")
@CodeVersion("1.0")
public interface ObservedByLdapExceptionListener
        extends EventListener
{

  /**
   * adds the specified {@code ldapExceptionListener} to receive LDAP
   * exception events from this class.
   * 
   * @param ldapExceptionListener
   *          the LDAP exception listener. If
   *          {@code ldapExceptionListener} is {@code null}, no action
   *          is taken and no exception is thrown.
   */
  void addLdapExceptionListener(LdapExceptionListener ldapExceptionListener);



  /**
   * notifies the listener that an {@code LdapExceptionEvent} has
   * occurred.
   * 
   * @param ldapConnection
   *          connection to an LDAP server
   * @param ldapException
   *          the exception which caused the notification
   */
  void fireLdapExceptionListener(LDAPConnection ldapConnection,LDAPException ldapException);



  /**
   * removes the specified {@code ldapExceptionListener} to receive LDAP
   * exception events from this class.
   * 
   * @param ldapExceptionListener
   *          the LDAP exception listener. If
   *          {@code ldapExceptionListener} is {@code null}, no action
   *          is taken and no exception is thrown.
   */
  void removeLdapExceptionListener(LdapExceptionListener ldapExceptionListener);
}
