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


import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.Modification;
import com.unboundid.ldap.sdk.ModificationType;
import com.unboundid.ldap.sdk.ModifyRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.controls.AssertionRequestControl;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides a demonstration of the assertion request control described
 * in RFC 4528. The demonstrator should provide an assertion, an
 * attribute name (--attribute), a filter (--filter) and a new value for
 * that attribute (--newAttributeValue).
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 4, 2011")
@CodeVersion("1.24")
public final class AssertionRequestControlDemo
        extends LDAPCommandLineTool
{

  /**
   * The long identifier of the command line argument whose parameter is
   * the new value of the attribute specified by the --attribute command
   * line argument.
   */
  public static final String ARG_NAME_NEW_ATTRIBUTE_VALUE;



  /**
   * The short identifier of the command line argument whose parameter
   * is the new value of the attribute specified by the --attribute
   * command line argument.
   */
  public static final Character SHORT_ID_NEW_ATTRIBUTE_VALUE;



  /**
   * The description of this tool. This description is used in help and
   * diagnostic output and for other purposes.
   */
  private static final String TOOL_DESCRIPTION;



  /**
   * The name of this tool. This name is used in help and diagnostic
   * output and for other purposes.
   */
  private static final String TOOL_NAME;



  /**
   * Launch the AssertionRequestControlDemo application. <blockquote>
   * <p/>
   * 
   * <pre>
   * 
   * Provides a demonstration of the use of the assertion request control.The
   * assertion request control allows an LDAP client to specify that a request be
   * executed if an assertion evaluates to true.The assertion request control is
   * described in RFC4528.
   * 
   * Usage:  AssertionRequestControlDemo {options}
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
   * -v, --newAttributeValue {attribute-value}
   *     The value to which the attribute specified by the --attribute  is set.
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * <p/>
   * </blockquote>
   * 
   * @param args
   *          command line arguments, less the JVM arguments.
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final MinimalLogFormatter loggingFormatter = new MinimalLogFormatter();
    final AssertionRequestControlDemo assertionRequestControlDemo =
            AssertionRequestControlDemo.newAssertionRequestControlDemo(outStream,errStream,
                    loggingFormatter);
    final ResultCode resultCode = assertionRequestControlDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(assertionRequestControlDemo,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  private static AssertionRequestControlDemo newAssertionRequestControlDemo(
          final OutputStream outStream,final OutputStream errStream,
          final Formatter loggingFormatter)
  {
    Validator.ensureNotNull(outStream,errStream,loggingFormatter);
    return new AssertionRequestControlDemo(outStream,errStream,loggingFormatter);
  }


  static
  {
    ARG_NAME_NEW_ATTRIBUTE_VALUE = "newAttributeValue";
    SHORT_ID_NEW_ATTRIBUTE_VALUE = Character.valueOf('v');

    TOOL_NAME = "AssertionRequestControlDemo";
    TOOL_DESCRIPTION =
            "Provides a demonstration of the use of the assertion request "
                    + "control. The assertion request control allows an LDAP client to "
                    + "specify that a request be executed if an assertion evaluates to true."
                    + "The assertion request control is described in RFC4528.";
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = new AssertionRequestControlDemoCommandLineOptions(argumentParser);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {
    ResultCode resultCode = null;
    try
    {

      /*
       * Get a connection to the directory server by utilizing the basic
       * command line arguments such as --hostname, --port, --bindDN,
       * --bindPassword, and so forth.
       */
      final LDAPConnection ldapConnection = getConnection();
      logRecord = new LogRecord(Level.INFO,"Connected to LDAP server.");
      out(loggingFormatter.format(logRecord));

      /*
       * Set LDAP connection options.
       */
      final LDAPConnectionOptions ldapConnectionOptions =
              commandLineOptions.newLDAPConnectionOptions();
      ldapConnection.setConnectionOptions(ldapConnectionOptions);

      /*
       * Check whether the assertion request control is supported by the
       * server to which this LDAP client is connected.
       */
      final SupportedFeature supportedFeature =
              SupportedFeature.newSupportedFeature(ldapConnection);
      final String controlOID = AssertionRequestControl.ASSERTION_REQUEST_OID;
      supportedFeature.isControlSupported(controlOID);
      msg = String.format("OID %s is supported by this server.",controlOID);
      logRecord = new LogRecord(Level.INFO,msg);
      out(loggingFormatter.format(logRecord));

      /*
       * Create the assertion request control using the filter specified
       * by the --filter command line argument.
       */
      final Filter filter = commandLineOptions.getFilter();
      final AssertionRequestControl assertionRequestControl =
              new AssertionRequestControl(filter.toString());

      /*
       * Attempt to modify the entry specified by the --bindDn command
       * line argument. The attribute specified by the --attribute
       * command line argument will be set to the value provided as an
       * parameter to the --newAttributeValue command line argument. The
       * modification will only succeed if the bindDN has permission to
       * modify the entry and the assertion specified in the --filter
       * parameter evaluates to true. The bind DN is required by this
       * demonstration program but not by the LDAPCommandLineTool,
       * therefore, check for it before using it.
       */
      final DN dn = commandLineOptions.getBindDn();
      if(dn == null)
      {
        msg =
                "A valid distinguished name must be supplied using the --bindDN "
                        + "command line argument. This demonstration cannot use the root DSE.";
        final LogRecord record = new LogRecord(Level.SEVERE,msg);
        out(loggingFormatter.format(record));
        return ResultCode.PARAM_ERROR;
      }
      final String bindDn = dn.toString();
      msg = String.format("Using bind DN '%s'",bindDn);
      logRecord = new LogRecord(Level.INFO,msg);
      out(loggingFormatter.format(logRecord));

      /*
       * Use only the first --attributes parameter
       */
      final String attributeName = commandLineOptions.getRequestedAttributes()[0];
      if(attributeName.equals(CommandLineOptions.DEFAULT_ATTRIBUTE_NAME))
      {
        final String msg =
                String.format("An attribute name must be specified with the --attribute argument.");
        logRecord = new LogRecord(Level.SEVERE,msg);
        err(loggingFormatter.format(logRecord));
        return ResultCode.PARAM_ERROR;
      }
      msg = String.format("Using attribute '%s'",attributeName);
      logRecord = new LogRecord(Level.INFO,msg);
      out(loggingFormatter.format(logRecord));

      /*
       * Retrieve the new value for the attribute from the parameter to
       * the --newAttributeValue command line argument.
       */
      final String newAttributeValue = commandLineOptions.getNewAttributeValue();

      /*
       * Construct the modification and transmit the modify request to
       * the server. Set a maximum response timeout (taken from the
       * command line argument).
       */
      final Modification modification =
              new Modification(ModificationType.ADD,attributeName,newAttributeValue);
      final ModifyRequest modifyRequest = new ModifyRequest(bindDn,modification);
      modifyRequest.addControl(assertionRequestControl);
      final int responseTimeout = commandLineOptions.getMaxResponseTimeMillis();
      modifyRequest.setResponseTimeoutMillis(responseTimeout);
      ldapConnection.modify(modifyRequest);

    }
    catch(final LDAPException ldapException)
    {
      final LogRecord logRecord =
              new LogRecord(Level.SEVERE,ldapException.getExceptionMessage());
      err(loggingFormatter.format(logRecord));
      resultCode = ldapException.getResultCode();
    }
    catch(final SupportedFeatureException e)
    {
      // The assertion request control is not supported.
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
    return AssertionRequestControlDemo.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return AssertionRequestControlDemo.TOOL_NAME;
  }



  /**
   * Prepares a new AssertionRequestControlDemo object using
   * {@code System.out} for the output stream, {@code System.err} as the
   * error stream, and a {@code MinimalLogFormatter}.
   */
  public AssertionRequestControlDemo()
  {
    this(System.out,System.err,new MinimalLogFormatter());
  }



  /**
   * Prepares a new AssertionRequestControlDemo object using the
   * provided {@code outStream} for the output stream, {@code errStream}
   * as the error stream, and a {@code loggingFormatter}.
   */
  private AssertionRequestControlDemo(
          final OutputStream outStream,final OutputStream errStream,
          final Formatter loggingFormatter)
  {
    super(outStream,errStream);
    this.loggingFormatter = loggingFormatter;
  }



  /**
   * Provides services for use with command line parameters and
   * arguments. Handles adding a fairly standard set of arguments to the
   * argument parser and retrieving their parameters.
   */
  private AssertionRequestControlDemoCommandLineOptions commandLineOptions;



  /**
   * Provides logging services.
   */
  private final Formatter loggingFormatter;



  /**
   * The object used to log messages.
   */
  private LogRecord logRecord;



  /**
   * String representation of messages.
   */
  private String msg;
}
