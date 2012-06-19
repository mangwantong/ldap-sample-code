package samplecode.exception;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.Validator;


import java.util.Locale;
import java.util.ResourceBundle;


import samplecode.StaticData;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/** Provides support for creating exception message generators */
@Author("terry.gardner@unboundID.com")
@Since("Jun 17, 2012")
@CodeVersion("1.0")
public final class ExceptionMsgFactory
{

  /** The locale used when no locale is otherwise specified */
  public static final Locale DEFAULT_LOCALE = StaticData.getDefaultLocale();



  /** The base name of the exceptions resource bundle */
  public static final String EXCEPTIONS_RESOURCE_BUNDLE_BASE_NAME = "exceptions";



  private static final ResourceBundle resourceBundle;



  /**
   * Create an exception message generator from the provided
   * {@code ldapException} - which is not permitted to be {@code null}.
   */
  public static LdapException getMessageGenerator(final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapException);
    LdapException msg;
    if(ldapException.getResultCode() == ResultCode.INVALID_CREDENTIALS)
    {
      msg = BindException.newBindException(ExceptionMsgFactory.resourceBundle,ldapException);
    }
    else if(ldapException.getResultCode() == ResultCode.CONNECT_ERROR)
    {
      // Client-side connection error
      msg =
              ClientSideConnectExceptionMessageGenerator
                      .newClientSideConnectExceptionMessageGenerator(
                              ExceptionMsgFactory.resourceBundle,ldapException);
    }
    else
    {
      msg = new LdapException(ExceptionMsgFactory.resourceBundle,ldapException);
    }
    return msg;
  }



  static
  {
    // exceptions_XX.properties must exist, otherwise a
    // MissingResourceException is thrown from here
    resourceBundle =
            ResourceBundle.getBundle(ExceptionMsgFactory.EXCEPTIONS_RESOURCE_BUNDLE_BASE_NAME,
                    ExceptionMsgFactory.DEFAULT_LOCALE);
  }
}
