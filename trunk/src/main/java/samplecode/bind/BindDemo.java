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



import com.unboundid.ldap.sdk.BindResult;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.ldap.sdk.controls.PasswordExpiredControl;
import com.unboundid.ldap.sdk.controls.PasswordExpiringControl;
import com.unboundid.util.CommandLineTool;
import com.unboundid.util.Validator;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.controls.ResponseControlAware;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdapSearchExceptionEvent;
import samplecode.listener.LdapSearchExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.listener.ObservedByLdapSearchExceptionListener;
import samplecode.tools.AbstractTool;
import samplecode.tools.BasicToolCompletedProcessing;
import samplecode.tools.ToolCompletedProcessing;


/**
 * Provides a demonstration of authenticating to a directory server.
 */
@Author("terry.gardner@unboundid.com")
@Since("01-Sep-2011")
@CodeVersion("1.24")
@Launchable
public final class BindDemo
        extends AbstractTool
        implements LdapExceptionListener,ObservedByLdapExceptionListener,
        ObservedByLdapSearchExceptionListener,LdapSearchExceptionListener
{

  /**
   * Provides services necessary to display the state of a
   * {@code Control}.
   */
  private static class ControlDisplayValues
  {

    /**
     * Display the control in a generic fashion.
     * 
     * @return A string representation of the value of the control.
     */
    public Object msg()
    {

      final StringBuilder builder = new StringBuilder(control.getClass().getCanonicalName());
      builder.append(" ");
      builder.append(control.getControlName());
      builder.append(" ");
      builder.append(control.getOID());
      builder.append(" ");
      if(control instanceof PasswordExpiredControl)
      {
        control.toString(builder);
      }
      else if(control instanceof PasswordExpiringControl)
      {
        control.toString(builder);
      }
      return builder.toString();
    }



    @Override
    public String toString()
    {
      return "ControlDisplayValues [" + (control != null ? "control=" + control : "") + "]";
    }



    /**
     * Constructs a {@code ControlDisplayValues} object from the
     * specified control.
     * 
     * @param control
     *          An LDAP control,which may not be {@code null}.
     */
    public ControlDisplayValues(
            final Control control)
    {
      Validator.ensureNotNull(control);
      this.control = control;
    }



    private final Control control;
  }



  /**
   * The response control handler is used to process any response
   * controls attached to the bind response.
   */
  private static final ResponseControlAware responseControlHandler = new ResponseControlAware()
  {

    /**
     * {@inheritDoc}
     * <p>
     * For the purposes of the demonstration,this method returns an
     * indication that {@code processResponseControl} is always safe to
     * invoke.
     */
    @Override
    public boolean invoke()
    {
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
    public void processResponseControl(final LDAPResult ldapResult) throws LDAPException
    {
      if(ldapResult != null)
      {

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
        if(passwordExpiredControl == null)
        {
          msg = "PasswordExpiredControl was not included in the bind response";
          getLogger().log(Level.INFO,msg);
        }
        else
        {
          msg = "PasswordExpiredControl included in the bind response";
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
        if(passwordExpiringControl == null)
        {
          msg = "PasswordExpiringControl not included in the bind response";
          getLogger().log(Level.INFO,msg);
        }
        else
        {
          msg = "PasswordExpiringControl included in the bind response";
          getLogger().log(Level.INFO,msg);
        }
      }

    }



    Logger getLogger()
    {
      return Logger.getLogger(getClass().getName());
    }



    private String msg;

  };



  /**
   * <blockquote>
   * 
   * <pre>
   * Demonstrate the use of the bind request
   * 
   * Usage:  BindDemo {options}
   * 
   * Available options include:
   * -h,--hostname {host}
   *     The IP address or resolvable name to use to connect to the directory
   *     server.  If this is not provided,then a default value of 'localhost' will
   *     be used.
   * -p,--port {port}
   *     The port to use to connect to the directory server.  If this is not
   *     provided,then a default value of 389 will be used.
   * -D,--bindDN {dn}
   *     The DN to use to bind to the directory server when performing simple
   *     authentication.
   * -w,--bindPassword {password}
   *     The password to use to bind to the directory server when performing simple
   *     authentication or a password-based SASL mechanism.
   * -j,--bindPasswordFile {path}
   *     The path to the file containing the password to use to bind to the
   *     directory server when performing simple authentication or a password-based
   *     SASL mechanism.
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
   *     The path to the file containing the password to use to access the key store
   *     contents.
   * --keyStoreFormat {format}
   *     The format (e.g.,jks,jceks,pkcs12,etc.) for the key store file.
   * -P,--trustStorePath {path}
   *     The path to the file to use as trust store when determining whether to
   *     trust a certificate presented by the directory server.
   * -T,--trustStorePassword {password}
   *     The password to use to access the trust store contents.
   * -U,--trustStorePasswordFile {path}
   *     The path to the file containing the password to use to access the trust
   *     store contents.
   * --trustStoreFormat {format}
   *     The format (e.g.,jks,jceks,pkcs12,etc.) for the trust store file.
   * -N,--certNickname {nickname}
   *     The nickname (alias) of the client certificate in the key store to present
   *     to the directory server for SSL client authentication.
   * -o,--saslOption {name=value}
   *     A name-value pair providing information to use when performing SASL
   *     authentication.
   * -b,--baseObject {distinguishedName}
   *     The base object used in the search request.
   * -f,--filter {filter}
   *     The search filter used in the search request.
   * -i,--initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server when
   *     creating the connection pool.
   * -m,--maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server when
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
   * 
   * </blockquote>
   * 
   * @param args
   *          JVM command line options.
   * @see CommandLineTool
   */
  public static void main(final String... args)
  {

    /*
     * Construct the BindDemo object.
     */
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final BindDemo bindDemo = new BindDemo(outStream,errStream);
    bindDemo.addLdapExceptionListener(bindDemo);
    bindDemo.addResponseControlHandler(BindDemo.responseControlHandler);
    final ResultCode resultCode = bindDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(bindDemo,resultCode);
    completedProcessing.displayMessage(Logger.getLogger(BindDemo.class.getCanonicalName()));
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapSearchExceptionListener(
          final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.add(ldapSearchExceptionListener);
    }
  }



  /**
   * Adds the specified {@code responseControlHandler} to the list of
   * handlers to be invoked when response controls have been added to
   * the response by the server. No {@code null} response controls
   * handlers can be added to the list of response control handlers.
   * 
   * @param responseControlHandler
   *          A response control handler to be invoked when response
   *          controls have been added to the response by the server. If
   *          {@code responseControlHandler} is {@code null},no action
   *          is taken and no exception is thrown.
   */
  public void addResponseControlHandler(final ResponseControlAware responseControlHandler)
  {
    if(responseControlHandler != null)
    {
      responseControlHandlers.add(responseControlHandler);
    }
  }



  @Override
  public ResultCode executeToolTasks()
  {
    introduction();
    if(isVerbose())
    {
      displayArguments();
    }

    /*
     * The tool requires a valid distinguished name with which to bind
     * to directory server. If no DN was provided,print a helpful
     * message and return an indication that the parameter set is
     * invalid.
     */
    if(commandLineOptions.getBindDn() == null)
    {
      final StringBuilder builder = new StringBuilder();
      builder.append(getToolName());
      builder.append(" requires a valid bind DN (--bindDn) to proceed. ");
      builder.append("The --bindDn command line argument did not appear.");
      getLogger().log(Level.SEVERE,builder.toString());
      return ResultCode.PARAM_ERROR;
    }
    getLogger().log(Level.INFO,
            "Using distinguished name \"" + commandLineOptions.getBindDn() + "\"");


    /*
     * Obtain a pool of connections to the LDAP server from the
     * LDAPCommandLineTool services,this requires specifying a
     * connection to the LDAP server,a number of initial connections
     * (--initialConnections) in the pool,and the maximum number of
     * connections (--maxConnections) that the pool should create.
     */
    try
    {
      final String msg =
              String.format("Establishing connections to %s.",commandLineOptions.getHostname());
      getLogger().log(Level.INFO,msg);
      ldapConnection = connectToServer();
      ldapConnectionPool = getLdapConnectionPool(ldapConnection);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }



    /*
     * Authenticate to directory server.
     */
    BindResult bindResult;
    try
    {
      final SimpleBindRequest bindRequest =
              new SimpleBindRequest(commandLineOptions.getBindDn(),
                      commandLineOptions.getBindPassword());
      getLogger().log(Level.INFO,"transmitting bind request");
      bindResult = ldapConnectionPool.bind(bindRequest);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }


    /*
     * Handle response controls that may be attached to the bind
     * response. Response controls that might be attached are the
     * PasswordExpiredControl and the PasswordExpiringControl.
     */
    for(final ResponseControlAware responseControlHandler : responseControlHandlers)
    {
      if(responseControlHandler.invoke())
      {
        try
        {
          responseControlHandler.processResponseControl(bindResult);
        }
        catch(final LDAPException ldapException)
        {
          fireLdapExceptionListener(ldapConnection,ldapException);
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
    try
    {
      searchRequest =
              new SearchRequest(commandLineOptions.getBaseObject(),
                      commandLineOptions.getSearchScope(),commandLineOptions.getFilter(),
                      commandLineOptions.getRequestedAttributes().toArray(new String[0]));
      searchRequest.setSizeLimit(commandLineOptions.getSizeLimit());
      searchRequest.setTimeLimitSeconds(commandLineOptions.getTimeLimit());
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }


    /*
     * Issue search request:
     */
    SearchResult searchResult;
    try
    {
      if(commandLineOptions.isVerbose())
      {
        getLogger().log(Level.INFO,"transmitting search request: " + searchRequest);
      }
      searchResult = ldapConnectionPool.search(searchRequest);
    }
    catch(final LDAPSearchException ldapSearchException)
    {
      fireLdapSearchExceptionListener(ldapConnection,ldapSearchException);
      return ldapSearchException.getResultCode();
    }



    /*
     * Handle response controls that may be attached to the search
     * response.
     */
    if(searchResult != null)
    {
      if(searchResult.hasResponseControl())
      {
        for(final Control control : searchResult.getResponseControls())
        {
          final ControlDisplayValues controlDisplayValues = new ControlDisplayValues(control);
          msg = (String)controlDisplayValues.msg();
          getLogger().log(Level.INFO,msg);
        }
      }
      else
      {
        msg = "no response controls attached to search response.";
        getLogger().log(Level.INFO,msg);
      }
    }

    ldapConnectionPool.close();

    return ResultCode.SUCCESS;
  }



  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapSearchExceptionListener(final LDAPConnection ldapConnection,
          final LDAPSearchException ldapSearchException)
  {
    Validator.ensureNotNull(ldapConnection,ldapSearchException);
    Vector<LdapSearchExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapSearchExceptionListener>)ldapSearchExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapSearchExceptionEvent ev =
            new LdapSearchExceptionEvent(this,ldapConnection,ldapSearchException);
    for(final LdapSearchExceptionListener l : copy)
    {
      l.searchRequestFailed(ev);
    }
  }



  @Override
  public Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  @Override
  public UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapSearchExceptionListener(
          final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.remove(ldapSearchExceptionListener);
    }
  }



  /**
   * Removes the specified {@code responseControlHandler} from the list
   * of handlers to be invoked when response controls have been added to
   * the response by the server.
   * 
   * @param responseControlHandler
   *          A response control handler that must have been previously
   *          added to the list of response control handlers. If
   *          {@code responseControlHandler} is {@code null},no action
   *          is taken and no exception is thrown.
   */
  public void removeResponseControlHandler(final ResponseControlAware responseControlHandler)
  {
    if(responseControlHandler != null)
    {
      responseControlHandlers.remove(responseControlHandler);
    }
  }



  @Override
  public void searchRequestFailed(final LdapSearchExceptionEvent ldapSearchExceptionEvent)
  {
    getLogger().log(Level.SEVERE,
            ldapSearchExceptionEvent.getLdapSearchException().getExceptionMessage());
  }



  @Override
  public String toString()
  {
    return "BindDemo [" +
            (commandLineOptions != null ? "commandLineOptions=" + commandLineOptions : "") +
            "]";
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "BindDemo.properties";
  }



  /**
   * Prepares {@code BindDemo} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public BindDemo()
  {
    this(System.out,System.err);
  }



  private BindDemo(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
    responseControlHandlers = new ArrayList<ResponseControlAware>();
  }



  private String msg;



  private final List<ResponseControlAware> responseControlHandlers;
}
