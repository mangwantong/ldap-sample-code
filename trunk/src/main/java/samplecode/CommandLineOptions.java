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
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.args.BooleanPropertiesBackedArgument;
import samplecode.args.FilterPropertiesBackedArgument;
import samplecode.args.IntegerPropertiesBackedArgument;
import samplecode.args.SearchScopePropertiesBackedArgument;
import samplecode.args.StringPropertiesBackedArgument;


/**
 * Provides services related to managing command line options for
 * clients that use the LDAPCommandLineTool class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Nov 28, 2011")
@CodeVersion("2.1")
public class CommandLineOptions
{



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the LDAP SDK should abandon an operation if
   * the operation times out. This command line argument is optional and
   * can occur exactly once.
   */
  private static final String ARG_NAME_ABANDON_ON_TIMEOUT = "abandonOnTimeout";



  /**
   * The long identifier of the command line argument whose parameter is
   * the name or type of an attribute to retrieve. This command line
   * argument is optional and can occur multiple times.
   */
  private static final String ARG_NAME_ATTRIBUTE = "attribute";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the LDAP SDK should automatically reconnect
   * when a connection is lost. This command line argument is optional
   * and can occur exactly once.
   */
  private static final String ARG_NAME_AUTO_RECONNECT = "autoReconnect";



  /**
   * The long identifier of the command line argument whose parameter is
   * the base object used in searches and other operations where a
   * distinguished name is required.
   */
  private static final String ARG_NAME_BASE_OBJECT = "baseObject";



  /**
   * The long identifier of the command line argument whose value is the
   * distinguished name used to bind to directory server.
   */
  private static final String ARG_NAME_BIND_DN = "bindDn";



  /**
   * The long identifier of the command line argument whose value is the
   * bind password.
   */
  private static final String ARG_NAME_BIND_PASSWORD = "bindPassword";



  /**
   * The long identifier of the command line argument which is present
   * indicates that simple bind requests using a DN require a password.
   */
  private static final String ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD =
          "bindWithDnRequiresPassword";



  /**
   * The long identifier of the command line argument whose parameter is
   * the connect timeout in milliseconds. This command line argument is
   * not required, has a default value of 60 seconds and can occur
   * exactly one time.
   */
  private static final String ARG_NAME_CONNECT_TIMEOUT_MILLIS = "connectTimeoutMillis";



  /**
   * The long identifier of the command line argument whose parameter is
   * the filter used in searches.
   */
  private static final String ARG_NAME_FILTER = "filter";



  /**
   * The long identifier of the command line argument whose parameter is
   * the hostname or address of the server to which the LDAP SDK will
   * connection. This command line argument is optional and can occur
   * one time.
   */
  private static final String ARG_NAME_HOSTNAME = "hostname";



  /**
   * The long identifier of the command line argument whose parameter is
   * the number of initial connections to directory server used when
   * creating a connection pool. This parameter is not required, has a
   * default value, and may be specified exactly once.
   */
  private static final String ARG_NAME_INITIAL_CONNECTIONS = "initialConnections";



  /**
   * The long identifier of the command line argument whose parameter is
   * the length in characters of the introduction column.
   */
  private static final String ARG_NAME_INTRODUCTION_COLUMN_WIDTH = "introductionColumnWidth";



  /**
   * The long identifier of the command line argument whose parameter is
   * the maximum number of connections in connection pools. This
   * parameter has a default value, is not required, and can be
   * specified exactly one time.
   */
  private static final String ARG_NAME_MAX_CONNECTIONS = "maxConnections";



  /**
   * The long identifier of the command line argument whose parameter is
   * the maximum length of time in milliseconds that at operation should
   * be allowed to block, with 0 (zero) or less meaning no timeout is
   * enforced. This command line argument is optional and can occur
   * exactly one time.
   */
  private static final String ARG_NAME_MAX_RESPONSE_TIME_MILLIS = "maxResponseTimeMillis";



  /**
   * The long identifier of the command line argument whose parameter is
   * the number of threads to use for a tool. This command line argument
   * is optional and can occur exactly once.
   */
  private static final String ARG_NAME_NUM_THREADS = "numThreads";



  /**
   * The long identifier of the command line argument whose parameter is
   * the client-requested page size in simple paged request controls.
   * This parameter has a default value, is not required, and can be
   * specified exactly one time.
   */
  private static final String ARG_NAME_PAGE_SIZE = "pageSize";



  /**
   * The long identifier of the command line argument whose parameter is
   * the port to which the LDAP SDK will connect. This command line
   * argument is optional and can occur one time.
   */
  private static final String ARG_NAME_PORT = "port";



  /**
   * The long identifier of the command line argument whose parameter is
   * the maximum number of reports. This command line argument is
   * applicable to tools that have a repeating number of reports. The
   * time between reports of such tools is specified by the
   * --reportInterval command line argument. This argument is not
   * required, has a default value, and can be specified exactly one
   * time.
   */
  private static final String ARG_NAME_REPORT_COUNT = "reportCount";



  /**
   * The long identifier of the command line argument whose parameter is
   * the reporting interval in milliseconds. This command line argument
   * is optional and can occur one time.
   */
  private static final String ARG_NAME_REPORT_INTERVAL = "reportInterval";



  /**
   * The long identifier of the command line argument whose parameter is
   * the scope of a search request. This command line argument has a
   * default value, is optional and can occur exactly one time.
   */
  private static final String ARG_NAME_SCOPE = "scope";



  /**
   * The long identifier of the command line argument whose parameter is
   * the client requested size limit.
   */
  private static final String ARG_NAME_SIZE_LIMIT = "sizeLimit";



  private static final String ARG_NAME_TIME_LIMIT = "timeLimit";



  /**
   * The long identifier of the command line argument whose parameter is
   * the name of a properties file.
   */
  private static final String ARG_NAME_USE_PROPERTIES_FILE = "usePropertiesFile";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the LDAP SDK should try to use schema
   * information. This command line argument is optional and can occur
   * exactly once.
   */
  private static final String ARG_NAME_USE_SCHEMA = "useSchema";



  /**
   * The long identifier of the command line argument whose parameter is
   * an indicator of whether the should be verbose. This command line
   * argument is optional and can occur exactly once.
   */
  private static final String ARG_NAME_VERBOSE = "verbose";



  /**
   * a set of useful arguments.
   * 
   * @param resourceBundle
   *          the resource bundle used for argument parameters
   * 
   * 
   * 
   * @return a set of useful arguments
   * 
   * @throws ArgumentException
   *           when a problem occurs creating an argument.
   */
  public static final Argument[] createDefaultArguments(final ResourceBundle resourceBundle)
          throws ArgumentException
  {
    return new Argument[]
    {
            BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_ABANDON_ON_TIMEOUT).getArgument(),
            StringPropertiesBackedArgument.newStringPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_ATTRIBUTE).getArgument(),
            BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_AUTO_RECONNECT).getArgument(),
            StringPropertiesBackedArgument.newStringPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_BASE_OBJECT).getArgument(),
            BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_CONNECT_TIMEOUT_MILLIS).getArgument(),
            FilterPropertiesBackedArgument.newFilterPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_FILTER).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_INITIAL_CONNECTIONS).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_INTRODUCTION_COLUMN_WIDTH).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_MAX_CONNECTIONS).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_MAX_RESPONSE_TIME_MILLIS).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_NUM_THREADS).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_PAGE_SIZE).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_REPORT_COUNT).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_REPORT_INTERVAL).getArgument(),
            SearchScopePropertiesBackedArgument.newSearchScopePropertiesBackedArgument(
                    resourceBundle,CommandLineOptions.ARG_NAME_SCOPE).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_SIZE_LIMIT).getArgument(),
            IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_TIME_LIMIT).getArgument(),
            StringPropertiesBackedArgument.newStringPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_USE_PROPERTIES_FILE).getArgument(),
            BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_USE_SCHEMA).getArgument(),
            BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument(resourceBundle,
                    CommandLineOptions.ARG_NAME_VERBOSE).getArgument(),
    };
  }



  /**
   * Obtain an instance of the {@code CommandLineOptions} class that
   * will use the specified set of arguments.
   * 
   * @param argumentParser
   *          The argumentParser from the command line tool.
   * 
   * @return A CommandLineOptions object initialized with the
   *         {@code argumentParser} parameter.
   * 
   * @throws ArgumentException
   *           If an error occurs while creating or adding a command
   *           line argument.
   */
  public static CommandLineOptions newCommandLineOptions(final ArgumentParser argumentParser,
          final Argument[] arguments) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    return new CommandLineOptions(arguments,argumentParser);
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
   * 
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
    return ((IntegerArgument)argumentParser
            .getNamedArgument(CommandLineOptions.ARG_NAME_MAX_RESPONSE_TIME_MILLIS)).getValue()
            .intValue();
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
    return ((IntegerArgument)argumentParser
            .getNamedArgument(CommandLineOptions.ARG_NAME_NUM_THREADS)).getValue();
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
    return ((IntegerArgument)argumentParser
            .getNamedArgument(CommandLineOptions.ARG_NAME_REPORT_INTERVAL)).getValue()
            .intValue();
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
  public List<String> getRequestedAttributes()
  {
    return ((StringArgument)argumentParser
            .getNamedArgument(CommandLineOptions.ARG_NAME_ATTRIBUTE)).getValues();
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
    return ((IntegerArgument)argumentParser
            .getNamedArgument(CommandLineOptions.ARG_NAME_TIME_LIMIT)).getValue().intValue();
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
   * 
   * </blockquote> and the {@code getValue} method is invoked thus:
   * 
   * <blockquote>
   * 
   * <pre>
   * 
   * 
   * 
   * 
   * 
   * 
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
    return ((BooleanArgument)argumentParser
            .getNamedArgument(CommandLineOptions.ARG_NAME_VERBOSE)).isPresent();
  }



  /**
   * @param properties
   *          properties file used for the value of the properties file.
   * 
   * @return the command line argument that indicates whether the LDAP
   *         SDK should automatically reconnect when a connection is
   *         lost.
   */
  public BooleanArgument newAutoReconnectArgument(final Properties properties)
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
   * Preconditions:
   * <p/>
   * The {@code argumentParser} is not permitted to be {@code null}.
   * 
   * @param argumentParser
   *          parses command line arguments
   * @throws ArgumentException
   *           if an argument cannot be parsed or added.
   */
  protected CommandLineOptions(
          final Argument[] arguments,final ArgumentParser argumentParser)
          throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);

    this.argumentParser = argumentParser;
    addArguments(arguments);
  }



  /**
   * The command line argument parser provided as a service by the
   * {@code LDAPCommandLineTool} class.
   */
  private final ArgumentParser argumentParser;
}
