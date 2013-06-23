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

package samplecode.bind;

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.PasswordExpiredControl;
import com.unboundid.ldap.sdk.controls.PasswordExpiringControl;
import com.unboundid.util.CommandLineTool;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.cli.CommandLineOptions;
import samplecode.controls.ResponseControlAware;
import samplecode.display.ControlDisplayValues;
import samplecode.ldap.DefaultUnsolicitedNotificationHandler;
import samplecode.listener.*;
import samplecode.tools.AbstractTool;
import samplecode.tools.BasicToolCompletedProcessing;
import samplecode.tools.ToolCompletedProcessing;
import samplecode.util.SampleCodeCollectionUtils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * Provides a demonstration of authenticating to a directory server.
 */
@Author("terry.gardner@unboundid.com")
@Since("01-Sep-2011")
@CodeVersion("1.25")
@Launchable
public final class BindDemo extends AbstractTool
  implements LdapExceptionListener, ObservedByLdapExceptionListener,
  ObservedByLdapSearchExceptionListener, LdapSearchExceptionListener {


  /**
   * <blockquote>
   * <p/>
   * <pre>
   * Demonstrate the use of the bind request
   *
   * Usage:  BindDemo {options}
   *
   * Available options include:
   * -h,--hostname {host}
   *     The IP address or resolvable name to use to connect to the directory
   *     server.  If this is not provided,then a default value of 'localhost'
   *     will  be used.
   * -p,--port {port}
   *     The port to use to connect to the directory server.  If this is not
   *     provided,then a default value of 389 will be used.
   * -D,--bindDN {dn}
   *     The DN to use to bind to the directory server when performing simple
   *     authentication.
   * -w,--bindPassword {password}
   *     The password to use to bind to the directory server when performing
   *     simple authentication or a password-based SASL mechanism.
   * -j,--bindPasswordFile {path}
   *     The path to the file containing the password to use to bind to the
   *     directory server when performing simple authentication or a
   *     password-based SASL mechanism.
   * -Z,--useSSL
   *     Use SSL when communicating with the directory server.
   * -q,--useStartTLS
   *     Use StartTLS when communicating with the directory server.
   * -X,--trustAll
   *     Trust any certificate presented by the directory server.
   * -K,--keyStorePath {path}
   *     The path to the file to use as the key store for obtaining client
   *     certificates when communicating securely with the directory server.
   * -W,--keyStorePassword {password}
   *     The password to use to access the key store contents.
   * -u,--keyStorePasswordFile {path}
   *     The path to the file containing the password to use to access the
   *     key store contents.
   * --keyStoreFormat {format}
   *     The format (e.g.,jks,jceks,pkcs12,etc.) for the key store file.
   * -P,--trustStorePath {path}
   *     The path to the file to use as trust store when determining whether to
   *     trust a certificate presented by the directory server.
   * -T,--trustStorePassword {password}
   *     The password to use to access the trust store contents.
   * -U,--trustStorePasswordFile {path}
   *     The path to the file containing the password to use to access the
   * trust
   *     store contents.
   * --trustStoreFormat {format}
   *     The format (e.g.,jks,jceks,pkcs12,etc.) for the trust store file.
   * -N,--certNickname {nickname}
   *     The nickname (alias) of the client certificate in the key store to
   *     present to the directory server for SSL client authentication.
   * -o,--saslOption {name=value}
   *     A name-value pair providing information to use when performing SASL
   *     authentication.
   * -b,--baseObject {distinguishedName}
   *     The base object used in the search request.
   * -f,--filter {filter}
   *     The search filter used in the search request.
   * -i,--initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server
   * when
   *     creating the connection pool.
   * -m,--maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server
   * when
   *     creating the connection pool.
   * -s,--scope {searchScope}
   *     The scope of the search request
   * --sizeLimit {positiveInteger}
   *     The search size limit
   * --timeLimit {positiveInteger}
   *     The search time limit
   * --pageSize {positiveInteger}
   *     The search page size
   * -H,-?,--help
   *     Display usage information for this program.
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param args
   *   JVM command line options.
   *
   * @see CommandLineTool
   */
  public static void main(final String... args) {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final BindDemo bindDemo = new BindDemo(outStream,errStream);
    bindDemo.addLdapExceptionListener(bindDemo);
    bindDemo.addResponseControlHandler(BindDemo.responseControlHandler);
    final ResultCode resultCode = bindDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
      new BasicToolCompletedProcessing(bindDemo,resultCode);
    final Log logger = LogFactory.getLog(BindDemo.class);
    completedProcessing.displayMessage(logger);
    if(!resultCode.equals(ResultCode.SUCCESS)) {
      System.exit(resultCode.intValue());
    }
  }



  /**
   * The response control handler is used to process any response
   * controls attached to the bind response.
   */
  private static final ResponseControlAware responseControlHandler =
    new ResponseControlAware() {

      /**
       * {@inheritDoc}
       * <p>
       * For the purposes of the demonstration,this method returns an
       * indication that {@code processResponseControl} is always safe to
       * invoke.
       */
      @Override
      public boolean invoke() {
        return true;
      }



      /**
       * {@inheritDoc}
       * <p>
       * Checks for the presence of the {@code PasswordExpiredControl} and
       * the {@code PasswordExpiringControl} and appends text to the
       * {@code builder} field that describes whether the controls are
       * present.
       */
      @Override
      public void processResponseControl(final LDAPResult ldapResult)
        throws LDAPException {
        if(ldapResult != null) {
          /*
           * The server may have included the password expired response
           * control which may be included in the response for an
           * unsuccessful bind operation to indicate that the reason for
           * the failure is that the target user's password has expired
           * and must be reset before the user will be allowed to
           * authenticate. Some servers may also include this control in a
           * successful bind response to indicate that the authenticated
           * user must change his or her password before being allowed to
           * perform any other operation.
           */
          final PasswordExpiredControl passwordExpiredControl =
            PasswordExpiredControl.get(ldapResult);
          if(passwordExpiredControl == null) {
            msg = "PasswordExpiredControl was not included in the" +
              " bind response";
            getLogger().log(Level.INFO,msg);
          } else {
            msg = "PasswordExpiredControl included in the bind " +
              "response";
            getLogger().log(Level.INFO,msg);
          }

          /*
           * The server may have included the password expiring response
           * control. It may be used to indicate that the authenticated
           * user's password will expire in the near future. The value of
           * this control includes the length of time in seconds until the
           * user's password actually expires.
           */
          final PasswordExpiringControl passwordExpiringControl =
            PasswordExpiringControl.get(ldapResult);
          if(passwordExpiringControl == null) {
            msg = "PasswordExpiringControl not included in the " +
              "bind response";
            getLogger().log(Level.INFO,msg);
          } else {
            msg = "PasswordExpiringControl included in the bind " +
              "response";
            getLogger().log(Level.INFO,msg);
          }
        }
      }



      private String msg;



      Logger getLogger() {
        return Logger.getLogger(getClass().getName());
      }

    };



  public BindDemo(final OutputStream outStream,
                  final OutputStream errStream) {
    super(outStream,errStream);

  }



  /**
   * Adds the specified {@code responseControlHandler} to the list of
   * handlers to be invoked when response controls have been added to
   * the response by the server. No {@code null} response controls
   * handlers can be added to the list of response control handlers.
   *
   * @param responseControlHandler
   *   A response control handler to be invoked when response
   *   controls have been added to the response by the server. If
   *   {@code responseControlHandler} is {@code null},no action
   *   is taken and no exception is thrown.
   */
  public void addResponseControlHandler
  (final ResponseControlAware responseControlHandler) {
    if(responseControlHandler != null) {
      getResponseControlHandlers().add(responseControlHandler);
    }
  }



  /**
   * Fetches the list of response control handlers.
   *
   * @return the list of response control handlers.
   */
  public List<ResponseControlAware> getResponseControlHandlers() {
    if(responseControlHandlers == null) {
      responseControlHandlers = new ArrayList<ResponseControlAware>();
    }
    return responseControlHandlers;
  }



  @Override
  public synchronized void addLdapSearchExceptionListener
    (final LdapSearchExceptionListener ldapSearchExceptionListener) {
    if(ldapSearchExceptionListener != null) {
      ldapSearchExceptionListeners.add(ldapSearchExceptionListener);
    }
  }



  @Override
  public void fireLdapSearchExceptionListener(final LDAPConnection ldapConnection,
                                              final LDAPSearchException ldapSearchException) {
    ensureNotNull(ldapConnection,ldapSearchException);

    List<LdapSearchExceptionListener> copy;
    synchronized(this) {
      copy = SampleCodeCollectionUtils.newArrayList(ldapSearchExceptionListeners);
    }
    if(copy.size() == 0) {
      return;
    }
    final LdapSearchExceptionEvent ev =
      new LdapSearchExceptionEvent(this,ldapConnection,ldapSearchException);
    for(final LdapSearchExceptionListener l : copy) {
      l.searchRequestFailed(ev);
    }
  }



  @Override
  public synchronized void removeLdapSearchExceptionListener(
    final LdapSearchExceptionListener ldapSearchExceptionListener) {
    if(ldapSearchExceptionListener != null) {
      ldapSearchExceptionListeners.remove
        (ldapSearchExceptionListener);
    }
  }



  @Override
  public String toString() {
    return "BindDemo [" +
      (commandLineOptions != null ? "commandLineOptions=" +
        commandLineOptions : "") +
      "]";
  }



  @Override
  public void searchRequestFailed
    (final LdapSearchExceptionEvent ldapSearchExceptionEvent) {
    ensureNotNull(ldapSearchExceptionEvent);

    if(getLogger().isInfoEnabled()) {
      final String exceptionMessage =
        ldapSearchExceptionEvent.getLdapSearchException().getExceptionMessage();
      getLogger().info(exceptionMessage);
    }
  }



  /**
   * Removes the specified {@code responseControlHandler} from the list
   * of handlers to be invoked when response controls have been added to
   * the response by the server.
   *
   * @param responseControlHandler
   *   A response control handler that must have been previously
   *   added to the list of response control handlers. If
   *   {@code responseControlHandler} is {@code null},no action
   *   is taken and no exception is thrown.
   */
  @SuppressWarnings("unused")
  public void removeResponseControlHandler
  (final ResponseControlAware responseControlHandler) {
    if(responseControlHandler != null) {
      getResponseControlHandlers().remove(responseControlHandler);
    }
  }



  /**
   * Mark {@code --bindDN} and {@code --bindPassword} as required arguments.
   */
  @Override
  protected void addArguments(final ArgumentParser argumentParser)
    throws ArgumentException {
    String argName = CommandLineOptions.ARG_NAME_BIND_DN;
    final Argument bindDNArg = argumentParser.getNamedArgument(argName);
    argName = CommandLineOptions.ARG_NAME_BIND_PASSWORD;
    final Argument bindPasswordArg = argumentParser.getNamedArgument(argName);
    argumentParser.addRequiredArgumentSet(bindDNArg,bindPasswordArg);
  }



  @Override
  public UnsolicitedNotificationHandler getUnsolicitedNotificationHandler() {
    return new DefaultUnsolicitedNotificationHandler(this);
  }



  @Override
  public ResultCode executeToolTasks() {

    if(getLogger().isInfoEnabled()) {
      final DN dn = commandLineOptions.getBindDn();
      final String msg = String.format("Using distinguished name '%s'",dn);
      getLogger().info(msg);
    }

    /*
     * Obtain a pool of connections to the LDAP server from the
     * LDAPCommandLineTool services,this requires specifying a
     * connection to the LDAP server,a number of initial connections
     * (--initialConnections) in the pool,and the maximum number of
     * connections (--maxConnections) that the pool should create.
     */
    try {
      if(getLogger().isInfoEnabled()) {
        final String hostname = commandLineOptions.getHostname();
        final String msg =
          String.format("Establishing connections to %s.",hostname);
        getLogger().info(msg);
      }
      ldapConnection = connectToServer();
      ldapConnectionPool = getLdapConnectionPool(ldapConnection);
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      if(ldapConnection != null) {
        ldapConnection.close();
      }
      return ldapException.getResultCode();
    }

    /*
     * Authenticate to directory server.
     */
    final BindResult bindResult;
    final String bindDN = commandLineOptions.getBindDn().toString();
    final String bindPassword = commandLineOptions.getBindPassword();
    try {
      final BindRequest bindRequest =
        new SimpleBindRequest(bindDN,bindPassword);
      if(getLogger().isInfoEnabled()) {
        getLogger().info("transmitting bind request");
      }
      bindResult = ldapConnectionPool.bind(bindRequest);
    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      ldapConnection.close();
      return ldapException.getResultCode();
    }

    /*
     * Handle response controls that may be attached to the bind
     * response. Response controls that might be attached are the
     * PasswordExpiredControl and the PasswordExpiringControl.
     */
    final List<ResponseControlAware> handlers = getResponseControlHandlers();
    for(final ResponseControlAware responseControlHandler : handlers) {
      if(responseControlHandler.invoke()) {
        try {
          responseControlHandler.processResponseControl(bindResult);
        } catch(final LDAPException ldapException) {
          fireLdapExceptionListener(ldapConnection,ldapException);
          ldapConnection.close();
          return ldapException.getResultCode();
        }
      }
    }

    /*
     * Construct a search request. Set a size limit with the value from
     * the {@code --sizeLimit} command line argument and a time limit
     * with the value from the {@code --timeLimit} command line
     * argument.
     */
    final SearchRequest searchRequest;
    try {
      final String baseObject = commandLineOptions.getBaseObject();
      final SearchScope searchScope = commandLineOptions.getSearchScope();
      Filter filter = commandLineOptions.getFilter();
      if(filter == null) {
        filter = Filter.createPresenceFilter("objectClass");
      }
      if(getLogger().isTraceEnabled()) {
        getLogger().trace("Creating search request with filter "
          + filter + ", base object " + baseObject + ", " +
          "and scope " + searchScope);
      }
      final List<String> requestedAttributes =
        commandLineOptions.getRequestedAttributes();
      final String[] attributes = new String[requestedAttributes.size()];
      requestedAttributes.toArray(attributes);
      searchRequest =
        new SearchRequest(baseObject,searchScope,filter,attributes);

      final int sizeLimit = commandLineOptions.getSizeLimit();
      searchRequest.setSizeLimit(sizeLimit);

      final int timeLimit = commandLineOptions.getTimeLimit();
      searchRequest.setTimeLimitSeconds(timeLimit);

    } catch(final LDAPException ldapException) {
      fireLdapExceptionListener(ldapConnection,ldapException);
      ldapConnection.close();
      return ldapException.getResultCode();
    }

    /*
     * Issue search request:
     */
    final SearchResult searchResult;
    try {
      if(commandLineOptions.isVerbose()) {
        getLogger().trace("transmitting search request: " +
          searchRequest);
      }
      searchResult = ldapConnectionPool.search(searchRequest);
    } catch(final LDAPSearchException ldapSearchException) {
      fireLdapSearchExceptionListener(ldapConnection,
        ldapSearchException);
      ldapConnection.close();
      return ldapSearchException.getResultCode();
    }

    /*
     * Handle response controls that may be attached to the search
     * response.
     */
    if(searchResult != null) {
      String msg;
      if(searchResult.hasResponseControl()) {
        for(final Control control : searchResult.getResponseControls()) {
          final ControlDisplayValues controlDisplayValues =
            new ControlDisplayValues(control);
          msg = (String) controlDisplayValues.msg();
          if(getLogger().isInfoEnabled()) {
            getLogger().info(msg);
          }
        }
      } else {
        if(getLogger().isInfoEnabled()) {
          msg = "no response controls attached to search " +
            "response.";
          getLogger().info(msg);
        }
      }
    }

    ldapConnectionPool.close();

    return ResultCode.SUCCESS;
  }



  @Override
  protected String classSpecificPropertiesResourceName() {
    return "BindDemo.properties";
  }



  // A list of objects which perform tasks on response controls
  private List<ResponseControlAware> responseControlHandlers;
}
