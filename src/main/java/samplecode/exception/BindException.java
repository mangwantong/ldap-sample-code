package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.util.Validator;


/**
 * Supports the generation of a helpful message when an exception is
 * thrown when attempting to bind.
 */
public class BindException
        extends LdapException
{

  /**
   * Creates a new {@code BindException} object using the provided
   * {@code ldapException} object (which must not be null). Example
   * usage: <blockquote>
   * 
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
   * 
   * </blockquote>
   */
  public static LdapException newBindException(final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapException);
    return new BindException(ldapException);
  }



  @Override
  public String msg()
  {
    // TODO: add ResourceBundle support.
    final LDAPResult result = getLdapException().toLDAPResult();
    return String.format("An exception was thrown when attempting to change "
            + "the authentication state of a connection using "
            + "the bind request. The result code was %s. This "
            + "could mean that the password provided to a simple "
            + "bind request was incorrect, or that the "
            + "distinguished name provided to the bind request did not exist.",
            result.getResultCode());
  }



  private BindException(
          final LDAPException ldapException)
  {
    super(ldapException);
  }

}
