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


import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.DNArgument;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.tools.AbstractTool;


/**
 * Demonstrates the use of matching rules for attribute value
 * comparisons by comparing an attribute from two entries. NB: the
 * {@code --attribute} command line argument can be specified multiple
 * times, but only the first instance of the command line argument is
 * used.
 * 
 * @see CommandLineOptions
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 22, 2011")
@CodeVersion("1.7")
public final class MatchingRuleDemo
        extends AbstractTool
        implements LdapExceptionListener,ObservedByLdapExceptionListener
{

  /**
   * Provides a service useful demonstrating the technique for comparing
   * the values of attributes using matching rules.
   */
  private class AttributeMatcher
  {

    private void match() throws LDAPException,AttributeValueMatchException
    {
      // Get the raw values of the attributes:
      final ASN1OctetString[] strs1 = attributeEntry1.getRawValues();
      final ASN1OctetString[] strs2 = attributeEntry2.getRawValues();
      // Only compare the first values:
      final ASN1OctetString asnOctetString1 = strs1[0];
      final ASN1OctetString asnOctetString2 = strs2[0];
      // Get the matching rule and check for value match:
      final MatchingRule matchingRule = attributeEntry1.getMatchingRule();
      if(!matchingRule.valuesMatch(asnOctetString1,asnOctetString2))
      {
        throw new AttributeValueMatchException(attributeEntry1,attributeEntry2);
      }
    }



    /**
     * Provides a service that will use matching rules to match the two
     * attributes specified as {@code attributeEntry1} and
     * {@code attributeEntry2}.
     * 
     * @param attributeEntry1
     *          An attribute from entry1.
     * @param attributeEntry2
     *          An attribute from entry2.
     */
    private AttributeMatcher(
            final Attribute attributeEntry1,final Attribute attributeEntry2)
    {
      Validator.ensureNotNull(attributeEntry1,attributeEntry2);
      this.attributeEntry1 = attributeEntry1;
      this.attributeEntry2 = attributeEntry2;
    }



    /**
     * The attribute from entry 1 to be compared with the attribute of
     * the same name/type from entry 2.
     */
    private final Attribute attributeEntry1;



    /**
     * The attribute from entry 2 to be compared with the attribute of
     * the same name/type from entry 1.
     */
    private final Attribute attributeEntry2;
  }




  /**
   * An exception which is thrown when two attributes values do not
   * match.
   */
  @SuppressWarnings("serial")
  private class AttributeValueMatchException
          extends Exception
  {

    /**
     * @return The attribute from entry 1 to be compared with the
     *         attribute of the same name/type from entry 2.
     */
    private Attribute getAttribute1()
    {
      return attributeEntry1;
    }



    /**
     * @return The attribute from entry 2 to be compared with the
     *         attribute of the same name/type from entry 1.
     */
    private Attribute getAttribute2()
    {
      return attributeEntry2;
    }



    private AttributeValueMatchException(
            final Attribute attributeEntry1,final Attribute attributeEntry2)
    {
      Validator.ensureNotNull(attributeEntry1,attributeEntry2);
      this.attributeEntry1 = attributeEntry1;
      this.attributeEntry2 = attributeEntry2;
    }



    /**
     * The attribute from entry 1 to be compared with the attribute of
     * the same name/type from entry 2.
     */
    private final Attribute attributeEntry1;



    /**
     * The attribute from entry 2 to be compared with the attribute of
     * the same name/type from entry 1.
     */
    private final Attribute attributeEntry2;
  }



  /**
   * The description of this tool; used for help output and for other
   * purposes.
   */
  public static final String TOOL_DESCRIPTION =
          "Provides a demonstration of the use of matching rules "
                  + "by comparing a specified attribute between two entries. "
                  + "The attribute to be compared is specified by --attribute "
                  + ", and the entries are specified by --entryDn1 and --entryDn2.";



  /**
   * The name of this tool; used for help output and for other purposes.
   */
  public static final String TOOL_NAME = "MatchingRuleDemo";



  /**
   * Launch the Matching Rules Demonstration application.
   * <p/>
   * The following example compares the {@code description} attribute
   * from two entries from the directory server running at
   * {@code ldap://localhost:1389}: <blockquote>
   * <p/>
   * 
   * <pre>
   * java samplecode.MatchingRuleDemo \
   *         --filter '(&)' \
   *         --scope SUB \
   *         --entryDn1 uid=user.0,ou=people, dc=example,dc=com \
   *         --entryDn2 uid=user.1,ou=people,dc=example,dc=com \
   *         --attribute description \
   *         --hostname localhost \
   *         --port 1389
   * </pre>
   * <p/>
   * </blockquote>
   * <p/>
   * Note that the demonstration uses the filter specified by the
   * {@code --filter} command line argument and search scope speecified
   * by the {@code scope} command line argument to retrieve entry1 and
   * entry2 from the directory. The complete set of command line
   * arguments is reproduced below:<blockquote>
   * 
   * <pre>
   * Provides a demonstration of the use of matching rules by comparing a specified
   * attribute between two entries. The attribute to be compared is specified by
   * --attribute, and the entries are specified by --entryDn1 and --entryDn2
   * 
   * Usage:  MatchingRuleDemo {options}
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
   * --entryDn1 {DN}
   *     The distinguished name to be compared with entry2
   * --entryDn2 {DN}
   *     The distinguished name to be compared with entry1
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *          The list of command line arguments (less the JVM-specific
   *          arguments).
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final MatchingRuleDemo matchingRuleDemo = new MatchingRuleDemo(outStream,errStream);
    final ResultCode resultCode = matchingRuleDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(matchingRuleDemo,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   * <p>
   * Creates the standard command line options processor, and sets up
   * the {@code --entryDn1} and {@code --entryDn2} command line
   * arguments.
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = new MatchingRuleDemoCommandLineOptions(argumentParser);

  }



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapExceptionListener(
          final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.add(ldapExceptionListener);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();
    /*
     * Retrieve the parameters provided to the entryDn1, entryDn2, and
     * attribute arguments:
     */
    final DN entryDn1 = commandLineOptions.getEntryDn1();
    final DN entryDn2 = commandLineOptions.getEntryDn2();

    /*
     * Retreive the first attribute name specified by the command line
     * argument '--attribute'. That command line argument may appear
     * multiple times however this demonstration only uses the first
     * occurrences.
     */
    final String attributeName = commandLineOptions.getRequestedAttributes()[0];
    Validator.ensureFalse(attributeName.length() == 0,
            "This tool requires that the attribute name " + "have a length greater than zero.");

    /*
     * Use the {@code LDAPCommandLineTool} class to obtain a pool of
     * connctions to the directory server that was specified with the
     * command line options for number of initial connections
     * (--initialConnections) and maximum number of connections
     * (--maxConnections).
     */
    LDAPConnectionPool ldapConnectionPool;
    LDAPConnection ldapConnection;
    try
    {
      ldapConnection = getConnection();
      final int initialConnections = commandLineOptions.getInitialConnections();
      final int maxConnections = commandLineOptions.getMaxConnections();
      ldapConnectionPool =
              new LDAPConnectionPool(ldapConnection,initialConnections,maxConnections);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(null,ldapException);
      return ldapException.getResultCode();
    }

    /*
     * Check that supported attribute is supported by the directory
     * server to which this LDAP client is connected.
     */
    try
    {
      final LDAPConnection connection = ldapConnectionPool.getConnection();
      SupportedUserAttribute.getInstance().supported(connection,attributeName);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }
    catch(final AttributeNotSupportedException e)
    {
      final StringBuilder builder = new StringBuilder();
      builder.append(String.format("attribute '%s' is not supported "
              + "by this directory server.",e.getAttributeName()));
      err(builder.toString());
      return ResultCode.PARAM_ERROR;
    }

    /*
     * Retrieve entry 1 and entry 2 from the directory:
     */
    final Entry entry1;
    final Entry entry2;
    try
    {

      /*
       * Create the search request
       */
      String baseObject = entryDn1.toString();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final String[] requestedAttributes = new String[]
      {
        attributeName
      };
      SearchRequest searchRequest =
              new SearchRequest(baseObject,scope,filter,requestedAttributes);
      searchRequest.setSizeLimit(commandLineOptions.getSizeLimit());
      searchRequest.setTimeLimitSeconds(commandLineOptions.getTimeLimit());
      entry1 = ldapConnectionPool.searchForEntry(searchRequest);

      /*
       * Repeat for entryDn2
       */
      baseObject = entryDn2.toString();
      searchRequest = new SearchRequest(baseObject,scope,filter,requestedAttributes);
      searchRequest.setSizeLimit(commandLineOptions.getSizeLimit());
      searchRequest.setTimeLimitSeconds(commandLineOptions.getTimeLimit());
      entry2 = ldapConnectionPool.searchForEntry(searchRequest);

      /*
       * Retrieve the attribute specified by the --attribute command
       * line argument from each entry and compare one to the other.
       */
      if((entry1 != null) && (entry2 != null))
      {
        final Attribute attributeEntry1 = entry1.getAttribute(attributeName);
        final Attribute attributeEntry2 = entry2.getAttribute(attributeName);
        if(attributeEntry1 == null)
        {
          final StringBuilder builder = new StringBuilder();
          builder.append(String.format("attribute '%s' was not present in %s",attributeName,
                  entryDn1));
          final String msg = builder.toString();
          err(msg);
          return ResultCode.PARAM_ERROR;
        }
        if(attributeEntry2 == null)
        {
          final StringBuilder builder = new StringBuilder();
          builder.append(String.format("attribute '%s' was not present in %s",attributeName,
                  entryDn2));
          final String msg = builder.toString();
          err(msg);
          return ResultCode.PARAM_ERROR;
        }
        final AttributeMatcher attributeMatcher =
                new AttributeMatcher(attributeEntry1,attributeEntry2);
        attributeMatcher.match();
      }
      else
      {
        logger.log(Level.INFO,"no entries were returned.");
      }
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }
    catch(final AttributeValueMatchException attributeValueMatchException)
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("Attribute values did not match.\n");
      builder.append("entry1:\n");
      builder.append(attributeValueMatchException.getAttribute1());
      builder.append('\n');
      builder.append("entry2:\n");
      builder.append(attributeValueMatchException.getAttribute2());
      err(builder.toString());
      return ResultCode.SUCCESS;
    }

    return ResultCode.SUCCESS;
  }



  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
          final LDAPException ldapException)
  {
    Vector<LdapExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapExceptionListener>)ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev = new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }



  @Override
  public Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return MatchingRuleDemo.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return MatchingRuleDemo.TOOL_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    logger.log(Level.SEVERE,ldapExceptionEvent.getLdapException().getExceptionMessage());
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapExceptionListener(
          final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.remove(ldapExceptionListener);
    }
  }



  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }



  /**
   * Prepares {@code MatchingRuleDemo} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public MatchingRuleDemo()
  {
    this(System.out,System.err);
  }



  /**
   * Prepares {@code MatchingRuleDemo} for use by a client - the
   * specified output streams are used.
   */
  private MatchingRuleDemo(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



  /**
   * Provides services related to managing command line options for
   * clients that use the LDAPCommandLineTool class.
   */
  private MatchingRuleDemoCommandLineOptions commandLineOptions;



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
          new Vector<LdapExceptionListener>();

}


/**
 * Provides support for the comand line arguments used by the matching
 * rule demo class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 19, 2011")
@CodeVersion("1.7")
class MatchingRuleDemoCommandLineOptions
        extends CommandLineOptions
{

  /**
   * The long identifier of the argument whose parameter is the name of
   * entry number 1.
   */
  private static final String ARG_NAME_ENTRY_DN_1 = "entryDn1";



  /**
   * The long identifier of the argument whose parameter is the name of
   * entry number 2.
   */
  private static final String ARG_NAME_ENTRY_DN_2 = "entryDn2";



  DN getEntryDn1()
  {
    final DNArgument arg =
            (DNArgument)getArgumentParser().getNamedArgument(
                    MatchingRuleDemoCommandLineOptions.ARG_NAME_ENTRY_DN_1);
    return arg.getValue();
  }



  DN getEntryDn2()
  {
    final DNArgument arg =
            (DNArgument)getArgumentParser().getNamedArgument(
                    MatchingRuleDemoCommandLineOptions.ARG_NAME_ENTRY_DN_2);
    return arg.getValue();
  }



  MatchingRuleDemoCommandLineOptions(
          final ArgumentParser argumentParser)
          throws ArgumentException
  {
    super(argumentParser);

    /**
     * Add the argument whose parameter is the DN of an entry to be
     * compared with entry 2
     */
    String longIdentifier = MatchingRuleDemoCommandLineOptions.ARG_NAME_ENTRY_DN_1;
    boolean isRequired = true;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{distinguishedName}";
    String description = "The distinguished name to be compared with entry-2";
    final DNArgument entryDn1 =
            new DNArgument(null,longIdentifier,isRequired,maxOccurrences,valuePlaceholder,
                    description);
    argumentParser.addArgument(entryDn1);

    /**
     * Add the argument whose parameter is the DN of an entry to be
     * compared with entry 1. This argument is required, and it must
     * only appear once on the command lien.
     */
    longIdentifier = MatchingRuleDemoCommandLineOptions.ARG_NAME_ENTRY_DN_2;
    isRequired = true;
    description = "The distinguished name to be compared with entry-1";
    final DNArgument entryDn2 =
            new DNArgument(null,longIdentifier,isRequired,maxOccurrences,valuePlaceholder,
                    description);
    argumentParser.addArgument(entryDn2);
  }
}
