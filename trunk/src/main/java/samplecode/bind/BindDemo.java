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
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.controls.*;
import com.unboundid.util.CommandLineTool;
import com.unboundid.util.args.*;
import java.io.OutputStream;
import java.util.*;
import java.util.logging.*;
import org.apache.commons.logging.*;
import samplecode.annotation.*;
import samplecode.cli.CommandLineOptions;
import samplecode.controls.ResponseControlAware;
import samplecode.display.ControlDisplayValues;
import samplecode.listener.*;
import samplecode.tools.*;
import samplecode.util.SampleCodeCollectionUtils;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * Provides a demonstration of authenticating to a directory server.
 *
 * @author Terry Gardner
 */
@Since("01-Sep-2011")
@CodeVersion("1.26")
@Launchable
public final class BindDemo extends AbstractTool
   implements LdapExceptionListener, ObservedByLdapExceptionListener,
   ObservedByLdapSearchExceptionListener, LdapSearchExceptionListener
{

   /**
    * The names of each of the required arguments.
    */
   private static final String[] requiredArgumentNames = {
      CommandLineOptions.ARG_NAME_BASE_OBJECT,
      CommandLineOptions.ARG_NAME_BIND_DN,
      CommandLineOptions.ARG_NAME_BIND_PASSWORD,
      CommandLineOptions.ARG_NAME_FILTER,
      CommandLineOptions.ARG_NAME_SCOPE,
   };


   /**
    * The response control handler is used to process any response controls attached to the bind
    * response.
    */
   private static final ResponseControlAware responseControlHandler =
      new ResponseControlAware()
      {

         private String msg;


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
         public void processResponseControl(final LDAPResult ldapResult)
            throws LDAPException
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
                  msg = "PasswordExpiredControl was not included in the" +
                     " bind response";
                  getLogger().log(Level.INFO,msg);
               }
               else
               {
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
               if(passwordExpiringControl == null)
               {
                  msg = "PasswordExpiringControl not included in the " +
                     "bind response";
                  getLogger().log(Level.INFO,msg);
               }
               else
               {
                  msg = "PasswordExpiringControl included in the bind " +
                     "response";
                  getLogger().log(Level.INFO,msg);
               }
            }
         }


         Logger getLogger()
         {
            return Logger.getLogger(getClass().getName());
         }

      };


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
    *    JVM command line options.
    *
    * @see CommandLineTool
    */
   public static void main(String... args)
   {
      BindDemo bindDemo = new BindDemo(System.out,System.err);
      bindDemo.addLdapExceptionListener(bindDemo);
      bindDemo.addResponseControlHandler(BindDemo.responseControlHandler);
      ResultCode resultCode = bindDemo.runTool(args);
      ToolCompletedProcessing completedProcessing =
         new BasicToolCompletedProcessing(bindDemo,resultCode);
      Log logger = LogFactory.getLog(BindDemo.class);
      completedProcessing.displayMessage(logger);
      if(!resultCode.equals(ResultCode.SUCCESS))
      {
         System.exit(resultCode.intValue());
      }
   }


   // A list of objects which perform tasks on response controls
   private List<ResponseControlAware> responseControlHandlers;


   private DN dn;


   /**
    * Initializes this object using the provided outStream and errStream.
    *
    * @param outStream
    *    regular output
    * @param errStream
    *    error output
    */
   public BindDemo(OutputStream outStream,OutputStream errStream)
   {
      super(outStream,errStream);
   }


   /**
    * Adds the specified {@code responseControlHandler} to the list of handlers to be invoked when
    * response controls have been added to the response by the server. No {@code null} response
    * controls handlers can be added to the list of response control handlers.
    *
    * @param responseControlHandler
    *    A response control handler to be invoked when response controls have been added to the
    *    response by the server. If {@code responseControlHandler} is {@code null},no action is
    *    taken
    *    and no exception is thrown.
    */
   public void addResponseControlHandler
   (ResponseControlAware responseControlHandler)
   {
      if(responseControlHandler != null)
      {
         getResponseControlHandlers().add(responseControlHandler);
      }
   }


   /**
    * Fetches a list of response control handlers.
    *
    * @return a list of response control handlers.
    */
   public List<ResponseControlAware> getResponseControlHandlers()
   {
      if(responseControlHandlers == null)
      {
         responseControlHandlers = SampleCodeCollectionUtils.newArrayList();
      }
      return responseControlHandlers;
   }


   @Override
   public synchronized void addLdapSearchExceptionListener
      (LdapSearchExceptionListener ldapSearchExceptionListener)
   {
      if(ldapSearchExceptionListener != null)
      {
         ldapSearchExceptionListeners.add(ldapSearchExceptionListener);
      }
   }


   @Override
   public void fireLdapSearchExceptionListener(final LDAPConnection ldapConnection,
      final LDAPSearchException ldapSearchException)
   {
      ensureNotNull(ldapConnection,ldapSearchException);

      List<LdapSearchExceptionListener> copy;
      synchronized(this)
      {
         copy = SampleCodeCollectionUtils.newArrayList(ldapSearchExceptionListeners);
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
   public synchronized void removeLdapSearchExceptionListener(
      final LdapSearchExceptionListener ldapSearchExceptionListener)
   {
      if(ldapSearchExceptionListener != null)
      {
         ldapSearchExceptionListeners.remove
            (ldapSearchExceptionListener);
      }
   }


   /**
    * Removes the specified {@code responseControlHandler} from the list of handlers to be invoked
    * when response controls have been added to the response by the server.
    *
    * @param responseControlHandler
    *    A response control handler that must have been previously added to the list of response
    *    control handlers. If {@code responseControlHandler} is {@code null},no action is taken and
    *    no
    *    exception is thrown.
    */
   @SuppressWarnings("unused")
   public void removeResponseControlHandler
   (ResponseControlAware responseControlHandler)
   {
      if(responseControlHandler != null)
      {
         getResponseControlHandlers().remove(responseControlHandler);
      }
   }


   @Override
   public void searchRequestFailed(LdapSearchExceptionEvent ldapSearchExceptionEvent)
   {
      ensureNotNull(ldapSearchExceptionEvent);

      if(getLogger().isInfoEnabled())
      {
         final String exceptionMessage =
            ldapSearchExceptionEvent.getLdapSearchException().getExceptionMessage();
         getLogger().info(exceptionMessage);
      }
   }


   @Override
   public ResultCode executeToolTasks()
   {
      getCommandLineArguments();
      try
      {
         getLDAPConnections();
         authenticate();
         search();
      }
      catch(LDAPException e)
      {
         err(e);
         ldapConnectionPool.close(true,getNumCloseThreads());
         return e.getResultCode();
      }

      ldapConnectionPool.close(true,getNumCloseThreads());

      return ResultCode.SUCCESS;
   }


   @Override
   protected String classSpecificPropertiesResourceName()
   {
      return "BindDemo.properties";
   }


   /**
    * Marks {@code --bindDN} and {@code --bindPassword} as required arguments.
    */
   @Override
   protected void addArguments(ArgumentParser argumentParser)
      throws ArgumentException
   {
      Collection<Argument> requiredArguments = SampleCodeCollectionUtils.newArrayList();
      for(String argName : requiredArgumentNames)
      {
         Argument arg = argumentParser.getNamedArgument(argName);
         requiredArguments.add(arg);
      }
      argumentParser.addRequiredArgumentSet(requiredArguments);
   }


   private void search() throws LDAPException
   {

      // Construct a search request. Set a size limit with the value from
      // the {@code --sizeLimit} command line argument and a time limit
      // with the value from the {@code --timeLimit} command line
      // argument.
      SearchRequest searchRequest;

      Filter filter = commandLineOptions.getFilter();
      if(filter == null) filter = Filter.createPresenceFilter("objectClass");
      List<String> requestedAttributes = commandLineOptions.getRequestedAttributes();
      String[] attributes = new String[requestedAttributes.size()];
      requestedAttributes.toArray(attributes);
      searchRequest =
         new SearchRequest(commandLineOptions.getBaseObject(),commandLineOptions.getSearchScope(),
                           filter,attributes);
      searchRequest.setSizeLimit(commandLineOptions.getSizeLimit());
      searchRequest.setTimeLimitSeconds(commandLineOptions.getTimeLimit());

      // Perform the search
      SearchResult searchResult = ldapConnectionPool.search(searchRequest);

    /*
     * Handle response controls that may be attached to the search
     * response.
     */
      if(searchResult != null)
      {
         String msg;
         if(searchResult.hasResponseControl())
         {
            for(Control control : searchResult.getResponseControls())
            {
               ControlDisplayValues controlDisplayValues = new ControlDisplayValues(control);
               msg = (String)controlDisplayValues.displayControl();
               if(getLogger().isInfoEnabled())
               {
                  getLogger().info(msg);
               }
            }
         }
         else
         {
            out("no response controls attached to response.");
         }
      }
   }


   /**
    * Retrieves the number of threads to use when closing pooled connections. TODO: This hsould be
    * specified as a property or parameter somewhere.
    */
   private int getNumCloseThreads()
   {
      return 4;
   }


   /**
    * Copies the value of the required command lines arguments
    * to class fields.
    */
   private void getCommandLineArguments()
   {
      this.dn = commandLineOptions.getBindDn();
      if(this.dn == null)
      {
         err("The --bindDN command line argument is required.");
         System.exit(1);
      }
   }


   private void authenticate() throws LDAPException
   {
      // Authenticate the connection using a SimpleBindRequest
      BindResult bindResult;
      BindRequest bindRequest = new SimpleBindRequest(dn.toString(),
                                                      commandLineOptions.getBindPassword());
      bindRequest.setResponseTimeoutMillis(getResponseTimeMillis());
      bindResult = ldapConnectionPool.bind(bindRequest);

      // Handle response controls that may be attached to the bind
      // response. Response controls that might be attached include the
      // PasswordExpiredControl and the PasswordExpiringControl.
      for(ResponseControlAware responseControlHandler : getResponseControlHandlers())
      {
         if(responseControlHandler.invoke())
         {
            try
            {
               responseControlHandler.processResponseControl(bindResult);
            }
            catch(LDAPException ldapException)
            {
               fireLdapExceptionListener(ldapConnection,ldapException);
            }
         }
      }
   }


   /**
    * Obtain a pool of connections to the LDAP server from the
    * LDAPCommandLineTool services. This requires specifying a
    * connection to the LDAP server,a number of initial connections
    * (--initialConnections) in the pool,and the maximum number of
    * connections (--maxConnections) that the pool should create.
    */
   private void getLDAPConnections() throws LDAPException
   {
      ldapConnection = connectToServer();
      ldapConnectionPool = getLdapConnectionPool(ldapConnection);
   }
}
