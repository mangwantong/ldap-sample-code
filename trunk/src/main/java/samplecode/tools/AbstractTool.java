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

import com.unboundid.ldap.sdk.*;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.args.*;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samplecode.CommandLineOptions;
import samplecode.DefaultUnsolicitedNotificationHandler;
import samplecode.SampleCodeCollectionUtils;
import samplecode.StaticData;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.exception.ExceptionMsgFactory;
import samplecode.exception.LdapException;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdapSearchExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;

import java.io.*;
import java.util.*;

/**
 * A minimal implementation of the {@code LDAPCommandLineTool} class.
 * <p/>
 * Clients should override {@code getToolName()} and
 * {@code getToolDescription()} if a properties file is not available.
 */
@Author("terry.gardner@unboundid.com") @CodeVersion("2.3")
public abstract class AbstractTool extends LDAPCommandLineTool
        implements LdapExceptionListener, ObservedByLdapExceptionListener
{

  private static final int DEFAULT_ERROR_INDENTATION = 0;

  private static final short DEFAULT_INTRODUCTION_WIDTH = 72;

  private static ClassLoader classLoader;

  private static ClassLoader getClassLoader()
  {
    if(classLoader == null)
    {
      classLoader = AbstractTool.class.getClassLoader();
    }
    return classLoader;
  }

  protected AbstractTool(final OutputStream outStream, final OutputStream errStream)
  {
    super(outStream, errStream);
    defaultErrorIndentation = DEFAULT_ERROR_INDENTATION;
    defaultIntroductionWidth = DEFAULT_INTRODUCTION_WIDTH;
  }

  protected AbstractTool()
  {
    this(System.out, System.err);
  }

  /**
   * retrieves an {@code LDAPConnectionOptions} object that has been
   * initialized with values from command line options. The
   * {@link samplecode.tools.AbstractTool#getUnsolicitedNotificationHandler()}
   * method is used to attach an unsolicited notification handler.
   *
   * @return an {@code LDAPConnectionOptions} object
   */
  protected LDAPConnectionOptions getLdapConnectionOptions()
  {
    // get a LDAPConnectionOptions object initialized with values from command line arguments.
    final LDAPConnectionOptions ldapConnectionOptions =
            commandLineOptions.newLDAPConnectionOptions();

    // unsolicited notification handler
    UnsolicitedNotificationHandler handler = getUnsolicitedNotificationHandler();
    ldapConnectionOptions.setUnsolicitedNotificationHandler(handler);

    return ldapConnectionOptions;
  }

  /**
   * @return a suitable default unsolicited notification handler
   */
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new DefaultUnsolicitedNotificationHandler(this);
  }

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
   */
  @Override
  public ResultCode doToolProcessing()
  {
    return executeToolTasks();
  }

  /**
   * executes the tasks defined in this tool
   */
  protected abstract ResultCode executeToolTasks();

  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    if(argumentParser == null)
    {
      throw new IllegalArgumentException("argumentParser must not be null.");
    }
    this.argumentParser = argumentParser;
    commandLineOptions =
            CommandLineOptions.newCommandLineOptions(argumentParser,
                    CommandLineOptions.createDefaultArguments(StaticData.getResourceBundle()));
    addArguments(argumentParser);
  }

  protected void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    // This block deliberately left empty
  }

  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    final LdapException messageGenerator =
            ExceptionMsgFactory.getMessageGenerator(ldapExceptionEvent.getLdapException());
    getLogger().error(messageGenerator.msg());
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked") @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
          final LDAPException ldapException)
  {
    Vector<LdapExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapExceptionListener>) ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev = new LdapExceptionEvent(this, ldapConnection, ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }

  protected LDAPConnectionPool getLdapConnectionPool(final LDAPConnection c,
          final int initialConnections, final int maxConnections) throws LDAPException
  {
    return new LDAPConnectionPool(c, initialConnections, maxConnections);
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
    return getLdapConnectionPool(c, initialConnections, maxConnections);
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
    return getLdapConnectionPool(connectToServer(), initialConnections, maxConnections);
  }

  /**
   * Connect to the LDAP server specified in the LDAP connection command
   * line arguments. The command line arguments which affect the
   * connection are:
   * <p/>
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
   * @throws LDAPException If a problem occurs while creating the connection.
   */
  protected LDAPConnection connectToServer() throws LDAPException
  {
    final LDAPConnection c = getConnection();
    c.setConnectionOptions(getLdapConnectionOptions());
    return c;
  }

  public Log getLogger()
  {
    if(logger == null)
    {
      logger = LogFactory.getLog(getClass());
    }
    return logger;
  }

  private Log logger;

  /**
   * @return the text to be used for the introduction string.
   */
  protected String getIntroductionString()
  {
    return String.format("%s: %s", getToolName(), getToolDescription());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    try
    {
      return getToolName(classSpecificProperties());
    }
    catch(final IOException exception)
    {
      return "no name available: " + exception.getMessage();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    try
    {
      return getToolDescription(classSpecificProperties());
    }
    catch(final IOException exception)
    {
      return "no description available: " + exception.getMessage();
    }
  }

  private Properties classSpecificProperties() throws IOException
  {
    final Properties properties = new Properties();
    final String resourceName = classSpecificPropertiesResourceName();
    final InputStream inputStream = classSpecificPropertiesInputStream(resourceName);
    if(inputStream != null)
    {
      properties.load(inputStream);
    }
    return properties;
  }

  /**
   * return the class-specific properties resource name
   */
  protected abstract String classSpecificPropertiesResourceName();

  /**
   * Get the input stream from which class-specific properties might be
   * read. Following is an example of a class-specific resources file:
   * <p/>
   * <blockquote>
   * <p/>
   * <pre>
   * toolDescription = The AuthDemo class provides a demonstration of  \
   *              the Authorization Identity Request Control and       \
   *              the Who Am I? extended operation. The class displays \
   *              the authZid of the connection state using the        \
   *              distinguished name supplied to the --bindDN          \
   *              command line argument.
   * toolName = AuthDemo
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param classSpecificPropertiesResourceName
   *         the name of the resource from which properties might be
   *         read. classSpecificPropertiesResourceName ispermitted to
   *         be {@code null}.
   * @return the input stream or {@code null} if the resource cannot be
   *         located.
   */
  private InputStream classSpecificPropertiesInputStream(
          final String classSpecificPropertiesResourceName)
  {
    if(classSpecificPropertiesResourceName == null)
    {
      throw new IllegalArgumentException("classSpecificPropertiesResourceName must not be " +
              "null.");
    }
    InputStream classSpecificPropertiesInputStream = null;
    if(classSpecificPropertiesResourceName != null)
    {
      ClassLoader cl = getClassLoader();
      classSpecificPropertiesInputStream =
              cl.getResourceAsStream(classSpecificPropertiesResourceName);
    }
    return classSpecificPropertiesInputStream;
  }

  protected String getRequiredArgumentsMessage(final ArgumentParser argumentParser)
  {
    if(argumentParser == null)
    {
      throw new IllegalArgumentException("argumentParser must not be null.");
    }
    final List<Set<Argument>> requiredArgumentSets = argumentParser.getRequiredArgumentSets();
    String lineSeparator = System.getProperty("line.separator");
    final String toolNameString = getToolName() + " required arguments: " + lineSeparator;
    final StringBuilder sb = new StringBuilder(toolNameString);
    for(final Set<Argument> set : requiredArgumentSets)
    {
      final Iterator<Argument> i = set.iterator();
      while(true)
      {
        if(!i.hasNext())
        {
          break;
        }
        final Argument argument = i.next();
        final String fmt = String.format("--%s\n", argument.getLongIdentifier());
        sb.append(fmt);
        final String wrappedDescription =
                WordUtils.wrap(argument.getDescription(), 84, null, false);
        sb.append(wrappedDescription);
        sb.append(lineSeparator);
        sb.append(lineSeparator);
      }
    }
    return sb.toString();
  }

  /**
   * Get the tool description text from the properties file associated
   * with the class.
   *
   * @param properties properties from which the tool description text is
   *                   extracted
   * @return the tool description text
   */
  protected String getToolDescription(final Properties properties)
  {
    final String description = properties.getProperty(toolDescriptionPropertyName());
    return description == null ? "no description available." : description;
  }

  /**
   * @return the name/key of the property which specifies the tool
   *         description
   */
  protected String toolDescriptionPropertyName()
  {
    return "toolDescription";
  }

  protected String getToolName(final Properties properties)
  {
    final String name = properties.getProperty(toolNamePropertyName());
    return name == null ? "no name available." : name;
  }

  /**
   * @return the name/key of the property which specifies the tool name
   */
  protected String toolNamePropertyName()
  {
    return "toolName";
  }

  protected int getErrorIndentation()
  {
    return defaultErrorIndentation;
  }

  protected void addRequiredArgumentSet(final ArgumentParser argumentParser,
          final Argument... requiredArguments)
  {
    if(argumentParser == null)
    {
      throw new IllegalArgumentException("argumentParser must not be null.");
    }
    final List<Argument> requiredArgumentList = SampleCodeCollectionUtils.newArrayList();
    for(Argument argument : requiredArguments)
    {
      requiredArgumentList.add(argument);
    }
    argumentParser.addRequiredArgumentSet(requiredArgumentList);
  }

  /**
   * The logger must have INFO enabled at a minimum
   */
  protected void displayArguments()
  {
    if(getLogger().isInfoEnabled())
    {
      for(final Argument arg : commandLineOptions.getArgumentParser().getNamedArguments())
      {
        if(arg.isPresent())
        {
          final List<String> msgs = SampleCodeCollectionUtils.newArrayList();
          if(arg instanceof BooleanArgument)
          {
            for(int i = 0; i < arg.getNumOccurrences(); ++i)
            {
              msgs.add("--" + arg.getLongIdentifier());
            }
          }
          else if(arg instanceof DNArgument)
          {
            final DNArgument a = DNArgument.class.cast(arg);
            for(final DN value : a.getValues())
            {
              msgs.add(String.format("--%s %s", a.getLongIdentifier(), value));
            }
          }
          else if(arg instanceof FileArgument)
          {
            final FileArgument a = FileArgument.class.cast(arg);
            for(final File value : a.getValues())
            {
              msgs.add(String.format("--%s %s", a.getLongIdentifier(), value));
            }
          }
          else if(arg instanceof FilterArgument)
          {
            final FilterArgument a = FilterArgument.class.cast(arg);
            for(final Filter value : a.getValues())
            {
              msgs.add(String.format("--%s %s", a.getLongIdentifier(), value));
            }
          }
          else if(arg instanceof ScopeArgument)
          {
            final ScopeArgument a = ScopeArgument.class.cast(arg);
            msgs.add(String.format("--%s %s", a.getLongIdentifier(), a.getValue()));
          }
          else if(arg instanceof StringArgument)
          {
            final StringArgument a = StringArgument.class.cast(arg);
            for(final String value : a.getValues())
            {
              msgs.add(String.format("--%s %s", a.getLongIdentifier(), value));
            }
          }
          else if(arg instanceof IntegerArgument)
          {
            final IntegerArgument a = IntegerArgument.class.cast(arg);
            for(final Integer value : a.getValues())
            {
              msgs.add(String.format("--%s %s", a.getLongIdentifier(), value));
            }
          }
          else
          {
            msgs.add("");
          }
          for(final String string : msgs)
          {
            getLogger().info(string);
          }
        }
      }
    }
  }

  protected void displayServerInformation()
  {
    if(getLogger().isInfoEnabled())
    {
      final String hostname = commandLineOptions.getHostname();
      final int port = commandLineOptions.getPort();
      final String msg =
              String.format("Will attempt to connect to server %s:%d", hostname, port);
      getLogger().info(msg);
    }
  }

  /**
   * Display introductory matter. This implementation display the name
   * of the tool and the tool description.
   */
  protected void introduction()
  {
    // TODO: Add support for a configurable introduction indentation and
    // add support for resource bundles

    final int indentation = 0;
    int width = getIntroductionWidth();
    if(width <= 0)
    {
      width = DEFAULT_INTRODUCTION_WIDTH;
    }
    if(commandLineOptions != null)
    {
      width = commandLineOptions.getIntroductionColumnWidth();
    }
    wrapOut(indentation, width, getToolName() + ":\n" + getToolDescription());
    out();
  }

  protected int getIntroductionWidth()
  {
    defaultIntroductionWidth = commandLineOptions.getIntroductionColumnWidth();
    return defaultIntroductionWidth;
  }

  /**
   * @param printStream a non-null print stream to which the message is
   *                    transmitted.
   * @param msg         a non-null message to transmit
   */
  protected void verbose(final PrintStream printStream, final String msg)
  {
    if(getLogger().isTraceEnabled())
    {
      getLogger().trace(msg);
    }
  }

  /**
   * @param msg a non-null message to transmit to the standard output.
   */
  protected void verbose(final String msg)
  {
    if(getLogger().isTraceEnabled())
    {
      getLogger().trace(msg);
    }
  }

  /**
   * @return Whether the tool is verbose in its output.
   */
  public boolean isVerbose()
  {
    return commandLineOptions.isVerbose();
  }

  /**
   * A value which specifies the default timeout in milliseconds that
   * the SDK should wait for a response from the server before failing.
   * By default, a timeout of 300,000 milliseconds (5 minutes) will be
   * used.
   *
   * @return the maximum response time in milliseconds
   */
  public long getResponseTimeMillis()
  {
    return commandLineOptions.getMaxResponseTimeMillis();
  }

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

  /**
   * Provides services for use with command line parameters and
   * arguments. Handles adding a fairly standard set of arguments to the
   * argument parser and retrieving their parameters.
   */
  protected CommandLineOptions commandLineOptions;

  protected LDAPConnectionPool ldapConnectionPool;

  protected LDAPConnection ldapConnection;

  /**
   * The name of the class that implements {@code AbstractTool}.
   */
  protected String className;

  private ArgumentParser argumentParser;

  protected ArgumentParser getArgumentParser()
  {
    return argumentParser;
  }

  private int defaultErrorIndentation;

  private int defaultIntroductionWidth;

}
