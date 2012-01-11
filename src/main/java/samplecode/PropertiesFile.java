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
import java.io.Reader;
import java.util.Properties;
import java.util.Vector;


import com.unboundid.util.Validator;


import samplecode.listener.FileNotFoundExceptionEvent;
import samplecode.listener.FileNotFoundExceptionListener;
import samplecode.listener.IOExceptionEvent;
import samplecode.listener.IOExceptionListener;
import samplecode.listener.ObservedByFileNotFoundExceptionListener;
import samplecode.listener.ObservedByIOExceptionListener;


/**
 * Services used for loading a file containing properties as defines in
 * {@code Properties}.
 * 
 * @see Properties
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 21, 2011")
@CodeVersion("1.2")
public final class PropertiesFile
    implements ObservedByIOExceptionListener,
    ObservedByFileNotFoundExceptionListener
{


  /**
   * Creates a new and distinct {@code PropertiesFile} object using the
   * specified {@code propertiesFileName}.
   * 
   * @param propertiesFileName
   *          the name of a file on the classpath containing Java
   *          properties.
   * @return a new and distinct {@code PropertiesFile} object
   */
  public static PropertiesFile of(final String propertiesFileName)
  {
    return new PropertiesFile(propertiesFileName);

  }


  /**
   * Reads a property list (key and element pairs) from the input byte
   * stream. The input stream is in a simple line-oriented format as
   * specified in {@link Properties#load(Reader)} and is assumed to use
   * the ISO 8859-1 character encoding; that is each byte is one Latin1
   * character. Characters not in Latin1, and certain special
   * characters, are represented in keys and elements using Unicode
   * escapes.
   * 
   * @param inStream
   *          the input stream.
   * @return a new properties object initialized from the contents of
   *         the resource to which the {@code inStream} is attached.
   * @throws IOException
   *           if an error occurred when reading from the input stream.
   */
  private static Properties loadPropertiesFile(final InputStream inStream)
      throws IOException
  {
    Validator.ensureNotNull(inStream);
    final Properties properties = new Properties();
    properties.load(inStream);
    inStream.close();
    return properties;
  }


  private final Vector<FileNotFoundExceptionListener> fileNotFoundExceptionListeners =
      new Vector<FileNotFoundExceptionListener>();


  private final Vector<IOExceptionListener> ioExceptionListeners =
      new Vector<IOExceptionListener>();


  private final String name;


  /**
   * Initializes the {@code PropertiesFile} using the resource
   * {@code name}. The {@code name} must be located on the
   * {@code CLASSPATH}.
   * <p>
   * Postcondition: throws {@code PropertiesFileNotFoundException} if
   * {@code name} cannot be found.
   * 
   * @param name
   *          the resource name on the {@code CLASSPATH}. {@code name}
   *          is not permitted to be {@code null}.
   */
  public PropertiesFile(
      final String name)
  {

    Validator.ensureNotNullWithMessage(name,
        "The name of the resource must not be null.");


    this.name = name;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void addFileNotFoundExceptionListener(
      final FileNotFoundExceptionListener fileNotFoundExceptionListener)
  {
    if(fileNotFoundExceptionListener != null)
    {
      fileNotFoundExceptionListeners.add(fileNotFoundExceptionListener);
    }
  }


  @Override
  public void addIOExceptionListener(
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
  @SuppressWarnings("unchecked")
  @Override
  public void fireFileNotFoundExceptionListener(
      final FileNotFoundException fileNotFoundException)
  {
    Vector<FileNotFoundExceptionListener> copy;
    synchronized (this)
    {
      copy =
          (Vector<FileNotFoundExceptionListener>)fileNotFoundExceptionListeners
              .clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final FileNotFoundExceptionEvent ev =
        new FileNotFoundExceptionEvent(this,getName(),fileNotFoundException);
    for(final FileNotFoundExceptionListener l : copy)
    {
      l.fileNotFound(ev);
    }
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireIOExceptionListener(final IOException ioException)
  {
    Vector<IOExceptionListener> copy;
    synchronized (this)
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
   * @return the name
   */
  public String getName()
  {
    return name;
  }


  /**
   * @return the properties
   */
  public final Properties getProperties()
  {
    final InputStream inputStream = getInputStreamFromResource(getName());
    if(inputStream == null)
    {
      fireFileNotFoundExceptionListener(new FileNotFoundException(getName() +
          " was not found."));
      return null;
    }
    try
    {
      return PropertiesFile.loadPropertiesFile(inputStream);
    }
    catch (final IOException iox)
    {
      fireIOExceptionListener(iox);
      return null;
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeFileNotFoundExceptionListener(
      final FileNotFoundExceptionListener fileNotFoundExceptionListener)
  {
    if(fileNotFoundExceptionListener != null)
    {
      fileNotFoundExceptionListeners.remove(fileNotFoundExceptionListener);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void removeIOExceptionListener(
      final IOExceptionListener ioExceptionListener)
  {
    if(ioExceptionListener != null)
    {
      ioExceptionListeners.remove(ioExceptionListener);
    }
  }


  /**
   * Open for reading, a resource of the specified name from the search
   * path used to load classes. This method locates the resource through
   * the system class loader (see getSystemClassLoader()).
   * 
   * @param name
   *          the resource name.
   * @return An input stream for reading the resource, or {@code null}
   *         if the resource could not be found
   */
  private InputStream getInputStreamFromResource(final String name)
  {
    Validator.ensureNotNull(name);
    return getClass().getClassLoader().getResourceAsStream(name);
  }


}
