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
public class ClientSideConnectExceptionMessageGenerator
  extends LdapException {

  private static final String PROP_NAME_CONNECT_EXCEPTION = "clientSideConnectExceptionMessage";



  /**
   * Creates a new {@code ClientSideConnectExceptionMessageGenerator}
   * object using the provided {@code ldapException} object (which must
   * not be null). Example usage: <blockquote>
   * <p/>
   * <pre>
   * try
   * {
   *   ldapResult = getConnection().modifyDN(modifyDnRequest);
   *   ldapConnection.close();
   * }
   * catch(final LDAPException exception)
   * {
   *   final LdapException displayControl = ExceptionMsgFactory.getLdapExceptionMsg(exception);
   *   getLogger().log(Level.SEVERE,displayControl.displayControl());
   *   return exception.getResultCode();
   * }
   * </pre>
   * <p/>
   * </blockquote>
   */
  public static LdapException newClientSideConnectExceptionMessageGenerator(
    final ResourceBundle resourceBundle, final LDAPException ldapException) {
    Validator.ensureNotNull(ldapException);
    return new ClientSideConnectExceptionMessageGenerator(resourceBundle,ldapException);
  }



  @Override
  public String msg() {
    // TODO: add ResourceBundle support.
    final LDAPResult result = getLdapException().toLDAPResult();
    return String.format(
      getResourceBundle().getString(
        ClientSideConnectExceptionMessageGenerator.PROP_NAME_CONNECT_EXCEPTION),
      getLdapException().getExceptionMessage(),result.getResultCode());
  }



  private ClientSideConnectExceptionMessageGenerator(
    final ResourceBundle resourceBundle, final LDAPException ldapException) {
    super(resourceBundle,ldapException);
  }

}
