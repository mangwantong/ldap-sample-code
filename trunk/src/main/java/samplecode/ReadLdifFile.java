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


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;


import com.unboundid.ldap.sdk.AddRequest;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldif.LDIFChangeRecord;
import com.unboundid.ldif.LDIFException;
import com.unboundid.ldif.LDIFReader;
import com.unboundid.util.Validator;


import samplecode.listener.IOExceptionEvent;
import samplecode.listener.IOExceptionListener;
import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdifEntryEvent;
import samplecode.listener.LdifEntryEventListener;
import samplecode.listener.ObservedByIOExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.listener.ObservedByLdifEntryEventListener;


/**
 * Provides support for adding entries to a directory server database
 * from a file containing entries in the form of LDIF.
 * <p>
 * <b>preconditions</b>
 * <p>
 * <ul>
 * <li>A valid connection to an LDAP server.</li>
 * <li>An authorization identity that is permitted to add the entries
 * contained in the LDIf file</li>
 * <li>A file on the CLASSPATH that contains valid entries in the form
 * of LDIF</li>
 * <li>None of the entries in the file can already exist in the LDAP
 * server database</li>
 * </ul>
 * <p>
 * <b>postconditions</b>
 * <p>
 * <ul>
 * <li>All of the entries in the file are now present in the LDAP server
 * database.</li>
 * </ul>
 * <p>
 * <b>invariants</b>
 * <p>
 * <ul>
 * <li>The connection to the LDAP server is not closed</li>
 * </ul>
 * 
 * @see <a href="http://tools.ietf.org/html/rfc2849">LDIF</a>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.0")
public class ReadLdifFile
    implements ObservedByLdapExceptionListener,
    ObservedByLdifEntryEventListener,ObservedByIOExceptionListener
{


  // singleton instance
  private static ReadLdifFile instance = null;


  /**
   * get an instance of {@code LdapAddEntriesFromResource}.
   * 
   * @return an instance of {@code LdapAddEntriesFromResource}
   */
  public static ReadLdifFile getInstance()
  {
    if(ReadLdifFile.instance == null)
    {
      ReadLdifFile.instance = new ReadLdifFile();
    }
    return ReadLdifFile.instance;
  }


  /**
   * The list of io exception listeners.
   */
  private volatile Vector<IOExceptionListener> ioExceptionListeners =
      new Vector<IOExceptionListener>();


  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
      new Vector<LdapExceptionListener>();


  /**
   * The list of event listeners.
   */
  private volatile Vector<LdifEntryEventListener> ldifEventListeners =
      new Vector<LdifEntryEventListener>();


  private int numberOfEntriesRead = 0;


  /**
   * Adds the entries from the specified resource. The resource must be
   * on the CLASSPATH.
   * 
   * @param ldapConnection
   *          a connection to the LDAP server.
   * @param resourceContainingLdif
   *          a file containing entries in the form of LDIF to add to
   *          the directory server database.
   *          {@code resourceContainingLdif} is not permitted to be
   *          {@code null}.
   * @param controls
   *          any controls to be added to the add requests.
   *          {@code controls} is permitted to be {@code null}.
   * @return number of entries read from the resource file.
   * @throws IOException
   *           if the file cannot be read.
   * @throws LDIFException
   *           if the LDIF in the file is invalid.
   * @throws LDAPException
   *           if the current entry already exists or the connection
   *           fails
   */
  public int addEntriesInFile(final LDAPConnection ldapConnection,
      final String resourceContainingLdif,final Control[] controls)
      throws LDIFException,IOException,LDAPException
  {
    Validator.ensureNotNull(ldapConnection,resourceContainingLdif);


    final InputStream inputStreamConnectionToResourceContainingLdif =
        getClass().getClassLoader().getResourceAsStream(resourceContainingLdif);
    if(inputStreamConnectionToResourceContainingLdif == null)
    {
      final String exceptionMsg =
          String.format("An error has occurred because the "
              + "specified resource '%s' was not found on the CLASSPATH.",
              resourceContainingLdif);
      throw new FileNotFoundException(exceptionMsg);
    }
    return addEntriesFromInputStream(ldapConnection,
        inputStreamConnectionToResourceContainingLdif,controls);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addIOExceptionListener(
      final IOExceptionListener ioExceptionListener)
  {
    if(ioExceptionListener != null)
    {
      ioExceptionListeners.add(ioExceptionListener);
    }
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
  public synchronized void addLdifEventListener(
      final LdifEntryEventListener ldifEventListener)
  {
    if(ldifEventListener != null)
    {
      ldifEventListeners.add(ldifEventListener);
    }
  }


  /**
   * Apply changes that arrive in LDIF format via the
   * {@code ldifInputStream}. Changes are processed in a single-threaded
   * fashion (like the {@code ldapmodify} tool).
   * <p>
   * <b>example LDIF</b><blockquote>
   * 
   * <pre>
   * dn: uid=user.0,ou=people,dc=example,dc=com
   * changetype: modify
   * replace: userPassword
   * userPassword: new-password-value
   * </pre>
   * </blockquote>
   * 
   * @param ldapConnection
   *          a connection to the LDAP server that will receive the
   *          changes
   * @param ldifInputStream
   *          the stream from which LDIF entries are read
   * @param millisBetweenChanges
   *          the number of milliseconds between changes
   * @return the number of entries read from the stream.
   */
  public int applyChangesFromLdifInputStream(
      final LDAPConnection ldapConnection,final InputStream ldifInputStream,
      final long millisBetweenChanges)
  {
    Validator.ensureNotNull(ldapConnection,ldifInputStream);

    final LDIFReader reader = new LDIFReader(ldifInputStream);
    numberOfEntriesRead = 0;
    while(true)
    {
      LDIFChangeRecord ldifChangeRecord = null;
      try
      {

        /*
         * read a change record and update the number of entries that
         * have been read.
         */
        ldifChangeRecord = reader.readChangeRecord();
        if(ldifChangeRecord == null)
        {
          break;
        }
        synchronized(this)
        {
          ++numberOfEntriesRead;
        }


        /*
         * transmit the change to the server.
         */
        ldifChangeRecord.processChange(ldapConnection);


        /*
         * pause if desired.
         */
        if(millisBetweenChanges > 0)
        {
          Thread.sleep(millisBetweenChanges);
        }
      }
      catch(final LDIFException ldifException)
      {
        if(ldifException.mayContinueReading())
        {
          continue;
        }
        ldifException.printStackTrace();
        break;
      }
      catch(final IOException iox)
      {
        fireIOExceptionListener(iox);
        break;
      }
      catch(final LDAPException ldapException)
      {
        fireLdapExceptionListener(ldapConnection,ldapException);
        break;
      }
      catch(final InterruptedException exception)
      {
        // this block deliberately left empty.
      }
    }
    try
    {
      reader.close();
    }
    catch(final IOException exception)
    {
      // TODO Auto-generated catch block
      exception.printStackTrace();
    }
    return numberOfEntriesRead;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("unchecked")
  public void fireIOExceptionListener(final IOException ioException)
  {
    Validator.ensureNotNull(ioException);
    Vector<IOExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<IOExceptionListener>)ioExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final IOExceptionEvent ev = new IOExceptionEvent(this,ioException);
    for(final IOExceptionListener l : copy)
    {
      l.ioExceptionOccurred(ev);
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
    final LdapExceptionEvent ev =
        new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdifEventListener(final Entry entry)
  {
    Validator.ensureNotNull(entry);
    Vector<LdifEntryEventListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdifEntryEventListener>)ldifEventListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdifEntryEvent ev = new LdifEntryEvent(this,entry);
    for(final LdifEntryEventListener l : copy)
    {
      l.entryReadFromLdifFile(ev);
    }
  }


  /**
   * @return the numberOfEntriesRead
   */
  public synchronized int getNumberOfEntriesRead()
  {
    return numberOfEntriesRead;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeIOExceptionListener(
      final IOExceptionListener ioExceptionListener)
  {
    if(ioExceptionListener != null)
    {
      ioExceptionListeners.remove(ioExceptionListener);
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
  public synchronized void removeLdifEventListener(
      final LdifEntryEventListener ldifEventListener)
  {
    if(ldifEventListener != null)
    {
      ldifEventListeners.remove(ldifEventListener);
    }
  }


  // TODO: fire the ldap exception listener.
  private int addEntriesFromInputStream(final LDAPConnection ldapConnection,
      final InputStream inputStreamConnectionToResourceContainingLdif,
      final Control[] controls) throws LDIFException,IOException,LDAPException
  {
    Validator.ensureNotNull(inputStreamConnectionToResourceContainingLdif);

    /*
     * Create an LDIFReader object. Using the reader object, read
     * entries one at a time from the file and transmit an add request
     * to the server that includes any controls the client specified.
     * The reader.readEntry() method returns a null object when the EOF
     * is reached. In the event of an exception, the LDAPException
     * object thrown will contain the ResultCode that resulted in the
     * exception and this method has no other use for the result code
     * from the add(), therefore the result code from the add() is
     * ignored.
     */
    final LDIFReader reader =
        new LDIFReader(inputStreamConnectionToResourceContainingLdif);
    numberOfEntriesRead = 0;
    while(true)
    {
      final Entry entry = reader.readEntry();
      if(entry == null)
      {
        break;
      }
      fireLdifEventListener(entry);
      final AddRequest addRequest = new AddRequest(entry,controls);
      ldapConnection.add(addRequest);
      synchronized(this)
      {
        ++numberOfEntriesRead;
      }
    }
    reader.close();
    return numberOfEntriesRead;
  }
}
