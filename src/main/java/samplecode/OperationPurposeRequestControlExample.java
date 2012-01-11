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


import java.io.OutputStream;


import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


/**
 * An launchable class that provides a demonstration of the Operation
 * Purpose Request Control from the UnboundID LDAP SDK (commercial
 * edition).
 * <p>
 * The class demonstrates creating a connection pool, determining
 * whether the Operation Purpose request control is supported, and if it
 * is supported, attached to a search request.
 */
@Author("terry.gardner@unboundID.com")
@Since("Oct 30, 2011")
@CodeVersion("1.1")
@Launchable
public final class OperationPurposeRequestControlExample
  extends LDAPCommandLineTool {


  private class OperationPurposeRequestControlDemo {


    private OperationPurposeRequestControlDemo(
        final LDAPCommandLineTool commandLineTool,
        final CommandLineOptions commandLineOptions) {
      if(commandLineTool == null) {
        throw new NullPointerException("null command line tool not allowed.");
      }
      if(commandLineOptions == null) {
        throw new NullPointerException(
            "null command line options processor not allowed.");
      }
      this.commandLineTool = commandLineTool;
      this.commandLineOptions = commandLineOptions;
    }


    private void execute() {


      /*
       * Get a connection pool
       */
      final int initialConnections = commandLineOptions.getInitialConnections();
      final int maxConnections = commandLineOptions.getMaxConnections();
      LDAPConnectionPool ldapConnectionPool;
      try {
        ldapConnectionPool =
            commandLineTool
                .getConnectionPool(initialConnections,maxConnections);
      } catch (final LDAPException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
        return;
      }


      /*
       * Validate that the directory server supports the
       * OperationPurposeRequestControl.
       */
      try {
        final SupportedFeature supportedControl =
            SupportedFeature.newSupportedFeature(ldapConnectionPool);
        final String oid =
            OperationPurposeRequestControl.OPERATION_PURPOSE_REQUEST_OID;
        operationPurposeRequestControlSupported = true;
        supportedControl.isControlSupported(oid);
      } catch (final SupportedFeatureException supportedControlException) {
        operationPurposeRequestControlSupported = false;
      } catch (final LDAPException ldapException) {
        return;
      }


      /*
       * Display the naming context entries
       */
      String[] namingContexts;
      try {
        namingContexts = ldapConnectionPool.getRootDSE().getNamingContextDNs();
      } catch (final LDAPException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
        return;
      }
      if(namingContexts != null) {
        for(final String namingContext : namingContexts) {
          final String baseObject = namingContext;
          final SearchScope scope = SearchScope.BASE;
          final String filter = "(&)";
          final String[] requestedAttributes =
              commandLineOptions.getRequestedAttributes();
          SearchRequest searchRequest;
          try {
            searchRequest =
                new SearchRequest(baseObject,scope,filter,requestedAttributes);
          } catch (final LDAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
          }


          /*
           * Set size limit and time limit
           */
          final int sizeLimit = commandLineOptions.getSizeLimit();
          searchRequest.setSizeLimit(sizeLimit);
          final int timeLimit = commandLineOptions.getTimeLimit();
          searchRequest.setTimeLimitSeconds(timeLimit);


          /*
           * Add the operation purpose request control, if it is
           * supported by this server.
           */
          if(operationPurposeRequestControlSupported) {
            final String applicationName = commandLineTool.getToolName();
            final String applicationVersion = "1.0";
            final int codeLocationFrames = 0;
            final String requestPurpose = commandLineTool.getToolDescription();
            final OperationPurposeRequestControl operationPurposeRequestControl =
                new OperationPurposeRequestControl(applicationName,
                    applicationVersion,codeLocationFrames,requestPurpose);
            searchRequest.addControl(operationPurposeRequestControl);
          }


          /*
           * Transmit search request to the directory server.
           */
          SearchResult searchResult;
          try {
            searchResult = ldapConnectionPool.search(searchRequest);
          } catch (final LDAPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return;
          }


          /*
           * Display the results.
           */
          if(searchResult != null &&
              searchResult.getResultCode().equals(ResultCode.SUCCESS) &&
              searchResult.getEntryCount() > 0) {
            for(final SearchResultEntry entry : searchResult.getSearchEntries()) {
              final LdapEntryDisplay ldapEntryDisplay =
                  new BasicLdapEntryDisplay(entry);
              ldapEntryDisplay.display();
            }
          }


        }
      }
      ldapConnectionPool.close();
    }


    /**
     * Manages common command line options.
     */
    private final CommandLineOptions commandLineOptions;


    /*
     * The command line tool object.
     */
    private final LDAPCommandLineTool commandLineTool;


    /*
     * Whether the operation purpose request control is supported.
     */
    private boolean operationPurposeRequestControlSupported;
  }


  /**
   * Entry point. See {@link CommandLineOptions} class for options
   * supported, or use the {@code --help} command line option. The
   * output of the {@code --help} command line parameter is shown below
   * for convenience:<blockquote>
   * 
   * <pre>
   * Demonstrates the use of the operation purpose request control
   * 
   * Usage:  OperationPurposerequestControlExample {options}
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
   * -a, --attribute {attribute name or type}
   *     The attribute used in the search request. This command line argument is not
   *     required, and can be specified multiple times. If this command line
   *     argument is not specified, the value '*' is used.
   * -f, --filter {filter}
   *     The search filter used in the search request.
   * -i, --initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server when
   *     creating the connection pool.
   * -m, --maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server when
   *     creating the connection pool.
   * -s, --scope {searchScope}
   *     The scope of the search request
   * --sizeLimit {positiveInteger}
   *     The search size limit
   * --timeLimit {positiveInteger}
   *     The search time limit
   * --pageSize {positiveInteger}
   *     The search page size
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *          Command line arguments, less the JVM specific arguments.
   */
  public static void main(final String... args) {


    /*
     * Construct the demo object.
     */
    final OutputStream outStream = System.out;
    final OutputStream errStream = System.err;
    final OperationPurposeRequestControlExample demo =
        OperationPurposeRequestControlExample
            .newOperationPurposeRequestControlExample(outStream,errStream);
    demo.runTool(args);


  }


  private static OperationPurposeRequestControlExample
      newOperationPurposeRequestControlExample(final OutputStream outStream,
          final OutputStream errStream) {
    if(outStream == null) {
      throw new NullPointerException("null output stream is not allowed.");
    }
    if(errStream == null) {
      throw new NullPointerException("null error stream is not allowed.");
    }
    return new OperationPurposeRequestControlExample(outStream,errStream);
  }


  /**
   * Prepares {@code OperationPurposeRequestControlExample} for use by a
   * client - the {@code System.out} and
   * {@code System.err OutputStreams} are used.
   */
  public OperationPurposeRequestControlExample() {
    this(System.out,System.err);
  }


  private OperationPurposeRequestControlExample(
      final OutputStream outStream,final OutputStream errStream) {
    super(outStream,errStream);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser)
      throws ArgumentException {
    if(argumentParser == null) {
      throw new NullPointerException("null argument parser is not allowed.");
    }
    commandLineOptions =
        CommandLineOptions.newCommandLineOptions(argumentParser);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing() {


    final OperationPurposeRequestControlDemo demo =
        new OperationPurposeRequestControlDemo(this,commandLineOptions);
    demo.execute();


    return null;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription() {
    return "Demonstrates the use of the operation purpose request control";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName() {
    return "OperationPurposerequestControlExample";
  }


  /**
   * Manages common command line options.
   */
  private CommandLineOptions commandLineOptions;


}
