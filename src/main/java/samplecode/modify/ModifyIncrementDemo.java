/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved. Copyright
 * (C) 2008-2011 UnboundID Corp. This program is free software; you can
 * redistribute it and/or modify it under the terms of the GNU General
 * Public License (GPLv2 only) or the terms of the GNU Lesser General
 * Public License (LGPLv2.1 only) as published by the Free Software
 * Foundation. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details. You should have received a
 * copy of the GNU General Public License along with this program; if
 * not, see <http://www.gnu.org/licenses>.
 */

package samplecode.modify;

import com.unboundid.ldap.sdk.*;
import com.unboundid.util.args.*;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.cli.CommandLineOptions;
import samplecode.tools.AbstractTool;

import java.io.OutputStream;
import java.util.List;

/**
 * Provides a demonstration of the use of the modify-increment extension
 * defined in RFC4525. This extension lets LDAP clients increment an
 * 'increment-able' attribute value by a specified amount, either
 * positive or negative. The modify-increment extension aids in
 * producing robust client code that is not dependent on timing and
 * replication by performing an increment in an atomic transaction (as
 * opposed to read-increment). The demonstration searches for the entry
 * (which is not strictly necessary but given as an example of
 * constructing and transmitting a search request), then transmits a
 * modify request with the pre-read and post-read request controls
 * attached. If the pre-read request control is permitted by the server,
 * the pre-read response will contain the value of the specified
 * attribute before the modification occurs. If the post-read request
 * control is permitted by the server, the post-read response will
 * contain the value of the attribute after the modification occurs. if
 * the server permits, the operation purpose request control is attached
 * to all requests. <blockquote>
 * <p/>
 * <pre>
 * Provides a demonstration of the use of the modify-increment extension. The
 * ModifyIncrementDemo requires the --entry argument and increments the
 * attributes
 * specified by the --attribute command line arguments in that entry by the
 * value
 * specified in the --incrementValue command line argument.
 * If the --incrementValue command line argument is not present,
 * a default value is used.
 *
 * Usage:  ModifyIncrementDemo {options}
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
 *     The path to the file containing the password to use to access the trust
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
 *     The attribute used in the search request or other request. This command
 *     line argument is not required, and can be specified multiple times. If
 * this
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
 * -n, --incrementValue {integer}
 *     Specifies the increment, either positive or negative, to use in
 *     incrementing the value of an attribute. The attribute that is
 * incremented
 *     is specified with the --attribute command line argument. This command
 * line
 *     argument is optional, has a default value, and may be specified exactly
 * one
 *     time.
 * -e, --entry {distinguishedName}
 *     Specifies the distinguished name of the entry (which must exist) whose
 *     attributes are to be incremented. This command line argument is
 * required,
 *     has no default value, and may be specified exactly once.
 * -H, -?, --help
 *     Display usage information for this program.
 *
 * ModifyIncrementDemo has completed processing. The result code was: 0
 * (success)
 *
 * </pre>
 * <p/>
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 1, 2011")
@CodeVersion("2.12")
@Launchable
public final class ModifyIncrementDemo extends AbstractTool
{


  /**
   * The short identifier of the command line argument which is used to
   * specify the entry in which the attribute specified by the
   * --attribute command lien arguments are incremented.
   */
  public static final Character SHORT_ID_ENTRY = 'e';


  /**
   * The short identifier of the command line argument that is used to
   * specify the increment value used in the modify-increment
   * demonstration. This command line argument is optional, has a
   * default value, and may only be specified one time.
   */
  public static final Character SHORT_ID_INCREMENT_VALUE = 'n';


  /**
   * The default value by which the attributes are incremented using the
   * modify-increment extension.
   */
  public static final Integer DEFAULT_INCREMENT_VALUE = 1;


  /**
   * The long identifier of the command line argument which is used to
   * specify the entry in which the attribute specified by the
   * --attribute command lien arguments are incremented.
   */
  public static final String ARG_NAME_ENTRY = "entry";


  /**
   * The long identifier of the command line argument that is used to
   * specify the increment value used in the modify-increment
   * demonstration. This command line argument is optional, has a
   * default value, and may only be specified one time.
   */
  public static final String ARG_NAME_INCREMENT_VALUE = "incrementValue";


  private String[] requestedAttributes;


  private SearchScope scope;


  private Filter filter;


  private DN entryDn;


  private DNArgument dnArgument;


  private IntegerArgument integerArgument;


  private int incrementValue;


  private ModifyIncrementDemo(final OutputStream outStream,
                              final OutputStream errStream)
  {
    super(outStream, errStream);
  }


  /**
   * Execute the modify-increment demonstration.
   *
   * @param args The command line arguments, less the JVM specific
   *             arguments.
   */
  public static void main(final String... args)
  {
    OutputStream outStream = System.out;
    OutputStream errStream = System.err;
    ModifyIncrementDemo modifyIncrementDemo =
      new ModifyIncrementDemo(outStream, errStream);
    String msg = modifyIncrementDemo.getToolDescription();
    modifyIncrementDemo.out(msg);
    ResultCode resultCode = modifyIncrementDemo.runTool(args);
    if (resultCode != null)
    {
      StringBuilder builder =
        new StringBuilder(modifyIncrementDemo.getToolName());
      builder.append(" has completed processing. The result code was: ");
      builder.append(resultCode);
      modifyIncrementDemo.out(builder.toString());
    }
  }


  @Override
  public void addArguments(final ArgumentParser argumentParser)
    throws ArgumentException
  {
    /*
     * Add the command line argument whose parameter is the increment
     * value (which can be positive or negative) by which to increment
     * the value of the attributes specified by the --attribute command
     * line arguments.
     */
    Character shortIdentifier = ModifyIncrementDemo.SHORT_ID_INCREMENT_VALUE;
    String longIdentifier = ModifyIncrementDemo.ARG_NAME_INCREMENT_VALUE;
    boolean isRequired = false;
    int maxOccurrences = 1;
    String valuePlaceholder = "{integer}";
    final StringBuilder builder = new StringBuilder();
    builder.append("Specifies the increment, either positive or negative, ");
    builder.append("to use in incrementing the value of an attribute. The ");
    builder.append("attribute that is incremented is specified with the ");
    builder.append("--attribute command line argument. This command line argument ");
    builder.append("is optional, has a default value, and may be specified ");
    builder.append("exactly one time.");
    final Integer defaultValue = ModifyIncrementDemo.DEFAULT_INCREMENT_VALUE;
    String description = builder.toString();
    integerArgument =
      new IntegerArgument(shortIdentifier, longIdentifier, isRequired, maxOccurrences,
        valuePlaceholder, description, defaultValue);
    argumentParser.addArgument(integerArgument);

    /*
     * Add the command line argument whose parameter is the
     * distinguished name of the entry whose attributes are to be
     * incremented to the argument parser.
     */
    shortIdentifier = ModifyIncrementDemo.SHORT_ID_ENTRY;
    longIdentifier = ModifyIncrementDemo.ARG_NAME_ENTRY;
    isRequired = true;
    maxOccurrences = 0;
    valuePlaceholder = "{distinguishedName}";
    builder.delete(0, builder.capacity());
    builder.append("Specifies the distinguished name of the entry ");
    builder.append("(which must exist) whose attributes are to be incremented. ");
    builder.append("This command line argument is required, has no default value, ");
    builder.append("and may be specified exactly once.");
    description = builder.toString();
    dnArgument =
      new DNArgument(shortIdentifier, longIdentifier, isRequired, maxOccurrences,
        valuePlaceholder, description);
    argumentParser.addArgument(dnArgument);

    Argument filterArgument = commandLineOptions.getFilterArgument();
    filterArgument.setMaxOccurrences(1);

    addRequiredArgumentSet(argumentParser, dnArgument, filterArgument);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "ModifyIncrementDemo.properties";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {

    ResultCode resultCode = ResultCode.SUCCESS;
    try
    {
      modifyIncrementAttributes();
    }
    catch(LDAPException e)
    {
      getLogger().fatal(e);
      resultCode = e.getResultCode();
    }
    return resultCode;

  }


  SearchScope getSearchScope()
  {
    String argName = CommandLineOptions.ARG_NAME_SCOPE;
    Argument argument = commandLineOptions.getNamedArgument(argName);
    return ((ScopeArgument) argument).getValue();
  }


  Filter getFilter()
  {
    String argName = CommandLineOptions.ARG_NAME_FILTER;
    Argument argument = commandLineOptions.getNamedArgument(argName);
    return ((FilterArgument) argument).getValue();
  }


  String[] getRequestedAttributes()
  {
    List<String> requestedAttributesList =
      commandLineOptions.getRequestedAttributes();
    int size = requestedAttributesList.size();
    return requestedAttributesList.toArray(new String[size]);
  }


  void initializeDemo()
  {
    scope = getSearchScope();
    filter = getFilter();
    if (filter == null)
    {
      err("--filter is a required argument.");
      return;
    }
    entryDn = dnArgument.getValue();
    requestedAttributes = getRequestedAttributes();
    incrementValue = integerArgument.getValue();
  }


  void modifyIncrementAttributes() throws LDAPException
  {

    initializeDemo();
    LDAPConnection ldapConnection = getConnection();
    ModifyStrategy modifyEntry =
      new IncrementModifyStrategy(ldapConnection, scope, filter);

    for(final String attribute : requestedAttributes)
    {
      try
      {
        modifyEntry.modify(entryDn, attribute, incrementValue);
      }
      finally
      {
        ldapConnection.close();
      }
    }

  }


}
