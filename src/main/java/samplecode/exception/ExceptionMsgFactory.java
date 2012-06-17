package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/** Provides support for creating exception message generators */
@Author("terry.gardner@unboundID.com")
@Since("Jun 17, 2012")
@CodeVersion("1.0")
public final class ExceptionMsgFactory
{

  /**
   * Create an exception message generator from the provided
   * {@code ldapException} - which is not permitted to be {@code null}.
   */
  public static LdapException getLdapExceptionMsg(final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapException);
    LdapException msg;
    if(ldapException.getResultCode() == ResultCode.INVALID_CREDENTIALS)
    {
      msg = BindException.newBindException(ldapException);
    }
    else
    {
      msg = new LdapException(ldapException);
    }
    return msg;
  }
}
