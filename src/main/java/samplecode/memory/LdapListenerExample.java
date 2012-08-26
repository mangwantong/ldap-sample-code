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
package samplecode.memory;

import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.Validator;
import com.unboundid.util.args.*;
import samplecode.config.ConfigStrategy;
import samplecode.util.SampleCodeCollectionUtils;
import samplecode.ldap.SupportedFeature;
import samplecode.ldap.SupportedFeatureException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.tools.AbstractTool;
import samplecode.tools.BasicToolCompletedProcessing;
import samplecode.tools.ToolCompletedProcessing;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * An example demonstrating how to create and use an in-memory directory
 * server for testing. The demonstration creates an in-memory directory
 * server using the naming context specified by the --namingContext
 * parameter, adds schema files specified by the --schemaFile command
 * line argument (which can occur multiple times), checks every control
 * OID specified with the --controlOID command line argument, and adds
 * entries that are found in the file named in the parameter to the
 * --ldifFile command line argument. <blockquote>
 * <p/>
 * <pre>
 * Demonstrates the use of the in-memory directory server.
 *
 * Usage:  LdapListenerExample {options}
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
 * -n, --namingContext {distinguishedName}
 *     The naming context to use when creating the in-memory directory server.
 *     This parameter is required and must be specified exactly once. The value
 *     specified must be in valid distinguished name syntax.
 * -c, --schemaFile {filename}
 *     The path to a file containing LDIF that should be loaded into the in-memory
 *     directory server schema. This parameter is optional and may be specified
 *     zero, one or more times. Any path name referenced the --schemaFile
 *     parameter must exist and be readable.
 * -l, --ldifFile {filename}
 *     A file containing entries in LDIF format to be loaded into the directory
 *     server.This parameter is required and may be specified one time.Any
 *     pathname referenced by the --ldifFile argument must exist and must be
 *     readable.
 * -d, --controlOID {object identifier}
 *     A series of dot-separated octets that are a control OID that should be
 *     checked against the root DSE. This parameter is optional, and may be
 *     specified zero, one, or more times.
 * -H, -?, --help
 *     Display usage information for this program.
 * </pre>
 * <p/>
 * </blockquote>
 *
 * @see LDAPConnectionPool
 * @see InMemoryDirectoryServer
 * @see LDAPCommandLineTool
 * @see <a href="http://su.pr/23n9ht">in-memory directory server</a>
 */
@Author("terry.gardner@unboundid.com") @Since("01-Nov-2011") @CodeVersion("1.9") @Launchable
public final class LdapListenerExample extends AbstractTool
{

  /**
   * Provides a service which displays an entry.
   */
  private class EntryDisplay
  {

    private EntryDisplay(final Entry entry)
    {
      this.entry = entry;
    }

    private String asString()
    {
      /*
      * Change this to display the entry as desired; this
      * construction just calls the toString() method of the Entry
      * class.
      */
      final StringBuilder builder = new StringBuilder("entry: ");
      builder.append(entry.toString());
      return builder.toString();
    }

    private final Entry entry;

  }

  /**
   * The long identifier of the argument which specifies the control
   * OID that the in-memory directory server demonstration will check
   * against the root DSE.
   */
  public static final String ARG_NAME_CONTROL_OID = "controlOID";

  public static final String ARG_NAME_GENERATE_OPERATIONAL_ATTRIBUTES =
          "generateOperationalAttributes";

  /**
   * The long identifier of the argument which specifies the file
   * containing entries in LDIF format that will be loaded into
   * directory server.
   */
  public static final String ARG_NAME_LDIF_FILE = "ldifFile";

  /**
   * The long identifier of the argument which specifies the naming
   * context to use in the in-memory directory server.
   */
  public static final String ARG_NAME_NAMING_CONTEXT = "namingContext";

  /**
   * The long identifier of the argument which specifies the schema
   * files to load in the in-memory directory server. This parameter
   * is optional and may be specified zero, one, or more times.
   */
  public static final String ARG_NAME_SCHEMA_FILE = "schemaFile";

  /**
   * The bind DN to use when the operator does not supply one on the
   * command line with the {@code --bindDn} argument. This bind DN is
   * used to interact with the in-memory directory server.
   */
  private static final String DEFAULT_BIND_DN = "cn=admin";

  /**
   * The bind password to use when the operator does not supply on on
   * the command line with the {@code --bindPassword} argument. This
   * password is used for the DEFAULT_BIND_DN when interacting with
   * the in-memory directory server.
   */
  private static final String DEFAULT_BIND_PASSWORD = "password";


  /**
   * Prepares {@code LdapListenerExample} for use by a client - the
   * provided output streams are used.
   */
  private LdapListenerExample(final OutputStream outStream, final OutputStream errStream)
  {
    super(outStream, errStream);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "LdapListenerExample.properties";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();

    if(isVerbose())
    {
      displayArguments();
      displayServerInformation();
    }

    /*
     * Retrieve from the argument parser the parameter to the --ldifFile
     * command line argument; the --ldifFile command line argument is
     * used to specify the file containing entries in LDIF format that
     * are to be added to the in-memory directory server. This file must
     * exist.
     */
    final File ldifFile = ldifFileArgument.getValue();
    if(!ldifFile.exists())
    {
      getLogger().fatal(String.format("%s does not exist.", ldifFile.getAbsolutePath()));
      return ResultCode.PARAM_ERROR;
    }

    ResultCode resultCode = ResultCode.SUCCESS;
    InMemoryDirectoryServer server = null;

    // a listener which displays an entry
    final InMemoryDirectoryServerListener displayEntryListener =
            new InMemoryDirectoryServerListener()
            {

              @Override
              public ResultCode entryAction(final LDAPConnectionPool ldapConnectionPool,
                      final Entry entry) throws LDAPException
              {
                final String msg = String.format("received entry: %s", entry);
                getLogger().info(msg);
                return ResultCode.SUCCESS;
              }

            };

    // a listener that adds the entry
    final InMemoryDirectoryServerListener addEntryListener =
            new InMemoryDirectoryServerListener()
            {

              @Override
              public ResultCode entryAction(final LDAPConnectionPool ldapConnectionPool,
                      final Entry entry) throws LDAPException
              {
                return ldapConnectionPool.add(entry).getResultCode();
              }

            };

    // get an array of distinguished names to use for the in-memory server
    final List<DN> dns = dnArgument.getValues();
    final int size = dns.size();
    final DN[] dnArray = dns.toArray(new DN[size]);

    // get the port from the --port command line option
    final int port = commandLineOptions.getPort();

    ConfigStrategy<InMemoryDirectoryServerConfig> configStrategy =
            InMemoryDirectoryServerConfigStrategy.newInstance(dnArray, port);
    InMemoryDirectoryServerConfig cfg = configStrategy.createConfiguration();

    if(operationalAttributeArgument.isPresent())
    {
      cfg.generateOperationalAttributes();
    }

    // Retrieve the bind DN and bind password from the command line options.
    final DN bindDn = commandLineOptions.getBindDn();
    String dn;
    if(bindDn != null)
    {
      dn = bindDn.toString();
    }
    else
    {
      dn = DEFAULT_BIND_DN;
    }
    String password = commandLineOptions.getBindPassword();
    if((password == null) || (password.length() == 0))
    {
      password = DEFAULT_BIND_PASSWORD;
    }

    try
    {
      cfg.addAdditionalBindCredentials(dn, password);
    }
    catch(final LDAPException ldapException)
    {
      getLogger().fatal(ldapException);
      return ldapException.getResultCode();
    }

    try
    {
      server = new InMemoryDirectoryServer(cfg);

      addListener(displayEntryListener);
      addListener(addEntryListener);

      // start the listener
      server.startListening();

      // get a pool of connection to the in-memory server
      ldapConnectionPool = server.getConnectionPool(5);

      // Read the entries from the specified LDIF file and execute the listeners
      getEntriesFromFile(ldifFile);

      // Retrieve the list of control OIDs that the in-memory server
      // demonstration will check as to whether the controlOIDs are
      // supported by the in-memory server.
      final List<String> controlOIDs = controlOidArgument.getValues();

      // see which request controls are supported by the server
      checkRequestControls(controlOIDs);

      // display all entries loaded into the server
      displayEntries();
    }
    catch(final LDAPException e)
    {
      getLogger().fatal(e);
      resultCode = e.getResultCode();
    }
    catch(final LDIFException e)
    {
      getLogger().fatal(e);
      resultCode = ResultCode.PARAM_ERROR;
    }
    catch(final IOException e)
    {
      getLogger().fatal(e);
      resultCode = ResultCode.PARAM_ERROR;
    }
    server.shutDown(true);
    return resultCode;
  }

  /**
   * Appends a {@code InMemoryDirectoryServerListener} to the list of
   * listeners. if the {@code l} (listener) parameter is {@code null},
   * no action is taken, and no exception is thrown.
   *
   * @param l A {@code InMemoryDirectoryServerListener}
   */
  private void addListener(final InMemoryDirectoryServerListener l)
  {
    if(l != null)
    {
      listeners.add(l);
    }
  }

  /**
   * Retrieves all entries from the file {@code ldifFile}. The
   * {@code entryAction} method is invoked for each entry.
   *
   * @param ldifFile A file containing entries in the form of LDIF.
   * @throws IOException   If an eror occurs reading the file.
   * @throws LDIFException If there is something wrong with the LDIF in the file.
   * @throws LDAPException If something goes wrong with handling the entries read
   *                       from the file.
   */
  private void getEntriesFromFile(final File ldifFile)
          throws IOException, LDIFException, LDAPException
  {
    // Read each entry from the file containing entries in LDIF format and invoke the
    // listeners for each entry.
    final LDIFReader ldifReader = new LDIFReader(ldifFile);
    while(true)
    {
      try
      {
        final Entry entry = ldifReader.readEntry();
        if(entry == null)
        {
          break;
        }
        for(final InMemoryDirectoryServerListener l : listeners)
        {
          l.entryAction(ldapConnectionPool, entry);
        }
      }
      catch(final LDIFException e)
      {
         if(! e.mayContinueReading())
         {
           getLogger().fatal(e);
           break;
         }
      }
    }
    ldifReader.close();
  }

  /**
   * For each request control in {@code requestControlOIDs}, validate
   * that the server supports each response control in the list, but
   * do not quit if one or more are not supported, just print a
   * helpful message and proceed.
   *
   * @param requestControlOIDs A list of request control object identifiers. This list
   *                           cannot be {@code null} but it can be empty.
   * @throws LDAPException If an error occurs during the validation process. This
   *                       exception is not thrown if a designated request control
   *                       is not supported.
   */
  public void checkRequestControls(final List<String> requestControlOIDs) throws LDAPException
  {
    Validator.ensureNotNull(requestControlOIDs);

    /*
    * Create the object that provides the services related to
    * checking whether a request control is supported.
    */
    final SupportedFeature supportedControl =
            SupportedFeature.newSupportedFeature(ldapConnectionPool);

    /*
    * Validate that the server supports each response control in the
    * list, but do not quit if one or more are not supported, just
    * print a helpful message and proceed.
    */
    for(final String oid : requestControlOIDs)
    {
      try
      {
        supportedControl.isControlSupported(oid);
        final StringBuilder builder = new StringBuilder("Request Control '");
        builder.append(oid);
        builder.append("' is supported by this server.");
        out(builder.toString());
      }
      catch(final SupportedFeatureException supportedControlException)
      {
        final StringBuilder builder = new StringBuilder("Request Control ");
        builder.append(oid);
        builder.append(" is not supported by this server ");
        builder.append(" (it might be a response control or just not supported at all).");
        out(builder.toString());
      }
    }
  }

  /**
   * Retrieves all entries that have been added to the in-memory
   * directory server database and displays them to the stdout.
   *
   * @throws LDAPException If the search fails.
   */
  public void displayEntries() throws LDAPException
  {
    final String baseObject = commandLineOptions.getBaseObject();
    final SearchScope scope = SearchScope.SUB;
    final Filter filter = Filter.create("(&)");
    List<String> requestedAttributes = commandLineOptions.getRequestedAttributes();
    final int size = requestedAttributes.size();
    final String[] requestedAttributesArray = requestedAttributes.toArray(new String[size]);
    final SearchRequest searchRequest =
            new SearchRequest(baseObject, scope, filter, requestedAttributesArray);
    final SearchResult searchResult = ldapConnectionPool.search(searchRequest);
    if((searchResult != null) && (searchResult.getEntryCount() > 0))
    {
      for(final SearchResultEntry entry : searchResult.getSearchEntries())
      {
        final EntryDisplay entryDisplay = new EntryDisplay(entry);
        out(entryDisplay.asString());
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    /*
     * Add the argument to the command line argument parser whose value
     * is the distinguished name to use as the naming context of the
     * directory server.
     */
    Character shortIdentifier = null;
    String longIdentifier = ARG_NAME_NAMING_CONTEXT;
    boolean isRequired = true;
    int maxOccurrences = 1;
    String valuePlaceholder = "{distinguishedName}";
    StringBuilder builder = new StringBuilder();
    builder.append("The naming context to use when creating ");
    builder.append("the in-memory directory server. This parameter is required ");
    builder.append("and must be specified exactly once. The value specified must be ");
    builder.append("in valid distinguished name syntax.");
    String description = builder.toString();
    DN[] defaultDnValues;
    try
    {
      defaultDnValues = new DN[]{new DN("dc=example,dc=com")};
    }
    catch(final LDAPException ldapException)
    {
      throw new ArgumentException(ldapException.getMessage());
    }
    dnArgument =
            new DNArgument(shortIdentifier, longIdentifier, isRequired, maxOccurrences,
                    valuePlaceholder, description, Arrays.asList(defaultDnValues));
    argumentParser.addArgument(dnArgument);

    /*
     * Add the argument whose parameter(s) are the schema files to load
     * for the in-memory directory server. This argument is specified by
     * --schemaFile DN and is optional, and can occur multiple times.
     */
    shortIdentifier = null;
    longIdentifier = ARG_NAME_SCHEMA_FILE;
    isRequired = false;
    maxOccurrences = 0;
    valuePlaceholder = "{filename}";
    builder.delete(0, builder.capacity());
    builder.append("The path to a file containing LDIF that should be loaded into the ");
    builder.append("in-memory directory server schema. This parameter is optional and ");
    builder.append("may be specified zero, one or more times. Any path name referenced ");
    builder.append("the --schemaFile parameter must exist and be readable.");
    description = builder.toString();
    schemaFileArgument =
            new FileArgument(shortIdentifier, longIdentifier, isRequired, maxOccurrences,
                    valuePlaceholder, description);
    argumentParser.addArgument(schemaFileArgument);

    /*
     * Add the argument whose parameter(s) are the LDIF files to load
     * for the in-memory directory server. This argument is specified by
     * --ldifFile DN and is required, and can occur one times.
     */
    shortIdentifier = null;
    longIdentifier = ARG_NAME_LDIF_FILE;
    isRequired = true;
    maxOccurrences = 1;
    valuePlaceholder = "{filename}";
    builder.delete(0, builder.capacity());
    builder.append("A file containing entries in LDIF format ");
    builder.append("to be loaded into the directory server.");
    builder.append("This parameter is required and may be specified one time.");
    builder.append("Any pathname referenced by the --ldifFile argument must exist ");
    builder.append("and must be readable.");
    description = builder.toString();
    ldifFileArgument =
            new FileArgument(shortIdentifier, longIdentifier, isRequired, maxOccurrences,
                    valuePlaceholder, description);
    argumentParser.addArgument(ldifFileArgument);

    /*
     * Add the command line argument to the argument parser whose
     * parameter is a dot-separated series of octets that represent a
     * control OID. This parameter is optional, and may be specified
     * zero, one or more times.
     */
    shortIdentifier = null;
    longIdentifier = ARG_NAME_CONTROL_OID;
    isRequired = false;
    maxOccurrences = 0;
    valuePlaceholder = "{object identifier}";
    builder.delete(0, builder.capacity());
    builder.append("A series of dot-separated octets that ");
    builder.append("are a control OID that should be checked ");
    builder.append("against the root DSE. This parameter ");
    builder.append("is optional, and may be specified zero, one, or more times.");
    description = builder.toString();
    controlOidArgument =
            new StringArgument(shortIdentifier, longIdentifier, isRequired, maxOccurrences,
                    valuePlaceholder, description);
    argumentParser.addArgument(controlOidArgument);

    /*
    * Add the command line argument to the argument parser whose
    * presence indicates the in-memory directory server should generate
    * operational attributes.
    */
    shortIdentifier = null;
    longIdentifier = ARG_NAME_GENERATE_OPERATIONAL_ATTRIBUTES;
    description = "causes the in-memory directory server to generate operational attributes";
    operationalAttributeArgument =
            new BooleanArgument(shortIdentifier, longIdentifier, description);
    argumentParser.addArgument(operationalAttributeArgument);
  }

  private BooleanArgument operationalAttributeArgument;

  private DNArgument dnArgument;

  private FileArgument ldifFileArgument;

  private FileArgument schemaFileArgument;

  private List<InMemoryDirectoryServerListener> listeners =
          SampleCodeCollectionUtils.newArrayList();

  private StringArgument controlOidArgument;

  /**
   * @param args list of arguments
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final LdapListenerExample ldapServer = new LdapListenerExample(outStream, errStream);
    final ResultCode resultCode = ldapServer.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(ldapServer, resultCode);
    completedProcessing.displayMessage(outStream, errStream);
  }

}
