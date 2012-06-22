package samplecode;


import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;


/**
 * Demonstrates the unsolicited notification. Use properties
 * {@code hostname, port, bindDn, and bindPassword} to specify the
 * corresponding values.
 * <p/>
 * Sample usage:
 * <blockquote>
 * <pre>
 * java -cp $SAMPLECODE_CLASSPATH -Dhostname=ldap.example.com \
 *                                -Dport=10389                \
 *                                -DbindDn=cn=RootDn          \
 *                                -DbindPassword=password     \
 *                                samplecode.UnsolicitedNotification
 * </pre>
 * </blockquote>
 * @see <a href="http://tools.ietf.org/html/rfc4511#section-4.4">unsolicited notification</a>
 */
@Launchable
@Since("21-Jun-2012")
@CodeVersion("1.1")
@Author("terry.gardner@unboundid.com")
public final class UnsolicitedNotification
{

  /**
   * The value to use for the bind DN when the {@code bindDn} property
   * is not populated
   */
  private static final String DEFAULT_BIND_DN = "cn=unsolicited notification test,dc=example,dc=com";



  /**
   * The value to use for the password when the {@code bindPassword}
   * property is not populated
   */
  private static final String DEFAULT_BIND_PASSWORD = "password";



  /**
   * The value to use for the hostname when the {@code hostname}
   * property is not populated
   */
  private static final String DEFAULT_HOSTNAME = "localhost";



  /**
   * The value to use for the port when the {@code port} property is not
   * populated
   */
  private static final int DEFAULT_PORT = 389;



  /** The property that specifies the distinguished name to use */
  private static final String PROP_NAME_DN = "bindDn";



  /** The property that specifies the hostname to be used. */
  private static final String PROP_NAME_HOSTNAME = "hostname";



  /**
   * The property that specifies the password for the distinguished name
   */
  private static final String PROP_NAME_PASSWORD = "bindPassword";



  /** The property that specifies the port to be used. */
  private static final String PROP_NAME_PORT = "port";



  /**
   * Demonstrate the unsolicited notification specified in RFC4511 by
   * establishing a connection to Directory Server and displaying a
   * message when the server transmits a notification.
   */
  public static void main(final String... args)
  {
    /*
     * The properties specified on the command can include:
     * bindDn: the distinguished name used in the bind request
     * bindPassword: the password in the entry of the distinguished name
     * hostname: the hostname where the server runss
     * port: the non secure port upon which the server listens
     */
    final String hostname = UnsolicitedNotification.getHostname();
    final int port = UnsolicitedNotification.getPort();
    final String bindDn = UnsolicitedNotification.getBindDn();
    final String bindPassword = UnsolicitedNotification.getBindPassword();

    try
    {
      /*
       * Create and assign a new unsolicited notification handler
       */
      final LDAPConnectionOptions options = new LDAPConnectionOptions();
      options.setUnsolicitedNotificationHandler(new UnsolicitedNotificationHandler()
      {

        /**
         * prints a message to the standard output when an unsolicited
         * notification has been received on the connection
         */
        @Override
        public void handleUnsolicitedNotification(final LDAPConnection connection,
                final ExtendedResult notification)
        {
          System.err.println(String.format(
                  "The Directory Server has transmitted an unsolicited notification.\n"
                          + " The notification was: %s",notification.toString()));
          System.exit(0);
        }
      });

      /*
       * Connect to the server and loop indefinitely. The 'options' assigns the unsolicited
       * notification handler.
       */
      @SuppressWarnings("unused")
      final LDAPConnection c = new LDAPConnection(options,hostname,port,bindDn,bindPassword);
      System.out.println(String.format("connected to the server at %s:%d",hostname,port));
      while(true)
      {
        Thread.sleep(1000);
      }
    }
    catch(final LDAPException ldapException)
    {
      ldapException.printStackTrace();
      return;
    }
    catch(final Exception exception)
    {
      exception.printStackTrace();
      return;
    }
  }



  /** @return the bind DN from the properties list */
  private static String getBindDn()
  {
    final String s = System.getProperty(UnsolicitedNotification.PROP_NAME_DN);
    return s == null ? UnsolicitedNotification.DEFAULT_BIND_DN : s;
  }



  /** @return the bind password from the properties list */
  private static String getBindPassword()
  {
    final String s = System.getProperty(UnsolicitedNotification.PROP_NAME_PASSWORD);
    return s == null ? UnsolicitedNotification.DEFAULT_BIND_PASSWORD : s;
  }



  /** @return the hostname from the properties list */
  private static String getHostname()
  {
    final String hostname = System.getProperty(UnsolicitedNotification.PROP_NAME_HOSTNAME);
    return hostname == null ? UnsolicitedNotification.DEFAULT_HOSTNAME : hostname;
  }



  /** @return the port from the properties list */
  private static int getPort()
  {
    final String portProperty = System.getProperty(UnsolicitedNotification.PROP_NAME_PORT);
    return portProperty == null ? UnsolicitedNotification.DEFAULT_PORT : Integer
            .parseInt(portProperty);
  }
}
