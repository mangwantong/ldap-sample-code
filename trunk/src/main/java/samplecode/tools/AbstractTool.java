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
package samplecode.tools;


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.CommandLineOptions;
import samplecode.annotation.CodeVersion;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdapSearchExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;


/**
 * A minimal implementation of the {@code LDAPCommandLineTool} class.
 */
@CodeVersion("2.1")
public abstract class AbstractTool
        extends LDAPCommandLineTool
        implements LdapExceptionListener,ObservedByLdapExceptionListener
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
  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    addArguments(argumentParser);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {
    return executeToolTasks();
  }



  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
          final LDAPException ldapException)
  {
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
   * Get the response time in milliseconds. TODO: Better documentatian
   * of this method
   * 
   * @return response time in milliseconds
   */
  public long getResponseTimeout()
  {
    return responseTimeoutMillis;
  }



  /**
   * 
   * TODO: Provide a comment for this method.
   * 
   * @return Whether the tool is verbose in its output.
   */
  public boolean isVerbose()
  {
    return verbose;
  }



  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    logger.log(Level.SEVERE,ldapExceptionEvent.getLdapException().getExceptionMessage());
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



  protected void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions = CommandLineOptions.newCommandLineOptions(argumentParser);
    return;
  }



  /**
   * Connect to the LDAP server specified in the LDAP connection command
   * line arguments. The command line arguments which affect the
   * connection are:
   * 
   * <ul>
   * <li>"-h {address}" or "--hostname {address}" -- Specifies the
   * address of the directory server. If this isn't specified, then a
   * default of "localhost" will be used.</li>
   * <li>"-p {port}" or "--port {port}" -- Specifies the port number of
   * the directory server. If this isn't specified, then a default port
   * of 389 will be used.</li>
   * <li>"-D {bindDN}" or "--bindDN {bindDN}" -- Specifies the DN to use
   * to bind to the directory server using simple authentication. If
   * this isn't specified, then simple authentication will not be
   * performed.</li>
   * <li>"-w {password}" or "--bindPassword {password}" -- Specifies the
   * password to use when binding with simple authentication or a
   * password-based SASL mechanism.</li>
   * <li>"-j {path}" or "--bindPasswordFile {path}" -- Specifies the
   * path to the file containing the password to use when binding with
   * simple authentication or a password-based SASL mechanism.</li>
   * <li>"-Z" or "--useSSL" -- Indicates that the communication with the
   * server should be secured using SSL.</li>
   * <li>"-q" or "--useStartTLS" -- Indicates that the communication
   * with the server should be secured using StartTLS.</li>
   * <li>"-X" or "--trustAll" -- Indicates that the client should trust
   * any certificate that the server presents to it.</li>
   * <li>"-K {path}" or "--keyStorePath {path}" -- Specifies the path to
   * the key store to use to obtain client certificates.</li>
   * <li>"-W {password}" or "--keyStorePassword {password}" -- Specifies
   * the password to use to access the contents of the key store.</li>
   * <li>"-u {path}" or "--keyStorePasswordFile {path}" -- Specifies the
   * path to the file containing the password to use to access the
   * contents of the key store.</li>
   * <li>"--keyStoreFormat {format}" -- Specifies the format to use for
   * the key store file.</li>
   * <li>"-P {path}" or "--trustStorePath {path}" -- Specifies the path
   * to the trust store to use when determining whether to trust server
   * certificates.</li>
   * <li>"-T {password}" or "--trustStorePassword {password}" --
   * Specifies the password to use to access the contents of the trust
   * store.</li>
   * <li>"-U {path}" or "--trustStorePasswordFile {path}" -- Specifies
   * the path to the file containing the password to use to access the
   * contents of the trust store.</li>
   * <li>"--trustStoreFormat {format}" -- Specifies the format to use
   * for the trust store file.</li>
   * <li>"-N {nickname}" or "--certNickname {nickname}" -- Specifies the
   * nickname of the client certificate to use when performing SSL
   * client authentication.</li>
   * <li>"-o {name=value}" or "--saslOption {name=value}" -- Specifies a
   * SASL option to use when performing SASL authentication.</li>
   * </ul>
   * If SASL authentication is to be used, then a "mech" SASL option
   * must be provided to specify the name of the SASL mechanism to use
   * (e.g., "--saslOption mech=EXTERNAL" indicates that the EXTERNAL
   * mechanism should be used). Depending on the SASL mechanism,
   * additional SASL options may be required or optional. They include:
   * <ul>
   * <li>
   * mech=ANONYMOUS
   * <ul>
   * <li>Required SASL options:</li>
   * <li>Optional SASL options: trace</li>
   * </ul>
   * </li>
   * <li>
   * mech=CRAM-MD5
   * <ul>
   * <li>Required SASL options: authID</li>
   * <li>Optional SASL options:</li>
   * </ul>
   * </li>
   * <li>
   * mech=DIGEST-MD5
   * <ul>
   * <li>Required SASL options: authID</li>
   * <li>Optional SASL options: authzID, realm</li>
   * </ul>
   * </li>
   * <li>
   * mech=EXTERNAL
   * <ul>
   * <li>Required SASL options:</li>
   * <li>Optional SASL options:</li>
   * </ul>
   * </li>
   * <li>
   * mech=GSSAPI
   * <ul>
   * <li>Required SASL options: authID</li>
   * <li>Optional SASL options: authzID, configFile, debug, protocol,
   * realm, kdcAddress, useTicketCache, requireCache, renewTGT,
   * ticketCachePath</li>
   * </ul>
   * </li>
   * <li>
   * mech=PLAIN
   * <ul>
   * <li>Required SASL options: authID</li>
   * <li>Optional SASL options: authzID</li>
   * </ul>
   * </li>
   * </ul>
   * 
   * @return a connection to an LDAP server
   * 
   * @throws LDAPException
   *           If a problem occurs while creating the connection.
   */
  protected LDAPConnection connectToServer() throws LDAPException
  {
    final LDAPConnection c = getConnection();
    c.setConnectionOptions(getLdapConnectionOptions());
    return c;
  }



  /** executes the tasks defined in this tool */
  protected abstract ResultCode executeToolTasks();



  /**
   * @return the text to be used for the introduction string.
   */
  protected String getIntroductionString()
  {
    return String.format("%s: %s",getToolName(),getToolDescription());
  }



  protected LDAPConnectionOptions getLdapConnectionOptions()
  {
    final LDAPConnectionOptions ldapConnectionOptions =
            commandLineOptions.newLDAPConnectionOptions();

    // unsolicited notification handler
    ldapConnectionOptions
            .setUnsolicitedNotificationHandler(getUnsolicitedNotificationHandler());

    // max request response time in milliseconds
    ldapConnectionOptions.setResponseTimeoutMillis(responseTimeoutMillis);

    return ldapConnectionOptions;
  }



  protected LDAPConnectionPool getLdapConnectionPool() throws LDAPException
  {
    int initialConnections;
    int maxConnections;
    if(commandLineOptions != null)
    {
      initialConnections = commandLineOptions.getInitialConnections();
      maxConnections = commandLineOptions.getMaxConnections();
    }
    else
    {
      initialConnections = 1;
      maxConnections = 2;
    }
    return getLdapConnectionPool(connectToServer(),initialConnections,maxConnections);
  }



  protected LDAPConnectionPool getLdapConnectionPool(final LDAPConnection c)
          throws LDAPException
  {
    int initialConnections;
    int maxConnections;
    if(commandLineOptions != null)
    {
      initialConnections = commandLineOptions.getInitialConnections();
      maxConnections = commandLineOptions.getMaxConnections();
    }
    else
    {
      initialConnections = 1;
      maxConnections = 2;
    }
    return getLdapConnectionPool(c,initialConnections,maxConnections);
  }



  protected LDAPConnectionPool getLdapConnectionPool(final LDAPConnection c,
          final int initialConnections,final int maxConnections) throws LDAPException
  {
    return new LDAPConnectionPool(c,initialConnections,maxConnections);
  }



  /** @return the logger */
  protected abstract Logger getLogger();



  /**
   * TODO: Provide a comment for this method.
   * 
   * @return
   */
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }



  /**
   * Display introductory matter. This implementation display the name
   * of the tool and the tool description.
   */
  protected void introduction()
  {

    // TODO: Add support for a configurable introduction indentation
    final int indentation = 0;
    int width;

    if(commandLineOptions != null)
    {
      width = commandLineOptions.getIntroductionColumnWidth();
    }
    else
    {
      width = 96;
    }
    wrapOut(indentation,width,getToolName() + ": " + getToolDescription());
    out();
  }



  /**
   * @param printStream
   *          a non-null print stream to which the message is
   *          transmitted.
   * @param msg
   *          a non-null message to transmit
   */
  protected void verbose(final PrintStream printStream,final String msg)
  {
    logger.log(Level.FINE,msg);
  }



  /**
   * @param msg
   *          a non-null message to transmit to the standard output.
   */
  protected void verbose(final String msg)
  {
    logger.log(Level.FINE,msg);
  }



  /**
   * 
   * Creates a {@code AbstractTool} with default state.
   * 
   */
  protected AbstractTool()
  {
    super(System.out,System.err);
  }



  /**
   * @param outStream
   * @param errStream
   */
  protected AbstractTool(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



  /**
   * Manages command line arguments
   */
  protected CommandLineOptions commandLineOptions;



  protected LDAPConnection ldapConnection;



  protected LDAPConnectionPool ldapConnectionPool;



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  protected volatile Vector<LdapExceptionListener> ldapExceptionListeners =
          new Vector<LdapExceptionListener>();



  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  protected volatile Vector<LdapSearchExceptionListener> ldapSearchExceptionListeners =
          new Vector<LdapSearchExceptionListener>();



  /** logging faciities */
  protected final Logger logger = getLogger();



  /**
   * maximum amount of time to spend waiting for a response from the
   * server
   */
  protected long responseTimeoutMillis;



  /** Whether the tool is verbose during execution */
  private boolean verbose;
}
