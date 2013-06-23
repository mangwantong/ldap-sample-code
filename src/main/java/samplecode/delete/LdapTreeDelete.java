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

package samplecode.delete;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.args.*;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.controls.ControlHandler;
import samplecode.ldif.LdifLoadProgressEvent;
import samplecode.ldif.ReadLdifFile;
import samplecode.listener.ProgressListener;
import samplecode.tools.AbstractTool;
import samplecode.tools.BasicToolCompletedProcessing;
import samplecode.tools.ToolCompletedProcessing;
import samplecode.util.SampleCodeCollectionUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;


/**
 * Provides a demonstration of how to use the LDAP tree delete control
 * extension. This code requires the UnboundID LDAP SDK commercial
 * edition. To compile and run with the standard edition, remove
 * references to the OperationPurposeRequestControl. The following
 * command line arguments are required and must be specified exactly
 * once: --deleteBranch, --ldifFile. <blockquote>
 * <p/>
 * <pre>
 * The tree delete tool provides a demonstration of how to use the Tree Delete
 * request control to delete a branch and all entries subordinate to the
 * branch.
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 30, 2011")
@CodeVersion("1.9")
public final class LdapTreeDelete extends AbstractTool {

  /**
   * <pre>
   * The tree delete tool provides a demonstration of how to use the Tree
   * Delete
   * request control to delete a branch and all entries subordinate to the
   * branch.
   *
   * Usage:  LdapTreeDelete {options}
   *
   * Available options include:
   * -h, --hostname {host}
   *     The IP address or resolvable name to use to connect to the directory
   *     server.  If this is not provided, then a default value of 'localhost'
   * will
   *     be used.
   * -p, --port {port}
   *     The port to use to connect to the directory server.  If this is not
   *     provided, then a default value of 389 will be used.
   * -D, --bindDN {dn}
   *     The DN to use to bind to the directory server when performing simple
   *     authentication.
   * -w, --bindPassword {password}
   *     The password to use to bind to the directory server when performing
   * simple
   *     authentication or a password-based SASL mechanism.
   * -j, --bindPasswordFile {path}
   *     The path to the file containing the password to use to bind to the
   *     directory server when performing simple authentication or a
   * password-based
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
   *     The path to the file containing the password to use to access the key
   * store
   *     contents.
   * --keyStoreFormat {format}
   *     The format (e.g., jks, jceks, pkcs12, etc.) for the key store file.
   * -P, --trustStorePath {path}
   *     The path to the file to use as trust store when determining whether to
   *     trust a certificate presented by the directory server.
   * -T, --trustStorePassword {password}
   *     The password to use to access the trust store contents.
   * -U, --trustStorePasswordFile {path}
   *     The path to the file containing the password to use to access the
   * trust
   *     store contents.
   * --trustStoreFormat {format}
   *     The format (e.g., jks, jceks, pkcs12, etc.) for the trust store file.
   * -N, --certNickname {nickname}
   *     The nickname (alias) of the client certificate in the key store to
   * present
   *     to the directory server for SSL client authentication.
   * -o, --saslOption {name=value}
   *     A name-value pair providing information to use when performing SASL
   *     authentication.
   * -b, --baseObject {distinguishedName}
   *     The base object used in the search request.
   * -a, --attribute {attribute name or type}
   *     The attribute used in the search request. This command line argument
   * is
   * not
   *     required, and can be specified multiple times. If this command line
   *     argument is not specified, the value '*' is used.
   * -f, --filter {filter}
   *     The search filter used in the search request.
   * -i, --initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server
   * when
   *     creating the connection pool.
   * -m, --maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server
   * when
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
   *     --deleteBranch and all of its subordinates is deleted. The intent is
   * to
   *     allow operators to add some entries and then delete them. This command
   * line
   *     argument is required and must be specified exactly once.
   * -d, --deleteBranch {distinguishedName}
   *     Operators should use this command line argument to specify the branch
   * to
   *     delete. This command line argument is required and must be specified
   *     exactly once.
   * -H, -?, --help
   *     Display usage information for this program.
   *
   * LdapTreeDelete has completed execution with the result code 0 (success)
   * </pre>
   *
   * @param args
   *   The command line arguments and parameters, less the JVM
   *   arguments.
   *
   * @throws IOException
   *   When an IO error with a file occurs.
   */
  public static void main(final String... args) throws IOException {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final LdapTreeDelete ldapTreeDelete = new LdapTreeDelete(outStream,errStream);
    final ResultCode resultCode = ldapTreeDelete.runTool(args);
    final ToolCompletedProcessing completedProcessing =
      new BasicToolCompletedProcessing(ldapTreeDelete,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  /**
   * The short identifier of the command line argument whose parameter
   * specifies the DN to be deleted with all of its subordinates.
   */
  public static final Character SHORT_NAME_DELETE_BRANCH = 'd';


  /**
   * The short identifier of the command line argument that specifies
   * the name of a file containing valid LDIF which is processed before
   * the branch specified by the --deleteBranch is deleted.
   */
  public static final Character SHORT_NAME_LDIF_FILE = 'l';


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
   * Prepares {@code LdapTreeDelete} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  @SuppressWarnings("unused")
  public LdapTreeDelete() {
    this(System.out,System.err);
  }



  public LdapTreeDelete(OutputStream outStream, OutputStream errStream) {
    super(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addArguments(ArgumentParser argumentParser)
    throws ArgumentException {
    this.argumentParser = argumentParser;

    Argument ldifFileArgument =
      new StringArgument(SHORT_NAME_LDIF_FILE,ARG_NAME_LDIF_FILE,true,1,
        "{resource on classpath}","Demonstrators must use this command line " +
        "argument to specify a file containing valid LDIF that is processed " +
        "before the DN specified by the --deleteBranch and all of its " +
        "subordinates is deleted. The intent is to allow observers of the " +
        "demonstration to add some entries and then delete them. This " +
        "command line argument is  required, the filename specified must be " +
        "found on the classpath, and must be specified exactly once.");

    argumentParser.addArgument(ldifFileArgument);

    /*
     * Add the command line argument to the argument parser whose
     * value is the distinguished name of the branch that will be
     * deleted along with all of its subordinates.
     */
    Argument dnArgument =
      new DNArgument(SHORT_NAME_DELETE_BRANCH,ARG_NAME_DELETE_BRANCH,true,1,
        "{distinguished name}","Demonstrators must use this command line " +
        "argument to specify the branch to delete. This command line argument" +
        " is required,  the specified value must be a valid distinguished " +
        "name. and must be specified exactly once.");
    argumentParser.addArgument(dnArgument);

    addRequiredArgumentSet(argumentParser,dnArgument,ldifFileArgument);
  }



  @Override
  protected ResultCode executeToolTasks() {

    final int indentation = getErrorIndentation();
    final int width = getIntroductionWidth();

    /*
    * Get a connection to directory server. The connection is made
    * using the standard command line options supported by
    * CommandLineTool and CommandLineOptions.
    */
    LDAPConnection ldapConnection;
    try {
      ldapConnection = getConnection();
      final LDAPConnectionOptions connectionOptions =
        getLdapConnectionOptions();
      ldapConnection.setConnectionOptions(connectionOptions);

      if(getLogger().isTraceEnabled()) {
        getLogger().trace("connected to LDAP directory server");
      }
    } catch(final LDAPException ldapException) {
      return ldapException.getResultCode();
    }

    /*
     * Retrieve the name of the file containing LDIF from the command
     * line argument parameter. The LDIF file name is specified by the
     * --ldifFile command line argument and the filename specified must
     * be located on the classpath. The entries in the file are
     * added to the DIT and then deleted
     */
    final String ldifFile = getLdifFile();

    /*
     * Retrieve the name of the DN to be deleted (along with all of its
     * subordinates) from the command line argument parameter. The
     * distinguished name is specified by the --deleteBranch command
     * line argument.
     */
    final DN dnToDelete = getDN();

    /*
     * Construct the demonstration service provider. This object will
     * provide all the services necessary for demonstrating the use of
     * the tree delete request control.
     */
    final LdapDeleteBranch deleter = LdapDeleteBranch.getInstance();
    deleter.addLdapExceptionListener(this);

    /*
     * Create the progress listeners. These objects report on the
     * progress made by the loading of the LDIF file.
     */
    final List<ProgressListener<LdifLoadProgressEvent>>
      listOfLoadProgressListeners = SampleCodeCollectionUtils.newArrayList();
    final ProgressListener<LdifLoadProgressEvent> loadProgressListener =
      new ProgressListener<LdifLoadProgressEvent>() {

        @Override
        public void progress(final LdifLoadProgressEvent progressEvent) {
          if(progressEvent == null) {
            throw new IllegalArgumentException("progressEvent must not be null.");
          }
          if(getLogger().isTraceEnabled()) {
            final String msg = progressEvent.getProgressMessage();
            getLogger().trace(msg);
          }
        }

      };
    listOfLoadProgressListeners.add(loadProgressListener);

    try {
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
    } catch(final LDAPException ldapException) {
      final StringBuilder builder = new StringBuilder();
      builder.append("An LDAP exception was detected:\n");
      builder.append(ldapException.getExceptionMessage());
      wrapErr(indentation,width,builder.toString());
      return ldapException.getResultCode();
    } catch(final IOException ioException) {
      final String localisedMessage = ioException.getLocalizedMessage();
      final String ioExceptionMsg =
        String.format("An condition has transpired that has resulted in an " +
          "I/O  exception: %s",localisedMessage);
      wrapErr(indentation,width,ioExceptionMsg);
      return ResultCode.OPERATIONS_ERROR;
    } catch(final LDIFException ldifException) {
      final StringBuilder builder = new StringBuilder();
      builder.append("An LDIF exception was detected:\n");
      builder.append(ldifException.getExceptionMessage());
      wrapErr(indentation,width,builder.toString());
      return ResultCode.OPERATIONS_ERROR;
    }

    return ResultCode.SUCCESS;
  }



  /**
   * return the class-specific properties resource name
   */
  @Override
  protected String classSpecificPropertiesResourceName() {
    return "LdapTreeDelete.properties";
  }



  public String getLdifFile() {
    final String argName = ARG_NAME_LDIF_FILE;
    Argument arg = argumentParser.getNamedArgument(argName);
    return ((StringArgument) arg).getValue();
  }



  public DN getDN() {
    final String argName = ARG_NAME_DELETE_BRANCH;
    Argument arg = argumentParser.getNamedArgument(argName);
    return ((DNArgument) arg).getValue();
  }



  private ArgumentParser argumentParser;


}
