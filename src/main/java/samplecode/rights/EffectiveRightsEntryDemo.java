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
package samplecode.rights;


import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.ldap.sdk.unboundidds.controls.AttributeRight;
import com.unboundid.ldap.sdk.unboundidds.controls.EffectiveRightsEntry;
import com.unboundid.ldap.sdk.unboundidds.controls.GetEffectiveRightsRequestControl;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.StringArgument;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


import samplecode.BasicToolCompletedProcessing;
import samplecode.CheckEffectiveRights;
import samplecode.CheckEffectiveRightsException;
import samplecode.CommandLineOptions;
import samplecode.SupportedFeatureException;
import samplecode.ToolCompletedProcessing;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdapSearchExceptionEvent;
import samplecode.listener.LdapSearchExceptionListener;
import samplecode.tools.AbstractTool;


/**
 * Provides services used to demonstrate the use of the
 * {@link EffectiveRightsEntry} class and the
 * {@link GetEffectiveRightsRequestControl}. <blockquote>
 * 
 * <pre>
 * Demonstrates the use of the EffectiveRightsEntry class.
 * 
 * Usage:  EffectiveRightsEntryDemo {options}
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
 * -e, --entry {DN}
 *     The distinguished name of the entry upon which the effective rights test is
 *     conducted.
 * -r, --right {compare, proxy, read, search, selfwrite_add, selfwrite_delete, or write}
 *     This command line argument specifies one of the following proxy, read,
 *     search, selfwrite_add, selfwrite_delete, or write. This argument is
 *     required and can be specified multiple times.
 * -z, --authZid {DN}
 *     The authentication ID to use when checking effective rights on the entry.
 * -H, -?, --help
 *     Display usage information for this program.
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 23, 2011")
@CodeVersion("1.29")
public class EffectiveRightsEntryDemo
        extends AbstractTool
{

  /**
   * The description of this tool; it used for help and diagnostic
   * output and for other purposes.
   */
  public static final String TOOL_DESCRIPTION =
          "Provides a demonstration of the GetEffectiveRightsRequestControl which "
                  + "provides a mechanism for extracting the effective rights information "
                  + "from an entry returned for a search request that included the "
                  + "get effective rights request control. In particular, it provides "
                  + "the ability to parse the values of the aclRights attributes in order "
                  + "to determine what rights the specified user may have when "
                  + "interacting with the entry. Command line options --entry, "
                  + "--authZid, and --right specify the entry for which to test, "
                  + "the authorization ID, and a set of "
                  + "rights. The command line argument --right can be "
                  + "specified multiple times.";



  /**
   * The name of this tool; it used for help and diagnostic output and
   * for other purposes.
   */
  public static final String TOOL_NAME = "EffectiveRightsEntryDemo";



  /**
   * Entry point for launching the EffectiveRightsEntryDemo tool. The
   * tool requires the following arguments:
   * <ul>
   * <li>{@code --entryDn} - the distinguished name of the entry to use
   * in determining whether the authorization state of the connection
   * has the specified rights to the specified attributes. This option
   * can only be specified one time, and is required.</li>
   * <li>{@code --attribute} - the name of an attribute in the entry.
   * This option can be specified multiple times, at least one is
   * required.</li>
   * <li>{@code --right} - takes one of the following values: compare,
   * proxy, read, search, selfwrite_add, selfwrite_delete, or write. The
   * case of the option is not significant, that is, {@code write} is
   * the same as {@code WRITE}. This option can be specified multiple
   * times, at least once is required, for example,
   * {@code --right search --right read}.</li>
   * <li>{@code --authZid} - This command line option's parameter to
   * specify the authentication ID to use when checking effective
   * rights. This command line option is required.</li>
   * </ul>
   * 
   * @param args
   *          command line options as noted above.
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final EffectiveRightsEntryDemo demo = new EffectiveRightsEntryDemo(outStream,errStream);
    final ResultCode resultCode = demo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(demo,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions =
            EffectiveRightsEntryDemoCommandLineOptions
                    .newEffectiveRightsEntryDemoCommandLineOptions(argumentParser,
                            validRightsSet);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();

    /*
     * Get the command line option from the parser whose parameter is
     * the authentication ID to use when checking effective rights on
     * entry.
     */
    final DN authZid = commandLineOptions.getAuthZID();

    /*
     * Get the command line option from the parser whose parameter(s) is
     * the name of an attribute.
     */
    final List<String> attributes = Arrays.asList(commandLineOptions.getRequestedAttributes());

    /*
     * Get the command line option from the parser whose parameter is
     * the distinguished name of an entry.
     */
    final DN entryDn = commandLineOptions.getEntry();

    /*
     * Get the command line option from the parser whose parameter(s)
     * are the names of rights.
     */
    final List<String> rights = commandLineOptions.getRights();

    /*
     * Obtain a pool of connections to the LDAP server from the
     * LDAPCommandLineTool services,this requires specifying a
     * connection to the LDAP server,a number of initial connections
     * (--initialConnections) in the pool,and the maximum number of
     * connections (--maxConnections) that the pool should create.
     */
    try
    {
      ldapConnection = connectToServer();
      ldapConnectionPool = getLdapConnectionPool(ldapConnection);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }

    /*
     * For each attribute and right check to see whether the right is
     * available for that attribute name. This same procedure can be
     * used for rights to the entry itself.
     */
    String baseObject;
    try
    {
      baseObject = commandLineOptions.getBaseObject();
    }
    catch(final LDAPException ldapException)
    {
      return ldapException.getResultCode();
    }

    /*
     * check to see that entry specified by the --authZid command line
     * arguments.
     */
    if(!entryExists())
    {
      final String msg =
              String.format("the DN specified for authZid '%s' does not exist.",authZid);
      getLogger().severe(msg);
      return ResultCode.NO_SUCH_OBJECT;
    }

    CheckEffectiveRights effectiveRights;
    effectiveRights = new CheckEffectiveRights(ldapConnection);
    effectiveRights.addLdapExceptionListener(new EffectiveRightsEntryDemoLdapExceptionListener(
            System.err));
    effectiveRights
            .addLdapSearchExceptionListener(new EffectiveRightsEntryDemoLdapSearchExceptionListener(
                    System.err));

    for(final String a : attributes)
    {
      for(final String right : rights)
      {
        try
        {
          final SearchRequest searchRequest =
                  new SearchRequest(baseObject,commandLineOptions.getSearchScope(),
                          commandLineOptions.getFilter(),a);
          final AttributeRight attributeRight = rightsMap.get(right.toLowerCase());
          effectiveRights.hasRight(searchRequest,a,attributeRight,"dn:" + authZid.toString());
          reportDoesHaveRight(String.format("'%s' does have '%s' right for attribute %s",
                  entryDn.toString(),attributeRight,a));
        }
        catch(final CheckEffectiveRightsException effectiveRightsException)
        {
          reportDoesNotHaveRight(String.format(
                  "'%s' does not have '%s' right for attribute %s",entryDn.toString(),
                  effectiveRightsException.attributeRight(),
                  effectiveRightsException.attributeName()));
        }
        catch(final SupportedFeatureException exception)
        {
          ldapConnection.close();
          return ResultCode.OPERATIONS_ERROR;
        }
      }
    }

    return ResultCode.SUCCESS;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return EffectiveRightsEntryDemo.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return EffectiveRightsEntryDemo.TOOL_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    final int maxLen = 10;
    return "EffectiveRightsEntryDemo [" +
            (rightsMap != null ? "rightsMap=" + this.toString(rightsMap.entrySet(),maxLen) +
                    ", " : "") +
            (validRightsSet != null ? "validRightsSet=" + this.toString(validRightsSet,maxLen)
                    : "") + "]";
  }



  @Override
  protected int getIntroductionColumnWidth()
  {
    return 80;
  }



  @Override
  protected Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }



  private boolean entryExists()
  {
    boolean entryExists;
    SearchResult searchResult;
    try
    {
      searchResult =
              getConnection().search(commandLineOptions.getBaseObject(),
                      commandLineOptions.getSearchScope(),commandLineOptions.getFilter(),
                      SearchRequest.NO_ATTRIBUTES);
      entryExists = searchResult.getEntryCount() > 0;
    }
    catch(final LDAPSearchException exception)
    {
      exception.printStackTrace();
      entryExists = false;
    }
    catch(final LDAPException exception)
    {
      exception.printStackTrace();
      entryExists = false;
    }
    return entryExists;
  }



  private void reportDoesHaveRight(final String msg)
  {
    getLogger().info(msg);
  }



  private void reportDoesNotHaveRight(final String msg)
  {
    getLogger().info(msg);
  }



  private String toString(final Collection<?> collection,final int maxLen)
  {
    final StringBuilder builder = new StringBuilder();
    builder.append("[");
    int i = 0;
    for(final Iterator<?> iterator = collection.iterator(); iterator.hasNext() && (i < maxLen); i++)
    {
      if(i > 0)
      {
        builder.append(", ");
      }
      builder.append(iterator.next());
    }
    builder.append("]");
    return builder.toString();
  }



  /**
   * Prepares {@code EffectiveRightsEntryDemo} for use by a client with
   * a default state.
   */
  public EffectiveRightsEntryDemo()
  {
    this(System.out,System.err);
  }



  private EffectiveRightsEntryDemo(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);

    /*
     * Construct a map of AttributeRight objects keyed by String.
     */
    rightsMap = new HashMap<String,AttributeRight>();
    rightsMap.put("compare",AttributeRight.COMPARE);
    rightsMap.put("proxy",AttributeRight.PROXY);
    rightsMap.put("read",AttributeRight.READ);
    rightsMap.put("search",AttributeRight.SEARCH);
    rightsMap.put("selfwrite_add",AttributeRight.SELFWRITE_ADD);
    rightsMap.put("selfwrite_delete",AttributeRight.SELFWRITE_DELETE);
    rightsMap.put("write",AttributeRight.WRITE);
  }



  // Handles command line arguments for EffectiveRightsEntryDemo
  private EffectiveRightsEntryDemoCommandLineOptions commandLineOptions;



  // The map of rights keyed by a string
  private final Map<String,AttributeRight> rightsMap;



  // The set of rights for which to test.
  private final Set<String> validRightsSet = new HashSet<String>(Arrays.asList("compare",
          "proxy","read","search","selfwrite_add","selfwrite_delete","write"));
}


/**
 * Provides support for command line arguments required by the effective
 * rights demo class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 22, 2011")
@CodeVersion("1.1")
final class EffectiveRightsEntryDemoCommandLineOptions
        extends CommandLineOptions
{

  /**
   * The isRequired parameter of the {code Argument} whose parameter is
   * the DN of an entry.
   */
  public static final boolean ARG_IS_REQUIRED_ENTRY = true;



  /**
   * The long identifier of the {code Argument} whose parameter is the
   * name of an attribute.
   */
  public static final String ARG_NAME_ATTRIBUTE = "attribute";



  /**
   * The long identifier of the {code Argument} whose parameter is the
   * name of an authentication identity.
   */
  public static final String ARG_NAME_AUTHZID = "authZid";



  /**
   * The long identifier of the {code Argument} whose parameter is the
   * DN of an entry.
   */
  public static final String ARG_NAME_ENTRY = "entry";



  /**
   * The long identifier of the {code Argument} whose parameter is the
   * name of an attribute right to check. The parameter must be compare,
   * proxy, read, search, selfwrite_add, selfwrite_delete, or write.
   */
  public static final String ARG_NAME_RIGHT = "right";



  /**
   * The short identifier of the {code Argument} whose parameter is the
   * name of an authentication identity.
   */
  public static final Character SHORT_ID_AUTHZID = Character.valueOf('z');



  /**
   * The short identifier of the {code Argument} whose parameter is the
   * DN of an entry.
   */
  public static final Character SHORT_ID_ENTRY = Character.valueOf('e');



  /**
   * The short identifier of the {code Argument} whose parameter is the
   * name of an attribute right to check. The parameter must be compare,
   * proxy, read, search, selfwrite_add, selfwrite_delete, or write.
   */
  public static final Character SHORT_ID_RIGHT = Character.valueOf('r');



  /**
   * Creates a new {@code EffectiveRightsEntryDemoCommandLineOptions}.
   * 
   * @param argumentParser
   *          handles the parsing of command line arguments.
   * @param validRightsSet
   *          a set of valid rights
   * @throws ArgumentException
   *           when a command line argument cannot br created or added
   *           to the parser.
   * @return a new {@code EffectiveRightsEntryDemoCommandLineOptions}.
   */
  public static EffectiveRightsEntryDemoCommandLineOptions
          newEffectiveRightsEntryDemoCommandLineOptions(final ArgumentParser argumentParser,
                  final Set<String> validRightsSet) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser,validRightsSet);
    return new EffectiveRightsEntryDemoCommandLineOptions(argumentParser,validRightsSet);
  }



  /**
   * Retrieves the authZid.
   * 
   * @return the authZid.
   */
  public DN getAuthZID()
  {
    return authZIDArgument.getValue();
  }



  /**
   * Retrieves the entry.
   * 
   * @return the entry.
   */
  public DN getEntry()
  {
    return entryArgument.getValue();
  }



  /**
   * Retrieves the list of rights.
   * 
   * @return the list of rights.
   */
  public List<String> getRights()
  {
    return rightArgument.getValues();
  }



  /**
   * Creates a {@code EffectiveRightsEntryDemoCommandLineOptions} with
   * default state.
   * 
   * @param argumentParser
   *          handles the parsing of command line arguments.
   * @throws ArgumentException
   *           when a command line argument cannot br created or added
   *           to the parser.
   */
  private EffectiveRightsEntryDemoCommandLineOptions(
          final ArgumentParser argumentParser,final Set<String> validRightsSet)
          throws ArgumentException
  {
    super(argumentParser);

    /*
     * Add the command line option to the parser whose parameter is the
     * distinguished name of an entry. This command line option is
     * required.
     */
    Character shortIdentifier = EffectiveRightsEntryDemoCommandLineOptions.SHORT_ID_ENTRY;
    String longIdentifier = EffectiveRightsEntryDemoCommandLineOptions.ARG_NAME_ENTRY;
    final boolean isRequired = EffectiveRightsEntryDemoCommandLineOptions.ARG_IS_REQUIRED_ENTRY;
    String valuePlaceholder = "{DN}";
    int maxOccurrences = 1;
    final StringBuilder builder = new StringBuilder();
    builder.append("The distinguished name of the entry upon which ");
    builder.append("the effective rights test is conducted.");
    String description = builder.toString();
    entryArgument =
            new DNArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(entryArgument);

    /*
     * Add the command line option to the parser whose parameter(s) is
     * the rights to test. This command line option is required.
     */
    shortIdentifier = EffectiveRightsEntryDemoCommandLineOptions.SHORT_ID_RIGHT;
    longIdentifier = EffectiveRightsEntryDemoCommandLineOptions.ARG_NAME_RIGHT;
    valuePlaceholder =
            "{compare, proxy, read, search, selfwrite_add, selfwrite_delete, or write}";
    maxOccurrences = 0;
    builder.delete(0,builder.capacity());
    builder.append("This command line argument specifies one of the following ");
    builder.append("proxy, read, search, selfwrite_add, selfwrite_delete, or write. ");
    builder.append("This argument is required and can be specified multiple times.");
    description = builder.toString();
    rightArgument =
            new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description,validRightsSet);
    argumentParser.addArgument(rightArgument);

    /*
     * Add the command line option to the parser whose parameter is the
     * authentication ID to use when checking for effective rights. This
     * command line option is required.
     */
    shortIdentifier = EffectiveRightsEntryDemoCommandLineOptions.SHORT_ID_AUTHZID;
    longIdentifier = EffectiveRightsEntryDemoCommandLineOptions.ARG_NAME_AUTHZID;
    valuePlaceholder = "{DN}";
    maxOccurrences = 1;
    description = "The authentication ID to use when checking effective rights on the entry.";
    authZIDArgument =
            new DNArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(authZIDArgument);
  }



  // The command line argument whose value is the authZid
  private final DNArgument authZIDArgument;



  // The command line argument whose value is the entry.
  private final DNArgument entryArgument;



  // The command line argument whose value is a right.
  private final StringArgument rightArgument;

}


final class EffectiveRightsEntryDemoLdapExceptionListener
        implements LdapExceptionListener
{

  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    Validator.ensureNotNull(ldapExceptionEvent);
    final String helpfulMessage = ldapExceptionEvent.getLdapException().getExceptionMessage();
    final LogRecord record = new LogRecord(Level.SEVERE,helpfulMessage);
    errorPrintStream.println(formatter.format(record));
  }



  EffectiveRightsEntryDemoLdapExceptionListener(
          final PrintStream errorPrintStream)
  {
    Validator.ensureNotNull(errorPrintStream);
    this.errorPrintStream = errorPrintStream;
  }



  private final PrintStream errorPrintStream;



  private final Formatter formatter = new MinimalLogFormatter();

}


final class EffectiveRightsEntryDemoLdapSearchExceptionListener
        implements LdapSearchExceptionListener
{

  @Override
  public void searchRequestFailed(final LdapSearchExceptionEvent ldapSearchExceptionEvent)
  {
    Validator.ensureNotNull(ldapSearchExceptionEvent);
    final String helpfulMessage =
            ldapSearchExceptionEvent.getLdapSearchException().getExceptionMessage();
    final LogRecord record = new LogRecord(Level.SEVERE,helpfulMessage);
    errorPrintStream.println(formatter.format(record));
  }



  EffectiveRightsEntryDemoLdapSearchExceptionListener(
          final PrintStream errorPrintStream)
  {
    Validator.ensureNotNull(errorPrintStream);
    this.errorPrintStream = errorPrintStream;
  }



  private final PrintStream errorPrintStream;



  private final Formatter formatter = new MinimalLogFormatter();



}
