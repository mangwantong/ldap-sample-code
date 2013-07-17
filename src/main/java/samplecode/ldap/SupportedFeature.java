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

package samplecode.ldap;

import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPInterface;
import com.unboundid.ldap.sdk.RootDSE;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import static com.unboundid.util.Validator.ensureNotNullWithMessage;


/**
 * Provides support for methods used to validate whether a directory server
 * supports a request control or extended operation. Requires an {@code
 * LDAPConnectionPool} or {@code LDAPConnection} with a connection to
 * directory server. The provided connection is not closed. This class cannot
 * be instantiated
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 28, 2011")
@CodeVersion("1.1")
public abstract class SupportedFeature {


  /**
   * Checks that the request control named by the specified {@code oid}
   * is supported by the server.
   *
   * @param conn
   *   a connection to the LDAP server
   * @param controlOID
   *   The OID of a request control to determine if the server
   *   supports.
   */
  public static boolean isControlSupported(LDAPInterface conn,
                                           String controlOID) {
    ensureNotNullWithMessage(conn,"conn was null.");
    ensureNotNullWithMessage(controlOID,"controlOID was null.");

    final RootDSE rootDSE;
    try {
      rootDSE = conn.getRootDSE();
      return rootDSE.supportsControl(controlOID);
    } catch(LDAPException e) {
// TODO exception handling
    }
    return false;
  }



  /**
   * Checks that the extended operation named by the specified
   * {@code oid} is supported by the server.
   *
   * @param extensionOID
   *   The OID of an extended operation to determine if the
   *   server supports.
   */
  public static boolean isExtendedOperationSupported(LDAPInterface conn,
                                                     String extensionOID) {
    ensureNotNullWithMessage(conn,"conn was null.");
    ensureNotNullWithMessage(extensionOID,"extensionOID was null.");

    final RootDSE rootDSE;
    try {
      rootDSE = conn.getRootDSE();
      return rootDSE.supportsExtendedOperation(extensionOID);
    } catch(LDAPException e) {
// TODO exception handling
    }
    return false;
  }

}
