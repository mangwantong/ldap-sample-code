package samplecode.test;


import com.unboundid.ldap.sdk.*;
import org.junit.Test;
import samplecode.basics.*;

import static org.junit.Assert.assertTrue;


/**
 * Provides a collection of test cases to validate the functionality and performance of {@link
 * Connect}.
 *
 * @author Terry J. Gardner
 */
public final class ConnectTestCases
{

  @Test
  public void firstTestCase() throws LDAPException
  {
    LDAPConnection ldapConnection = getLDAPConnection();
    assertTrue(ldapConnection.isConnected());
    ldapConnection.close();
  }




  private LDAPConnection getLDAPConnection() throws LDAPException
  {
    return new LDAPConnectionHelper.Builder().hostname(getHostname()).port(getPort()).build()
                                             .getLDAPConnection();
  }




  private int getPort() { return 1489; }




  private String getHostname() { return "centos.example.com"; }
}
