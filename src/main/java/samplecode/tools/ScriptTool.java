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
package samplecode.tools;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;
import samplecode.ScriptGenerator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Provides a command line interface to {@code ScriptGenerator}.
 *
 * @see ScriptGenerator
 */
@Author("terry.gardner@unboundid.com") @Since("Jan 1, 2012") @CodeVersion("1.2") @Launchable
public final class ScriptTool extends AbstractTool
{

  /**
   * The description of this tool; this is used for help and diagnostic
   * output, and for other purposes.
   */
  public static final String TOOL_DESCRIPTION =
          "Generates an executable script for use in " + "invoking the tools provided by the " +
                  "samplecode package. " + "ScriptTool requires --writeableDirectory, " +
                  "" + "--className, and --classPath. Optionally supply --jvmOptions.";

  /**
   * The name of this tool; this is used for help and diagnostic output,
   * and for other purposes.
   */
  public static final String TOOL_NAME = "ScriptTool";

  /**
   * <blockquote>
   * <p/>
   * <pre>
   * Generates an executable script for use in invoking the tools provided by the
   * samplecode package. ScriptTool requires --writeableDirectory, --className, and
   * --classPath. Optionally supply --jvmOptions.
   *
   * Usage:  ScriptTool {options}
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
   * --abandonOnTimeout
   *     Whether the LDAP SDK should abandon an operation that has timed out.
   * -a, --attribute {attribute name or type}
   *     The attribute used in the search request or other request. This command
   *     line argument is not required, and can be specified multiple times. If this
   *     command line argument is not specified, the value '*' is used.
   * --autoReconnect
   *     Whether the LDAP SDK should automatically reconnect when a connection is
   *     lost.
   * -b, --baseObject {distinguishedName}
   *     The base object used in the search request.
   * --connectTimeoutMillis {connect-timeout-millis-integer}
   *     Specifies the maximum length of time in milliseconds that a connection
   *     attempt should be allowed to continue before giving up. A value of zero
   *     indicates that there should be no connect timeout.
   * -f, --filter {filter}
   *     The search filter used in the search request.
   * -i, --initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server when
   *     creating the connection pool.
   * --maxConnections {max-response-time-in-milliseconds}
   *     The maximum length of time in milliseconds that an operation should be
   *     allowed to block, with 0 or less meaning no timeout is enforced. This
   *     command line argument is optional and has a default value of zero.
   * --maxResponseTimeMillis {max-response-time-in-milliseconds}
   *     The maximum length of time in milliseconds that an operation should be
   *     allowed to block, with 0 or less meaning no timeout is enforced. This
   *     command line argument is optional and has a default value of zero.
   * --numThreads {number-of-threads}
   *     Specifies the number of threads to use when running the application.
   * --pageSize {positiveInteger}
   *     The search page size
   * --reportCount {positive-integer}
   *     Specifies the maximum number of reports. This command line argument is
   *     applicable to tools that display repeated reports. The time between
   *     repeated reports is specified by the --reportInterval command line
   *     argument.
   * --reportInterval {positive-integer}
   *     The report interval in milliseconds.
   * -s, --scope {searchScope}
   *     The scope of the search request; allowed values are BASE, ONE, and SUB
   * --sizeLimit {positiveInteger}
   *     The client-request maximum number of results which are returned to the
   *     client. If the number of entries which match the search parameter is
   *     greater than the client-requested size limit or the server-imposed size
   *     limit a SIZE_LIMIT_EXCEEDED code is returned in the result code in the
   *     search response.
   * --timeLimit {positiveInteger}
   *     The client-request maximum time that the directory server will devote to
   *     processing the search request. If the client-requested time limit or the
   *     server-imposed time limit a TIME_LIMIT_EXCEEDED code is returned in the
   *     result code in the search response.
   * --useSchema
   *     Whether the LDAP SDK should attempt to use server schema information, for
   *     example, for matching rules.
   * --verbose
   *     Whether the tool should be verbose.
   * --className {full-qualified classname}
   *     The name of a class for which to generate an executable shell script.
   * --classPath {class-path}
   *     the class path that ScriptTool inserts into a script
   * --writableDirectory {folder-or-directory}
   *     the directory in which ScriptTool creats a script
   * --jvmOptions {jvm options}
   *     JVM options for the shell script.
   * -H, -?, --help
   *     Display usage information for this program.
   *
   * [01/Jan/2012:11:37:16 -0500] ScriptTool has completed processing,
   * the result code was: 0 (success)
   *
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param args
   */
  public static void main(final String... args)
  {
    final ScriptTool tool = new ScriptTool();
    final ResultCode resultCode = tool.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(tool, resultCode);
    completedProcessing.displayMessage(System.out, System.err);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNullWithMessage(argumentParser, "argument parser was null.");

    classNameArgument = newClassNameArgument();
    classPathArgument = newClassPathArgument();
    directoryArgument = newDirectoryArgument();
    spaceSeparatedJVMOptionsArgument = newJvmOptionsArgument();
    argumentParser.addArgument(classNameArgument);
    argumentParser.addArgument(classPathArgument);
    argumentParser.addArgument(directoryArgument);
    argumentParser.addArgument(spaceSeparatedJVMOptionsArgument);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();
    final String className = classNameArgument.getValue();
    final String classPath = classPathArgument.getValue();
    final String spaceSeparatedJVMOptions = spaceSeparatedJVMOptionsArgument.getValue();
    final String directory = directoryArgument.getValue();
    final ScriptGenerator gen =
            new ScriptGenerator(className, classPath, spaceSeparatedJVMOptions, directory);
    try
    {
      gen.generateScript();
      final String helpfulMessage =
              String.format("Created %s/%s", directory, gen.getFilename());
      final LogRecord record = new LogRecord(Level.INFO, helpfulMessage);
      out(new MinimalLogFormatter().format(record));
    }
    catch(final IOException exception)
    {
      return ResultCode.OPERATIONS_ERROR;
    }
    return ResultCode.SUCCESS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return ScriptTool.TOOL_NAME;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "ScriptTool.properties";
  }

  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }

  /**
   * @return the full-qualified class name for which a script is to be
   *         generated, i.e., {@code "samplecode.tool.ScriptTool"}.
   */
  String getClassName()
  {
    return getClassNameArgument().getValue();
  }

  /**
   * @return the class path to use in the script.
   */
  String getClassPath()
  {
    return getClassPathArgument().getValue();
  }

  /**
   * @return the directory
   */
  String getDirectory()
  {
    return getDirectoryArgument().getValue();
  }

  /**
   * @return the JVM options.
   */
  String getJvmOptions()
  {
    return getJvmOptionsArgument().getValue();
  }

  /**
   * @return the classNameArgument
   */
  private StringArgument getClassNameArgument()
  {
    return classNameArgument;
  }

  /**
   * @return the classPathArgument
   */
  private StringArgument getClassPathArgument()
  {
    return classPathArgument;
  }

  /**
   * @return the directoryArgument
   */
  private StringArgument getDirectoryArgument()
  {
    return directoryArgument;
  }

  private StringArgument getJvmOptionsArgument()
  {
    return spaceSeparatedJVMOptionsArgument;
  }

  private StringArgument newClassNameArgument() throws ArgumentException
  {
    return new StringArgument(null, "className", true, 1, "{full-qualified classname}",
            "The name of a class for which to generate an executable shell script.");
  }

  private StringArgument newClassPathArgument() throws ArgumentException
  {
    return new StringArgument(null, "classPath", true, 1, "{class-path}",
            "the class path that ScriptTool inserts into a script");
  }

  private StringArgument newDirectoryArgument() throws ArgumentException
  {
    return new StringArgument(null, "writableDirectory", true, 1, "{folder-or-directory}",
            "the directory in which ScriptTool creats a script");
  }

  private StringArgument newJvmOptionsArgument() throws ArgumentException
  {
    return new StringArgument(null, "jvmOptions", false, 1, "{jvm options}",
            "JVM options for the shell script.", "-Xms32m -Xmx32m -d64");
  }

  /**
   * Constructs a {@code ScriptTool}.
   */
  public ScriptTool()
  {
    super(System.out, System.err);

  }

  /**
   * The string argument that is used to specify the fully-qualified
   * class name for which {@code ScriptTool} creates an executable
   * script. {@code --className} is required, and can be specified
   * exactly one time.
   */
  private StringArgument classNameArgument;

  /**
   * The string argument that is used to specify the class path that
   * {@code ScriptTool} inserts into an executable script.
   * {@code --classPath} is required, and can be specified exactly one
   * time.
   */
  private StringArgument classPathArgument;

  /**
   * The string argument that is used to specify the directory where
   * {@code ScriptTool} creates an executable script.
   * {@code --directory} is required, and can be specified exactly one
   * time.
   */
  private StringArgument directoryArgument;

  /**
   * The string argument that is used to specify the JVM Options yjsy
   * {@code ScriptTool} inserts into an executable script.
   * {@code --jvmOptions} is required, and can be specified exactly one
   * time.
   */
  private StringArgument spaceSeparatedJVMOptionsArgument;
}
