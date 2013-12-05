package samplecode.basics;


import com.unboundid.ldap.sdk.*;


/**
 * Provides a <em>Builder</em> interface that can be used to create {@code LDAPConnection} objects.
 *
 * @author Terry J. Gardner
 */
public class LDAPConnectionHelper
{

  /**
   * Provide methods and data used to collect the parameters necessary to create a new {link
   * LDAPConnection} object.
   * <p/>
   * <b>Default values</b> <ul> <li><b>hostname:</b> "localhost</li> <li><b>port:</b>
   * 1489</li></ul>
   */
  public static class Builder
  {
    /**
     * The hostname that will be used when a hostname is not otherwise specified.
     */
    private static final String DEFAULT_HOSTNAME = "localhost";


    /**
     * The post that will be used when a port is not otherwise specified.
     */
    private static int DEFAULT_PORT = 389;


    private LDAPConnectionOptions options = new LDAPConnectionOptions();


    /**
     * The hostname that will be used when the {@code LDAPConnection} object is created at the end
     * of the build process.  See the class javadoc for the hostname that will be used when it is
     * not specified when the builder is created.
     */
    private String hostname = DEFAULT_HOSTNAME;


    /**
     * The port that will be used when the {@code LDAPConnection} object is created at the end of
     * the build process. See the class javadoc for the port number that will be used when it is
     * not
     * specified when the builder is created.
     */
    private int port = DEFAULT_PORT;




    public Builder ldapConnectionOptions(LDAPConnectionOptions options)
    {
      this.options = options;
      return this;
    }




    /**
     * Sets the {@code hostname} that will be used when the {@code LDAPConnection} is created.
     *
     * @param hostname
     *   The hostname or IP address of the server to which an LDAP session should be established.
     *
     * @return this object after the {@code hostname} is set.
     */
    public Builder hostname(String hostname)
    {
      this.hostname = hostname;
      return this;
    }




    /**
     * Sets the {@code port} that will be used when the {@code LDAPConnection} is created.
     *
     * @param port
     *   The port on which the server at {@code hostname} is listening for client connections.
     *
     * @return this object after the {@code port} is set.
     */
    public Builder port(int port)
    {
      this.port = port;
      return this;
    }




    /**
     * Gets the {@code LDAPConnectionHelper} object with the parameters specified during the build
     * process.
     */
    public LDAPConnectionHelper build()
    {
      return new LDAPConnectionHelper(this);
    }

  }




  private final String hostname;


  private int port;




  private LDAPConnectionHelper(Builder builder)
  {
    this.hostname = builder.hostname;
    this.port = builder.port;
  }




  public LDAPConnection getLDAPConnection() throws LDAPException
  {
    return new LDAPConnection(hostname,port);
  }
}
