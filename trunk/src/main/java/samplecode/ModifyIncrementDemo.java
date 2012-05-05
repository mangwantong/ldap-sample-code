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


package samplecode;


import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldap.sdk.controls.PostReadResponseControl;
import com.unboundid.ldap.sdk.controls.PreReadRequestControl;
import com.unboundid.ldap.sdk.controls.PreReadResponseControl;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.IntegerArgument;


import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.tools.AbstractTool;


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
 * 
 * <pre>
 * Provides a demonstration of the use of the modify-increment extension. The 
 * ModifyIncrementDemo requires the --entry argument and increments the attributes
 * specified by the --attribute command line arguments in that entry by the value
 * specified in the --incrementValue command line argument. 
 * If the --incrementValue command line argument is not present,
 * a default value is used.
 * 
 * Usage:  ModifyIncrementDemo {options}
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
 * -n, --incrementValue {integer}
 *     Specifies the increment, either positive or negative, to use in
 *     incrementing the value of an attribute. The attribute that is incremented
 *     is specified with the --attribute command line argument. This command line
 *     argument is optional, has a default value, and may be specified exactly one
 *     time.
 * -e, --entry {distinguishedName}
 *     Specifies the distinguished name of the entry (which must exist) whose
 *     attributes are to be incremented. This command line argument is required,
 *     has no default value, and may be specified exactly once.
 * -H, -?, --help
 *     Display usage information for this program.
 * 
 * ModifyIncrementDemo has completed processing. The result code was: 0 (success)
 * 
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 1, 2011")
@CodeVersion("1.1")
public final class ModifyIncrementDemo
        extends AbstractTool
{

  /**
   * Provides the services necessary to demonstrate the modify-increment
   * extension to the modify request.
   */
  private static class ModifyEntry
  {

    private void increment(final DN entryDn,final String attribute,final int incrementValue)
            throws LDAPException
    {
      Validator.ensureNotNull(entryDn,attribute);

      /*
       * Obtain a connection to the directory server.
       */
      final LDAPConnection ldapConnection = ldapCommandLineTool.getConnection();

      /*
       * Create the search request. The base object is the DN 'entryDn',
       * the scope and filter are taken from the command line arguments,
       * and the attribute is '1.1'. '1.1' is an OID that can never
       * match an attribute type, therefore no attributes are returned.
       */
      final String baseObject = entryDn.toString();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final SearchRequest searchRequest =
              new SearchRequest(baseObject,scope,filter,new String[]
              {
                SearchRequest.NO_ATTRIBUTES
              });

      /*
       * Search for the entry specified by the entryDn.
       */
      final SearchResult searchResult = ldapConnection.search(searchRequest);
      if(searchResult.getEntryCount() == 0)
      {

        /*
         * NB: This will not be reached if the base object did not
         * exist.
         */
        ldapCommandLineTool.out("no entries were returned from the search.");
        return;
      }

      /*
       * Create the modify request with the modify-increment extension.
       * This requires using the INCREMENT modification type and the
       * incrementValue specified on the command line.
       */
      final List<Modification> modifications = new ArrayList<Modification>();
      final Modification modification =
              new Modification(ModificationType.INCREMENT,attribute,
                      String.valueOf(incrementValue));
      modifications.add(modification);
      final ModifyRequest modifyRequest = new ModifyRequest(entryDn,modifications);

      final SupportedFeature supportedControl =
              SupportedFeature.newSupportedFeature(ldapConnection);

      /*
       * If the pre-read request control is supported by the server, add
       * the control to the modify request.
       */
      try
      {

        final String controlOID = PreReadRequestControl.PRE_READ_REQUEST_OID;
        supportedControl.isControlSupported(controlOID);

        /*
         * Create a pre-read request control to get the value of the
         * attribute before the modification; then add the control to
         * the modify request.
         */
        final boolean isCritical = true;
        final PreReadRequestControl control = new PreReadRequestControl(isCritical,attribute);
        modifyRequest.addControl(control);

      }
      catch(final SupportedFeatureException ex)
      {
        // The request control is not supported.
      }

      /*
       * If the post-read request control is supported by the server,
       * add the control to the modify request.
       */
      try
      {

        final String controlOID = PreReadRequestControl.PRE_READ_REQUEST_OID;
        supportedControl.isControlSupported(controlOID);

        /*
         * Create a post-read request control to get the value of the
         * attribute after the modification; then add the control to the
         * modify request.
         */
        final boolean isCritical = true;
        final PostReadRequestControl control = new PostReadRequestControl(isCritical,attribute);
        modifyRequest.addControl(control);

      }
      catch(final SupportedFeatureException ex)
      {
        // The request control is not supported.
      }

      /*
       * Transmit the modify request.
       */
      final LDAPResult ldapResult = ldapConnection.modify(modifyRequest);

      /*
       * Check for the pre-read response control and display the value
       * of the attribute before the modification occurred.
       */
      final PreReadResponseControl preReadResponseControl =
              PreReadResponseControl.get(ldapResult);
      if((preReadResponseControl != null) && preReadResponseControl.hasValue())
      {
        final Entry entry = preReadResponseControl.getEntry();
        if(entry != null)
        {
          final Attribute attr = entry.getAttribute(attribute);
          final StringBuilder builder = new StringBuilder();
          builder.append("Before modification the value of ");
          builder.append(attr.getBaseName());
          builder.append(" was ");
          builder.append(attr.getValue());
          builder.append(". The value of modify-increment is ");
          builder.append(incrementValue);
          builder.append(".");
          final String msg = builder.toString();
          ldapCommandLineTool.out(msg);
        }
      }

      /*
       * Check for the post-read response control and display the value
       * of the attribute before the modification occurred.
       */
      final PostReadResponseControl postReadResponseControl =
              PostReadResponseControl.get(ldapResult);
      if((postReadResponseControl != null) && postReadResponseControl.hasValue())
      {
        final Entry entry = postReadResponseControl.getEntry();
        if(entry != null)
        {
          final Attribute attr = entry.getAttribute(attribute);
          final StringBuilder builder = new StringBuilder();
          builder.append("After modification the value of ");
          builder.append(attr.getBaseName());
          builder.append(" is ");
          builder.append(attr.getValue());
          builder.append(". The value of modify-increment is ");
          builder.append(incrementValue);
          builder.append(".");
          final String msg = builder.toString();
          ldapCommandLineTool.out(msg);
        }
      }

    }



    private ModifyEntry(
            final LDAPCommandLineTool ldapCommandLineTool,
            final CommandLineOptions commandLineOptions)
    {
      Validator.ensureNotNull(ldapCommandLineTool,commandLineOptions);
      this.commandLineOptions = commandLineOptions;
      this.ldapCommandLineTool = ldapCommandLineTool;
    }



    private final CommandLineOptions commandLineOptions;



    private final LDAPCommandLineTool ldapCommandLineTool;
  }



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



  /**
   * The default value by which the attributes are incremented using the
   * modify-increment extension.
   */
  public static final Integer DEFAULT_INCREMENT_VALUE = Integer.valueOf(1);



  /**
   * The short identifier of the command line argument which is used to
   * specify the entry in which the attribute specified by the
   * --attribute command lien arguments are incremented.
   */
  public static final Character SHORT_ID_ENTRY = Character.valueOf('e');



  /**
   * The short identifier of the command line argument that is used to
   * specify the increment value used in the modify-increment
   * demonstration. This command line argument is optional, has a
   * default value, and may only be specified one time.
   */
  public static final Character SHORT_ID_INCREMENT_VALUE = Character.valueOf('n');



  /**
   * Execute the modify-increment demonstration.
   * 
   * @param args
   *          The command line arguments, less the JVM specific
   *          arguments.
   */
  public static void main(final String... args)
  {
    final OutputStream outStream = System.out;
    final OutputStream errStream = System.err;
    final ModifyIncrementDemo modifyIncrementDemo =
            new ModifyIncrementDemo(outStream,errStream);
    final String msg = modifyIncrementDemo.getToolDescription();
    modifyIncrementDemo.out(msg);
    final ResultCode resultCode = modifyIncrementDemo.runTool(args);
    if(resultCode != null)
    {
      final StringBuilder builder = new StringBuilder(modifyIncrementDemo.getToolName());
      builder.append(" has completed processing. The result code was: ");
      builder.append(resultCode);
      modifyIncrementDemo.out(builder.toString());
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);

    /*
     * Create the object which provides command line argument services.
     */
    commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);

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
    final IntegerArgument integerArgument =
            new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description,defaultValue);
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
    builder.delete(0,builder.capacity());
    builder.append("Specifies the distinguished name of the entry ");
    builder.append("(which must exist) whose attributes are to be incremented. ");
    builder.append("This command line argument is required, has no default value, ");
    builder.append("and may be specified exactly once.");
    description = builder.toString();
    final DNArgument dnArgument =
            new DNArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(dnArgument);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();

    /*
     * Retrieve the distinguished name parameter of the command line
     * argument.
     */
    final DNArgument dnArgument =
            (DNArgument)commandLineOptions.getArgumentParser().getNamedArgument(
                    ModifyIncrementDemo.ARG_NAME_ENTRY);
    final DN entryDn = dnArgument.getValue();

    /*
     * Retrieve the array of requested attributes from the parameter of
     * the command line argument(s).
     */
    final String[] requestedAttributes = commandLineOptions.getRequestedAttributes();

    /*
     * Retrieve the increment value from the parameter of the command
     * line argument.
     */
    final IntegerArgument integerArgument =
            (IntegerArgument)commandLineOptions.getArgumentParser().getNamedArgument(
                    ModifyIncrementDemo.ARG_NAME_INCREMENT_VALUE);
    final int incrementValue = integerArgument.getValue().intValue();

    final ModifyEntry modifyEntry = new ModifyEntry(this,commandLineOptions);
    ResultCode resultCode = null;
    for(final String attribute : requestedAttributes)
    {
      try
      {
        modifyEntry.increment(entryDn,attribute,incrementValue);
      }
      catch(final LDAPSearchException ldapSearchException)
      {
        resultCode = ldapSearchException.getResultCode();
      }
      catch(final LDAPException ldapException)
      {
        resultCode = ldapException.getResultCode();

      }
    }

    return resultCode;
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
    final StringBuilder builder = new StringBuilder();
    builder.append("Provides a demonstration of the use of the ");
    builder.append("modify-increment extension. The\n");
    builder.append(getToolName());
    builder.append(" requires the --entry argument and increments\n");
    builder.append("the attributes specified by the --attribute ");
    builder.append("command line arguments in that entry by the\n");
    builder.append("value specified in the --incrementValue command line argument.\n");
    builder.append("If the --incrementValue command line argument is not ");
    builder.append("present, a default value is used.\n");
    return builder.toString();
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return "ModifyIncrementDemo";
  }



  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }



  /**
   * Prepares {@code ModifyIncrementDemo} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public ModifyIncrementDemo()
  {
    this(System.out,System.err);
  }



  private ModifyIncrementDemo(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



  private CommandLineOptions commandLineOptions;

}
