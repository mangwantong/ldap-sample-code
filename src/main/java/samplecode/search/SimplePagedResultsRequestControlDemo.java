/*
 * This file contains a demonstration of the simple paged results
 * control extension as described in RFC2696. To compile and run, the
 * UnboundID LDAP SDK is required.
 */

package samplecode.search;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.SimplePagedResultsControl;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import samplecode.cli.CommandLineOptions;
import samplecode.ldap.SupportedFeature;
import samplecode.ldap.SupportedFeatureException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.ldap.DefaultUnsolicitedNotificationHandler;
import samplecode.listener.ExceptionListener;
import samplecode.tools.AbstractTool;

import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Demonstrates the use of the simple paged size request control.
 * <p/>
 * Example usage <blockquote>
 * <p/>
 * <pre>
 * java samplecode.SimplePagedResultsRequestControlDemo \
 *   --baseObject ou=people,dc=example,dc=com \
 *   --bindDn uid=user.0,ou=people,dc=example,dc=com \
 *   --bindPasswordFile path-to-file-containing-the-credentials \
 *   --filter objectClass=shadowAccount \
 *   --hostname localhost \
 *   --pageSize 311 --port 1389 --scope SUB --sizeLimit 12345 --timeLimit 60
 *
 * The class should print something similar to the following.
 * A page size not a factor of 2000 was selected (there are 2000
 * entries that match the specified search parameters) in order
 * to show the progress of the search requests and search results:
 *
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 311
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 311
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 311
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 311
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 311
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 311
 * [20/Dec/2011:07:09:59 -0500] searchRequest transmitted, pageSize: 311, entries returned: 134
 * [20/Dec/2011:07:09:59 -0500] total entries returned: 2000
 * [20/Dec/2011:07:09:59 -0500] SimplePagedResultsRequestControlDemo has completed processing
 * . The result code was: 0 (success)
 *
 * </pre>
 * <p/>
 * </blockquote>
 *
 * @see <a href="http://su.pr/1wlWcQ">Simple Paged Results</a>
 * @see SimplePagedResultsControl
 */
@Author("terry.gardner@unboundid.com") @Since("Nov 23, 2011") @CodeVersion("1.6") @Launchable
public final class SimplePagedResultsRequestControlDemo extends AbstractTool
{

  /**
   * Demonstrates the use of the simple paged control extension.
   */
  private static class SimplePagedResultsDemo
  {

    private SimplePagedResultsDemo(final LDAPCommandLineTool ldapCommandLineTool,
            final CommandLineOptions commandLineOptions)
    {
      Validator.ensureNotNull(ldapCommandLineTool, commandLineOptions);
      this.ldapCommandLineTool = ldapCommandLineTool;
      this.commandLineOptions = commandLineOptions;
    }

    private ResultCode demo(
            final ExceptionListener<SupportedFeatureException>
                    supportedFeatureExceptionListener)
            throws LDAPException
    {
      /*
       * Get connection to the server. When the connection is
       * established, set the connection options using the values from
       * the standard command line arguments.
       */
      final LDAPConnection ldapConnection = ldapCommandLineTool.getConnection();
      final LDAPConnectionOptions connectionOptions =
              commandLineOptions.newLDAPConnectionOptions();
      ldapConnection.setConnectionOptions(connectionOptions);

      /*
       * Check that the simple paged results control is supported by
       * server to which the client is connected.
       */
      final SupportedFeature supportedFeature =
              SupportedFeature.newSupportedFeature(ldapConnection);
      final String controlOID = SimplePagedResultsControl.PAGED_RESULTS_OID;
      try
      {
        supportedFeature.isControlSupported(controlOID);
      }
      catch(final SupportedFeatureException exception)
      {
        if(supportedFeatureExceptionListener.invoke())
        {
          supportedFeatureExceptionListener.processException(exception);
        }
        return ResultCode.OPERATIONS_ERROR;
      }

      /*
       * Create a search request, set a size limit and a time limit, and
       * add a simple paged results request control to it:
       */
      final String baseObject = commandLineOptions.getBaseObject();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final String[] requestedAttributes =
              commandLineOptions.getRequestedAttributes().toArray(new String[0]);
      SearchRequest searchRequest;
      searchRequest = new SearchRequest(baseObject, scope, filter, requestedAttributes);
      final int sizeLimit = commandLineOptions.getSizeLimit();
      final int timeLimit = commandLineOptions.getTimeLimit();
      searchRequest.setSizeLimit(sizeLimit);
      searchRequest.setTimeLimitSeconds(timeLimit);

      /*
       * Add the simple paged results request control
       */
      final int pageSize = commandLineOptions.getPageSize();
      ASN1OctetString cookie = null;
      int total = 0;

      do
      {
        /*
         * Set the simple paged results control (if the cookie is null
         * this indicates the first time through the loop).
         */
        final SimplePagedResultsControl simplePagedResultsRequestControl =
                new SimplePagedResultsControl(pageSize, cookie);
        searchRequest.setControls(simplePagedResultsRequestControl);

        /*
         * Issue the search request:
         */
        SearchResult searchResult;
        searchResult = ldapConnection.search(searchRequest);
        final String msg =
                String.format("searchRequest transmitted, pageSize: %d, " +
                        "entries returned: %d", Integer.valueOf(pageSize),
                        Integer.valueOf(searchResult.getEntryCount()));
        logger.log(Level.INFO, msg);

        total += searchResult.getEntryCount();

        // Get the cookie from the paged results control.
        cookie = null;
        final SimplePagedResultsControl c = SimplePagedResultsControl.get(searchResult);
        if(c != null)
        {
          cookie = c.getCookie();
        }
      } while((cookie != null) && (cookie.getValueLength() > 0));

      ldapConnection.close();

      final String msg = String.format("total entries returned: %d", Integer.valueOf(total));
      logger.log(Level.INFO, msg);

      return ResultCode.SUCCESS;
    }

    /**
     * Provides services related to command line options.
     */
    private final CommandLineOptions commandLineOptions;

    /**
     * The command line tool.
     */
    private final LDAPCommandLineTool ldapCommandLineTool;

    private final Logger logger = Logger.getLogger(getClass().getName());

  }

  /**
   * Prepares {@code SimplePagedResultsRequestControlDemo} for use by a
   * client using the specified {@code OutStream} and {@code errStream}.
   */
  private SimplePagedResultsRequestControlDemo(final OutputStream outStream,
          final OutputStream errStream)
  {
    super(outStream, errStream);
  }

  /**
   * Prepares {@code SimplePagedResultsRequestControlDemo} for use by a
   * client - the {@code System.out} and
   * {@code System.err OutputStreams} are used.
   */
  public SimplePagedResultsRequestControlDemo()
  {
    this(System.out, System.err);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "SimplePagedResultsrequestControlDemo.properties";
  }

  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new DefaultUnsolicitedNotificationHandler(this);
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();

    if(isVerbose())
    {
      displayArguments();
    }

    ResultCode resultCode;

    final ExceptionListener<SupportedFeatureException> supportedFeatureExceptionListener =
            new ExceptionListener<SupportedFeatureException>()
            {

              /*
               * {@inheritDoc} <p> Always returns an indication that
               * {@code processException} is safe to invoke.
               */
              @Override
              public boolean invoke()
              {
                return true;
              }

              /*
               * {@inheritDoc}
               */
              @Override
              public void processException(final SupportedFeatureException exception)
              {
                final String msg =
                        String.format("%s is not supported by this server.",
                                SimplePagedResultsControl.PAGED_RESULTS_OID);
                getLogger().fatal(msg);
              }

            };

    final SimplePagedResultsDemo simplePagedResultsDemo =
            new SimplePagedResultsDemo(this, commandLineOptions);
    try
    {
      resultCode = simplePagedResultsDemo.demo(supportedFeatureExceptionListener);
    }
    catch(final LDAPException ldapException)
    {
      final String msg =
              String.format("LDAP Exception: %s", ldapException.getExceptionMessage());
      getLogger().fatal(msg);
      return ResultCode.OPERATIONS_ERROR;
    }

    return resultCode;
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return "Demonstrates the simple paged results request control.";
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
  }

  /**
   * <blockquote>
   * <p/>
   * <pre>
   * Demonstrates the simple paged results request control.
   *
   * Usage:  SimplePagedResultsRequestControlDemo {options}
   *
   * Available options include:
   * -h, --hostname {host}
   *     The IP address or resolvable name to use to connect to the directory
   *     server.  If this is not provided, then a default value of 'localhost' will
   *     be used.
   * -p, --port {port}
   *     The port to use to connect to the directory server.  If this is not
   *     provided, then a default value of 389 will be used.
   * -D, --bindDN {dn}
   *     The DN to use to bind to the directory server when performing simple
   *     authentication.
   * -w, --bindPassword {password}
   *     The password to use to bind to the directory server when performing simple
   *     authentication or a password-based SASL mechanism.
   * -j, --bindPasswordFile {path}
   *     The path to the file containing the password to use to bind to the
   *     directory server when performing simple authentication or a password-based
   *     SASL mechanism.
   * -Z, --useSSL
   *     Use SSL when communicating with the directory server.
   * -q, --useStartTLS
   *     Use StartTLS when communicating with the directory server.
   * -X, --trustAll
   *     Trust any certificate presented by the directory server.
   * -K, --keyStorePath {path}
   *     The path to the file to use as the key store for obtaining client
   *     certificates when communicating securely with the directory server.
   * -W, --keyStorePassword {password}
   *     The password to use to access the key store contents.
   * -u, --keyStorePasswordFile {path}
   *     The path to the file containing the password to use to access the key store
   *     contents.
   * --keyStoreFormat {format}
   *     The format (e.g., jks, jceks, pkcs12, etc.) for the key store file.
   * -P, --trustStorePath {path}
   *     The path to the file to use as trust store when determining whether to
   *     trust a certificate presented by the directory server.
   * -T, --trustStorePassword {password}
   *     The password to use to access the trust store contents.
   * -U, --trustStorePasswordFile {path}
   *     The path to the file containing the password to use to access the trust
   *     store contents.
   * --trustStoreFormat {format}
   *     The format (e.g., jks, jceks, pkcs12, etc.) for the trust store file.
   * -N, --certNickname {nickname}
   *     The nickname (alias) of the client certificate in the key store to present
   *     to the directory server for SSL client authentication.
   * -o, --saslOption {name=value}
   *     A name-value pair providing information to use when performing SASL
   *     authentication.
   * --numThreads {number-of-threads}
   *     Specifies the number of threads to use when running the application.
   * --useSchema
   *     Whether the LDAP SDK should attempt to use server schema information, for
   *     example, for matching rules.
   * --verbose
   *     Whether the tool should be verbose.
   * --abandonOnTimeout
   *     Whether the LDAP SDK should abandon an operation that has timed out.
   * --autoReconnect
   *     Whether the LDAP SDK should automatically reconnect when a connection is
   *     lost.
   * --connectTimeoutMillis {connect-timeout-millis-integer}
   *     Specifies the maximum length of time in milliseconds that a connection
   *     attempt should be allowed to continue before giving up. A value of zero
   *     indicates that there should be no connect timeout.
   * -b, --baseObject {distinguishedName}
   *     The base object used in the search request.
   * --maxResponseTimeMillis {max-response-time-in-milliseconds}
   *     The maximum length of time in milliseconds that an operation should be
   *     allowed to block, with 0 or less meaning no timeout is enforced. This
   *     command line argument is optional and has a default value of zero.
   * --reportInterval {positive-integer}
   *     The report interval in milliseconds.
   * --reportCount {positive-integer}
   *     Specifies the maximum number of reports. This command line argument is
   *     applicable to tools that display repeated reports. The time between
   *     repeated reports is specified by the --reportInterval command line
   *     argument.
   * -a, --attribute {attribute name or type}
   *     The attribute used in the search request or other request. This command
   *     line argument is not required, and can be specified multiple times. If this
   *     command line argument is not specified, the value '*' is used.
   * -f, --filter {filter}
   *     The search filter used in the search request.
   * -i, --initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server when
   *     creating the connection pool.
   * -m, --maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server when
   *     creating the connection pool.
   * -s, --scope {searchScope}
   *     The scope of the search request; allowed values are BASE, ONE, and SUB
   * --sizeLimit {positiveInteger}
   *     The client-request maximum number of results which are returned to the
   *     client. If the number of entries which match the search parameter is
   *     greater than the client-requested size limit or the server-imposed size
   *     limit a SIZE_LIMIT_EXCEEDED code is returned in the result code in the
   *     search response.
   * --timeLimit {positiveInteger}
   *     The client-request maximum time to search used by the server. If the time
   *     of the search is greater than the client-requested time limit or the
   *     server-imposed time limit a TIME_LIMIT_EXCEEDED code is returned in the
   *     result code in the search response.
   * --pageSize {positiveInteger}
   *     The search page size
   * -H, -?, --help
   *     Display usage information for this program.
   *
   * [20/Dec/2011:05:22:11 -0500] SimplePagedResultsRequestControlDemo has completed
   * processing. The result code was: 0 (success)
   *
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param args
   */
  public static void main(final String... args)
  {
    final SimplePagedResultsRequestControlDemo demo =
            SimplePagedResultsRequestControlDemo.getSimplePagedResultsRequestControlDemo();
    final ResultCode resultCode = demo.runTool(args);
    if(resultCode != null)
    {
      final StringBuilder builder = new StringBuilder(demo.getToolName());
      builder.append(" has completed processing. The result code was: ");
      builder.append(resultCode);
      Logger.getAnonymousLogger().log(Level.INFO, builder.toString());
    }
  }

  private static SimplePagedResultsRequestControlDemo getSimplePagedResultsRequestControlDemo()
  {
    return new SimplePagedResultsRequestControlDemo(System.out, System.err);
  }

  /*
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return "SimplePagedResultsRequestControlDemo";
  }

}
