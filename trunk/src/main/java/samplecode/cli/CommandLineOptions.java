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

package samplecode.cli;

import com.unboundid.ldap.sdk.*;
import com.unboundid.util.args.*;
import java.io.File;
import java.util.*;
import samplecode.annotation.CodeVersion;
import samplecode.args.*;

import static java.util.Collections.emptyList;

import static com.unboundid.util.Validator.ensureNotNull;

/**
 * Provides services related to managing command line options for clients that use the {@code
 * LDAPCommandLineTool} class.
 *
 * @author Terry J. Gardner
 * @see Argument
 */
@CodeVersion("3.5")
public class CommandLineOptions
{

  /**
   * Creates a set of useful arguments. The arguments created are:
   * <p/>
   * <ul> <li>--baseObject</li> <li>--abandonOnTimeout</li> <li>--attribute</li>
   * <li>--autoReconnect</li> <li>--bindDnRequiresPassword</li> <li>--connectTimeoutMillis</li>
   * <li>--filter</li> <li>--initialConnections</li> <li>--introductionColumnWidth</li>
   * <li>--maxConnections</li> <li>--maxResponseTimeMillis</li> <li>--numThreads</li>
   * <li>--pageSize</li> <li>--reportCount</li> <li>--reportInterval</li> <li>--scope</li>
   * <li>--sizeLimit</li> <li>--timeLimit</li> <li>--usePropertiesFile</li> <li>--useSchema</li>
   * <li>--verbose</li> </ul>
   *
   * @param resourceBundle
   *   the resource bundle used for argument parameters
   *
   * @return a set of useful arguments
   */
  public static Argument[] createDefaultArguments(ResourceBundle resourceBundle)
    throws ArgumentException
  {
    String argName = ARG_NAME_BASE_OBJECT;
    Argument baseObjectArgument =
      StringPropertiesBackedArgument.newStringPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_ABANDON_ON_TIMEOUT;
    Argument abandonOnTimeoutArgument =
      BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_ATTRIBUTE;
    Argument attributeArgument =
      StringPropertiesBackedArgument.newStringPropertiesBackedArgument
        (resourceBundle,
          argName).getArgument();

    argName = ARG_NAME_AUTO_RECONNECT;
    Argument autoReconnectArgument =
      BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD;
    Argument requiresPasswordArgument =
      BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_CONNECT_TIMEOUT_MILLIS;
    Argument connectTimeoutArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_FILTER;
    Argument filterArgument =
      FilterPropertiesBackedArgument.newFilterPropertiesBackedArgument
        (resourceBundle,
          argName).getArgument();

    argName = ARG_NAME_INITIAL_CONNECTIONS;
    Argument initialConnectionArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_INTRODUCTION_COLUMN_WIDTH;
    Argument cwArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_MAX_CONNECTIONS;
    Argument maxConnectionArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_MAX_RESPONSE_TIME_MILLIS;
    Argument maxResponseTimeArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_NUM_THREADS;
    Argument numThreadsArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_PAGE_SIZE;
    Argument pageSizeArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_REPORT_COUNT;
    Argument reportCountArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_REPORT_INTERVAL;
    Argument reportIntervalArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_SCOPE;
    Argument scopeArgument =
      SearchScopePropertiesBackedArgument.newSearchScopePropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_SIZE_LIMIT;
    Argument sizeLimitArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_TIME_LIMIT;
    Argument timeLimitArgument =
      IntegerPropertiesBackedArgument.newIntegerPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_USE_PROPERTIES_FILE;
    Argument usePropertiesFileArgument =
      StringPropertiesBackedArgument.newStringPropertiesBackedArgument
        (resourceBundle,
          argName).getArgument();

    argName = ARG_NAME_USE_SCHEMA;
    Argument useSchemaArgument =
      BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    argName = ARG_NAME_VERBOSE;
    Argument verboseArgument =
      BooleanPropertiesBackedArgument.newBooleanPropertiesBackedArgument
        (resourceBundle,argName).getArgument();

    return new Argument[]{
      abandonOnTimeoutArgument,
      attributeArgument,
      autoReconnectArgument,
      baseObjectArgument,
      requiresPasswordArgument,
      connectTimeoutArgument,
      filterArgument,
      initialConnectionArgument,
      cwArgument,
      maxConnectionArgument,
      maxResponseTimeArgument,
      numThreadsArgument,
      pageSizeArgument,
      reportCountArgument,
      reportIntervalArgument,
      scopeArgument,
      sizeLimitArgument,
      timeLimitArgument,
      usePropertiesFileArgument,
      useSchemaArgument,
      verboseArgument
    };
  }


  /**
   * Obtain an instance of the {@code CommandLineOptions} class that will use the specified set of
   * arguments.
   *
   * @param argumentParser
   *   The argumentParser from the command line tool.
   *
   * @return A CommandLineOptions object initialized with the {@code argumentParser} parameter.
   *
   * @throws ArgumentException
   *   If an error occurs while creating or adding a command line argument.
   */
  public static CommandLineOptions
  newCommandLineOptions(ArgumentParser argumentParser,
    Argument[] arguments)
    throws ArgumentException
  {
    ensureNotNull(argumentParser,arguments);

    return new CommandLineOptions(arguments,argumentParser);
  }


  /**
   * The long identifier of the command line argument whose parameter is an indicator of whether the
   * LDAP SDK should abandon an operation if the operation times out. This command line argument is
   * optional and can occur exactly once.
   */
  public static final String ARG_NAME_ABANDON_ON_TIMEOUT = "abandonOnTimeout";

  /**
   * The long identifier of the command line argument whose parameter is the name or type of an
   * attribute to retrieve. This command line argument is optional and can occur multiple times.
   */
  public static final String ARG_NAME_ATTRIBUTE = "attribute";

  /**
   * The long identifier of the command line argument whose parameter is an indicator of whether the
   * LDAP SDK should automatically reconnect when a connection is lost. This command line argument
   * is optional and can occur exactly once.
   */
  public static final String ARG_NAME_AUTO_RECONNECT = "autoReconnect";

  /**
   * The long identifier of the command line argument whose parameter is the base object used in
   * searches and other operations where a distinguished name is required.
   */
  public static final String ARG_NAME_BASE_OBJECT = "baseObject";

  /**
   * The long identifier of the command line argument whose value is the distinguished name used to
   * bind to directory server.
   */
  public static final String ARG_NAME_BIND_DN = "bindDn";

  /**
   * The long identifier of the command line argument whose value is the bind password.
   */
  public static final String ARG_NAME_BIND_PASSWORD = "bindPassword";

  /**
   * The long identifier whose value is the pathname to a file containing the BIND password.
   */
  public static final String ARG_NAME_BIND_PASSWORD_FILE = "bindPasswordFile";

  /**
   * The long identifier of the command line argument which is present indicates that simple bind
   * requests using a DN require a password.
   */
  public static final String ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD =
    "bindWithDnRequiresPassword";

  /**
   * The long identifier of the command line argument whose parameter is the connect timeout in
   * milliseconds. This command line argument is not required, has a default value of 60 seconds and
   * can occur exactly one time.
   */
  public static final String ARG_NAME_CONNECT_TIMEOUT_MILLIS =
    "connectTimeoutMillis";

  /**
   * The long identifier of the command line argument whose parameter is the filter used in
   * searches.
   */
  public static final String ARG_NAME_FILTER = "filter";

  /**
   * The long identifier of the command line argument whose parameter is the number of threads to
   * use for a tool. This command line argument is optional and can occur exactly once.
   */
  public static final String ARG_NAME_NUM_THREADS = "numThreads";

  /**
   * The long identifier of the command line argument whose parameter is the scope of a search
   * request. This command line argument has a default value, is optional and can occur exactly one
   * time.
   */
  public static final String ARG_NAME_SCOPE = "scope";

  /**
   * The long identifier of the command line argument whose parameter is the hostname or address of
   * the server to which the LDAP SDK will connection. This command line argument is optional and
   * can occur one time.
   */
  private static final String ARG_NAME_HOSTNAME = "hostname";

  /**
   * The long identifier of the command line argument whose parameter is the number of initial
   * connections to directory server used when creating a connection pool. This parameter is not
   * required, has a default value, and may be specified exactly once.
   */
  private static final String ARG_NAME_INITIAL_CONNECTIONS = "initialConnections";

  /**
   * The long identifier of the command line argument whose parameter is the length in characters of
   * the introduction column.
   */
  private static final String ARG_NAME_INTRODUCTION_COLUMN_WIDTH =
    "introductionColumnWidth";

  /**
   * The long identifier of the command line argument whose parameter is the maximum number of
   * connections in connection pools. This parameter has a default value, is not required, and can
   * be specified exactly one time.
   */
  private static final String ARG_NAME_MAX_CONNECTIONS = "maxConnections";

  /**
   * The long identifier of the command line argument whose parameter is the maximum length of time
   * in milliseconds that at operation should be allowed to block, with 0 (zero) or less meaning no
   * timeout is enforced. This command line argument is optional and can occur exactly one time.
   */
  private static final String ARG_NAME_MAX_RESPONSE_TIME_MILLIS = "maxResponseTimeMillis";

  /**
   * The long identifier of the command line argument whose parameter is the client-requested page
   * size in simple paged request controls. This parameter has a default value, is not required, and
   * can be specified exactly one time.
   */
  private static final String ARG_NAME_PAGE_SIZE = "pageSize";

  /**
   * The long identifier of the command line argument whose parameter is the port to which the LDAP
   * SDK will connect. This command line argument is optional and can occur one time.
   */
  private static final String ARG_NAME_PORT = "port";

  /**
   * The long identifier of the command line argument whose parameter is the maximum number of
   * reports. This command line argument is applicable to tools that have a repeating number of
   * reports. The time between reports of such tools is specified by the --reportInterval command
   * line argument. This argument is not required, has a default value, and can be specified exactly
   * one time.
   */
  private static final String ARG_NAME_REPORT_COUNT = "reportCount";

  /**
   * The long identifier of the command line argument whose parameter is the reporting interval in
   * milliseconds. This command line argument is optional and can occur one time.
   */
  private static final String ARG_NAME_REPORT_INTERVAL = "reportInterval";

  /**
   * The long identifier of the command line argument whose parameter is the client requested size
   * limit.
   */
  private static final String ARG_NAME_SIZE_LIMIT = "sizeLimit";

  /**
   * The long identifier of the command line argument whose parameter is the client requested time
   * limit.
   */
  private static final String ARG_NAME_TIME_LIMIT = "timeLimit";

  /**
   * The long identifier of the command line argument whose parameter is the name of a properties
   * file.
   */
  private static final String ARG_NAME_USE_PROPERTIES_FILE = "usePropertiesFile";

  /**
   * The long identifier of the command line argument whose parameter is an indicator of whether the
   * LDAP SDK should try to use schema information. This command line argument is optional and can
   * occur exactly once.
   */
  private static final String ARG_NAME_USE_SCHEMA = "useSchema";

  /**
   * The long identifier of the command line argument whose parameter is an indicator of whether the
   * should be verbose. This command line argument is optional and can occur exactly once.
   */
  private static final String ARG_NAME_VERBOSE = "verbose";

  /**
   * The command line argument parser provided as a service by the {@code LDAPCommandLineTool}
   * class.
   */
  private final ArgumentParser argumentParser;


  /**
   * initializes a {@code CommandLineOptions} object by adding all the arguments specified by the
   * {@code arguments} parameter to the specified {@code argumentParser}.
   *
   * @param argumentParser
   *   parses command line arguments
   */
  protected CommandLineOptions(Argument[] arguments, ArgumentParser argumentParser)
    throws ArgumentException
  {
    if(arguments == null)
    {
      String msg =
        "arguments violates the contract of this method (it was null).";
      throw new NullPointerException(msg);
    }
    if(argumentParser == null)
    {
      String msg =
        "argumentParser violates the contract of this method (it was null).";
      throw new NullPointerException(msg);
    }

    this.argumentParser = argumentParser;
    addArguments(arguments);
  }


  /**
   * Adds each of the specified arguments to the {@code argumentParser}, thereby making the
   * arguments available to command line clients.
   *
   * @param arguments
   *   A list of arguments to be added (cannot be {@code null}).
   */
  public void addArguments(Argument... arguments) throws ArgumentException
  {
    if(arguments == null)
    {
      final String msg =
        "arguments violates the contract of this method (it was null).";
      throw new NullPointerException(msg);
    }

    for(Argument argument : arguments)
    {
      if(argument != null) argumentParser.addArgument(argument);
    }
  }


  /**
   * Retrieves the value of the command line argument named by the {@code longIdentifier} parameter.
   * If no argument with the given {@code longIdentifier} has been registered with the argument
   * parser, {@code get} returns {@code null}.
   *
   * @param longIdentifier
   *   The long identifier of a command line option.
   *
   * @return The value of the command line argument named by the {@code longIdentifier} or {@code
   *         null} if no such argument has been registered with the argument parser.
   */
  public Object get(String longIdentifier)
  {
    if(longIdentifier == null)
    {
      throw new NullPointerException("longIdentifier must not be null.");
    }
    Object get = null;
    Argument argument = getNamedArgument(longIdentifier);
    if(argument == null) return null;
    if(argument.getClass() == StringArgument.class) get = ((StringArgument)argument).getValue();
    else if(argument.getClass() == DNArgument.class) get = ((DNArgument)argument).getValue();
    else if(argument.getClass() == IntegerArgument.class)
      get = ((IntegerArgument)argument).getValue();
    else if(argument.getClass() == BooleanArgument.class) get = argument.isPresent();
    else if(argument.getClass() == FilterArgument.class)
      get = ((FilterArgument)argument).getValue();
    else
    {
      StringBuilder builder = new StringBuilder(argument.getClass().toString());
      builder.append(" is not supported by the get() method.");
      throw new UnsupportedOperationException(builder.toString());
    }

    return get;
  }


  /**
   * Gets the identifier named by {@code longIdentifier} from the argument parser. The
   * longIdentifier argument is not permitted to be {@code null}.
   * <p/>
   * Usage:
   * <p/>
   * <pre>
   * IntegerArgument portArg = getNamedArgument(&quot;port&quot;);
   * int port;
   * if(portArg != null &amp;&amp; portArg.isPresent()) {
   *   port = portArg.getValue();
   * } else {
   *   port = 389;
   * }
   * </pre>
   *
   * @param longIdentifier
   *   the long identifier, for example, {@code "port"}.
   *
   * @return the argument associated with the long identifier
   */
  @SuppressWarnings("unchecked")
  public <T extends Argument> T getNamedArgument(String longIdentifier)
  {
    return (T)argumentParser.getNamedArgument(longIdentifier);
  }


  /**
   * Retrieve the argument parser with which this command line options object was initialized.
   *
   * @return The argument parser.
   */
  public ArgumentParser getArgumentParser()
  {
    return argumentParser;
  }


  /**
   * Retrieves the value of the base object as specified by the {@code --baseObject} command line
   * option.
   *
   * @return The value of the command line argument named by the {@code --baseObject} command line
   *         option.
   *
   * @throws LDAPException
   */
  public String getBaseObject()
  {
    return ((StringArgument)getNamedArgument(ARG_NAME_BASE_OBJECT)).getValue();
  }


  /**
   * Retrieves the value of the bindDn as specified by the {@code --bindDn} command line option.
   *
   * @return The value of the command line argument named by the {@code --bindDn} command line
   *         option.
   */
  public final DN getBindDn()
  {
    DN bindDn = null;
    DNArgument arg = getBindDnArgument();
    if((arg != null) && arg.isPresent())
    {
      bindDn = arg.getValue();
    }
    return bindDn;
  }


  /**
   * Retrieves the {@code --bindDN} argument
   */
  public DNArgument getBindDnArgument()
  {
    return getNamedArgument(ARG_NAME_BIND_DN);
  }


  /**
   * Retrieves the value of the bind password as specified by the {@code --bindPassword} command
   * line option.
   *
   * @return The value of the command line argument named by the {@code --bindPassword} command line
   *         option.
   */
  public final String getBindPassword()
  {
    String bindPassword = "";
    StringArgument arg = getBindPasswordArgument();
    if((arg != null) && arg.isPresent())
    {
      bindPassword = arg.getValue();
    }
    return bindPassword;
  }


  /**
   * Retrieves the {@code --bindPassword} argument.
   */
  public final StringArgument getBindPasswordArgument()
  {
    return getNamedArgument(ARG_NAME_BIND_PASSWORD);
  }


  /**
   * @return the value of the command line argument {@code --bindPasswordFile} , or {@code null} if
   *         the argument was not supplied on the command line.
   */
  public final File getBindPasswordFile()
  {
    return ((FileArgument)getNamedArgument(ARG_NAME_BIND_PASSWORD_FILE)).getValue();
  }


  /**
   * Retrieves the parameter provided to the {@code --connectTimeoutMillis} command line argument.
   *
   * @return connect timeout in milliseconds.
   */
  public Object getConnectTimeoutMillis()
  {
    return ((IntegerArgument)getNamedArgument(ARG_NAME_CONNECT_TIMEOUT_MILLIS)).getValue();
  }


  /**
   * Retrieves the parameter to the {@code --filter} command line option.
   *
   * @return The search filter.
   */
  public Filter getFilter()
  {
    return getFilterArgument().getValue();
  }


  /**
   * Retrieves the command line argument whose value is a search filter.
   */
  public FilterArgument getFilterArgument()
  {
    return getNamedArgument(ARG_NAME_FILTER);
  }


  /**
   * Retrieve the parameters to the {@code --filter} command line option.
   *
   * @return The list of search filters.
   */
  public List<Filter> getFilters()
  {
    return getFilterArgument().getValues();
  }


  /**
   * Retrieves the value of the {@code --hostname} command line option.
   *
   * @return The hostname or IP address where a directory server is expected to be listening for
   *         connections. If the {@code --hostname} or {@code -h} command line option is not
   *         present, {@code "localhost"} is returned.
   */
  public final String getHostname()
  {
    String hostname = "localhost";
    StringArgument arg = getNamedArgument(ARG_NAME_HOSTNAME);
    if((arg != null) && arg.isPresent())
    {
      hostname = arg.getValue();
    }
    return hostname;
  }


  /**
   * Retrieve the number of initial connections to the directory server from the {@code
   * --initialConnections} command line option. The {@code --initialConnections} command line option
   * has a default value.
   *
   * @return The number of initial connections to the connection pool specified on the command line
   *         or the default value if {@code --initialConnections} is found on the command line.
   */
  public int getInitialConnections()
  {
    int initialConnections = 1;
    String argName = ARG_NAME_INITIAL_CONNECTIONS;
    IntegerArgument arg = getNamedArgument(argName);
    if((arg != null) && arg.isPresent())
    {
      initialConnections = arg.getValue().intValue();
    }
    return initialConnections > 0 ? initialConnections : 1;
  }


  /**
   * Provides access to the value of the introduction column width as specified by the {@code
   * --introductionColumnWidth} command line option.
   *
   * @return introduction column width
   */
  public int getIntroductionColumnWidth()
  {
    int value = 0;
    String argName = ARG_NAME_INTRODUCTION_COLUMN_WIDTH;
    IntegerArgument arg = getNamedArgument(argName);
    if((arg != null) && arg.isPresent())
    {
      value = arg.getValue();
    }
    return value;
  }


  /**
   * Retrieve the maximum number of connections to the directory server from the {@code
   * --maxConnections} command line option. The {@code --maxConnections} command line option has a
   * default value.
   *
   * @return The maximum number of connections to the connection pool specified on the command line
   *         or the default value if {@code --maxConnections} is found on the command line.
   */
  public int getMaxConnections()
  {
    int maxConnections = 2;
    String argName = ARG_NAME_MAX_CONNECTIONS;
    IntegerArgument arg = getNamedArgument(argName);
    if((arg != null) && arg.isPresent())
    {
      maxConnections = arg.getValue().intValue();
    }
    return maxConnections > 0 ? maxConnections : 1;
  }


  /**
   * Retrieves the number of threads as specified by the {@code --numThreads} command line option.
   *
   * @return The value of the command line argument named by the {@code --numThreads} command line
   *         option.
   */
  public int getNumThreads()
  {
    IntegerArgument arg = getNamedArgument(ARG_NAME_NUM_THREADS);
    return arg == null ? 0 : arg.getValue();
  }


  /**
   * Retrieve the page size to use for simple paged results. This parameter is specified by the
   * {@code --pageSize} command line argument.
   *
   * @return the page size to use for simple page results.
   */
  public int getPageSize()
  {
    int pageSize = 10;
    String argName = ARG_NAME_PAGE_SIZE;
    IntegerArgument arg = getNamedArgument(argName);
    if((arg != null) && arg.isPresent())
    {
      pageSize = arg.getValue().intValue();
    }
    return pageSize;
  }


  /**
   * Retrieve the port from the {@code --port} or {@code -p} command line option. The port command
   * line option has a default value.
   *
   * @return The port specified on the command line or the default value if neither {@code --port}
   *         or {@code -p} is found on the command line.
   */
  public int getPort()
  {
    int port = 389;
    IntegerArgument arg = getPortArgument();
    if((arg != null) && arg.isPresent()) port = arg.getValue().intValue();
    return port;
  }


  /**
   * Gets the {@code IntegerArgument} with the long identifier of {@code "--port"}.
   *
   * @return the argument associated with {@code --port}.
   */
  public IntegerArgument getPortArgument()
  {
    return getNamedArgument(ARG_NAME_PORT);
  }


  /**
   * Gets the value of the {@code --usePropertiesFile} argument, if present, otherwise {@code
   * null}.
   *
   * @return properties filename.
   */
  public String getPropertiesFile()
  {
    StringArgument argument = getPropertiesFileArgument();
    return argument.isPresent() ? argument.getValue() : null;
  }


  /**
   * Gets the {@code StringArgument} associated with the properties file
   */
  public StringArgument getPropertiesFileArgument()
  {
    return getNamedArgument(ARG_NAME_USE_PROPERTIES_FILE);
  }


  /**
   * Get the parameter specified to the [@code --reportCount} command line argument. This parameter
   * is the maximum number of reports, applicable to a tool that uses repeating reports. The time
   * interval between reports is specified by the {@code --reportInterval} command line argument.
   *
   * @return the maximum number of reports.
   */
  public int getReportCount()
  {
    int reportCount = Integer.MAX_VALUE;
    IntegerArgument arg = getNamedArgument(ARG_NAME_REPORT_COUNT);
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
    String argName = ARG_NAME_REPORT_INTERVAL;
    Argument arg = getNamedArgument(argName);
    return arg == null ? 0 : ((IntegerArgument)arg).getValue().intValue();
  }


  /**
   * Retrieve the attributes requested from the {@code --attribute} command line option. The {@code
   * --attribute} command line option has a default value, is not required, and can be specified
   * multiple times.
   * <p/>
   * The default value(s) are taken from the {@code commandLineOptions.properties} file.
   *
   * @return The attributes requested as provided by the command line option {@code --attribute}, or
   *         a default value is the {@code --attribute} command line option is not present.
   */
  public List<String> getRequestedAttributes()
  {
    String argName = ARG_NAME_ATTRIBUTE;
    Argument arg = getNamedArgument(argName);
    if(arg == null)
    {
      return emptyList();
    }
    return ((StringArgument)arg).getValues();
  }


  /**
   * Retrieve the value of the parameter to the {@code --scope} command line option.
   *
   * @return the search scope.
   */
  public SearchScope getSearchScope()
  {
    SearchScope searchScope = SearchScope.SUB;
    String argName = ARG_NAME_SCOPE;
    ScopeArgument arg = getNamedArgument(argName);
    if((arg != null) && arg.isPresent())
    {
      searchScope = arg.getValue();
    }
    return searchScope;
  }


  /**
   * Retrieves the value of the search size limit as specified by the {@code --sizeLimit} command
   * line option.
   *
   * @return The value of the command line argument named by the {@code --sizeLimit} command line
   *         option.
   */
  public int getSizeLimit()
  {
    int sizeLimit = 1;
    String argName = ARG_NAME_SIZE_LIMIT;
    IntegerArgument arg = getNamedArgument(argName);
    if((arg != null) && arg.isPresent())
    {
      sizeLimit = arg.getValue().intValue();
    }
    return sizeLimit;
  }


  /**
   * Retrieves the value of the search time limit as specified by the {@code --timeLimit} command
   * line option.
   *
   * @return The value of the command line argument named by the {@code --timeLimit} command line
   *         option.
   */
  public int getTimeLimit()
  {
    String argName = ARG_NAME_TIME_LIMIT;
    IntegerArgument arg = getNamedArgument(argName);
    return arg == null ? 0 : arg.getValue().intValue();
  }


  /**
   * Returns a {@link String} representation of the value of a property specified by {@code
   * propertyName} or {@code defaultValue} if the property cannot be found.
   * <p/>
   * For example, is a properties file contains: <blockquote>
   * <p/>
   * <pre>
   * hostname = ldap.example.com
   * </pre>
   * <p/>
   * </blockquote> and the {@code getValue} method is invoked thus:
   * <p/>
   * <blockquote>
   * <p/>
   * <pre>
   * String portString = getValue(&quot;port&quot;,&quot;389&quot;);
   * </pre>
   * <p/>
   * </blockquote>
   * <p/>
   * {@code portString} will contain {@code "389"}.
   *
   * @param propertyName
   *   the name of a property in a Java properties fileNot permitted to be {@code null}.
   * @param defaultValue
   *   the value of the property to return in the event the property specified by the {@code
   *   propertyName} parameter is not present in the properties file.Not permitted to be {@code
   *   null}.
   * @param properties
   *   the properties object from which values are taken. Not permitted to be {@code null}.
   *
   * @return a {@link String} representation of the value of the property whose key is specified by
   *         the {@code propertyName} parameter if that property is present in the properties file.
   *         If the property is not present in the properties file, the {@code defaultValue} is
   *         returned.
   */
  public String getValue(final String propertyName, final String defaultValue,
    final Properties properties)
  {
    if(propertyName == null)
    {
      throw new IllegalArgumentException("propertyName must not be null.");
    }
    if(defaultValue == null)
    {
      throw new IllegalArgumentException("defaultValue must not be null.");
    }
    if(properties == null)
    {
      throw new IllegalArgumentException("properties must not be null.");
    }
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
   * @return Whether the {@code --verbose} command line option is present.
   */
  public boolean isVerbose()
  {
    String argName = ARG_NAME_VERBOSE;
    BooleanArgument arg = getNamedArgument(argName);
    return arg.isPresent();
  }


  /**
   * Constructs a new {@code LDAPConnectionOptions} object with parameters set to values specified
   * by command line argument parameters.
   * <p/>
   * Usage example: <blockquote>
   * <pre>
   * commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);
   * final LDAPConnection ldapConnection = new LDAPConnection();
   * LDAPConnectionOptions connectionOptions = commandLineOptions
   * .newLDAPConnectionOptions();
   * ldapConnection.setConnectionOptions(connectionOptions);
   * </pre>
   * </blockquote>
   *
   * @return a new {@code LDAPConnectionOptions} object.
   */
  public LDAPConnectionOptions newLDAPConnectionOptions()
  {
    LDAPConnectionOptions ldapConnectionOptions = new LDAPConnectionOptions();

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
    int responseTimeout = getMaxResponseTimeMillis();
    ldapConnectionOptions.setResponseTimeoutMillis(responseTimeout);

    return ldapConnectionOptions;
  }


  /**
   * Whether the {@code --useSchema} command line option is present.
   *
   * @return Whether the {@code --useSchema} command line option is present.
   */
  public boolean getUseSchema()
  {
    String argName = ARG_NAME_USE_SCHEMA;
    BooleanArgument arg = getNamedArgument(argName);
    return arg.isPresent();
  }


  /**
   * Retrieves the maximum response time in milliseconds (the parameter to the {@code
   * --maxResponseTimeMillis} command line argument. Specifies the maximum length of time in
   * milliseconds that an operation should be allowed to block while waiting for a response from the
   * server. A value of zero indicates that there should be no timeout.
   *
   * @return The maximum allowable response time in milliseconds.
   */
  public int getMaxResponseTimeMillis()
  {
    String argName = ARG_NAME_MAX_RESPONSE_TIME_MILLIS;
    IntegerArgument arg = getNamedArgument(argName);
    return arg == null ? 0 : arg.getValue().intValue();
  }


  /**
   * Whether the {@code --autoReconnect} command line option is present. The {@code --autoReconnect}
   * command line option controls whether the LDAP SDK should reconnect automatically when a
   * connection is lost.
   *
   * @return Whether the {@code --autoReconnect} command line option is present.
   */
  public boolean getAutoReconnect()
  {
    return getNamedArgument(ARG_NAME_AUTO_RECONNECT).isPresent();
  }


  /**
   * Whether the {@code --abandonOnTimeout} command line option is present.
   *
   * @return Whether the {@code --abandonOnTimeout} command line option is present.
   */
  public boolean getAbandonOnTimeout()
  {
    return getNamedArgument(ARG_NAME_ABANDON_ON_TIMEOUT).isPresent();
  }


  /**
   * @return Indicates whether the SDK should allow simple bind operations that contain a bind DN
   *         but no password. Binds of this type may represent a security vulnerability in client
   *         applications because they may cause the client to believe that the user is properly
   *         authenticated when the server considers it to be an unauthenticated connection.
   */
  public boolean bindDnRequiresPassword()
  {
    return getNamedArgument(ARG_NAME_BIND_WITH_DN_REQUIRES_PASSWORD).isPresent();
  }

}
