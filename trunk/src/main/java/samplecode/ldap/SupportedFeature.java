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

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.RootDSE;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * Used to validate whether a directory server supports a request
 * control or extended operation. Requires an {@code LDAPConnectionPool}
 * or {@code LDAPConnection} with a connection to directory server. The
 * provided connection is not closed.
 * <p/>
 * Clients should use this class to check whether a request control,
 * feature, or extension is supported by directory server before using
 * the control, feature, or extensions.
 * <p/>
 * This class has no public constructor; use
 * {@link SupportedFeature#newSupportedFeature(LDAPConnectionPool)} or
 * {@link SupportedFeature#newSupportedFeature(LDAPConnection)} to
 * obtain an instance of the object.
 * <p/>
 * Usage example:<blockquote>
 * <p/>
 * <pre>
 * SupportedControl supportedControl = SupportedControl.newSupportedControl(ldapConnectionPool);
 * try
 * {
 *   String oid = PreReadRequestControl.PRE_READ_REQUEST_OID;
 *   isControlSupported(oid);
 * }
 * catch(SupportedControlException supportedControlException)
 * {
 *   // The request control is not supported.
 * }
 * </pre>
 * <p/>
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 28, 2011")
@CodeVersion("1.0")
public final class SupportedFeature {

  private SupportedFeature(final LDAPConnection ldapConnection)
    throws LDAPException {
    rootDSE = ldapConnection.getRootDSE();
  }



  /**
   * Gets a new {@code SupportedControl} object.
   *
   * @param ldapConnection
   *         A valid connection to a directory server to be used to
   *         determine is a request control is supported.
   *         {@code ldapConnection} is not permitted to be {@code null}
   *         .
   *
   * @return A new {@code SupportedControl} object.
   */
  public static SupportedFeature
    newSupportedFeature(final LDAPConnection ldapConnection)
          throws LDAPException {
    ensureNotNull(ldapConnection);

    return new SupportedFeature(ldapConnection);
  }



  /**
   * Gets a new {@code SupportedControl} object. This convenience method
   * gets a connection from the specified {@code ldapConnectionPool}.
   *
   * @param ldapConnectionPool
   *         A valid pool of connections to be used to determine is a
   *         request control is supported. {@code ldapConnectionPool}
   *         is not permitted to be {@code null}.
   *
   * @return A new {@code SupportedControl} object.
   *
   * @throws LDAPException
   */
  public static SupportedFeature newSupportedFeature(
          final LDAPConnectionPool ldapConnectionPool) throws LDAPException {
    ensureNotNull(ldapConnectionPool);

    return newSupportedFeature(ldapConnectionPool.getConnection());
  }



  /**
   * Checks that the request control named by the specified {@code oid}
   * is supported by the server.
   * <p/>
   * Usage example:<blockquote>
   * <p/>
   * <pre>
   * SupportedControlOrExtendedOperation supportedControl =
   *         SupportedControlOrExtendedOperation(ldapConnectionPool);
   * try
   * {
   *   String oid = &quot;1.2.840.113556.1.4.319&quot;;
   *   validate(oid);
   * }
   * catch(SupportedControlOrExtendedOperationException supportedControlException)
   * {
   *   // The request control is not supported.
   * }
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param controlOID
   *         The OID of a request control to determine if the server
   *         supports.
   *
   * @throws SupportedFeatureException
   *         If the request control with {@code controlOID} is not
   *         supported by the server.
   */
  public boolean isControlSupported(final String controlOID)
    throws SupportedFeatureException {
    ensureNotNull(controlOID);

    boolean result = rootDSE.supportsControl(controlOID);
    if(!result) {
      final String exceptionMsg =
        String.format("The request control '%s' is not supported by this server.",
                      controlOID);
      throw new SupportedFeatureException(exceptionMsg,controlOID);
    }
    return result;
  }



  /**
   * Checks that the extended operation named by the specified
   * {@code oid} is supported by the server.
   * <p/>
   * Usage example:<blockquote>
   * <p/>
   * <pre>
   * SupportedControlOrExtendedOperation supportedControl =
   *         SupportedControlOrExtendedOperation(ldapConnectionPool);
   * try
   * {
   *   String oid = &quot;1.2.840.113556.1.4.319&quot;;
   *   validate(oid);
   * }
   * catch(SupportedControlOrExtendedOperationException supportedControlException)
   * {
   *   // The request control is not supported.
   * }
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param extensionOID
   *         The OID of an extended operation to determine if the
   *         server supports.
   *
   * @throws SupportedFeatureException
   *         If the extended operation with {@code extensionOID} is
   *         not supported by the server.
   */
  public boolean isExtendedOperationSupported(final String extensionOID)
          throws SupportedFeatureException {
    ensureNotNull(extensionOID);

    boolean result = rootDSE.supportsExtendedOperation(extensionOID);
    if(!result) {
      final String exceptionMsg =
              String.format("The extended operation '%s' " +
                            "is not supported by this server.",
                            extensionOID);
      throw new SupportedFeatureException(exceptionMsg,extensionOID);
    }
    return result;
  }



  @Override
  public String toString() {
    return "SupportedFeature [" + (rootDSE != null ? "rootDSE=" + rootDSE : "") + "]";
  }



  // The root DSE, used to determine whether the
  // desired control or extension is supported
  // by the server
  private final RootDSE rootDSE;

}
