package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.Validator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.ResourceBundle;


/**
 * Supports the generation of a helpful message when an exception is
 * thrown when attempting to bind.
 */
@Author("terry.gardner@unboundID.com")
@Since("Jun 17, 2012")
@CodeVersion("1.0")
public class BindException
  extends LdapException {

  private static final String PROP_NAME_BIND_EXCEPTION = "bindExceptionMessage";



  /**
   * Creates a new {@code BindException} object using the provided
   * {@code ldapException} object (which must not be null). Example
   * usage: <blockquote>
   * <p/>
   * <pre>
   * try
   * {
   *   ldapResult = getConnection().modifyDN(modifyDnRequest);
   *   ldapConnection.close();
   * }
   * catch(final LDAPException exception)
   * {
   *   final LdapException msg = ExceptionMsgFactory.getLdapExceptionMsg(exception);
   *   getLogger().log(Level.SEVERE,msg.msg());
   *   return exception.getResultCode();
   * }
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param resourceBundle
   */
  public static LdapException newBindException(final ResourceBundle resourceBundle,
                                               final LDAPException ldapException) {
    Validator.ensureNotNull(ldapException);
    return new BindException(resourceBundle,ldapException);
  }



  @Override
  public String msg() {
    final LDAPResult result = getLdapException().toLDAPResult();
    return String.format(getResourceBundle().getString(BindException.PROP_NAME_BIND_EXCEPTION),
      result.getResultCode());
  }



  private BindException(
    final ResourceBundle resourceBundle, final LDAPException ldapException) {
    super(resourceBundle,ldapException);
  }

}
