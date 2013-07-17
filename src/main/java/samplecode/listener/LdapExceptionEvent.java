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
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.EventObject;


/**
 * An event reported to an object that has requested a service that
 * involves LDAP.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 31, 2011")
@CodeVersion("1.0")
public class LdapExceptionEvent
  extends EventObject {

  private static final long serialVersionUID = -1429895347237817307L;



  /**
   * @param source
   *   The object on which the Event initially occurred.
   * @param ldapConnection
   *   facility for interacting with an LDAPv3 directory server.
   *   It provides a means of establishing a connection to the
   *   server, sending requests, and reading responses. See RFC
   *   4511 for the LDAPv3 protocol specification and more
   *   information about the types of operations defined in LDAP.
   * @param ldapException
   *   the exception which caused this event.
   */
  public LdapExceptionEvent(
    final Object source, final LDAPConnection ldapConnection,
    final LDAPException ldapException) {
    super(source);
    this.ldapConnection = ldapConnection;
    this.ldapException = ldapException;
  }



  /**
   * The connection associated with the event.
   */
  private final LDAPConnection ldapConnection;



  /**
   * @return the ldapConnection
   */
  public final LDAPConnection getLdapConnection() {
    return ldapConnection;
  }



  /**
   * The exception that caused this event.
   */
  private final LDAPException ldapException;



  /**
   * @return the ldapException
   */
  public final LDAPException getLdapException() {
    return ldapException;
  }

}
