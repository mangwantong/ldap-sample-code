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
package samplecode.auth;


import com.unboundid.ldap.sdk.DN;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


import java.io.PrintStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;


import samplecode.AuthorizedIdentity;
import samplecode.BasicToolCompletedProcessing;
import samplecode.CommandLineOptions;
import samplecode.DefaultUnsolicitedNotificationHandler;
import samplecode.SupportedFeatureException;
import samplecode.ToolCompletedProcessing;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.DefaultLdapExceptionListener;
import samplecode.tools.AbstractTool;


/**
 * Provides a demonstration of the Who Am I? extended operation and the
 * {@code AuthorizationIdentityRequestControl}.
 * <p>
 * Example usage:
 * 
 * <pre>
 * java -cp your-classpath samplecode.AuthDemo \
 *   --hostname localhost --port 1389 \
 *   --bindDn "uid=user.0,ou=people,dc=example,dc=com" \
 *   --bindPasswordFile ~/.pwdFile --useStartTLS --trustAll
 * 
 * [18/Dec/2011:19:47:34 -0500] Connected to LDAP server.
 * [18/Dec/2011:19:47:34 -0500] Who Am I? extension is supported.
 * [18/Dec/2011:19:47:34 -0500] Authorization Identity Request Control is supported.
 * [18/Dec/2011:19:47:34 -0500] AuthorizationID from the Who am I? extended request: 'dn:uid=user.0,ou=People,dc=example,dc=com'
 * [18/Dec/2011:19:47:34 -0500] AuthorizationID from the AuthorizationIdentityResponseControl: 'dn:uid=user.0,ou=People,dc=example,dc=com'
 * [18/Dec/2011:19:47:34 -0500] PasswordExpiredControl was not included in the bind response.
 * [18/Dec/2011:19:47:34 -0500] PasswordExpiringControl was not included in the bind response.
 * [18/Dec/2011:19:47:34 -0500] AuthDemo has completed processing. The result code was: 0 (success)
 * </pre>
 * 
 * Below is the output of the {@code --help|-H} command line
 * option:<blockquote>
 * 
 * <pre>
 * 
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("27-Nov-2011")
@CodeVersion("2.1")
public final class AuthDemo
        extends AbstractTool
{

  /**
   * The description of this tool; this is used in help output and for
   * other purposes.
   */
  public static final String TOOL_DESCRIPTION =
          "Provides a demonstration of the use of the account usable request "
                  + "and the Who Am I? extended operation. The account "
                  + "usable request control and the Who Am I? extended "
                  + "request use the authentication state of the connection "
                  + "to the LDAP server, that is, the connection authentication "
                  + "state as set by the --bindDn|-D command line argument "
                  + "and the --bindPassword|-w command line argument. The account usable request "
                  + "control was designed by Sun Microsystems and is not based on any "
                  + "RFC or draft.";



  /**
   * The name of this tool; this is used in help output and for other
   * purposes.
   */
  public static final String TOOL_NAME = "AuthDemo";



  /**
   * Launch the {@code AuthDemo} application. Takes the following
   * command line arguments in addition to the standard ones:
   * 
   * @param args
   *          command line arguments, less the JVM arguments.
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final AuthDemo authDemo = new AuthDemo();
    final ResultCode resultCode = authDemo.runTool(args);
    final ToolCompletedProcessing completedProcessing =
            new BasicToolCompletedProcessing(authDemo,resultCode);
    completedProcessing.displayMessage(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   * <p>
   * Does not add any additional command line arguments beyond the
   * standard ones supported by the {@code CommandLineOptions} class.
   */
  @Override
  public void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    setFieldsFromCommandLineOptions();
    introduction();
    ResultCode resultCode = ResultCode.SUCCESS;
    try
    {
      resultCode = authDemo();
    }
    catch(final SupportedFeatureException exception)
    {
      err(formatter.format(new LogRecord(Level.SEVERE,"feature or control not supported.")));
      resultCode = ResultCode.UNWILLING_TO_PERFORM;
    }
    return resultCode;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return AuthDemo.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return AuthDemo.TOOL_NAME;
  }



  @Override
  protected Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  private ResultCode authDemo() throws SupportedFeatureException
  {

    /*
     * Handles unsolicited notifications. An unsolicited notification is
     * an LDAPMessage sent from the server to the client that is not in
     * response to any LDAPMessage received by the server. It is used to
     * signal an extraordinary condition in the server or in the LDAP
     * session between the client and the server. The notification is of
     * an advisory nature, and the server will not expect any response
     * to be returned from the client.
     */
    final UnsolicitedNotificationHandler unsolicitedNotificationHandler =
            new DefaultUnsolicitedNotificationHandler(this);

    LDAPConnection ldapConnection;
    if(verbose)
    {
      verbose("connecting to LDAP server.");
    }
    try
    {
      ldapConnection = getConnection();
      final LDAPConnectionOptions connectionOptions =
              commandLineOptions.newLDAPConnectionOptions();
      connectionOptions.setResponseTimeoutMillis(responseTimeoutMillis);
      connectionOptions.setUnsolicitedNotificationHandler(unsolicitedNotificationHandler);
      ldapConnection.setConnectionOptions(connectionOptions);
    }
    catch(final LDAPException ldapException)
    {
      // ldapConnection is not initialized here
      err(formatter.format(new LogRecord(Level.SEVERE,ldapException.getExceptionMessage())));
      return ldapException.getResultCode();
    }

    /*
     * Instantiate the object which provides methods to get the
     * authorization identity.
     */
    if(verbose)
    {
      verbose("Creating the authorized identity object.");
    }
    final AuthorizedIdentity authorizedIdentity = new AuthorizedIdentity(ldapConnection);
    authorizedIdentity.addLdapExceptionListener(new DefaultLdapExceptionListener());

    /*
     * Demonstrate the user of the Who Am I? extended operation. This
     * procedure requires creating a WhoAmIExtendedRequest object and
     * using processExtendedOperation to transmit it.
     */
    if(verbose)
    {
      verbose("Getting the authorization identity using the Who Am I? extended request.");
    }
    String authId;
    authId =
            authorizedIdentity
                    .getAuthorizationIdentityWhoAmIExtendedOperation(getResponseTimeout());
    if(authId != null)
    {
      msg = String.format("AuthorizationID from the Who am I? extended request: '%s'",authId);
      out(formatter.format(new LogRecord(Level.INFO,msg)));
    }

    /*
     * Demonstrate the use of the AuthorizationIdentityRequestControl.
     */
    final DN bindDnAsDn = commandLineOptions.getBindDn();
    if(bindDnAsDn == null)
    {
      final String helpfulMessage =
              "Please specify a --bindDN argument to test the "
                      + "AuthorizationidentityRequestControl.";
      final LogRecord record = new LogRecord(Level.SEVERE,helpfulMessage);
      err(formatter.format(record));
      return ResultCode.PARAM_ERROR;
    }
    if(verbose)
    {
      verbose("Getting the authorization identity using the authorization identity request.");
    }
    final String bindDn = bindDnAsDn.toString();
    final String bindPassword = commandLineOptions.getBindPassword();
    authId =
            authorizedIdentity.getAuthorizationIdentityFromBindRequest(bindDn,bindPassword,
                    getResponseTimeout());
    if(authId != null)
    {
      msg =
              String.format("AuthorizationID from the "
                      + "AuthorizationIdentityResponseControl: '%s'",authId);
      out(formatter.format(new LogRecord(Level.INFO,msg)));
    }
    ldapConnection.close();
    return ResultCode.SUCCESS;
  }



  private long getResponseTimeout()
  {
    return responseTimeoutMillis;
  }



  /**
   * Prepares {@code AuthDemo} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public AuthDemo()
  {
    super(System.out,System.err);
  }



  /**
   * Provides logging services.
   */
  private final Formatter formatter = new MinimalLogFormatter();



  /**
   * String representation of messages that provide informative or
   * instructional messages.
   */
  private String msg;
}
