package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides support for helpful messages when an {@link LDAPException}
 * is thrown.
 */
@Author("terry.gardner@unboundID.com")
@Since("Jun 17, 2012")
@CodeVersion("1.0")
public class LdapException
        implements ExceptionMsg
{

  @Override
  public LDAPException getLdapException()
  {
    return ldapException;
  }



  @Override
  public String msg()
  {
    final String msg =
            String.format("An LDAP exception has occurred. The result code was %s",
                    ldapException.getResultCode());
    return msg;
  }



  /**
   * Creates an {@code LdapException} object from the provided
   * {@code ldapException} object - which is not permitted to be
   * {@code null}.
   */
  protected LdapException(
          final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapException);
    this.ldapException = ldapException;
  }



  /** The {@link LDAPException} that was thrown. */
  private final LDAPException ldapException;

}
