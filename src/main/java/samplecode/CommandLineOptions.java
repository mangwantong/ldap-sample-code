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


import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.Validator;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.DNArgument;
import com.unboundid.util.args.FileArgument;
import com.unboundid.util.args.FilterArgument;
import com.unboundid.util.args.IntegerArgument;
import com.unboundid.util.args.ScopeArgument;
import com.unboundid.util.args.StringArgument;


import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.FileNotFoundExceptionEvent;
import samplecode.listener.FileNotFoundExceptionListener;
import samplecode.listener.IOExceptionEvent;


/**
 * Provides services related to managing command line options for
 * clients that use the LDAPCommandLineTool class. Default values for
 * command line arguments that are not required and not provided are
 * taken from the Java properties file
 * {@code "commandLineOptions.properties"} which must be located on the
 * {@code CLASSPATH}.
 * <p/>
 * When this class is instantiated, it creates the following command
 * line arguments:<blockquote>
 * 
 * <pre>
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
 * --bindWithDnRequiresPassword
 *     Indicates whether the SDK should allow simple bind operations that contain
 *     a bind DN but no password. Binds of this type may represent a security
 *     vulnerability in client applications because they may cause the client to
 *     believe that the user is properly authenticated when the server considers
 *     it to be an unauthenticated connection.
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
 * --usePropertiesFile {path-to-properties-file}
 *     The path to a file containing Java properties.
 * --useSchema
 *     Whether the LDAP SDK should attempt to use server schema information, for
 *     example, for matching rules.
 * --verbose
 *     Whether the tool should be verbose.
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 28, 2011")
@CodeVersion("2.0")
public class CommandLineOptions
{

  /**
   * The short identifier for the {@code --abandonOnTimeout} command
   * line option.
   */
  public static final Character ABANDON_ON_TIMEOUT_SHORT_IDENTIFIER = null;



  /**
   * The value place-holder for the {@code --introductionColumnWidth}
   * command line options.
   */
  public static final String ARG_INTRODUCTION_COLUMN_WIDTH_VALUE_PLACEHOLDER =
          "{integer-column-width}";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the LDAP SDK should abandon an operation if
   * the operation times out. This command line argument is optional and
   * can occur exactly once.
   */
  public static final String ARG_NAME_ABANDON_ON_TIMEOUT = "abandonOnTimeout";



  /**
   * The long identifier of the command line argument whose parameter is
   * the name or type of an attribute to retrieve. This command line
   * argument is optional and can occur multiple times.
   */
  public static final String ARG_NAME_ATTRIBUTE = "attribute";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the LDAP SDK should automatically
   * recconnect when a connection is lost. This command line argument is
   * optional and can occur exactly once.
   */
  public static final String ARG_NAME_AUTO_RECONNECT = "autoReconnect";



  /**
   * The long identifier of the command line argument whose parameter is
   * the base object used in searches and other operations where a
   * distinguished name is required.
   */
  public static final String ARG_NAME_BASE_OBJECT = "baseObject";



  /**
   * The long identifier of the command line argument whose value is the
   * distinguished name used to bind to directory server.
   */
  public static final String ARG_NAME_BIND_DN = "bindDn";



  /**
   * The long identifier of the command line argument whose value is the
   * bind password.
   */
  public static final String ARG_NAME_BIND_PASSWORD = "bindPassword";



  /**
   * The long identifier of the command line argument which is present
   * indicates that simple bind requests using a DN require a password.
   */
  public static final String ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD =
          "bindWithDnRequiresPassword";



  /**
   * The long identifier of the command line argument whose parameter is
   * the connect timeout in milliseconds. This command line argument is
   * not required, has a default value of 60 seconds and can occur
   * exactly one time.
   */
  public static final String ARG_NAME_CONNECT_TIMEOUT_MILLIS = "connectTimeoutMillis";



  /**
   * The long identifier of the command line argument whose parameter is
   * the filter used in searches.
   */
  public static final String ARG_NAME_FILTER = "filter";



  /**
   * The long identifier of the command line argument whose parameter is
   * the hostname or address of the server to which the LDAP SDK will
   * connection. This command line argument is optional and can occur
   * one time.
   */
  public static final String ARG_NAME_HOSTNAME = "hostname";



  /**
   * The long identifier of the command line argument whose parameter is
   * the number of initial connections to directory server used when
   * creating a connection pool. This parameter is not required, has a
   * default value, and may be specified exactly once.
   */
  public static final String ARG_NAME_INITIAL_CONNECTIONS = "initialConnections";



  /**
   * The long identifier of the command line argument whose parameter is
   * the length in characters of the introduction column.
   */
  public static final String ARG_NAME_INTRODUCTION_COLUMN_WIDTH = "introductionColumnWidth";



  /**
   * The long identifier of the command line argument whose parameter is
   * the maximum number of connections in connection pools. This
   * parameter has a default value, is not required, and can be
   * specified exactly one time.
   */
  public static final String ARG_NAME_MAX_CONNECTIONS = "maxConnections";



  /**
   * The long identifier of the command line argument whose parameter is
   * the maximum length of time in milliseconds that at operation should
   * be allowed to block, with 0 (zero) or less meaning no timeout is
   * enforced. This command line argument is optional and can occur
   * exactly one time.
   */
  public static final String ARG_NAME_MAX_RESPONSE_TIME_MILLIS = "maxResponseTimeMillis";



  /**
   * The long identifier of the command line argument whose parameter is
   * the number of threads to use for a tool. This command line argument
   * is optional and can occur exactly once.
   */
  public static final String ARG_NAME_NUM_THREADS = "numThreads";



  /**
   * The long identifier of the command line argument whose parameter is
   * the client-requested page size in simple paged request controls.
   * This parameter has a default value, is not required, and can be
   * specified exactly one time.
   */
  public static final String ARG_NAME_PAGE_SIZE = "pageSize";



  /**
   * The long identifier of the command line argument whose parameter is
   * the port to which the LDAP SDK will connect. This command line
   * argument is optional and can occur one time.
   */
  public static final String ARG_NAME_PORT = "port";



  /**
   * The long identifier of the command line argument whose parameter is
   * the maximum number of reports. This command line argument is
   * applicable to tools that have a repeating number of reports. The
   * time between reports of such tools is specified by the
   * --reportInterval command line argument. This argument is not
   * required, has a default value, and can be specified exactly one
   * time.
   */
  public static final String ARG_NAME_REPORT_COUNT = "reportCount";



  /**
   * The long identifier of the command line argument whose parameter is
   * the reporting interval in milliseconds. This command line argument
   * is optional and can occur one time.
   */
  public static final String ARG_NAME_REPORT_INTERVAL = "reportInterval";



  /**
   * The long identifier of the command line argument whose parameter is
   * the scope of a search request. This command line argument has a
   * default value, is optional and can occur exactly one time.
   */
  public static final String ARG_NAME_SCOPE = "scope";



  /**
   * The long identifier of the command line argument whose parameter is
   * the client requested size limit.
   */
  public static final String ARG_NAME_SIZE_LIMIT = "sizeLimit";



  /**
   * The long identifier of the command line argument whose parameter is
   * the client requested time limit.
   */
  public static final String ARG_NAME_TIME_LIMIT = "timeLimit";



  /**
   * The long identifier of the command line argument whose parameter is
   * the name of a properties file.
   */
  public static final String ARG_NAME_USE_PROPERTIES_FILE = "usePropertiesFile";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the LDAP SDK should try to use schema
   * information. This command line argument is optional and can occur
   * exactly once.
   */
  public static final String ARG_NAME_USE_SCHEMA = "useSchema";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the should be verbose. This command line
   * argument is optional and can occur exactly once.
   */
  public static final String ARG_NAME_VERBOSE = "verbose";



  /**
   * The value of the {@code abandonOnTimeout} property in the event
   * that:
   * <ul>
   * <li>The command line argument {@code --abandonOnTimeout} is not
   * present</li>
   * <li>There is no property named {@code abandonOnTimeout} in the
   * properties file</li>
   * <li>There is no properties file</li>
   * </ul>
   */
  public static final boolean DEFAULT_ABANDON_ON_TIMEOUT = true;



  /**
   * The default attribute value; used in the event that the command
   * line argument {@@code --attribute} is not provided.
   */
  public static final String DEFAULT_ATTRIBUTE_NAME = "*";



  /**
   * The default attribute value; used in the event that the command
   * line argument {@@code --attribute} is not provided, no
   * property exists, or the properties file cannot be loaded.
   */
  public static final String DEFAULT_BASE_OBJECT = "";



  /**
   * The default value of the {@code --connectTimeoutMillis} command
   * line argument; the default value is used if the command line
   * argument {@code --connectTimeoutMillis} is not present. This
   * command line argument's parameter is used as the connection
   * timeout.
   */
  public static final int DEFAULT_CONNECT_TIMEOUT_MILLIS = 60000;



  /**
   * The default value of the {@code --filter} command line argument;
   * the default value is used if the command line argument
   * {@code --filter} is not present. This command line argument's
   * parameter is used as the search filter.
   */
  public static final String DEFAULT_FILTER = "(&)";



  /**
   * The default value of the [@code --initialConnections} command line
   * option; the default value is used if the command line option
   * {@code --initialConnections} is not present. This value is used for
   * the default number of initial connections to directory server.
   */
  public static final int DEFAULT_INITIAL_CONNECTIONS = 2;



  /**
   * The default value of the [@code --maxConnections} command line
   * option; the default value is used if the command line option
   * {@code --maxConnections} is not present. This value is used for the
   * default maximum number of connections to directory server.
   */
  public static final int DEFAULT_MAX_CONNECTIONS = 3;



  /**
   * The default value of the {@code --maxResponseTimeMillis} command
   * line argument; used if the command line argument is not present.
   * This value is used to specify a client-requested maximum allowable
   * block time.
   */
  public static final Integer DEFAULT_MAX_RESPONSE_TIME_MILLIS = Integer.valueOf(0);



  /**
   * The default number of threads to use when no command line argument
   * {@code --numThreads} is provided.
   */
  public static final int DEFAULT_NUM_THREADS = 1;



  /**
   * The default value of the [@code --pageSize} command line option;
   * the default value is used if the command line option
   * {@code --pageSize} is not present. This value is used for the page
   * size of a simple paged search request control.
   */
  public static final int DEFAULT_PAGE_SIZE = 10;



  /**
   * The default value of the {@code --reportCount} command line
   * argument; the default value is used if the command line argument
   * {@code --reportCount} is not present. The parameter is used for
   * tools that use repeating reports.
   */
  public static final Integer DEFAULT_REPORT_COUNT = Integer.valueOf(Integer.MAX_VALUE);



  /**
   * The default value of the {@code --reportInterval} command line
   * argument; the default value is used if the command line argument
   * {@code --reportInterval} is not present. The parameter is used for
   * tools that display repeating reports.
   */
  public static final Integer DEFAULT_REPORT_INTERVAL = Integer.valueOf(1000);



  /**
   * The default value of the [@code --scope} command line option; the
   * default value is used if the command line option {@code --scope} is
   * not present. This value is used for the search scope.
   */
  public static final SearchScope DEFAULT_SEARCH_SCOPE = SearchScope.BASE;



  /**
   * The default value of the [@code --sizeLimit} command line option;
   * the default value is used if the command line option
   * {@code --sizeLimit} is not present. This value is used for the
   * search size limit.
   */
  public static final int DEFAULT_SIZE_LIMIT = 1;



  /**
   * The default value of the [@code --timeLimit} command line option;
   * the default value is used if the command line option
   * {@code --timeLimit} is not present. This value is used for the
   * search time limit.
   */
  public static final int DEFAULT_TIME_LIMIT = 10;



  /**
   * The default value of the {@code --introductionColumnWidth} command
   * line option.
   */
  public static final String INTRODUCTION_COLUMN_WIDTH_DEFAULT_VALUE = "72";



  /**
   * The description of the the {@code --introductionColumnWidth}
   * command line options.
   */
  public static final String INTRODUCTION_COLUMN_WIDTH_DESCRIPTION =
          "Specifies the maximum width of the introduction lines.";



  /**
   * The introduction column width command line option is not required.
   */
  public static final boolean INTRODUCTION_COLUMN_WIDTH_IS_REQUIRED = false;



  /**
   * The default value of the lower bound of the
   * {@code --introductionColumnWidth} command line option.
   */
  public static final String INTRODUCTION_COLUMN_WIDTH_LOWER_BOUND_DEFAULT_VALUE = "8";



  /**
   * The maximum number of occurrences of the
   * {@code --introductionColumnWith} command line option.
   */
  public final static int INTRODUCTION_COLUMN_WIDTH_MAX_OCCURENCES = 1;



  /**
   * The short identifier for the introduction column width argument
   */
  public final static Character INTRODUCTION_COLUMN_WIDTH_SHORT_IDENTIFIER = null;



  /**
   * The default value of the upper bound of the
   * {@code --introductionColumnWidth} command line option.
   */
  public static final String INTRODUCTION_COLUMN_WIDTH_UPPER_BOUND_DEFAULT_VALUE = "96";



  /**
   * The name of the property whose value is the description to use for
   * the abandonOnTime command line argument.
   */
  public static final String PROP_NAME_ABANDON_ON_TIMEOUT_DESCRIPTION =
          CommandLineOptions.ARG_NAME_ABANDON_ON_TIMEOUT + "Description";



  /**
   * The name of the property whose value is a sequence of attribute
   * names or types separated by commas.
   */
  public static final String PROP_NAME_ATTRIBUTE = CommandLineOptions.ARG_NAME_ATTRIBUTE;



  /**
   * The name of the property that can be used to specify the
   * description of the use schema command line argument.
   */
  public static final String PROP_NAME_USE_SCHEMA_DESCRIPTION =
          CommandLineOptions.ARG_NAME_USE_SCHEMA + "Description";



  /**
   * The name of the property that specifies the description to use for
   * the verbose argument.
   */
  public static final String PROP_NAME_VERBOSE_DESCRIPTION =
          CommandLineOptions.ARG_NAME_VERBOSE + "Description";



  /**
   * The name of the resource that contains properties for
   * {@code CommandLineOptions}.
   */
  public static final String PROPERTIES_RESOURCE_NAME = "commandLineOptions.properties";



  /**
   * The long identifier of the command line argument whose parameter is
   * the reporting interval in milliseconds. This command line argument
   * is optional and can occur one time.
   */
  public static final Character SHORT_ID_REPORT_INTERVAL = null;



  private static final String VERBOSE_DESCRIPTION =
          "If present, specifies that the tool should be verbose.";



  /**
   * Obtain an instance of the {@code CommandLineOptions} class.
   * 
   * @param argumentParser
   *          The argumentParser from the command line tool.
   * @return A CommandLineOptions object initialized with the
   *         {@code argumentParser} parameter.
   * @throws ArgumentException
   *           If an error occurs while creating or adding a command
   *           line argument.
   */
  public static CommandLineOptions newCommandLineOptions(final ArgumentParser argumentParser)
          throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    return new CommandLineOptions(argumentParser);
  }



  /**
   * Adds each of the specified arguments to the {@code argumentParser},
   * thereby making the arguments available to command line clients.
   * 
   * @param arguments
   *          A list of arguments to be added (cannot be {@code null}.
   * @throws ArgumentException
   *           if an argument cannot be added to the
   *           {@code argumentParser}.
   */
  public void addArguments(final Argument... arguments) throws ArgumentException
  {
    Validator.ensureNotNull(arguments);
    for(final Argument argument : arguments)
    {
      if(argument != null)
      {
        argumentParser.addArgument(argument);
      }
    }
  }



  /**
   * @return Indicates whether the SDK should allow simple bind
   *         operations that contain a bind DN but no password. Binds of
   *         this type may represent a security vulnerability in client
   *         applications because they may cause the client to believe
   *         that the user is properly authenticated when the server
   *         considers it to be an unauthenticated connection.
   */
  public boolean bindDnRequiresPassword()
  {
    final BooleanArgument booleanArgument =
            getNamedArgument(CommandLineOptions.ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD);
    return booleanArgument.isPresent();
  }



  /**
   * Retrieves the value of the command line argument named by the
   * {@code longIdentifier} parameter.
   * 
   * @param longIdentifier
   *          The long identifier of a command line option.
   * @return The value of the command line argument named by the
   *         {@code longIdentifier}.
   */

  public Object get(final String longIdentifier)
  {
    if(longIdentifier == null)
    {
      throw new NullPointerException();
    }
    Object get = null;
    final Argument argument = argumentParser.getNamedArgument(longIdentifier);
    if(argument != null)
    {
      if(argument.getClass() == StringArgument.class)
      {
        get = ((StringArgument)argument).getValue();
      }
      else if(argument.getClass() == DNArgument.class)
      {
        get = ((DNArgument)argument).getValue();
      }
      else if(argument.getClass() == IntegerArgument.class)
      {
        get = ((IntegerArgument)argument).getValue();
      }
      else if(argument.getClass() == BooleanArgument.class)
      {
        get = Boolean.valueOf(((BooleanArgument)argument).isPresent());
      }
      else
      {
        final StringBuilder builder = new StringBuilder(argument.getClass().toString());
        builder.append(" is not supported by the get() method.");
        throw new UnsupportedOperationException(builder.toString());
      }
    }
    return get;
  }



  /**
   * Whether the {@code --abandonOnTimeout} command line option is
   * present.
   * 
   * @return Whether the {@code --abandonOnTimeout} command line option
   *         is present.
   */
  public boolean getAbandonOnTimeout()
  {
    return argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_ABANDON_ON_TIMEOUT)
            .isPresent();
  }



  /**
   * Retrieve the argument parser with which this command line options
   * object was initialized.
   * 
   * @return The argument parser.
   */
  public ArgumentParser getArgumentParser()
  {
    return argumentParser;
  }



  /**
   * Whether the {@code --autoReconnect} command line option is present.
   * The {@code --autoReconnect} command line option controls whether
   * the LDAP SDK should reconnect automatically when a connection is
   * lost.
   * 
   * @return Whether the {@code --autoReconnect} command line option is
   *         present.
   */
  public boolean getAutoReconnect()
  {
    final BooleanArgument arg =
            (BooleanArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_AUTO_RECONNECT);
    return Boolean.valueOf(arg.isPresent());
  }



  /**
   * Retrieves the value of the base object as specified by the
   * {@code --baseObject} command line option.
   * 
   * @return The value of the command line argument named by the
   *         {@code --baseObject} command line option.
   * @throws LDAPException
   */
  public String getBaseObject() throws LDAPException
  {
    final StringArgument arg =
            (StringArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_BASE_OBJECT);
    return arg.getValue();
  }



  /**
   * Retrieves the value of the bindDn as specified by the
   * {@code --bindDn} command line option.
   * 
   * @return The value of the command line argument named by the
   *         {@code --bindDn} command line option.
   */
  public final DN getBindDn()
  {
    DN bindDn = null;
    final DNArgument arg =
            (DNArgument)argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_BIND_DN);
    if((arg != null) && arg.isPresent())
    {
      bindDn = arg.getValue();
    }
    return bindDn;
  }



  /**
   * Retrieves the value of the bind password as specified by the
   * {@code --bindPassword} command line option.
   * 
   * @return The value of the command line argument named by the
   *         {@code --bindPassword} command line option.
   */
  public final String getBindPassword()
  {
    String bindPassword = "";
    final StringArgument arg =
            (StringArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_BIND_PASSWORD);
    if((arg != null) && arg.isPresent())
    {
      bindPassword = arg.getValue();
    }
    return bindPassword;
  }



  /**
   * @return the value of the command line argument
   *         {@code --bindPasswordFile} , or {@code null} if the
   *         argument was not supplied on the command line.
   */
  public final File getBindPasswordFile()
  {
    return ((FileArgument)argumentParser.getNamedArgument("bindPasswordFile")).getValue();
  }



  /**
   * Retrieves the parameter provided to the
   * {@code --connectTimeoutMillis} command line argument.
   * 
   * @return connect timeout in milliseconds.
   */
  public Object getConnectTimeoutMillis()
  {
    final IntegerArgument integerArgument =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_CONNECT_TIMEOUT_MILLIS);
    return integerArgument.getValue();
  }



  /**
   * Retrieve the parameter to the {@code --filter} command line option.
   * 
   * @return The search filter.
   */
  public Filter getFilter()
  {
    final FilterArgument arg =
            (FilterArgument)argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_FILTER);
    return arg.getValue();
  }



  /**
   * Retrieve the parameters to the {@code --filter} command line
   * option.
   * 
   * @return The list of search filters.
   */
  public List<Filter> getFilters()
  {
    final FilterArgument arg =
            (FilterArgument)argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_FILTER);
    return arg.getValues();
  }



  /**
   * Retrieves the value of the {@code --hostname} command line option.
   * 
   * @return The hostname or IP address where a directory server is
   *         expected to be listening for connections.
   */
  public final String getHostname()
  {
    String hostname = "localhost";
    final StringArgument arg =
            (StringArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_HOSTNAME);
    if((arg != null) && arg.isPresent())
    {
      hostname = arg.getValue();
    }
    return hostname;
  }



  /**
   * Retrieve the number of initial connections to the directory server
   * from the {@code --initialConnections} command line option. The
   * {@code --initialConnections} command line option has a default
   * value.
   * 
   * @return The number of initial connections to the connection pool
   *         specified on the command line or the default value if
   *         {@code --initialConnections} is found on the command line.
   */
  public int getInitialConnections()
  {
    int initialConnections = 1;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_INITIAL_CONNECTIONS);
    if((arg != null) && arg.isPresent())
    {
      initialConnections = arg.getValue().intValue();
    }
    return initialConnections > 0 ? initialConnections : 1;
  }



  /**
   * Provides access to the value of the introduction column width as
   * specified by the {@code --introductionColumnWidth} command line
   * option.
   * 
   * @return introduction column width
   */
  public int getIntroductionColumnWidth()
  {
    int value = 0;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_INTRODUCTION_COLUMN_WIDTH);
    if((arg != null) && arg.isPresent())
    {
      value = arg.getValue();
    }
    return value;
  }



  /**
   * Retrieve the maximum number of connections to the directory server
   * from the {@code --maxConnections} command line option. The
   * {@code --maxConnections} command line option has a default value.
   * 
   * @return The maximum number of connections to the connection pool
   *         specified on the command line or the default value if
   *         {@code --maxConnections} is found on the command line.
   */
  public int getMaxConnections()
  {
    int maxConnections = 2;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_MAX_CONNECTIONS);
    if((arg != null) && arg.isPresent())
    {
      maxConnections = arg.getValue().intValue();
    }
    return maxConnections > 0 ? maxConnections : 1;
  }



  /**
   * Retrieves the maximum response time in milliseconds (the parameter
   * to the {@code --maxResponseTimeMillis} command line argument.
   * Specifies the maximum length of time in milliseconds that an
   * operation should be allowed to block while waiting for a response
   * from the server. A value of zero indicates that there should be no
   * timeout.
   * 
   * @return The maximum allowable response time in milliseconds.
   */
  public int getMaxResponseTimeMillis()
  {
    int maxResponseTimeMillis = CommandLineOptions.DEFAULT_MAX_RESPONSE_TIME_MILLIS.intValue();
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_MAX_RESPONSE_TIME_MILLIS);
    if((arg != null) && arg.isPresent())
    {
      maxResponseTimeMillis = arg.getValue().intValue();
    }
    return maxResponseTimeMillis;
  }



  /**
   * Gets the identifier named by {@code longIdentifier} from the
   * argument parser. The longIdentifier argument is not permitted to be
   * {@code null}.
   * <p/>
   * Usage:
   * 
   * <pre>
   * 
   * IntegerArgument portArg = getNamedArgument(&quot;port&quot;);
   * int port;
   * if(portArg != null &amp;&amp; portArg.isPresent())
   * {
   *   port = portArg.getValue();
   * }
   * else
   * {
   *   port = 389;
   * }
   * </pre>
   * 
   * @param longIdentifier
   *          the long identifier, for example, {@code "port"}.
   * 
   * @return the argument associated with the long identifier
   */
  @SuppressWarnings("unchecked")
  public <T extends Argument> T getNamedArgument(final String longIdentifier)
  {
    Validator.ensureNotNull(longIdentifier);
    return (T)argumentParser.getNamedArgument(longIdentifier);
  }



  /**
   * Retrieves the number of threads as specified by the
   * {@code --numThreads} command line option.
   * 
   * @return The value of the command line argument named by the
   *         {@code --numThreads} command line option.
   */
  public int getNumThreads()
  {
    int numThreads = CommandLineOptions.DEFAULT_NUM_THREADS;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_NUM_THREADS);
    if((arg != null) && arg.isPresent())
    {
      numThreads = arg.getValue().intValue();
    }
    return numThreads;
  }



  /**
   * Retrieve the page size to use for simple paged results. This
   * parameter is specified by the {@code --pageSize} command line
   * argument.
   * 
   * @return the page size to use for simple page results.
   */
  public int getPageSize()
  {
    int pageSize = 10;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_PAGE_SIZE);
    if((arg != null) && arg.isPresent())
    {
      pageSize = arg.getValue().intValue();
    }
    return pageSize;
  }



  /**
   * Retrieve the port from the {@code --port} or {@code -p} command
   * line option. The port command line option has a default value.
   * 
   * @return The port specified on the command line or the default value
   *         if neither {@code --port} or {@code -p} is found on the
   *         command line.
   */
  public int getPort()
  {
    int port = 10;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_PORT);
    if((arg != null) && arg.isPresent())
    {
      port = arg.getValue().intValue();
    }
    return port;
  }



  /**
   * Creates a {@link Properties} object using the resource name
   * returned by {@link CommandLineOptions#getPropertiesResourceName()}.
   * 
   * @return a properties object.
   */
  public Properties getProperties()
  {
    final PropertiesFile propertiesFile = newPropertiesFile(getPropertiesResourceName());
    final FileNotFoundExceptionListener fileNotFoundExceptionListener =
            new FileNotFoundExceptionListener()
            {

              @Override
              public void fileNotFound(final FileNotFoundExceptionEvent event)
              {
                logger.severe(event.getFileNotFoundException().getMessage());
              }



              @Override
              public void ioExceptionOccurred(final IOExceptionEvent ioExceptionEvent)
              {
                logger.severe(ioExceptionEvent.getIoException().getMessage());
              }

            };
    propertiesFile.addFileNotFoundExceptionListener(fileNotFoundExceptionListener);
    return propertiesFile.getProperties();
  }



  /**
   * retrieves the value of the {@code --useProertiesFile} argument, if
   * present, otherwise {@code null}.
   * 
   * @return properties filename.
   */
  public String getPropertiesFile()
  {
    String propertiesFile = null;
    final Argument argument =
            argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_USE_PROPERTIES_FILE);
    if(argument.isPresent())
    {
      propertiesFile = ((StringArgument)argument).getValue();
    }
    return propertiesFile;
  }



  /**
   * @return the name of the resource that contains properties
   */
  public String getPropertiesResourceName()
  {
    return propertiesResourceName;
  }



  /**
   * Retrieve the parameter specified to the [@code --reporCount}
   * command line argument. This parameter is the maximum number of
   * reports, applicable to a tool that uses repeating reports. The time
   * interval between reports is specified by the
   * {@code --reportInterval} command line argument.
   * 
   * @return the maximum number of reports.
   */
  public int getReportCount()
  {
    int reportCount = Integer.MAX_VALUE;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_REPORT_COUNT);
    if((arg != null) && arg.isPresent())
    {
      reportCount = arg.getValue().intValue();
    }
    return reportCount;
  }



  /**
   * Retrieve the report interval in milliseconds.
   * 
   * @return report interval in milliseconds.
   */
  public int getReportInterval()
  {
    int reportInterval = CommandLineOptions.DEFAULT_REPORT_INTERVAL.intValue();
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_REPORT_INTERVAL);
    if((arg != null) && arg.isPresent())
    {
      reportInterval = arg.getValue().intValue();
    }
    return reportInterval;
  }



  /**
   * Retrieve the attributes requested from the {@code --attribute}
   * command line option. The {@code --attribute} command line option
   * has a default value, is not required, and can be specified multiple
   * times.
   * <p>
   * The default value(s) are taken from the
   * {@code commandLineOptions.properties} file.
   * 
   * @return The attributes requested as provided by the command line
   *         option {@code --attribute}, or a default value is the
   *         {@code --attribute} command line option is not present.
   */
  public String[] getRequestedAttributes()
  {
    final StringArgument arg =
            (StringArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_ATTRIBUTE);
    String[] requestedAttributes;
    if(arg.isPresent())
    {
      requestedAttributes = new String[0];
      final List<String> listOfArgumentValues = arg.getValues();
      requestedAttributes = listOfArgumentValues.toArray(new String[0]);

    }
    else
    {
      requestedAttributes = new String[]
      {
        CommandLineOptions.DEFAULT_ATTRIBUTE_NAME
      };
    }
    return requestedAttributes;
  }



  /**
   * Retrieve the value of the parameter to the {@code --scope} command
   * line option.
   * 
   * @return the search scope.
   */
  public SearchScope getSearchScope()
  {
    SearchScope searchScope = SearchScope.SUB;
    final ScopeArgument arg =
            (ScopeArgument)argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_SCOPE);
    if((arg != null) && arg.isPresent())
    {
      searchScope = arg.getValue();
    }
    return searchScope;
  }



  /**
   * Retrieves the value of the search size limit as specified by the
   * {@code --sizeLimit} command line option.
   * 
   * @return The value of the command line argument named by the
   *         {@code --sizeLimit} command line option.
   */
  public int getSizeLimit()
  {
    int sizeLimit = 1;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_SIZE_LIMIT);
    if((arg != null) && arg.isPresent())
    {
      sizeLimit = arg.getValue().intValue();
    }
    return sizeLimit;
  }



  /**
   * Retrieves the value of the search time limit as specified by the
   * {@code --timeLimit} command line option.
   * 
   * @return The value of the command line argument named by the
   *         {@code --timeLimit} command line option.
   */
  public int getTimeLimit()
  {
    int timeLimit = 10;
    final IntegerArgument arg =
            (IntegerArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_TIME_LIMIT);
    if((arg != null) && arg.isPresent())
    {
      timeLimit = arg.getValue().intValue();
    }
    return timeLimit;
  }



  /**
   * Whether the {@code --useSchema} command line option is present.
   * 
   * @return Whether the {@code --useSchema} command line option is
   *         present.
   */
  public boolean getUseSchema()
  {
    final BooleanArgument arg =
            (BooleanArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_USE_SCHEMA);
    return arg.isPresent();
  }



  /**
   * Returns a {@link String} representation of the value of a property
   * specified by {@code propertyName} or {@code defaultValue} if the
   * property cannot be found.
   * <p>
   * For example, is a properties file contains: <blockquote>
   * 
   * <pre>
   * hostname = ldap.example.com
   * </pre>
   * </blockquote> and the {@code getValue} method is invoked thus:
   * 
   * <blockquote>
   * 
   * <pre>
   * String portString = getValue(&quot;port&quot;,&quot;389&quot;);
   * </pre>
   * 
   * </blockquote>
   * 
   * {@code portString} will contain {@code "389"}.
   * 
   * @param propertyName
   *          the name of a property in a Java properties fileNot
   *          permitted to be {@code null}.
   * 
   * @param defaultValue
   *          the value of the property to return in the event the
   *          property specified by the {@code propertyName} parameter
   *          is not present in the properties file.Not permitted to be
   *          {@code null}.
   * 
   * @param properties
   *          the properties object from which values are taken. Not
   *          permitted to be {@code null}.
   * 
   * @return a {@link String} representation of the value of the
   *         property whose key is specified by the {@code propertyName}
   *         parameter if that property is present in the properties
   *         file. If the property is not present in the properties
   *         file, the {@code defaultValue} is returned.
   */
  public String getValue(final String propertyName,final String defaultValue,
          final Properties properties)
  {
    Validator.ensureNotNull(propertyName,properties,defaultValue);
    String value = properties.getProperty(propertyName);
    if(value == null)
    {
      value = defaultValue;
    }
    return value;
  }



  /**
   * Whether the {@code --verbose} command line option is present.
   * 
   * @return Whether the {@code --verbose} command line option is
   *         present.
   */
  public boolean isVerbose()
  {
    final BooleanArgument arg =
            (BooleanArgument)argumentParser
                    .getNamedArgument(CommandLineOptions.ARG_NAME_VERBOSE);
    return arg.isPresent();
  }



  /**
   * Creates the argument that specifies whether operations should be
   * abandoned when they timeout, for example, <blockquote>
   * 
   * <pre> CommandName --abandonOnTime</pre>
   * 
   * </blockquote>
   * 
   * @param properties
   *          the properties which contain the default value - not
   *          permitted to be {@code null}.
   * 
   * @return a {@link BooleanArgument} that allows the operator to
   *         specify whether operations should be abandoned when they
   *         timeout. Optional.
   * 
   * @throws ArgumentException
   */
  public BooleanArgument newAbandonOnTimeoutArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    /*
     * Create the argument whose parameter is whether the LDAP SDK
     * should abandon an operation that has timed out. This argument is
     * optional, and can be specified exactly one time.
     */
    final Character shortIdentifier = CommandLineOptions.ABANDON_ON_TIMEOUT_SHORT_IDENTIFIER;
    final String longIdentifier = CommandLineOptions.ARG_NAME_ABANDON_ON_TIMEOUT;
    return new BooleanArgument(shortIdentifier,longIdentifier,getValue(
            CommandLineOptions.PROP_NAME_ABANDON_ON_TIMEOUT_DESCRIPTION,"",properties));
  }



  /**
   * Creates the argument that specifies an attribute, for example,
   * <blockquote>
   * 
   * <pre> CommandName --attribute uid --attribute cn</pre>
   * 
   * </blockquote>
   * 
   * @param properties
   *          the properties which contain the default attribute names -
   *          not permitted to be {@code null}.
   * 
   * @return a {@link StringArgument} that allows the operator to
   *         specify attribute names and types. May occur multiple
   *         times.
   * 
   * @throws ArgumentException
   */
  public StringArgument newAttributeArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    /*
     * Get the list of attributes from the properties, if present. The
     * list of attributes is given as a sequence of strings wherein each
     * list item is separated by a comma. TODO: Use the Apache
     * Configuration Libraries.
     */
    List<String> attributes;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_ATTRIBUTE);
    if(prop != null)
    {
      attributes = stringToList(prop);
    }
    else
    {
      attributes = Arrays.asList(CommandLineOptions.DEFAULT_ATTRIBUTE_NAME);
    }

    final String description =
            getValue(CommandLineOptions.PROP_NAME_ATTRIBUTE,
                    "The attribute used in the search request or other request. "
                            + "This command line argument "
                            + "is not required, and can be specified "
                            + "multiple times. If this command line "
                            + "argument is not specified, the value '*' is used.",properties);
    final Character shortIdentifier = Character.valueOf('a');
    final String longIdentifier = CommandLineOptions.ARG_NAME_ATTRIBUTE;
    final boolean isRequired = false;
    final int maxOccurrences = 0;
    final String valuePlaceholder = "{attribute name or type}";
    return new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,attributes);
  }



  /**
   * Constructs a new {@code LDAPConnectionOptions} object with
   * parameters set to values specified by command line argument
   * parameters.
   * <p>
   * Usage example:
   * <p>
   * <blockquote>
   * 
   * <pre>
   * commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);
   * LDAPConnection ldapConnection = new LDAPConnection();
   * LDAPConnectionOptions connectionOptions = commandLineOptions.newLDAPConnectionOptions();
   * ldapConnection.setConnectionOptions(connectionOptions);
   * </pre>
   * 
   * </blockquote>
   * 
   * @return a new {@code LDAPConnectionOptions} object.
   */
  public LDAPConnectionOptions newLDAPConnectionOptions()
  {
    final LDAPConnectionOptions ldapConnectionOptions = new LDAPConnectionOptions();

    /*
     * Indicates whether the LDAP SDK should attempt to abandon any
     * request for which no response is received in the maximum response
     * timeout period.
     */
    ldapConnectionOptions.setAbandonOnTimeout(getAbandonOnTimeout());

    /*
     * Indicates whether associated connections should attempt to
     * automatically reconnect to the target server if the connection is
     * lost. Note that this option will not have any effect on pooled
     * connections because defunct pooled connections will be replaced
     * by newly-created connections rather than attempting to
     * re-establish the existing connection.
     */
    ldapConnectionOptions.setAutoReconnect(getAutoReconnect());

    /*
     * set the flag that indicates whether the SDK should allow simple
     * bind operations that contain a bind DN but no password. Binds of
     * this type may represent a security vulnerability in client
     * applications because they may cause the client to believe that
     * the user is properly authenticated when the server considers it
     * to be an unauthenticated connection.
     */
    ldapConnectionOptions.setBindWithDNRequiresPassword(bindDnRequiresPassword());

    /*
     * Specifies whether to try to use schema information when reading
     * data from the server (e.g., to select the appropriate matching
     * rules for the attributes included in a search result entry).
     */
    ldapConnectionOptions.setUseSchema(getUseSchema());

    /*
     * Specifies the maximum length of time in milliseconds that an
     * operation should be allowed to block while waiting for a response
     * from the server. A value of zero indicates that there should be
     * no timeout.
     */
    final int responseTimeout = getMaxResponseTimeMillis();
    ldapConnectionOptions.setResponseTimeoutMillis(responseTimeout);

    return ldapConnectionOptions;
  }



  /**
   * @return the argument to the command line parser whose parameter is
   *         whether the LDAP SDK should try to use schema information,
   *         for example, to determine matching rules. This argument is
   *         optional, and can be specified exactly one time.
   */
  public BooleanArgument newUseSchemaArgument(final Properties properties)
          throws ArgumentException
  {
    /*
     * Add the argument to the command line parser whose parameter is
     * whether the LDAP SDK should try to use schema information, for
     * example, to determine matching rules. This argument is optional,
     * and can be specified exactly one time.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_USE_SCHEMA;
    final String description =
            getValue(CommandLineOptions.PROP_NAME_USE_SCHEMA_DESCRIPTION,
                    "Whether the LDAP SDK should attempt to use server schema "
                            + "information, for example, for matching rules.",properties);
    return new BooleanArgument(shortIdentifier,longIdentifier,description);
  }



  /**
   * @return an optional argument used to specify whether the tool
   *         should be verbose.
   */
  public BooleanArgument newVerboseArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    /*
     * Add the argument to the command line parser whose parameter is
     * whether the LDAP SDK should try to use schema information, for
     * example, to determine matching rules. This argument is optional,
     * and can be specified exactly one time.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_VERBOSE;
    final String description =
            getValue(CommandLineOptions.PROP_NAME_VERBOSE_DESCRIPTION,
                    CommandLineOptions.VERBOSE_DESCRIPTION,properties);
    return new BooleanArgument(shortIdentifier,longIdentifier,description);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String.format("CommandLineOptions [argumentParser=%s]",argumentParser);
  }



  private BooleanArgument newAutoReconnectArgument(final Properties properties)
          throws ArgumentException
  {

    /*
     * Create the argument whose parameter is whether the LDAP SDK
     * should automatically reconnect when a connection is lost. This
     * argument is optional, and can be specified exactly one time.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_AUTO_RECONNECT;
    final String description =
            "Whether the LDAP SDK should automatically reconnect when a connection is lost.";
    return new BooleanArgument(shortIdentifier,longIdentifier,description);
  }



  private StringArgument newBaseObjectArgument(final Properties properties)
          throws ArgumentException
  {

    String baseObject;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_BASE_OBJECT);
    if(prop != null)
    {
      baseObject = prop;
    }
    else
    {
      baseObject = CommandLineOptions.DEFAULT_BASE_OBJECT;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the base object to use in a search. This argument is optional,
     * and can be specified zero, one, or more times. This command line
     * argument has no default value.
     */
    final Character shortIdentifier = Character.valueOf('b');
    final String longIdentifier = CommandLineOptions.ARG_NAME_BASE_OBJECT;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{distinguishedName}";
    final String description = "The base object used in the search request.";
    return new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,baseObject);

  }



  private BooleanArgument newBindWithDnRequiresPasswordArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);
    /*
     * Add the command line argument whose presence indicates whether
     * the SDK should allow simple bind operations that contain a bind
     * DN but no password.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD;
    final String description =
            "Indicates whether the SDK should allow simple bind "
                    + "operations that contain a bind DN but no "
                    + "password. Binds of this type may represent "
                    + "a security vulnerability in client applications "
                    + "because they may cause the client to believe "
                    + "that the user is properly authenticated when "
                    + "the server considers it to be an unauthenticated connection.";
    return new BooleanArgument(shortIdentifier,longIdentifier,description);
  }



  private IntegerArgument newConnectTimeoutMillisArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    final int connectTimeoutMillis;
    final String prop =
            properties.getProperty(CommandLineOptions.ARG_NAME_CONNECT_TIMEOUT_MILLIS);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_CONNECT_TIMEOUT_MILLIS;
      }
      connectTimeoutMillis = value;
    }
    else
    {
      connectTimeoutMillis = CommandLineOptions.DEFAULT_CONNECT_TIMEOUT_MILLIS;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the connection timeout in milliseconds. This argument is
     * optional, and can be specified exactly one time. This command
     * line argument has a default value of
     * CommandLineOptions.DEFAULT_CONNECT_TIMEOUT_MILLIS.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_CONNECT_TIMEOUT_MILLIS;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{connect-timeout-millis-integer}";
    final String description =
            "Specifies the maximum length of time in milliseconds that "
                    + "a connection attempt should be allowed to continue before "
                    + "giving up. A value of zero indicates that there should "
                    + "be no connect timeout.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(connectTimeoutMillis));
  }



  private FilterArgument newFilterArgument(final Properties properties)
          throws ArgumentException,LDAPException
  {
    Validator.ensureNotNull(properties);

    final Filter filterDefaultValue;
    final String filterAsString = properties.getProperty(CommandLineOptions.ARG_NAME_FILTER);
    if(filterAsString != null)
    {
      filterDefaultValue = Filter.create(filterAsString);
    }
    else
    {
      filterDefaultValue = Filter.create(CommandLineOptions.DEFAULT_FILTER);
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the filter to use in a search. This parameter is optional, and
     * may be specified zero, more of more times.
     */
    final Character shortIdentifier = Character.valueOf('f');
    final String longIdentifier = CommandLineOptions.ARG_NAME_FILTER;
    final boolean isRequired = false;
    final int maxOccurrences = 0;
    final String valuePlaceholder = "{filter}";
    final String description = "The search filter used in the search request.";
    return new FilterArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,filterDefaultValue);
  }



  private IntegerArgument newInitialConnectionsArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultInitialConnections;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_INITIAL_CONNECTIONS);
    if(prop != null)
    {
      try
      {
        defaultInitialConnections = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        defaultInitialConnections = CommandLineOptions.DEFAULT_INITIAL_CONNECTIONS;
      }
    }
    else
    {
      defaultInitialConnections = CommandLineOptions.DEFAULT_INITIAL_CONNECTIONS;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the number of initial connections to establish in the connection
     * pool.
     */
    final Character shortIdentifier = Character.valueOf('i');
    final String longIdentifier = CommandLineOptions.ARG_NAME_INITIAL_CONNECTIONS;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{positiveInteger}";
    final String description =
            "The number of initial connections to establish to directory "
                    + "server when creating the connection pool.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(defaultInitialConnections));
  }



  private IntegerArgument newIntroductionColumnWidthArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);
    final int lowerBound =
            Integer.parseInt(getValue(CommandLineOptions.ARG_NAME_INTRODUCTION_COLUMN_WIDTH,
                    CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_LOWER_BOUND_DEFAULT_VALUE,
                    properties));
    final int upperBound =
            Integer.parseInt(getValue(CommandLineOptions.ARG_NAME_INTRODUCTION_COLUMN_WIDTH,
                    CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_UPPER_BOUND_DEFAULT_VALUE,
                    properties));
    final int defaultValue =
            Integer.parseInt(getValue(CommandLineOptions.ARG_NAME_INTRODUCTION_COLUMN_WIDTH,
                    CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_DEFAULT_VALUE,properties));
    return new IntegerArgument(CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_SHORT_IDENTIFIER,
            CommandLineOptions.ARG_NAME_INTRODUCTION_COLUMN_WIDTH,
            CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_IS_REQUIRED,
            CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_MAX_OCCURENCES,
            CommandLineOptions.ARG_INTRODUCTION_COLUMN_WIDTH_VALUE_PLACEHOLDER,
            CommandLineOptions.INTRODUCTION_COLUMN_WIDTH_DESCRIPTION,lowerBound,upperBound,
            defaultValue);
  }



  private IntegerArgument newMaxConnectionsArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultMaxConnections;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_MAX_CONNECTIONS);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_MAX_CONNECTIONS;
      }
      defaultMaxConnections = value;
    }
    else
    {
      defaultMaxConnections = CommandLineOptions.DEFAULT_MAX_CONNECTIONS;
    }

    /*
     * Add to the argument parser the command line argument whose
     * parameter is the maximum length of time in milliseconds that an
     * operation should be allowed to block, with 0 or less meaning no
     * timeout is enforced. This command line argument is optional and
     * has a default value of zero.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_MAX_CONNECTIONS;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{max-response-time-in-milliseconds}";
    final String description =
            "The maximum length of time in milliseconds that an "
                    + "operation should be allowed to block, with 0 or less meaning no "
                    + "timeout is enforced. This command line argument is optional and "
                    + "has a default value of zero.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(defaultMaxConnections));
  }



  private IntegerArgument newMaxResponseTimeMillisArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultMaxConnections;
    final String prop =
            properties.getProperty(CommandLineOptions.ARG_NAME_MAX_RESPONSE_TIME_MILLIS);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_MAX_RESPONSE_TIME_MILLIS;
      }
      defaultMaxConnections = value;
    }
    else
    {
      defaultMaxConnections = CommandLineOptions.DEFAULT_MAX_RESPONSE_TIME_MILLIS;
    }

    /*
     * Add to the argument parser the command line argument whose
     * parameter is the maximum length of time in milliseconds that an
     * operation should be allowed to block, with 0 or less meaning no
     * timeout is enforced. This command line argument is optional and
     * has a default value of zero.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_MAX_RESPONSE_TIME_MILLIS;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{max-response-time-in-milliseconds}";
    final String description =
            "The maximum length of time in milliseconds that an "
                    + "operation should be allowed to block, with 0 or less meaning no "
                    + "timeout is enforced. This command line argument is optional and "
                    + "has a default value of zero.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(defaultMaxConnections));
  }



  private IntegerArgument newNumThreadsArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultNumThreads;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_NUM_THREADS);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_NUM_THREADS;
      }
      defaultNumThreads = value;
    }
    else
    {
      defaultNumThreads = CommandLineOptions.DEFAULT_NUM_THREADS;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the number of threads to use. This argument is optional, and can
     * be specified exactly one time. This command line argument has a
     * default value of DEFAULT_NUM_THREADS.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_NUM_THREADS;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{number-of-threads}";
    final String description =
            "Specifies the number of threads to use when running the application.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,defaultNumThreads);
  }



  private IntegerArgument newPageSizeArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultPageSize;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_PAGE_SIZE);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_PAGE_SIZE;
      }
      defaultPageSize = value;
    }
    else
    {
      defaultPageSize = CommandLineOptions.DEFAULT_PAGE_SIZE;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the page size
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_PAGE_SIZE;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{positiveInteger}";
    final String description = "The search page size";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(defaultPageSize));
  }



  private PropertiesFile newPropertiesFile(final String name)
  {
    return name == null ? null : PropertiesFile.of(name);
  }



  private IntegerArgument newReportCountArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultReportCount;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_REPORT_COUNT);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_REPORT_COUNT;
      }
      defaultReportCount = value;
    }
    else
    {
      defaultReportCount = CommandLineOptions.DEFAULT_REPORT_COUNT;
    }

    /*
     * The command line argument whose parameter is the maximum number
     * of reports.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_REPORT_COUNT;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{positive-integer}";
    final String description =
            "Specifies the maximum number of reports. This command line "
                    + "argument is applicable to tools that display repeated "
                    + "reports. The time between repeated reports is specified by "
                    + "the --reportInterval command line argument.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,defaultReportCount);
  }



  private IntegerArgument newReportIntervalArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultReportInterval;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_REPORT_INTERVAL);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_REPORT_INTERVAL;
      }
      defaultReportInterval = value;
    }
    else
    {
      defaultReportInterval = CommandLineOptions.DEFAULT_REPORT_INTERVAL;
    }

    /*
     * Add to the argument parser the command line argument whose
     * parameter is the report interval in milliseconds.
     */
    final Character shortIdentifier = CommandLineOptions.SHORT_ID_REPORT_INTERVAL;
    final String longIdentifier = CommandLineOptions.ARG_NAME_REPORT_INTERVAL;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{positive-integer}";
    final String description = "The report interval in milliseconds.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,defaultReportInterval);
  }



  private ScopeArgument newScopeArgument(final Properties properties) throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    /*
     * Add the argument to the command line parser whose parameter is
     * the search scope.
     */
    final Character shortIdentifier = Character.valueOf('s');
    final String longIdentifier = CommandLineOptions.ARG_NAME_SCOPE;
    final boolean isRequired = false;
    final String valuePlaceholder = "{searchScope}";
    final String description =
            "The scope of the search request; allowed values are BASE, ONE, and SUB";
    return new ScopeArgument(shortIdentifier,longIdentifier,isRequired,valuePlaceholder,
            description);
  }



  private IntegerArgument newSizeLimitArgumentArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultSizeLimit;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_SIZE_LIMIT);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_SIZE_LIMIT;
      }
      defaultSizeLimit = value;
    }
    else
    {
      defaultSizeLimit = CommandLineOptions.DEFAULT_SIZE_LIMIT;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the search size limit.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_SIZE_LIMIT;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{positiveInteger}";
    final String description =
            "The client-request maximum number of results "
                    + "which are returned to the client. If the number of entries "
                    + "which match the search parameter is greater than the "
                    + "client-requested size limit or the server-imposed size limit "
                    + "a SIZE_LIMIT_EXCEEDED code is returned in the result code in the "
                    + "search response.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(defaultSizeLimit));
  }



  private IntegerArgument newTimeLimitArgumentArgument(final Properties properties)
          throws ArgumentException
  {
    Validator.ensureNotNull(properties);

    int defaultTimeLimit;
    final String prop = properties.getProperty(CommandLineOptions.ARG_NAME_TIME_LIMIT);
    if(prop != null)
    {
      int value;
      try
      {
        value = Integer.parseInt(prop);
      }
      catch(final NumberFormatException numberFormatException)
      {
        value = CommandLineOptions.DEFAULT_TIME_LIMIT;
      }
      defaultTimeLimit = value;
    }
    else
    {
      defaultTimeLimit = CommandLineOptions.DEFAULT_TIME_LIMIT;
    }

    /*
     * Add the argument to the command line parser whose parameter is
     * the search size limit.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_TIME_LIMIT;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{positiveInteger}";
    final String description =
            "The client-request maximum time that the directory server will "
                    + "devote to processing the search request. If the "
                    + "client-requested time limit or the server-imposed time limit "
                    + "a TIME_LIMIT_EXCEEDED code is returned in the result code in the "
                    + "search response.";
    return new IntegerArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description,Integer.valueOf(defaultTimeLimit));
  }



  private StringArgument newUsePropertiesFileArgument(final Properties properties)
          throws ArgumentException
  {

    /*
     * Create the argument whose parameter is a properties filename.
     */
    final Character shortIdentifier = null;
    final String longIdentifier = CommandLineOptions.ARG_NAME_USE_PROPERTIES_FILE;
    final boolean isRequired = false;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{path-to-properties-file}";
    final String description = "The path to a file containing Java properties.";
    return new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description);
  }



  private List<String> stringToList(final String prop)
  {
    Validator.ensureNotNull(prop);
    final List<String> stringToList = SampleCodeCollectionUtils.newArrayList();
    final String[] components = prop.split(",");
    for(final String component : components)
    {
      stringToList.add(component);
    }
    return stringToList;
  }



  /**
   * Preconditions:
   * <p/>
   * The {@code argumentParser} is not permitted to be {@code null}.
   * 
   * @param argumentParser
   *          parses command line arguments
   * @throws ArgumentException
   *           if an argument cannot be parsed or added.
   */
  public CommandLineOptions(
          final ArgumentParser argumentParser)
          throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    this.argumentParser = argumentParser;
    final Properties properties = getProperties();

    /*
     * Indicates whether the LDAP SDK should attempt to abandon any
     * request for which no response is received in the maximum response
     * timeout period.
     */
    final BooleanArgument abandonOnTimeoutArgument = newAbandonOnTimeoutArgument(properties);


    /*
     * The width of the introduction text columns.
     */
    final IntegerArgument introductionColumnWidthArgument =
            newIntroductionColumnWidthArgument(properties);


    final StringArgument attributeArgument = newAttributeArgument(properties);
    final BooleanArgument autoReconnectArgument = newAutoReconnectArgument(properties);
    final StringArgument baseObjectArgument = newBaseObjectArgument(properties);
    final IntegerArgument connectTimeoutMillisArgument =
            newConnectTimeoutMillisArgument(properties);
    FilterArgument filterArgument;
    try
    {
      filterArgument = newFilterArgument(properties);
    }
    catch(final LDAPException exception)
    {
      System.err.println(exception);
      return;
    }
    final IntegerArgument initialConnectionsArgument =
            newInitialConnectionsArgument(properties);
    final IntegerArgument maxConnectionsArgument = newMaxConnectionsArgument(properties);
    final IntegerArgument maxResponseTimeMillisArgument =
            newMaxResponseTimeMillisArgument(properties);
    final IntegerArgument numThreadsArgument = newNumThreadsArgument(properties);
    final IntegerArgument pageSizeArgument = newPageSizeArgument(properties);
    final IntegerArgument reportCountArgument = newReportCountArgument(properties);
    final IntegerArgument reportIntervalArgument = newReportIntervalArgument(properties);
    final ScopeArgument scopeArgument = newScopeArgument(properties);
    final IntegerArgument sizeLimitArgument = newSizeLimitArgumentArgument(properties);
    final IntegerArgument timeLimitArgument = newTimeLimitArgumentArgument(properties);
    final StringArgument usePropertiesFileArgument = newUsePropertiesFileArgument(properties);
    final BooleanArgument useSchemaArgument = newUseSchemaArgument(properties);
    final BooleanArgument verboseArgument = newVerboseArgument(properties);
    final BooleanArgument bindWithDnRequiresPassword =
            newBindWithDnRequiresPasswordArgument(properties);

    final Argument[] arguments =
            new Argument[]
            {
                    abandonOnTimeoutArgument, attributeArgument, autoReconnectArgument,
                    baseObjectArgument, bindWithDnRequiresPassword,
                    connectTimeoutMillisArgument, filterArgument, initialConnectionsArgument,
                    introductionColumnWidthArgument, maxConnectionsArgument,
                    maxResponseTimeMillisArgument, numThreadsArgument, pageSizeArgument,
                    reportCountArgument, reportIntervalArgument, scopeArgument,
                    sizeLimitArgument, timeLimitArgument, usePropertiesFileArgument,
                    useSchemaArgument, verboseArgument,
            };

    addArguments(arguments);

  }



  /**
   * The command line argument parser provided as a service by the
   * {@code LDAPCommandLineTool} class.
   */
  private final ArgumentParser argumentParser;



  /** Logging facilities */
  private final Logger logger = Logger.getLogger(getClass().getName());



  /**
   * The name of the properties resource. This field defaults to the
   * value of {@link CommandLineOptions#PROPERTIES_RESOURCE_NAME}.
   */
  private final String propertiesResourceName = CommandLineOptions.PROPERTIES_RESOURCE_NAME;
}
