package samplecode.basics;


import com.unboundid.ldap.sdk.LDAPConnection;
import samplecode.annotation.CodeVersion;


/**
 *
 * This class provides supporting methods and data for use with the "UnboundID LDAP SDK - The
 * Basics" series of web articles.
 *
 * @author Terry J. Gardner
 */
@CodeVersion("1.0")
public class Connect
{
  private LDAPConnection ldapConnection;




  /**
   * No network connection is made.
   */
  public LDAPConnection connect()
  {
    return new LDAPConnection();
  }
}
