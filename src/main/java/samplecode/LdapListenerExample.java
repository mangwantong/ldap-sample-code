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


import com.unboundid.ldap.listener.InMemoryDirectoryServer;
import com.unboundid.ldap.listener.InMemoryDirectoryServerConfig;
import com.unboundid.ldap.listener.InMemoryListenerConfig;
import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.schema.Schema;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.StringArgument;


import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * An example demonstrating how to create and use an in-memory directory
 * server for testing. The demonstration creates an in-memory directory
 * server using the naming context specified by the --namingContext
 * parameter, adds schema files specified by the --schemaFile command
 * line argument (which can occur multiple times), checks every control
 * OID specified with the --controlOID command line argument, and adds
 * entries that are found in the file named in the parameter to the
 * --ldifFile command line argument. <blockquote>
 * 
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
 * 
 * </blockquote>
 * 
 * @see LDAPConnectionPool
 * @see InMemoryDirectoryServer
 * @see LDAPCommandLineTool
 * @see <a href="http://su.pr/23n9ht">in-memory directory server</a>
 */
@Author("terry.gardner@unboundid.com")
@Since("01-Nov-2011")
@CodeVersion("1.8")
public final class LdapListenerExample
    extends LDAPCommandLineTool
{


  /**
   * Defines services which must be provided by a in-memory directory
   * server listener.
   */
  private interface InMemoryDirectoryServerListener
  {


    /**
     * This method is notified when a noteworthy event occurs with an
     * entry.
     * 
     * @param ldapCommandLineTool
     *          The object which provides services associated with
     *          running the program from the command line.
     * @param commandLineOptions
     *          The command line argument service provider.
     * @param ldapConnectionPool
     *          A valid {@code LDAPConnectionPool}.
     * @param entry
     *          A directory server entry.
     * @return The result code from any action that was taken by the
     *         method.
     * @throws LDAPException
     *           if an error occurs with an LDAP request or response or
     *           with the server.
     */
    ResultCode entryAction(LDAPCommandLineTool ldapCommandLineTool,
        CommandLineOptions commandLineOptions,
        LDAPConnectionPool ldapConnectionPool,Entry entry) throws LDAPException;
  }


  /**
   * Provides services necessary for the demonstration of the in-memory
   * directory server.
   */
  private static class InMemoryDirectoryServices
  {


    /**
     * Provides a service which displays an entry.
     */
    private class EntryDisplay
    {


      private final Entry entry;


      private EntryDisplay(
          final Entry entry)
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
    }


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
     * Create the services object using the provided parameters (none of
     * which may be [@code null}. The {@code commandLineOptions} object
     * is used to retrieve values specified by the operator on the
     * command line and the {@code ldapCommandLineTool} provides
     * services and a framework related to running the command from the
     * command line.
     * 
     * @param commandLineOptions
     *          The object that manages command line options.
     * @param ldapCommandLineTool
     *          The object that provides the command line framework.
     * @return a new and distinct {@code InMemoryDirectoryServices}
     *         object.
     * @throws LDAPException
     *           If errors related to an LDAP request, connection, DNs,
     *           or other LDAP-related exceptions occur.
     * @throws LDIFException
     *           When there is something wrong with the file containing
     *           LDIF, or the file does not exist, or something of that
     *           sort.
     * @throws IOException
     *           If an error occurs when reading the file containing
     *           LDIF
     */
    private static InMemoryDirectoryServices newLdapListenerExampleDemo(
        final LocalCommandLineOptions commandLineOptions,
        final LDAPCommandLineTool ldapCommandLineTool) throws LDAPException,
        LDIFException,IOException
    {
      Validator.ensureNotNull(commandLineOptions,ldapCommandLineTool);
      return new InMemoryDirectoryServices(commandLineOptions,
          ldapCommandLineTool);
    }


    private final CommandLineOptions commandLineOptions;


    private final LDAPCommandLineTool ldapCommandLineTool;


    private LDAPConnectionPool ldapConnectionPool;


    private final List<InMemoryDirectoryServerListener> listeners;


    private final Formatter loggingFormatter;


    private final DN namingContext;


    private final InMemoryDirectoryServer server;


    /**
     * Create the services object using the provided parameters (none of
     * which may be [@code null}. The {@code commandLineOptions} object
     * is used to retrieve values specified by the operator on the
     * command line and the {@code ldapCommandLineTool} provides
     * services and a framework related to running the command from the
     * command line.
     * 
     * @param commandLineOptions
     *          The object that manages command line options.
     * @param ldapCommandLineTool
     *          The object that provides the command line framework.
     * @throws LDAPException
     *           If errors related to an LDAP request, connection, DNs,
     *           or other LDAP-related exceptions occur.
     * @throws LDIFException
     *           When there is something wrong with the file containing
     *           LDIF, or the file does not exist, or something of that
     *           sort.
     * @throws IOException
     *           If an error occurs when reading the file containing
     *           LDIF
     */
    private InMemoryDirectoryServices(
        final LocalCommandLineOptions commandLineOptions,
        final LDAPCommandLineTool ldapCommandLineTool)
        throws LDAPException,LDIFException,IOException
    {

      loggingFormatter = new MinimalLogFormatter();

      this.commandLineOptions = commandLineOptions;
      this.ldapCommandLineTool = ldapCommandLineTool;
      listeners = new ArrayList<InMemoryDirectoryServerListener>();

      /*
       * Get the requested naming context.
       */
      namingContext = commandLineOptions.getNamingContext();

      /*
       * Create a configuration for the in-memory server.
       */
      InMemoryDirectoryServerConfig cfg;
      cfg = new InMemoryDirectoryServerConfig(namingContext);
      final int port = commandLineOptions.getPort();
      cfg.setListenerConfigs(InMemoryListenerConfig.createLDAPConfig("default",
          port));

      /**
       * Provide schema file loading services.
       */
      final class SchemaFiles
      {


        private final List<File> schemaFiles;


        /**
         * Constructs a {@code SchemaFiles} using the provided list of
         * schema files. The list of schema files may be {@code null}.
         * 
         * @param schemaFiles
         *          if not {@code null} a list of schema files.
         */
        private SchemaFiles(
            final List<File> schemaFiles)
        {
          this.schemaFiles = schemaFiles;
        }


        /**
         * retrieves a schema loaded with the schema files.
         * 
         * @return A schema suitable for loading into directory server.
         * @throws Exception
         *           If there are no schema files to load.
         */
        private Schema getSchema() throws Exception
        {
          if(schemaFiles == null)
          {
            throw new Exception("no schema files to load");
          }
          return Schema.getSchema(schemaFiles);
        }

      }

      /*
       * Get the list of schema files.
       */
      final SchemaFiles schFiles =
          new SchemaFiles(commandLineOptions.getSchemaFiles());
      try
      {
        cfg.setSchema(schFiles.getSchema());
      }
      catch(final Exception e)
      {
        // this block deliberately left empty.
      }

      /*
       * Retrieve the bind DN and bind password from the command line
       * options.
       */
      final DN bindDn = commandLineOptions.getBindDn();
      String dn;
      if(bindDn != null)
      {
        dn = bindDn.toString();
      }
      else
      {
        dn = InMemoryDirectoryServices.DEFAULT_BIND_DN;
      }
      String password = commandLineOptions.getBindPassword();
      if(password == null || password.length() == 0)
      {
        password = InMemoryDirectoryServices.DEFAULT_BIND_PASSWORD;
      }
      cfg.addAdditionalBindCredentials(dn,password);

      /*
       * Have in the in-memory server generate operational attributes.
       */
      cfg.generateOperationalAttributes();

      server = new InMemoryDirectoryServer(cfg);
      final LogRecord logRecord =
          new LogRecord(Level.INFO,"in-memory-server created.");
      final String msg = loggingFormatter.format(logRecord);
      ldapCommandLineTool.out(msg);
    }


    /**
     * Appends a {@code InMemoryDirectoryServerListener} to the list of
     * listeners. if the {@code l} (listener) parameter is {@code null},
     * no action is taken, and no exception is thrown.
     * 
     * @param l
     *          A {@code InMemoryDirectoryServerListener}
     */
    private void addListener(final InMemoryDirectoryServerListener l)
    {
      if(l != null)
      {
        listeners.add(l);
      }
    }


    /**
     * For each request control in {@code requestControlOIDs}, validate
     * that the server supports each response control in the list, but
     * do not quit if one or more are not supported, just print a
     * helpful message and proceed.
     * 
     * @param requestControlOIDs
     *          A list of request control object identifiers. This list
     *          cannot be {@code null} but it can be empty.
     * @throws LDAPException
     *           If an error occurs during the validation process. This
     *           exception is not thrown if a designated request control
     *           is not supported.
     */
    private void checkRequestControls(final List<String> requestControlOIDs)
        throws LDAPException
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
          ldapCommandLineTool.out(builder.toString());
        }
        catch(final SupportedFeatureException supportedControlException)
        {
          final StringBuilder builder = new StringBuilder("Request Control ");
          builder.append(oid);
          builder.append(" is not supported by this server ");
          builder
              .append(" (it might be a response control or just not supported at all).");
          ldapCommandLineTool.out(builder.toString());
        }
      }
    }


    /**
     * Retrieves all entries that have been added to the in-memory
     * directory server database and displays them to the stdout.
     * 
     * @throws LDAPException
     *           If the search fails.
     */
    private void displayEntries() throws LDAPException
    {
      final String baseObject = commandLineOptions.getBaseObject();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final SearchRequest searchRequest =
          new SearchRequest(baseObject,scope,filter,
              commandLineOptions.getRequestedAttributes());
      final SearchResult searchResult =
          ldapConnectionPool.search(searchRequest);
      if(searchResult != null && searchResult.getEntryCount() > 0)
      {
        for(final SearchResultEntry entry : searchResult.getSearchEntries())
        {
          final EntryDisplay entryDisplay = new EntryDisplay(entry);
          ldapCommandLineTool.out(entryDisplay.asString());
        }
      }
    }


    /**
     * Retrieves all entries from the file {@code ldifFile}. The
     * in-memory directory server
     * {@link InMemoryDirectoryServerListener#entryAction(LDAPCommandLineTool, CommandLineOptions, LDAPConnectionPool, Entry)}
     * method is invoked for each entry.
     * 
     * @param ldifFile
     *          A file containing entries in the form of LDIF.
     * @throws IOException
     *           If an eror occurs reading the file.
     * @throws LDIFException
     *           If there is something wrong with the LDIF in the file.
     * @throws LDAPException
     *           If something goes wrong with handling the entries read
     *           from the file.
     */
    private void getEntriesFromFile(final File ldifFile) throws IOException,
        LDIFException,LDAPException
    {

      Validator.ensureNotNull(ldifFile);

      if(ldapConnectionPool == null)
      {
        throw new IllegalStateException("The ldapConnectionPool was null.");
      }

      /*
       * Read each entry from the file containing entries in LDIF format
       * and invoke the listeners for each entry.
       */
      final LDIFReader ldifReader = new LDIFReader(ldifFile);
      while(true)
      {
        final Entry entry = ldifReader.readEntry();
        if(entry == null)
        {
          break;
        }
        for(final InMemoryDirectoryServerListener l : listeners)
        {
          l.entryAction(ldapCommandLineTool,commandLineOptions,
              ldapConnectionPool,entry);
        }
      }
      ldifReader.close();
    }


    /**
     * Start the server. When this method returns, the server is
     * listening on the port specified by the {@code --port} command
     * line option.
     * 
     * @return The in-memory directory server.
     * @throws LDAPException
     *           If an error occurs starting the server.
     */
    private InMemoryDirectoryServer startServer() throws LDAPException
    {

      /*
       * Startup the server.
       */
      server.startListening();

      /*
       * Get a connection pool from the in-memory server.
       */
      final int maxConnections = commandLineOptions.getMaxConnections();
      ldapConnectionPool = server.getConnectionPool(maxConnections);

      return server;
    }

  }


  private static class LocalCommandLineOptions
      extends CommandLineOptions
  {


    /**
     * The long identifier of the argument which specifies the control
     * OID that the in-memory directory server demonstration will check
     * against the root DSE.
     */
    public static final String ARG_NAME_CONTROL_OID;


    /**
     * The long identifier of the argument which specifies the file
     * containing entries in LDIF format that will be loaded into
     * directory server.
     */
    public static final String ARG_NAME_LDIF_FILE;


    /**
     * The long identifier of the argument which specifies the naming
     * context to use in the in-memory directory server.
     */
    public static final String ARG_NAME_NAMING_CONTEXT;


    /**
     * The long identifier of the argument which specifies the schema
     * files to load in the in-memory directory server. This parameter
     * is optional and may be specified zero, one, or more times.
     */
    public static final String ARG_NAME_SCHEMA_FILE;


    /**
     * The short identifier of the argument which specifies the control
     * OID that the in-memory directory server demonstration will check
     * against the root DSE. This command line is optional, and may be
     * specified zero, one, or more times.
     */
    public static final Character SHORT_ID_CONTROL_OID;


    /**
     * The short identifier of the argument which specifies the file
     * containing entries in LDIF format that will be loaded into
     * directory server. This parameter is required, the file to which
     * it refers must exist, and it can only be specified one time.
     */
    public static final Character SHORT_ID_LDIF_FILE;


    /**
     * The short identifier of the argument which specifies the naming
     * context to be used when creating the in-memory directory server.
     */
    public static final Character SHORT_ID_NAMING_CONTEXT;


    /**
     * The short identifier of the argument which specifies the file
     * containing schema entries in LDIF format that will be loaded into
     * directory server.
     */
    public static final Character SHORT_ID_SCHEMA_FILE;

    static
    {
      ARG_NAME_CONTROL_OID = "controlOID";
      ARG_NAME_LDIF_FILE = "ldifFile";
      ARG_NAME_NAMING_CONTEXT = "namingContext";
      ARG_NAME_SCHEMA_FILE = "schemaFile";
      SHORT_ID_CONTROL_OID = Character.valueOf('d');
      SHORT_ID_LDIF_FILE = Character.valueOf('l');
      SHORT_ID_NAMING_CONTEXT = Character.valueOf('n');
      SHORT_ID_SCHEMA_FILE = Character.valueOf('c');
    }


    private LocalCommandLineOptions(
        final ArgumentParser argumentParser)
        throws ArgumentException
    {
      super(argumentParser);

      /*
       * Add the argument to the command line argument parser whose
       * value is the distinguished name to use as the naming context of
       * the directory server.
       */
      Character shortIdentifier =
          LocalCommandLineOptions.SHORT_ID_NAMING_CONTEXT;
      String longIdentifier = LocalCommandLineOptions.ARG_NAME_NAMING_CONTEXT;
      boolean isRequired = true;
      int maxOccurrences = 1;
      String valuePlaceholder = "{distinguishedName}";
      final StringBuilder builder = new StringBuilder();
      builder.append("The naming context to use when creating ");
      builder
          .append("the in-memory directory server. This parameter is required ");
      builder
          .append("and must be specified exactly once. The value specified must be ");
      builder.append("in valid distinguished name syntax.");
      String description = builder.toString();
      DN[] defaultDnValues;
      try
      {
        defaultDnValues = new DN[]
        {
          new DN("dc=example,dc=com")
        };
      }
      catch(final LDAPException ldapException)
      {
        throw new ArgumentException(ldapException.getMessage());
      }
      final DNArgument dnArgument =
          new DNArgument(shortIdentifier,longIdentifier,isRequired,
              maxOccurrences,valuePlaceholder,description,
              Arrays.asList(defaultDnValues));
      argumentParser.addArgument(dnArgument);

      /*
       * Add the argument whose parameter(s) are the schema files to
       * load for the in-memory directory server. This argument is
       * specified by --schemaFile DN and is optional, and can occur
       * multiple times.
       */
      shortIdentifier = LocalCommandLineOptions.SHORT_ID_SCHEMA_FILE;
      longIdentifier = LocalCommandLineOptions.ARG_NAME_SCHEMA_FILE;
      isRequired = false;
      maxOccurrences = 0;
      valuePlaceholder = "{filename}";
      builder.delete(0,builder.capacity());
      builder
          .append("The path to a file containing LDIF that should be loaded into the ");
      builder
          .append("in-memory directory server schema. This parameter is optional and ");
      builder
          .append("may be specified zero, one or more times. Any path name referenced ");
      builder.append("the --schemaFile parameter must exist and be readable.");
      description = builder.toString();
      FileArgument fileArgument =
          new FileArgument(shortIdentifier,longIdentifier,isRequired,
              maxOccurrences,valuePlaceholder,description);
      argumentParser.addArgument(fileArgument);

      /*
       * Add the argument whose parameter(s) are the LDIF files to load
       * for the in-memory directory server. This argument is specified
       * by --ldifFile DN and is required, and can occur one times.
       */
      shortIdentifier = LocalCommandLineOptions.SHORT_ID_LDIF_FILE;
      longIdentifier = LocalCommandLineOptions.ARG_NAME_LDIF_FILE;
      isRequired = true;
      maxOccurrences = 1;
      valuePlaceholder = "{filename}";
      builder.delete(0,builder.capacity());
      builder.append("A file containing entries in LDIF format ");
      builder.append("to be loaded into the directory server.");
      builder
          .append("This parameter is required and may be specified one time.");
      builder
          .append("Any pathname referenced by the --ldifFile argument must exist ");
      builder.append("and must be readable.");
      description = builder.toString();
      fileArgument =
          new FileArgument(shortIdentifier,longIdentifier,isRequired,
              maxOccurrences,valuePlaceholder,description);
      argumentParser.addArgument(fileArgument);

      /*
       * Add the command line argument to the argument parser whose
       * parameter is a dot-separated series of octets that represent a
       * control OID. This parameter is optional, and may be specified
       * zero, one or more times.
       */
      shortIdentifier = LocalCommandLineOptions.SHORT_ID_CONTROL_OID;
      longIdentifier = LocalCommandLineOptions.ARG_NAME_CONTROL_OID;
      isRequired = false;
      maxOccurrences = 0;
      valuePlaceholder = "{object identifier}";
      builder.delete(0,builder.capacity());
      builder.append("A series of dot-separated octets that ");
      builder.append("are a control OID that should be checked ");
      builder.append("against the root DSE. This parameter ");
      builder
          .append("is optional, and may be specified zero, one, or more times.");
      description = builder.toString();
      final StringArgument stringArgument =
          new StringArgument(shortIdentifier,longIdentifier,isRequired,
              maxOccurrences,valuePlaceholder,description);
      argumentParser.addArgument(stringArgument);
    }


    /**
     * Retrieve the parameters to the command line argument
     * {@code --controlOID} in the form of a list of strings.
     * 
     * @return Parameters of {@code --controlOID}
     */
    private List<String> getControlOIDs()
    {
      final StringArgument stringArgument =
          (StringArgument)getArgumentParser().getNamedArgument(
              LocalCommandLineOptions.ARG_NAME_CONTROL_OID);
      return stringArgument.getValues();
    }


    /**
     * Retreive the file specified to the {@code --ldifFile} command
     * line argument.
     * 
     * @return The parameter provided to the {@code --ldifFile} command
     *         line argument.
     */
    private File getLdifFile()
    {
      final FileArgument fileArgument =
          (FileArgument)getArgumentParser().getNamedArgument(
              LocalCommandLineOptions.ARG_NAME_LDIF_FILE);
      return fileArgument.getValue();
    }


    /**
     * Retreive the string specified to the {@code --namingContext}
     * command line argument.
     * 
     * @return The parameter provided to the {@code --namingContext}
     *         command line argument.
     */
    private DN getNamingContext()
    {
      final DNArgument dnArgument =
          (DNArgument)getArgumentParser().getNamedArgument(
              LocalCommandLineOptions.ARG_NAME_NAMING_CONTEXT);
      return dnArgument.getValue();
    }


    /**
     * Retreive the files specified to the {@code --schemaFile} command
     * line argument.
     * 
     * @return The parameter(s) provided to the {@code --schemaFile}
     *         command line argument.
     */
    private List<File> getSchemaFiles()
    {
      final FileArgument fileArgument =
          (FileArgument)getArgumentParser().getNamedArgument(
              LocalCommandLineOptions.ARG_NAME_SCHEMA_FILE);
      return fileArgument.getValues();
    }

  }


  /**
   * This is the description of this tool; this is used for help output
   * and for other purposes.
   */
  public static final String TOOL_DESCRIPTION;


  /**
   * This is the name of this tool; this is used for help output and for
   * other purposes.
   */
  public static final String TOOL_NAME;


  /**
   * <ul>
   * <li>{@code --namingContext|-n} - the naming context to use for the
   * in-memory directory server.</li>
   * <li>{@code --schemaFile|-c filename} - a file containing schema to
   * load into the in-memory directory server. This parameter is
   * optional and can occur multiple times.</li>
   * <li>{@code --ldifFile|-l filename} - a file containing entries in
   * LDIF format to be added to the directory server database.</li>
   * <li>{@code --controlOID oid} - a request control OID</li>
   * <li>{@code --help|-H} - list the complete command line argument set
   * for the in-memory directory server demonstration tool</li>
   * </ul>
   * 
   * @param args
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final LdapListenerExample ldapServer =
        new LdapListenerExample(outStream,errStream);
    final ResultCode resultCode = ldapServer.runTool(args);
    final ToolCompletedProcessing completedProcessing =
        new BasicToolCompletedProcessing(ldapServer,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }


  /**
   * Initialize static fields.
   */
  static
  {
    TOOL_NAME = "LdapListenerExample";
    TOOL_DESCRIPTION =
        "An example demonstrating how to create and use an "
            + "in-memory directory server for testing. The demonstration creates an "
            + "in-memory directory server using the naming context specified by "
            + "the --namingContext parameter, adds schema files specified by "
            + "the --schemaFile command line argument (which can occur multiple times), "
            + "checks every control OID specified with the --controlOID "
            + "command line argument, and adds entries that are found in the "
            + "file named in the parameter to the --ldifFile final command line argument.";
  }


  private LocalCommandLineOptions commandLineOptions;


  /**
   * Prepares {@code LdapListenerExample} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public LdapListenerExample()
  {
    this(System.out,System.err);
  }


  /**
   * Prepares {@code LdapListenerExample} for use by a client - the
   * provided output streams are used.
   */
  private LdapListenerExample(
      final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser)
      throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = new LocalCommandLineOptions(argumentParser);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {

    /*
     * Retrieve from the argument parser the parameter to the --ldifFile
     * command line argument; the --ldifFile command line argument is
     * used to specify the file containing entries in LDIF format that
     * are to be added to the in-memory directory server. This file must
     * exist.
     */
    final File ldifFile = commandLineOptions.getLdifFile();
    if(!ldifFile.exists())
    {
      final StringBuilder builder = new StringBuilder();
      builder.append("The in-memory directory server dmeonstration tool ");
      builder.append("requires an existing file from which to load entries ");
      builder
          .append("into the in-memory directory server. The file specified '");
      builder.append(ldifFile.getAbsolutePath());
      builder.append("' does not exist.");
      err(builder.toString());
      return ResultCode.PARAM_ERROR;
    }

    ResultCode resultCode;
    InMemoryDirectoryServices inMemoryDirectoryServices = null;
    InMemoryDirectoryServer server = null;

    /*
     * A listener that display the entry.
     */
    final InMemoryDirectoryServerListener displayEntryListener =
        new InMemoryDirectoryServerListener()
        {


          @Override
          public ResultCode entryAction(
              final LDAPCommandLineTool ldapCommandLineTool,
              final CommandLineOptions commandLineOptions,
              final LDAPConnectionPool ldapConnectionPool,final Entry entry)
              throws LDAPException
          {
            Validator.ensureNotNull(ldapCommandLineTool,commandLineOptions,
                ldapConnectionPool,entry);
            final StringBuilder builder = new StringBuilder();
            builder.append("received entry: ");
            builder.append(entry.getDN());
            ldapCommandLineTool.out(builder.toString());
            return ResultCode.SUCCESS;
          }

        };

    final InMemoryDirectoryServerListener addEntryListener =
        new InMemoryDirectoryServerListener()
        {


          @Override
          public ResultCode entryAction(
              final LDAPCommandLineTool ldapCommandLineTool,
              final CommandLineOptions commandLineOptions,
              final LDAPConnectionPool ldapConnectionPool,final Entry entry)
              throws LDAPException
          {
            Validator.ensureNotNull(ldapCommandLineTool,commandLineOptions,
                ldapConnectionPool,entry);
            return ldapConnectionPool.add(entry).getResultCode();
          }

        };

    try
    {

      /*
       * Construct the demonstration object; the demonstration object
       * creates an in-memory directory server and populates it with
       * entries from an LDIF file.
       */
      inMemoryDirectoryServices =
          InMemoryDirectoryServices.newLdapListenerExampleDemo(
              commandLineOptions,this);
      inMemoryDirectoryServices.addListener(displayEntryListener);
      inMemoryDirectoryServices.addListener(addEntryListener);

      /*
       * Start the server listening.
       */
      server = inMemoryDirectoryServices.startServer();

      /*
       * Read the entries from the specified LDIF file and execute the
       * listeners.
       */
      inMemoryDirectoryServices.getEntriesFromFile(ldifFile);

      /*
       * Retrieve the list of control OIDs that the in-memory server
       * demonstration will check as to whether the controlOIDs are
       * supported by the in-memory server.
       */
      final List<String> controlOIDs = commandLineOptions.getControlOIDs();

      /*
       * See which request controls are supported by the server.
       */
      inMemoryDirectoryServices.checkRequestControls(controlOIDs);

      /*
       * Display all entries from the server.
       */
      inMemoryDirectoryServices.displayEntries();
    }
    catch(final LDAPException ldapException)
    {
      final StringBuilder builder =
          new StringBuilder("An LDAP Exception was detected: ");
      builder.append(ldapException.getResultCode());
      builder.append(ldapException.getExceptionMessage());
      final String matchedDn = ldapException.getMatchedDN();
      if(matchedDn != null)
      {
        builder.append(' ');
        builder.append(ldapException.getMatchedDN());
      }
      err(builder.toString());

      resultCode = ldapException.getResultCode();
    }
    catch(final LDIFException e)
    {
      e.printStackTrace();
      resultCode = ResultCode.PARAM_ERROR;
    }
    catch(final IOException ioException)
    {
      ioException.printStackTrace();
      resultCode = ResultCode.PARAM_ERROR;
    }
    finally
    {
      if(server != null)
      {
        server.shutDown(true);
      }
      resultCode = ResultCode.SUCCESS;
    }
    return resultCode;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return "Demonstrates the use of the in-memory directory server.";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return LdapListenerExample.TOOL_NAME;
  }
}
