package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.ResourceBundle;


/**
 * Provides support for helpful messages when an {@link LDAPException}
 * is thrown.
 */
@Author("terry.gardner@unboundID.com")
@Since("Jun 17, 2012")
@CodeVersion("1.0")
public class LdapException
  implements ExceptionMsg {

  @Override
  public LDAPException getLdapException() {
    return ldapException;
  }



  @Override
  public String msg() {
    final String msg =
      String.format("An LDAP exception has occurred. The exception was %s",ldapException);
    return msg;
  }



  @Override
  public String toString() {
    return String.format("LdapException [resourceBundle=%s, ldapException=%s]",resourceBundle,
      ldapException);
  }



  protected ResourceBundle getResourceBundle() {
    return resourceBundle;
  }



  /**
   * Creates an {@code LdapException} object from the provided
   * {@code ldapException} object - which is not permitted to be
   * {@code null}.
   *
   * @param resourceBundle
   */
  protected LdapException(
    final ResourceBundle resourceBundle, final LDAPException ldapException) {
    Validator.ensureNotNull(ldapException,resourceBundle);
    this.ldapException = ldapException;
    this.resourceBundle = resourceBundle;
  }



  /**
   * The {@link LDAPException} that was thrown.
   */
  private final LDAPException ldapException;


  private final ResourceBundle resourceBundle;

}
