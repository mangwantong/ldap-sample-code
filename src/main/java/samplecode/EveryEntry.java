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


import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultListener;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;


import java.io.PrintStream;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.ErrorListener;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;


/**
 * Provides a way to invoke the {@code searchEntryReturned} and
 * {@code searchReferenceReturned} methods of a
 * {@code SearchResultListener} for each entry returned from a search
 * based on the standard command line options. The
 * {@code SearchResultListener} is supplied as a command line argument.
 * <p>
 * <b>usage example</b>
 * <p>
 * The following invocation for each thread invokes the
 * searchEntryReturned and searchReferenceReturned methods in the class
 * {@code "samplecode.PrintEntrySearchResultListener"} for each entry
 * returned from the search request constructed by the base object
 * {@code "dc=example,dc=com"}, {@code subtree} scope, filter
 * {@code "(objectClass=*)"}, attributes {@code mail} and {@code uid}. A
 * size limit and time limit are specified, as is an instruction to
 * abandon operations that timed out and automatically reconnect to
 * directory server when the connection is lost. The connection to the
 * directory server will be authorized as
 * {@code "uid=user.0,ou=people,dc=example,dc=com"}, and the
 * {@code StartTLS} extended request will attempt to encrypt the
 * connection between client and server.<blockquote>
 * 
 * <pre>
 * java -cp your-classpath samplecode.EveryEntry \
 *    --abandonOnTimeout --attribute mail --attribute uid \
 *    --autoReconnect  --baseObject "dc=example,dc=com" \
 *    --bindDn "uid=user.0,ou=people,dc=example,dc=com" \
 *    --bindPasswordFile /Users/terrygardner/.pwdFile \
 *    --filter "(objectClass=*)" --hostname localhost \
 *    --port 1389 --scope SUB --sizeLimit 1000 --timeLimit 1 \
 *    --searchResultListener "samplecode.PrintEntrySearchResultListener" \ 
 *    --useStartTls --trustAll --numThreads 4
 * </pre>
 * 
 * </blockquote>
 * 
 * @see SearchResultListener
 * @see LDAPCommandLineTool
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 18, 2011")
@CodeVersion("1.3")
public final class EveryEntry
        extends LDAPCommandLineTool
{

  /**
   * The description of this tool; used for help, diagnostic, and other
   * purposes.
   */
  private static final String TOOL_DESCRIPTION =
          "Provides a way to invoke the  searchEntryReturned and  "
                  + "searchReferenceReturned methods of a SearchResultListener "
                  + "for each entry returned from a search based on the "
                  + "standard command line options.";



  /**
   * The name of this tool; used for help, diagnostic, and other
   * purposes.
   */
  private static final String TOOL_NAME = "EveryEntry";



  /**
   * <blockquote>
   * 
   * <pre>
   *   Provides a way to invoke the  searchEntryReturned and  searchReferenceReturned
   * methods of a SearchResultListener for each entry returned from a search based
   * on the standard command line options.
   * 
   * Usage:  EveryEntry {options}
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
   * --numThreads {number-of-threads}
   *     Specifies the number of threads to use when running the application.
   * --useSchema
   *     Whether the LDAP SDK should attempt to use server schema information, for
   *     example, for matching rules.
   * --verbose
   *     Whether the tool should be verbose.
   * --abandonOnTimeout
   *     Whether the LDAP SDK should abandon an operation that has timed out.
   * --autoReconnect
   *     Whether the LDAP SDK should automatically reconnect when a connection is
   *     lost.
   * --connectTimeoutMillis {connect-timeout-millis-integer}
   *     Specifies the maximum length of time in milliseconds that a connection
   *     attempt should be allowed to continue before giving up. A value of zero
   *     indicates that there should be no connect timeout.
   * -b, --baseObject {distinguishedName}
   *     The base object used in the search request.
   * --maxResponseTimeMillis {max-response-time-in-milliseconds}
   *     The maximum length of time in milliseconds that an operation should be
   *     allowed to block, with 0 or less meaning no timeout is enforced. This
   *     command line argument is optional and has a default value of zero.
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
   *     The client-request maximum number of results which are returned to the
   *     client. If the number of entries which match the search parameter is
   *     greater than the client-requested size limit or the server-imposed size
   *     limit a SIZE_LIMIT_EXCEEDED code is returned in the result code in the
   *     search response.
   * --timeLimit {positiveInteger}
   *     The client-request maximum time to search used by the server. If the time
   *     of the search is greater than the client-requested time limit or the
   *     server-imposed time limit a TIME_LIMIT_EXCEEDED code is returned in the
   *     result code in the search response.
   * --pageSize {positiveInteger}
   *     The search page size
   * --searchResultListener {class-name}
   *     The name of class which extends the AbstractSearchResultListener class. The
   *     searchEntryReturned method of this class is invoked when an entry is
   *     returned from a search.
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *          JVM command line arguments.
   */
  public static void main(final String... args)
  {
    final EveryEntry everyEntry = new EveryEntry();
    final ResultCode resultCode = everyEntry.runTool(args);
    if(resultCode != null)
    {
      final String message =
              String.format("%s has completed processing. The result code was: %s",
                      everyEntry.getToolName(),resultCode);
      final LogRecord logRecord = new LogRecord(Level.INFO,message);
      final String msg = new MinimalLogFormatter().format(logRecord);
      everyEntry.out(msg);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions =
            EveryEntryCommandLineOptions.newEveryEntryCommandLineOptions(argumentParser);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {

    if(commandLineOptions.isVerbose())
    {
      out(commandLineOptions);
    }

    /*
     * Set up an executor service with a fixed thread pool.
     */
    final int numThreads = commandLineOptions.getNumThreads();
    final ExecutorService executorService = Executors.newFixedThreadPool(numThreads);

    /*
     * Start searches, one per thread.
     */
    ResultCode resultCode = ResultCode.SUCCESS;
    resultCode = startSearches(executorService,numThreads);
    executorService.shutdown();
    return resultCode;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return EveryEntry.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return EveryEntry.TOOL_NAME;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return "EveryEntry [" +
            (commandLineOptions != null ? "commandLineOptions=" + commandLineOptions + ", "
                    : "") + (formatter != null ? "formatter=" + formatter : "") + "]";
  }



  /**
   * Starts all threads, one thread per task.
   * 
   * @param executorService
   *          the service providing a thread pool in which to execute
   *          tasks.
   * @param numThreads
   *          the number of threads (and tasks since there is one task
   *          per thread).
   * @return a single result code.
   */
  private ResultCode startSearches(final ExecutorService executorService,final int numThreads)
  {
    Validator.ensureNotNull(executorService);
    ResultCode resultCode = ResultCode.SUCCESS;
    for(int t = 0; t < numThreads; ++t)
    {
      final String searchListenerClassname =
              commandLineOptions.getSearchResultListenerClassname();
      EveryEntryImpl impl;
      try
      {
        /*
         * Get a connection to the server and create an error listener
         * for later assignment to a task. Create the task and submit to
         * the executor service.
         */
        final LDAPConnection ldapConnection = getConnection();
        final List<ErrorListener<ResultCode>> errorListeners =
                SampleCodeCollectionUtils.newArrayList();
        final ErrorListener<ResultCode> l = new ResultCodeErrorListener();
        errorListeners.add(l);
        impl =
                new EveryEntryImpl(searchListenerClassname,commandLineOptions,ldapConnection,
                        getErr(),errorListeners);
        impl.addLdapExceptionListener(new EveryEntryLdapExceptionListener());
        executorService.submit(impl);
      }
      catch(final LDAPException ldapException)
      {
        resultCode = ldapException.getResultCode();
      }
      catch(final InstantiationException instantiationException)
      {
        err(formatter.format(new LogRecord(Level.SEVERE,"Cannot instantiate " +
                instantiationException.getLocalizedMessage())));
        resultCode = ResultCode.PARAM_ERROR;
      }
      catch(final IllegalAccessException e)
      {
        resultCode = ResultCode.OPERATIONS_ERROR;
      }
      catch(final ClassNotFoundException classNotFoundException)
      {
        err(formatter.format(new LogRecord(Level.SEVERE,String.format(
                "The class '%s' specified as the search "
                        + "result listener could not be found.",searchListenerClassname))));
        resultCode = ResultCode.PARAM_ERROR;
      }
    }
    return resultCode;
  }



  /**
   * Constructs a new {@code EveryEntry} object that will use the
   * System.out and System.err output streams.
   */
  public EveryEntry()
  {
    super(System.out,System.err);
    formatter = new MinimalLogFormatter();
  }



  /**
   * The command line arguments processor.
   */
  private EveryEntryCommandLineOptions commandLineOptions;



  /**
   * Provides services for clients that require messages to be formatted
   * in a standardized way.
   */
  private final MinimalLogFormatter formatter;
}


/**
 * Provides command line argument services local to {@code EveryEntry}
 * including any command line arguments that used by {@code EveryEntry}.
 */
final class EveryEntryCommandLineOptions
        extends CommandLineOptions
{

  /**
   * The description of the search result listener command line
   * argument.
   */
  private static final String DESCRIPTION_SEARCH_RESULT_LISTENER =
          "The name of class which extends the AbstractSearchResultListener class. "
                  + "The searchEntryReturned method of this class is invoked when "
                  + "an entry is returned from a search.";



  /**
   * The isRequired parameter of the command line argument whose
   * parameter is the name of a class that extends
   * {@code SearchResultListener}.
   */
  private static final boolean IS_REQUIRED_SEARCH_RESULT_LISTENER = true;



  /**
   * The long identifier of the command line argument whose parameter is
   * the name of a class that extends {@code SearchResultListener}.
   */
  private static final String LONG_ID_SEARCH_RESULT_LISTENER = "searchResultListener";



  /**
   * The short identifier of the command line argument whose parameter
   * is now many times the search result listener may occur on the
   * command line.
   */
  private static final int MAX_OCCURRENCES_SEARCH_RESULT_LISTENER = 0;



  /**
   * The short identifier of the command line argument whose parameter
   * indicates whether the search result listener command line argument
   * is required.
   */
  private static final Character SHORT_ID_SEARCH_RESULT_LISTENER = null;



  /**
   * The value place-holder of the command line argument whose parameter
   * is the value place-holder of the search result listener command
   * line argument.
   */
  private static final String VALUE_PLACEHOLDER_SEARCH_RESULT_LISTENER = "{class-name}";



  /**
   * Get a new instance of {@code EveryEntryCommandLineOptions}. The
   * {@code argumentParser} (which cannot be {@code null}) is used to
   * add an additional command line argument.
   * 
   * @param argumentParser
   *          handles the task of parsing command line arguments.
   * @return a new instance of {@code EveryEntryCommandLineOptions}.
   * @throws ArgumentException
   *           if a problem transpired creating and adding an
   *           {@code Argument}.
   */
  public static EveryEntryCommandLineOptions newEveryEntryCommandLineOptions(
          final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    return new EveryEntryCommandLineOptions(argumentParser);
  }



  /**
   * Retrieves the parameter of the command line argument that specifies
   * the name of the class used as the search result listener.
   * 
   * @return The search result listener classname.
   */
  public String getSearchResultListenerClassname()
  {
    final StringArgument searchResultListenerArg =
            (StringArgument)getArgumentParser().getNamedArgument(
                    EveryEntryCommandLineOptions.LONG_ID_SEARCH_RESULT_LISTENER);
    return searchResultListenerArg.getValue();
  }



  /**
   * Create the argument used for transmitting the desired search result
   * listener classname.
   * 
   * @return a command line {@code Argument}.
   * @throws ArgumentException
   *           if a problem transpires creating the argument.
   */
  private Argument newSearchResultListenerArgument() throws ArgumentException
  {
    final Character shortIdentifier =
            EveryEntryCommandLineOptions.SHORT_ID_SEARCH_RESULT_LISTENER;
    final String longIdentifier = EveryEntryCommandLineOptions.LONG_ID_SEARCH_RESULT_LISTENER;
    final boolean isRequired = EveryEntryCommandLineOptions.IS_REQUIRED_SEARCH_RESULT_LISTENER;
    final int maxOccurrences =
            EveryEntryCommandLineOptions.MAX_OCCURRENCES_SEARCH_RESULT_LISTENER;
    final String valuePlaceholder =
            EveryEntryCommandLineOptions.VALUE_PLACEHOLDER_SEARCH_RESULT_LISTENER;
    final String description = EveryEntryCommandLineOptions.DESCRIPTION_SEARCH_RESULT_LISTENER;
    return new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
            valuePlaceholder,description);
  }



  private EveryEntryCommandLineOptions(
          final ArgumentParser argumentParser)
          throws ArgumentException
  {
    super(argumentParser);
    final Argument searchResultListenerArgument = newSearchResultListenerArgument();
    addArguments(searchResultListenerArgument);
  }
}


/**
 * Invokes the methods of {@code SearchResultListener} for entry entry
 * returned from a search request. Supply error listeners that will be
 * notified when an error or exception occurs.
 */
final class EveryEntryImpl
        implements Runnable,ObservedByLdapExceptionListener
{

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
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
          final LDAPException ldapException)
  {
    Validator.ensureNotNull(ldapConnection,ldapException);
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



  /**
   * {@inheritDoc}
   * <p>
   * Transmits the search request to the server and returns.
   */
  @Override
  public void run()
  {
    ResultCode resultCode;
    try
    {
      final long begin = System.currentTimeMillis();
      resultCode = search();
      @SuppressWarnings("unused")
      final long elapsed = System.currentTimeMillis() - begin;
      if(!resultCode.equals(ResultCode.SUCCESS))
      {
        notifyErrorListeners(resultCode);
      }
    }
    catch(final LDAPSearchException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      resultCode = ldapException.getResultCode();
      notifyErrorListeners(resultCode);
    }
  }



  @Override
  public String toString()
  {
    final int maxLen = 10;
    return "EveryEntryImpl [" +
            (commandLineOptions != null ? "commandLineOptions=" + commandLineOptions + ", "
                    : "") +
            (errorListeners != null ? "errorListeners=" +
                    errorListeners.subList(0,Math.min(errorListeners.size(),maxLen)) + ", "
                    : "") +
            (errStream != null ? "errStream=" + errStream + ", " : "") +
            (ldapConnection != null ? "ldapConnection=" + ldapConnection + ", " : "") +
            (searchListenerClassname != null ? "searchListenerClassname=" +
                    searchListenerClassname + ", " : "") +
            (searchRequest != null ? "searchRequest=" + searchRequest : "") + "]";
  }



  /**
   * Create the search request using the user-provided command line
   * argument parameters. Provide the size limit and time limit from the
   * command line argument parameters.
   * 
   * @throws LDAPException
   *           If the search request cannot be created.
   * @see CommandLineOptions
   */
  private SearchRequest createSearchRequest(
          final AbstractSearchResultListener searchResultListener) throws LDAPException
  {
    Validator.ensureNotNull(searchResultListener);
    SearchRequest sr;
    final String baseObject = commandLineOptions.getBaseObject();
    final SearchScope scope = commandLineOptions.getSearchScope();
    final Filter filter = commandLineOptions.getFilter();
    final String[] requestedAttributes = commandLineOptions.getRequestedAttributes();
    sr = new SearchRequest(searchResultListener,baseObject,scope,filter,requestedAttributes);
    final int sizeLimit = commandLineOptions.getSizeLimit();
    sr.setSizeLimit(sizeLimit);
    final int timeLimit = commandLineOptions.getTimeLimit();
    sr.setTimeLimitSeconds(timeLimit);
    return sr;
  }



  /**
   * Create the search result listener specified by the
   * {@code --searchResultListener} command line arguments.
   */
  private AbstractSearchResultListener newSearchResultListener() throws ClassNotFoundException,
          InstantiationException,IllegalAccessException
  {
    @SuppressWarnings("unchecked")
    final Class<? extends AbstractSearchResultListener> cl =
            (Class<? extends AbstractSearchResultListener>)Class
                    .forName(searchListenerClassname);
    final AbstractSearchResultListener searchResultListener = cl.newInstance();
    searchResultListener.setCommandLineOptions(commandLineOptions);
    searchResultListener.setLDAPConnection(ldapConnection);
    return searchResultListener;
  }



  /**
   * Notify each error listener in their natural ordering that an error
   * has occurred.
   * 
   * @param resultCode
   *          The result code of an operation
   */
  private void notifyErrorListeners(final ResultCode resultCode)
  {
    Validator.ensureNotNull(resultCode);
    for(final ErrorListener<ResultCode> l : errorListeners)
    {
      l.displayError(errStream,resultCode);
    }
  }



  /**
   * Transmits a search request on the {@code ldapConnection}. The
   * search result entries and search result references are handled by
   * the search result listener.
   * 
   * @return The result code from the response result.
   * @throws LDAPSearchException
   *           If the server rejects the request, or if a problem is
   *           encountered while sending the request or reading the
   *           response.
   */
  private ResultCode search() throws LDAPSearchException
  {
    final SearchResult searchResult = ldapConnection.search(searchRequest);
    return searchResult.getResultCode();
  }



  /**
   * Set options on the connection to the server. The options are taken
   * from the parameters of the command line arguments.
   * 
   * @see CommandLineOptions
   */
  private void setConnectionOptions()
  {
    final LDAPConnectionOptions connectionOptions =
            commandLineOptions.newLDAPConnectionOptions();
    ldapConnection.setConnectionOptions(connectionOptions);
  }



  /**
   * Get a new instance of {@code EveryEntryImpl}. None of the
   * parameters are permitted to be {@code null}.
   * 
   * @param searchListenerClassname
   *          the name of the class to be used as the search result
   *          listener.
   * @param commandLineOptions
   *          user-provided command line options.
   * @param ldapConnection
   *          a connection to an LDAP server.
   * @param errStream
   *          a stream to which error output is transmitted.
   * @param errorListeners
   *          they are notified when an error or exception transpires.
   * @throws LDAPException
   *           if a {@code SearchRequest} cannot be created using
   *           parameters from the command line arguments.
   * @throws InstantiationException
   *           if the class named by {@code searchListenerClassname}
   *           cannot be instantiated.
   * @throws IllegalAccessException
   *           if the class named by {@code searchListenerClassname}
   *           cannot be instantiated.
   * @throws ClassNotFoundException
   *           if the class named by {@code searchListenerClassname}
   *           cannot be found.
   */
  public EveryEntryImpl(
          final String searchListenerClassname,
          final EveryEntryCommandLineOptions commandLineOptions,
          final LDAPConnection ldapConnection,final PrintStream errStream,
          final List<ErrorListener<ResultCode>> errorListeners)
          throws LDAPException,InstantiationException,IllegalAccessException,
          ClassNotFoundException
  {
    Validator.ensureNotNull(searchListenerClassname,commandLineOptions,ldapConnection,
            errStream,errorListeners);
    this.errStream = errStream;
    this.errorListeners = errorListeners;
    this.searchListenerClassname = searchListenerClassname;
    this.commandLineOptions = commandLineOptions;
    this.ldapConnection = ldapConnection;
    setConnectionOptions();
    searchRequest = createSearchRequest(newSearchResultListener());
  }



  private final EveryEntryCommandLineOptions commandLineOptions;



  private final List<ErrorListener<ResultCode>> errorListeners;



  private final PrintStream errStream;



  private final LDAPConnection ldapConnection;



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
          new Vector<LdapExceptionListener>();



  private final String searchListenerClassname;



  private final SearchRequest searchRequest;

}


@NotMutable
final class EveryEntryLdapExceptionListener
        implements LdapExceptionListener
{

  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    System.err.println(formatter.format(new LogRecord(Level.SEVERE,ldapExceptionEvent
            .getLdapException().getExceptionMessage())));
  }



  /**
   * Provides services for clients that require messages to be formatted
   * in a standardized way.
   */
  private final MinimalLogFormatter formatter = new MinimalLogFormatter();
}


/**
 * An implementation of the {@code ErrorListener} that sends a message
 * on the error stream that includes a {@code ResultCode}.
 */
@NotMutable
final class ResultCodeErrorListener
        implements ErrorListener<ResultCode>
{

  /**
   * {@inheritDoc}
   * <p>
   * Sends a message on the {@code errStream} that will include the
   * {@code resultCode}.
   */
  @Override
  public void displayError(final PrintStream errStream,final ResultCode resultCode)
  {
    Validator.ensureNotNull(resultCode);
    final String msg = String.format("An error condition occurred: %s",resultCode);
    final LogRecord record = new LogRecord(Level.SEVERE,msg);
    errStream.println(new MinimalLogFormatter().format(record));
  }

}
