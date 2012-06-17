package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;


/** Provides support for creating exception message generators */
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
