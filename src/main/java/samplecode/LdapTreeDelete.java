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


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.StringArgument;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ProgressEvent;
import samplecode.listener.ProgressListener;


/**
 * Provides a demonstration of how to use the LDAP tree delete control
 * extension. This code requires the UnboundID LDAP SDK commercial
 * edition. To compile and run with the standard edition, remove
 * references to the OperationPurposeRequestControl. The following
 * command line arguments are required and must be specified exactly
 * once: --deleteBranch, --ldifFile. <blockquote>
 * 
 * <pre>
 * The tree delete tool provides a demonstration of how to use the Tree Delete
 * request control to delete a branch and all entries subordinate to the branch.
 * 
 * Usage:  LdapTreeDelete {options}
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
 *     The search size limit
 * --timeLimit {positiveInteger}
 *     The search time limit
 * --pageSize {positiveInteger}
 *     The search page size
 * -l, --ldifFile {filename}
 *     Operators should use this command line argument to specify a file
 *     containing valid LDIF that is processed before the DN specified by the
 *     --deleteBranch and all of its subordinates is deleted. The intent is to
 *     allow operators to add some entries and then delete them. This command line
 *     argument is required and must be specified exactly once.
 * -d, --deleteBranch {distinguishedName}
 *     Operators should use this command line argument to specify the branch to
 *     delete. This command line argument is required and must be specified
 *     exactly once.
 * -H, -?, --help
 *     Display usage information for this program.
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 30, 2011")
@CodeVersion("1.6")
public final class LdapTreeDelete
        extends LDAPCommandLineTool
        implements LdapExceptionListener
{

  /**
   * The long identifier of the command line argument whose parameter
   * specifies the DN to be deleted with all of its subordinates.
   */
  public static final String ARG_NAME_DELETE_BRANCH = "deleteBranch";



  /**
   * The long identifier of the command line argument that specifies the
   * name of a file containing valid LDIF which is processed before the
   * branch specified by the --deleteBranch is deleted.
   */
  public static final String ARG_NAME_LDIF_FILE = "ldifFile";



  /**
   * The short identifier of the command line argument whose parameter
   * specifies the DN to be deleted with all of its subordinates.
   */
  public static final Character SHORT_NAME_DELETE_BRANCH = Character.valueOf('d');



  /**
   * The short identifier of the command line argument that specifies
   * the name of a file containing valid LDIF which is processed before
   * the branch specified by the --deleteBranch is deleted.
   */
  public static final Character SHORT_NAME_LDIF_FILE = Character.valueOf('l');



  /**
   * The description of this tool; used in help and diagnostic output,
   * and for other purposes.
   */
  public static final String TOOL_DESCRIPTION =
          "The tree delete tool provides a demonstration "
                  + "of how to use the Tree Delete request control "
                  + "to delete a branch and all entries subordinate to the branch.";



  /**
   * The name of this tool; used in help and diagnostic output, and for
   * other purposes.
   */
  public static final String TOOL_NAME = "LdapTreeDelete";



  /**
   * <pre>
   * The tree delete tool provides a demonstration of how to use the Tree Delete
   * request control to delete a branch and all entries subordinate to the branch.
   * 
   * Usage:  LdapTreeDelete {options}
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
   *     The scope of the search request; allowed values are BASE, ONE, and SUB
   * --sizeLimit {positiveInteger}
   *     The search size limit
   * --timeLimit {positiveInteger}
   *     The search time limit
   * --pageSize {positiveInteger}
   *     The search page size
   * -l, --ldifFile {filename}
   *     Operators should use this command line argument to specify a file
   *     containing valid LDIF that is processed before the DN specified by the
   *     --deleteBranch and all of its subordinates is deleted. The intent is to
   *     allow operators to add some entries and then delete them. This command line
   *     argument is required and must be specified exactly once.
   * -d, --deleteBranch {distinguishedName}
   *     Operators should use this command line argument to specify the branch to
   *     delete. This command line argument is required and must be specified
   *     exactly once.
   * -H, -?, --help
   *     Display usage information for this program.
   * 
   * LdapTreeDelete has completed execution with the result code 0 (success)
   * </pre>
   * 
   * @param args
   *          The command line arguments and parameters, less the JVM
   *          arguments.
   * @throws IOException
   *           When an IO error with a file occurs.
   */
  public static void main(final String... args) throws IOException
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final LdapTreeDelete ldapTreeDelete = new LdapTreeDelete(outStream,errStream);
    final ResultCode resultCode = ldapTreeDelete.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(ldapTreeDelete,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);

    Character shortIdentifier = LdapTreeDelete.SHORT_NAME_LDIF_FILE;
    String longIdentifier = LdapTreeDelete.ARG_NAME_LDIF_FILE;
    final boolean isRequired = true;
    final int maxOccurrences = 1;
    String valuePlaceholder = "{resource on classpath}";
    String description =
            "Demonstrators must use this command line argument "
                    + "to specify a file containing valid LDIF that is "
                    + "processed before the DN specified by the --deleteBranch "
                    + "and all of its subordinates is deleted. The intent "
                    + "is to allow observers of the demonstration to add some entries and then "
                    + "delete them. This command line argument is required, the filename "
                    + "specified must be found on the classpath, and "
                    + "must be specified exactly once.";
    final StringArgument stringArgument =
            new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(stringArgument);

    /*
     * Add the command line argument to the argument parser whose
     * parameter is the distinguished name of the branch that will be
     * deleted along with all of its subordinates.
     */
    shortIdentifier = LdapTreeDelete.SHORT_NAME_DELETE_BRANCH;
    longIdentifier = LdapTreeDelete.ARG_NAME_DELETE_BRANCH;
    valuePlaceholder = "{distinguishedName}";
    description =
            "Demonstrators must use this command line argument "
                    + "to specify the branch to delete. This command line argument is "
                    + "required, the specified value must be a valid distnguished name. "
                    + "and must be specified exactly once.";
    final DNArgument dnArgument =
            new DNArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(dnArgument);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {

    /*
     * Get a connection to directory server. The connection is made
     * using the standard command line options supported by
     * CommandLineTool and CommandLineOptions.
     */
    LDAPConnection ldapConnection;
    try
    {
      ldapConnection = getConnection();
    }
    catch(final LDAPException ldapException)
    {
      return ldapException.getResultCode();
    }

    /*
     * Set the connection options as determined by command line
     * arguments.
     */
    final LDAPConnectionOptions connectionOptions =
            commandLineOptions.newLDAPConnectionOptions();
    ldapConnection.setConnectionOptions(connectionOptions);

    /*
     * Retrieve the name of the file containing LDIF from the command
     * line argument parameter. The LDIF file name is specified by the
     * --ldifFile command line argument and the filename specified must
     * be located on the classpath.
     */
    final StringArgument stringArgument =
            (StringArgument)commandLineOptions.getArgumentParser().getNamedArgument(
                    LdapTreeDelete.ARG_NAME_LDIF_FILE);
    final String ldifFile = stringArgument.getValue();

    /*
     * Retrieve the name of the DN to be deleted (along with all of its
     * subordinates) from the command line argument parameter. The
     * distinguished name is specified by the --deleteBranch command
     * line argument.
     */
    final DNArgument dnArgument =
            (DNArgument)commandLineOptions.getArgumentParser().getNamedArgument(
                    LdapTreeDelete.ARG_NAME_DELETE_BRANCH);
    final DN dnToDelete = dnArgument.getValue();

    /*
     * Construct the demonstration service provider. This object will
     * provide all the services necessary for demonstrating the use of
     * the tree delete request control.
     */
    final LdapDeleteBranch deleter = LdapDeleteBranch.getInstance();
    deleter.addLdapExceptionListener(this);

    /*
     * Create the progress listeners. These obejcts report on the
     * progress made by the loading of the LDIF file.
     */
    final List<ProgressListener<LdifLoadProgressEvent>> listOfLoadProgressListeners =
            SampleCodeCollectionUtils.newArrayList();
    final ProgressListener<LdifLoadProgressEvent> loadProgressListener =
            new ProgressListener<LdifLoadProgressEvent>()
            {

              @Override
              public void progress(final LdifLoadProgressEvent progressEvent)
              {
                Validator.ensureNotNull(progressEvent);
                final LogRecord record =
                        new LogRecord(Level.INFO,progressEvent.getProgressMessage());
                out(new MinimalLogFormatter().format(record));
              }

            };
    listOfLoadProgressListeners.add(loadProgressListener);

    try
    {

      final ReadLdifFile adder = ReadLdifFile.getInstance();

      /*
       * Add the entries from the file (in the form of LDIF) that was
       * specified as a parameter to the --ldifFile command line
       * argument.
       */
      final Control[] controls = null;
      adder.addEntriesInFile(ldapConnection,ldifFile,controls);

      /*
       * Delete the DN and all its subordinates. The DN is specified by
       * the --deleteBranch command line argument.
       */
      final ControlHandler[] controlHandlers = null;
      deleter.deleteTree(ldapConnection,dnToDelete,
              commandLineOptions.getMaxResponseTimeMillis(),controlHandlers);

    }
    catch(final LDAPException ldapException)
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("An LDAP exception was detected:\n");
      builder.append(ldapException.getExceptionMessage());
      err(builder.toString());
      return ldapException.getResultCode();
    }
    catch(final IOException ioException)
    {
      final String ioExceptionMsg =
              String.format(
                      "An condition has transpired that has resulted in an I/O exception: %s",
                      ioException.getLocalizedMessage());
      err(ioExceptionMsg);
      return ResultCode.OPERATIONS_ERROR;
    }
    catch(final LDIFException ldifException)
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("An LDIF exception was detected:\n");
      builder.append(ldifException.getExceptionMessage());
      err(builder.toString());
      return ResultCode.OPERATIONS_ERROR;
    }
    catch(final SupportedFeatureException unsupportedException)
    {
      err("The tree delete control or operation purpose request control "
              + "is not supported by this server.");
      return ResultCode.UNWILLING_TO_PERFORM;
    }

    return ResultCode.SUCCESS;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return LdapTreeDelete.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return LdapTreeDelete.TOOL_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    err(new MinimalLogFormatter().format(new LogRecord(Level.SEVERE,ldapExceptionEvent
            .getLdapException().getExceptionMessage())));
  }



  /**
   * Prepares {@code LdapTreeDelete} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public LdapTreeDelete()
  {
    this(System.out,System.err);
  }



  private LdapTreeDelete(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



  // handles command line arguments.
  private CommandLineOptions commandLineOptions;

}


final class LdifLoadProgressEvent
        implements ProgressEvent<String>
{

  /**
   * @return the progressMessage
   */
  @Override
  public final String getProgressMessage()
  {
    return progressMessage;
  }



  /**
   * Creates a {@code LdifLoadProgressEvent} with default state.
   * 
   * @param progressMessage
   */
  public LdifLoadProgressEvent(
          final String progressMessage)
  {
    this.progressMessage = progressMessage;
  }



  // The progress message from the client loading LDIF from a file.
  private final String progressMessage;

}
