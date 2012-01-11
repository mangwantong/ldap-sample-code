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
package samplecode.test;


import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.controls.PostReadRequestControl;
import com.unboundid.ldif.LDIFException;
import com.unboundid.util.MinimalLogFormatter;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.ReadLdifFile;
import samplecode.Since;
import samplecode.listener.IOExceptionEvent;
import samplecode.listener.IOExceptionListener;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByIOExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;


/**
 * unit tests for {@code LdapAdd}.
 * <p>
 * <b>usage examples</b> <blockquote>
 * 
 * <pre>
 * // Get the bind DN and password
 * LdapServerConnectionData data = DataUsedForTestingPurposesOnly
 * 		.getLdapServerConnectionData();
 * 
 * 
 * 
 * final String bindDn = data.getBindDn();
 * 
 * 
 * 
 * final String bindPassword = data.getBindPassword();
 * </pre>
 * </blockquote>
 * <p>
 * The tests use a properties file for information concerning how to
 * connect to directory server, and for other purposes. The name of the
 * properties file is {@code commandLineOptions.properties}.
 * <p>
 * <b>example contents of the {@code commandLineOptions.properties}
 * file:</b><blockquote>
 * 
 * <pre>
 * #
 * # Properties used by the testing harness to connect to
 * # directory server.
 * #
 * # Supported keywords:
 * # hostname      - the hostname where the directory server runs.
 * # port          - the port upon which the directory server listens for connections.
 * # bindDn        - the distinguished name used to authenticate a connection.
 * # bindPassword  - the credentials of the bind DN.
 * #
 * samplecode.test.LdapServerConnectionData.hostname = ldap.example.com
 * samplecode.test.LdapServerConnectionData.port = 1389
 * samplecode.test.LdapServerConnectionData.bindDn = CN=RootDN
 * samplecode.test.LdapServerConnectionData.bindPassword = password
 * samplecode.test.LdapServerConnectionData.sizeLimit = 32
 * </pre>
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.0")
public final class ReadLdifFileTest
    implements LdapExceptionListener,ObservedByLdapExceptionListener,
    IOExceptionListener,ObservedByIOExceptionListener
{


  private static final String RESOURCE_CONTAINING_CHANGE_LDIF = "changes.ldif";


  private static final String RESOURCE_CONTAINING_LDIF = "testfile.ldif";


  // connection to directory server.
  private LDAPConnection ldapConnection;


  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
      new Vector<LdapExceptionListener>();


  @Override
  public void addIOExceptionListener(
      final IOExceptionListener ioExceptionListener)
  {
    // TODO: This block deliberately left empty

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
   * Close the connection to the server.
   */
  @After
  public void closeConnection()
  {
    LdapConnectionUtils.closeConnection(ldapConnection);
  }


  @Override
  public void fireIOExceptionListener(final IOException ioException)
  {
    // TODO: This block deliberately left empty

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
    synchronized (this)
    {
      copy = (Vector<LdapExceptionListener>)ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev =
        new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }


  /**
   * Get connection to the server.
   * 
   * @throws LDAPException
   */
  @Before
  public void getConnection() throws LDAPException
  {
    ldapConnection = LdapConnectionUtils.getConnection();
  }


  @Override
  public void ioExceptionOccurred(final IOExceptionEvent ioExceptionEvent)
  {
    System.err.println(new MinimalLogFormatter().format(new LogRecord(
        Level.SEVERE,ioExceptionEvent.getIoException().getMessage())));
  }


  @Override
  public void ldapRequestFailed(final LdapExceptionEvent ldapExceptionEvent)
  {
    System.err.println(new MinimalLogFormatter().format(new LogRecord(
        Level.SEVERE,ldapExceptionEvent.getLdapException()
            .getExceptionMessage())));
  }


  @Override
  public void removeIOExceptionListener(
      final IOExceptionListener ioExceptionListener)
  {
    // TODO: This block deliberately left empty

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
   * tests adding entries from a resource file which must be on the
   * CLASSPATH and contain valid LDIF.
   * 
   * @throws IOException
   * @throws LDIFException
   * @throws LDAPException
   */
  @Test
  public void testAddEntryFromResource() throws LDIFException,IOException,
      LDAPException
  {
    final ReadLdifFile reader = ReadLdifFile.getInstance();
    reader.addLdapExceptionListener(this);
    reader.addIOExceptionListener(this);
    final Control[] controls = new Control[]
    {
      new PostReadRequestControl("cn")
    };
    final int numberOfEntriesRead =
        reader.addEntriesInFile(ldapConnection,
            ReadLdifFileTest.RESOURCE_CONTAINING_LDIF,controls);
    final String helpfulMessage =
        String.format("number of entries read from %s: %d",
            ReadLdifFileTest.RESOURCE_CONTAINING_LDIF,numberOfEntriesRead);
    TestUtils.displayHelpfulMessage(System.out,helpfulMessage);
  }


  /**
   * test applying changes from an input stream.
   */
  @Test
  public void testApplyChanges()
  {
    final ReadLdifFile reader = ReadLdifFile.getInstance();
    reader.addLdapExceptionListener(this);
    reader.addIOExceptionListener(this);
    final InputStream ldifInputStream = getChangesInputStream();
    if(ldifInputStream == null)
    {
      throw new NullPointerException();
    }
    final int numberOfEntriesRead =
        reader.applyChangesFromLdifInputStream(ldapConnection,ldifInputStream,
            getMillisecondsBetweenChanges());
    TestUtils.displayHelpfulMessage(System.out,"Number of entries read:" +
        numberOfEntriesRead);
  }


  private InputStream getChangesInputStream()
  {
    return getClass().getClassLoader().getResourceAsStream(
        ReadLdifFileTest.RESOURCE_CONTAINING_CHANGE_LDIF);
  }


  private long getMillisecondsBetweenChanges()
  {
    final String value =
        System.getProperties().getProperty("millisecondsBetweenChanges");
    return value == null ? 1L : Integer.parseInt(value);
  }
}
