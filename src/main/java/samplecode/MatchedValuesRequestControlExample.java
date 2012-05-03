/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses>.
 */
package samplecode;


import com.unboundid.ldap.sdk.ExtendedResult;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.ldap.sdk.controls.MatchedValuesFilter;
import com.unboundid.ldap.sdk.controls.MatchedValuesRequestControl;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.FilterArgument;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * <p>
 * Provides an example of the use of the
 * {@code MatchedValuesRequestControl}. The matched values request
 * control is useful in cases where an application wants to the server
 * to return only certain values of a multi-valued attribute. The values
 * to return match the {@literal "matched values filter"}.
 * <p>
 * The user provides the options required to connect to the directory
 * server, a base object, a filter, a search scope, and a list of
 * requested attributes plus a matched values filter specified by the
 * {@code --matchedValuesFilter} command line argument. The
 * demonstration displays the entry returned as the result of a search
 * request with the matched values request control attached.
 * <p>
 * <b>Matched Values Request Control</b>
 * <p>
 * 
 * @see <a href="http://tools.ietf.org/html/rfc3876">RFC 3876</a>
 * @see MatchedValuesRequestControl
 * @see MatchedValuesFilter
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 7, 2011")
@CodeVersion("1.0")
public final class MatchedValuesRequestControlExample
        extends LDAPCommandLineTool
{

  /**
   * The long identifier of the command line argument whose parameter is
   * the matched values filter, that is, the filter that is used to
   * match the values of an attribute to return.
   */
  public static final String ARG_NAME_MATCHED_VALUES_FILTER = "matchedValuesFilter";



  /**
   * The description of the tool; this description is used in displaying
   * command line help options and for other purposes.
   */
  public static final String THE_TOOL_DESCRIPTION;



  /**
   * The name of the tool; this name is used in displaying command line
   * help options and for other purposes.
   */
  public static final String THE_TOOL_NAME;



  /**
   * Launch the AuthDemo application. Takes the following command line
   * arguments in addition to the standard ones:<blockquote>
   * 
   * <pre>
   * Provides an example of the use of the MatchedValuesRequestControl. The matched
   * values request control is useful in cases where an application wants to the
   * server to return only certain values of a multi-valued attribute. The values to
   * return match the 'matched values filter'. The user provides the options
   * required to connect to the directory server, a base object, a filter, a search
   * scope, and a list of requested attributes plus a matched values filter
   * specified by the --matchedValuesFilter} command line argument. The
   * demonstration displays the entry returned as the result of a search request
   * with the matched values request control attached.
   * 
   * Usage:  MatchedValuesRequestControlExample {options}
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
   * --matchedValuesFilter {matched-values-filter}
   *     Specifies the simple filter item for use with the
   *     MatchedValuesRequestControl as defined in RFC 3876. It is similar to a
   *     search filter (see the Filter class), but may only contain a single element
   *     (i.e., no AND, OR, or NOT components are allowed), and extensible matching
   *     does not allow the use of the dnAttributes field
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *          command line arguments, less the JVM arguments.
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final Formatter loggingFormatter = new MinimalLogFormatter();
    final MatchedValuesRequestControlExample matchedValuesRequestControlExample =
            MatchedValuesRequestControlExample.newMatchedValuesRequestControlExample(outStream,
                    errStream,loggingFormatter);
    final ResultCode resultCode = matchedValuesRequestControlExample.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(matchedValuesRequestControlExample,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  private static MatchedValuesRequestControlExample newMatchedValuesRequestControlExample(
          final OutputStream outStream,final OutputStream errStream,
          final Formatter loggingFormatter)
  {
    Validator.ensureNotNull(outStream,errStream,loggingFormatter);
    return new MatchedValuesRequestControlExample(outStream,errStream,loggingFormatter);

  }


  static
  {
    THE_TOOL_NAME = "MatchedValuesRequestControlExample";
    THE_TOOL_DESCRIPTION =
            "Provides an example of the use of the MatchedValuesRequestControl. "
                    + "The matched values request control is useful in cases "
                    + "where an application wants to the server to return only "
                    + "certain values of a multi-valued attribute. The values to "
                    + "return match the 'matched values filter'. "
                    + "The user provides the options required to connect to the "
                    + "directory server, a base object, a filter, a search scope, "
                    + "and a list of requested attributes plus a matched values "
                    + "filter specified by the --matchedValuesFilter} command "
                    + "line argument. The demonstration displays the entry returned "
                    + "as the result of a search request with the matched values "
                    + "request control attached.";
  }



  /**
   * {@inheritDoc}
   * <p>
   * Constructs the standard command line options and adds the
   * {@code --matchedValuesFilter} command line option.
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);

    /*
     * The command line argument which specifies the filter to use to
     * match attribute values. This command line argument is required,
     * and can occur exactly once.
     */
    final Character shortIdentifier = null;
    final String longIdentifier =
            MatchedValuesRequestControlExample.ARG_NAME_MATCHED_VALUES_FILTER;
    final boolean isRequired = true;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{matched-values-filter}";
    final String description =
            "Specifies the simple filter item for use with the "
                    + "MatchedValuesRequestControl as defined in RFC 3876. "
                    + "It is similar to a search filter (see the Filter class),"
                    + " but may only contain a single element (i.e., no AND, OR, or "
                    + "NOT components are allowed), and extensible matching does "
                    + "not allow the use of the dnAttributes field";
    final FilterArgument filterArgument =
            new FilterArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(filterArgument);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {
    ResultCode resultCode = ResultCode.SUCCESS;
    LogRecord logRecord;
    try
    {
      final LDAPConnection ldapConnection = getConnection();
      logRecord = new LogRecord(Level.INFO,"Connected to LDAP server.");
      out(loggingFormatter.format(logRecord));

      /*
       * Determine whether the matched values request control is
       * supported by the server.
       */
      final SupportedFeature supportedFeature =
              SupportedFeature.newSupportedFeature(ldapConnection);
      final String controlOID = MatchedValuesRequestControl.MATCHED_VALUES_REQUEST_OID;
      supportedFeature.isControlSupported(controlOID);
      logRecord = new LogRecord(Level.INFO,"MatchedValuesRequestControl is supported.");
      out(loggingFormatter.format(logRecord));

      /*
       * Handles unsolicited notifications. An unsolicited notification
       * is an LDAPMessage sent from the server to the client that is
       * not in response to any LDAPMessage received by the server. It
       * is used to signal an extraordinary condition in the server or
       * in the LDAP session between the client and the server. The
       * notification is of an advisory nature, and the server will not
       * expect any response to be returned from the client.
       */
      final UnsolicitedNotificationHandler unsolicitedNotificationHandler =
              new UnsolicitedNotificationHandler()
              {

                @Override
                public void handleUnsolicitedNotification(final LDAPConnection ldapConnection,
                        final ExtendedResult extendedResult)
                {
                  final String msg =
                          String.format("recd unsolicited notification: %s",
                                  extendedResult.getDiagnosticMessage());
                  final LogRecord logRecord = new LogRecord(Level.WARNING,msg);
                  out(loggingFormatter.format(logRecord));
                }

              };

      final LDAPConnectionOptions ldapConnectionOptions =
              commandLineOptions.newLDAPConnectionOptions();

      /*
       * Install the unsolicited notification handler.
       */
      ldapConnectionOptions.setUnsolicitedNotificationHandler(unsolicitedNotificationHandler);
      ldapConnection.setConnectionOptions(ldapConnectionOptions);

      /*
       * Construct the search request using parameters from the
       * --baseObject, --scope, --filter, and --attribute command line
       * arguments.
       */
      final String baseObject = commandLineOptions.getBaseObject();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final String[] requestedAttributes = commandLineOptions.getRequestedAttributes();
      final SearchRequest searchRequest =
              new SearchRequest(baseObject,scope,filter,requestedAttributes);

      final ArgumentParser argumentParser = commandLineOptions.getArgumentParser();
      final FilterArgument filterArgument =
              (FilterArgument)argumentParser
                      .getNamedArgument(MatchedValuesRequestControlExample.ARG_NAME_MATCHED_VALUES_FILTER);
      final Filter filt = filterArgument.getValue();
      final MatchedValuesFilter matchedValuesFilter = MatchedValuesFilter.create(filt);
      final MatchedValuesRequestControl control =
              new MatchedValuesRequestControl(matchedValuesFilter);
      searchRequest.addControl(control);

      final SearchResult searchResult = ldapConnection.search(searchRequest);
      if((searchResult != null) && (searchResult.getEntryCount() > 0))
      {
        for(final SearchResultEntry entry : searchResult.getSearchEntries())
        {
          logRecord = new LogRecord(Level.INFO,entry.toString());
          final String msg = loggingFormatter.format(logRecord);
          out(msg);
        }
      }

    }
    catch(final LDAPException ldapException)
    {
      resultCode = ldapException.getResultCode();
    }
    catch(final SupportedFeatureException e)
    {
      logRecord = new LogRecord(Level.INFO,"MatchedValuesRequestControl not supported.");
      err(loggingFormatter.format(logRecord));
      resultCode = ResultCode.UNWILLING_TO_PERFORM;
    }
    return resultCode;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return MatchedValuesRequestControlExample.THE_TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return MatchedValuesRequestControlExample.THE_TOOL_NAME;
  }



  /**
   * Prepares {@code MatchedValuesRequestControlExample} for use by a
   * client - the {@code System.out} and
   * {@code System.err OutputStreams} are used.
   */
  public MatchedValuesRequestControlExample()
  {
    this(System.out,System.err,new MinimalLogFormatter());
  }



  private MatchedValuesRequestControlExample(
          final OutputStream outStream,final OutputStream errStream,
          final Formatter loggingFormatter)
  {
    super(outStream,errStream);
    this.loggingFormatter = loggingFormatter;
  }



  private CommandLineOptions commandLineOptions;



  private final Formatter loggingFormatter;
}
