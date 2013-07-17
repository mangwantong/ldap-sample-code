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

package samplecode.matchingrule;

import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.matchingrules.MatchingRule;
import com.unboundid.ldap.sdk.*;
import com.unboundid.util.Validator;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.DNArgument;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.cli.CommandLineOptions;
import samplecode.exception.AttributeNotSupportedException;
import samplecode.ldap.DefaultUnsolicitedNotificationHandler;
import samplecode.ldap.SupportedUserAttribute;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.tools.AbstractTool;
import samplecode.tools.BasicToolCompletedProcessing;
import samplecode.tools.ToolCompletedProcessing;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;


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
@CodeVersion("1.11")
@Launchable
public final class MatchingRuleDemo extends AbstractTool
  implements LdapExceptionListener, ObservedByLdapExceptionListener {

  /**
   * An exception which is thrown when two attributes values do not
   * match.
   */
  @SuppressWarnings("serial")
  private class AttributeValueMatchException extends Exception {

    private AttributeValueMatchException(final Attribute attributeEntry1,
                                         final Attribute attributeEntry2) {
      Validator.ensureNotNull(attributeEntry1,attributeEntry2);
      this.attributeEntry1 = attributeEntry1;
      this.attributeEntry2 = attributeEntry2;
    }



    /**
     * @return The attribute from entry 1 to be compared with the
     *         attribute of the same name/type from entry 2.
     */
    private Attribute getAttribute1() {
      return attributeEntry1;
    }



    /**
     * @return The attribute from entry 2 to be compared with the
     *         attribute of the same name/type from entry 1.
     */
    private Attribute getAttribute2() {
      return attributeEntry2;
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
   * Provides a service useful demonstrating the technique for comparing
   * the values of attributes using matching rules.
   */
  private class AttributeMatcher {

    /**
     * Provides a service that will use matching rules to match the two
     * attributes specified as {@code attributeEntry1} and
     * {@code attributeEntry2}.
     *
     * @param attributeEntry1
     *   An attribute from entry1.
     * @param attributeEntry2
     *   An attribute from entry2.
     */
    private AttributeMatcher(final Attribute attributeEntry1, final Attribute attributeEntry2) {
      Validator.ensureNotNull(attributeEntry1,attributeEntry2);
      this.attributeEntry1 = attributeEntry1;
      this.attributeEntry2 = attributeEntry2;
    }



    private void match() throws LDAPException, AttributeValueMatchException {
      // Get the raw values of the attributes:
      final ASN1OctetString[] strs1 = attributeEntry1.getRawValues();
      final ASN1OctetString[] strs2 = attributeEntry2.getRawValues();
      // Only compare the first values:
      final ASN1OctetString asnOctetString1 = strs1[0];
      final ASN1OctetString asnOctetString2 = strs2[0];
      // Get the matching rule and check for value match:
      final MatchingRule matchingRule = attributeEntry1.getMatchingRule();
      if(!matchingRule.valuesMatch(asnOctetString1,asnOctetString2)) {
        throw new AttributeValueMatchException(attributeEntry1,attributeEntry2);
      }
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
   * The long identifier of the argument whose parameter is the name of
   * entry number 1.
   */
  private static final String ARG_NAME_ENTRY_DN_1 = "entryDn1";


  /**
   * The long identifier of the argument whose parameter is the name of
   * entry number 2.
   */
  private static final String ARG_NAME_ENTRY_DN_2 = "entryDn2";



  /**
   * Prepares {@code MatchingRuleDemo} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  @SuppressWarnings("unused")
  public MatchingRuleDemo() {
    this(System.out,System.err);
  }



  /**
   * Prepares {@code MatchingRuleDemo} for use by a client - the
   * specified output streams are used.
   */
  private MatchingRuleDemo(final OutputStream outStream, final OutputStream errStream) {
    super(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName() {
    return "MatchingRuleDemo.properties";
  }



  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler() {
    return new DefaultUnsolicitedNotificationHandler(this);
  }



  /**
   * /** {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks() {
    introduction();

    /*
     * retrieve the first attribute name specified by the command line
     * argument '--attribute'. That command line argument may appear
     * multiple times however this demonstration only uses the first
     * occurrences.
     */
    final List<String> list = commandLineOptions.getRequestedAttributes();
    final int size = list.size();
    final String[] ary = new String[size];
    final String[] attributeNames = list.toArray(ary);
    final String attributeName = attributeNames[0];
    Validator.ensureFalse(attributeName.length() == 0,"This tool requires that the attribute" +
      " name " + "have a length greater than zero.");

    /*
     * Obtain a pool of connections to the LDAP server from the
     * LDAPCommandLineTool services,this requires specifying a
     * connection to the LDAP server,a number of initial connections
     * (--initialConnections) in the pool,and the maximum number of
     * connections (--maxConnections) that the pool should create.
     */
    try {
      ldapConnection = connectToServer();
      ldapConnectionPool = getLdapConnectionPool(ldapConnection);
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }

    /*
     * Check that supported attribute is supported by the directory
     * server to which this LDAP client is connected.
     */
    try {
      SupportedUserAttribute.getInstance().supported(ldapConnection,attributeName);
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    } catch(final AttributeNotSupportedException e) {
      final StringBuilder builder = new StringBuilder();
      builder.append(String.format("attribute '%s' is not supported " + "by this directory " +
        "server.",e.getAttributeName()));
      err(builder.toString());
      return ResultCode.PARAM_ERROR;
    }

    /*
     * Retrieve entry 1 and entry 2 from the directory:
     */
    final Entry entry1;
    final Entry entry2;
    try {
      /*
       * Create the search request
       */
      String baseObject = entryDN1.toString();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      if(filter == null) {
        final ArgumentParser argumentParser = getArgumentParser();
        final String msg = getRequiredArgumentsMessage(argumentParser);
        getLogger().fatal(msg);
        return ResultCode.PARAM_ERROR;
      }
      final String[] requestedAttributes = new String[]{attributeName};
      SearchRequest searchRequest =
        new SearchRequest(baseObject,scope,filter,requestedAttributes);
      searchRequest.setSizeLimit(commandLineOptions.getSizeLimit());
      searchRequest.setTimeLimitSeconds(commandLineOptions.getTimeLimit());
      entry1 = ldapConnectionPool.searchForEntry(searchRequest);

      /*
       * Repeat for entryDn2
       */
      baseObject = entryDN2.toString();
      searchRequest = new SearchRequest(baseObject,scope,filter,requestedAttributes);
      searchRequest.setSizeLimit(commandLineOptions.getSizeLimit());
      searchRequest.setTimeLimitSeconds(commandLineOptions.getTimeLimit());
      entry2 = ldapConnectionPool.searchForEntry(searchRequest);

      /*
       * Retrieve the attribute specified by the --attribute command
       * line argument from each entry and compare one to the other.
       */
      if((entry1 != null) && (entry2 != null)) {
        final Attribute attributeEntry1 = entry1.getAttribute(attributeName);
        final Attribute attributeEntry2 = entry2.getAttribute(attributeName);
        if(attributeEntry1 == null) {
          final StringBuilder builder = new StringBuilder();
          builder.append(String.format("attribute '%s' was not present in %s",attributeName,
            entryDN1));
          final String msg = builder.toString();
          err(msg);
          return ResultCode.PARAM_ERROR;
        }
        if(attributeEntry2 == null) {
          final StringBuilder builder = new StringBuilder();
          builder.append(String.format("attribute '%s' was not present in %s",attributeName,
            entryDN2));
          final String msg = builder.toString();
          err(msg);
          return ResultCode.PARAM_ERROR;
        }
        final AttributeMatcher attributeMatcher =
          new AttributeMatcher(attributeEntry1,attributeEntry2);
        attributeMatcher.match();
      } else {
        if(getLogger().isInfoEnabled()) {
          getLogger().info("no entries were returned.");
        }
      }
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    } catch(final AttributeValueMatchException attributeValueMatchException) {
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
   * <p/>
   * Creates the standard command line options processor, and sets up
   * the {@code --entryDn1} and {@code --entryDn2} command line
   * arguments.
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException {
    /**
     * Add the argument whose parameter is the DN of an entry to be
     * compared with entry 2
     */
    String longIdentifier = MatchingRuleDemo.ARG_NAME_ENTRY_DN_1;
    boolean isRequired = true;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{distinguishedName}";
    String description = "The distinguished name to be compared with entry-2";
    entryDN1 =
      new DNArgument(null,longIdentifier,isRequired,maxOccurrences,
        valuePlaceholder,description);
    argumentParser.addArgument(entryDN1);

    /**
     * Add the argument whose parameter is the DN of an entry to be
     * compared with entry 1. This argument is required, and it must
     * only appear once on the command lien.
     */
    longIdentifier = MatchingRuleDemo.ARG_NAME_ENTRY_DN_2;
    isRequired = true;
    description = "The distinguished name to be compared with entry-1";
    entryDN2 =
      new DNArgument(null,longIdentifier,isRequired,maxOccurrences,
        valuePlaceholder,description);
    argumentParser.addArgument(entryDN2);

    final String argName = CommandLineOptions.ARG_NAME_FILTER;
    final Argument filterArgument = argumentParser.getNamedArgument(argName);

    addRequiredArgumentSet(argumentParser,entryDN1,entryDN2,filterArgument);
  }



  private DNArgument entryDN1;


  private DNArgument entryDN2;



  /**
   * Launch the Matching Rules Demonstration application.
   * <p/>
   * The following example compares the {@code description} attribute
   * from two entries from the directory server running at
   * {@code ldap://localhost:1389}: <blockquote>
   * <p/>
   * <p/>
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
   * <p/>
   * <pre>
   * Provides a demonstration of the use of matching rules by comparing a
   * specified
   * attribute between two entries. The attribute to be compared is specified
   * by
   * --attribute, and the entries are specified by --entryDn1 and --entryDn2
   *
   * Usage:  MatchingRuleDemo {options}
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
   * --reportInterval {positive-integer}
   *     The report interval in milliseconds.
   * --reportCount {positive-integer}
   *     Specifies the maximum number of reports. This command line argument is
   *     applicable to tools that display repeated reports. The time between
   *     repeated reports is specified by the --reportInterval command line
   *     argument.
   * -a, --attribute {attribute name or type}
   *     The attribute used in the search request or other request. This
   * command
   *     line argument is not required, and can be specified multiple times. If
   * this
   *     command line argument is not specified, the value '*' is used.
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
   * --entryDn1 {DN}
   *     The distinguished name to be compared with entry2
   * --entryDn2 {DN}
   *     The distinguished name to be compared with entry1
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param args
   *   The list of command line arguments (less the JVM-specific
   *   arguments).
   */
  public static void main(final String... args) {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final MatchingRuleDemo matchingRuleDemo = new MatchingRuleDemo(outStream,errStream);
    final ResultCode resultCode = matchingRuleDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
      new BasicToolCompletedProcessing(matchingRuleDemo,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }

}
