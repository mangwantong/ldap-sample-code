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


import java.util.logging.Level;
import java.util.logging.LogRecord;


import junit.framework.Assert;


import org.junit.Test;


import com.unboundid.util.MinimalLogFormatter;


import samplecode.PropertiesFile;
import samplecode.listener.FileNotFoundExceptionEvent;
import samplecode.listener.FileNotFoundExceptionListener;
import samplecode.listener.IOExceptionEvent;
import samplecode.listener.IOExceptionListener;


/**
 * test module for {@code PropertiesFile}.
 */
public class PropertiesFileTest
    implements IOExceptionListener,FileNotFoundExceptionListener
{


  private static final String COMMAND_LINE_OPTIONS_PROPERTIES_RESOURCE_NAME =
      "commandLineOptions.properties";


  @Override
  public void fileNotFound(final FileNotFoundExceptionEvent event)
  {
    TestUtils.displayHelpfulMessage(System.err,new MinimalLogFormatter()
        .format(new LogRecord(Level.SEVERE,event.getFileNotFoundException()
            .getMessage())));
  }


  @Override
  public void ioExceptionOccurred(final IOExceptionEvent ioExceptionEvent)
  {
    TestUtils.displayHelpfulMessage(System.err,new MinimalLogFormatter()
        .format(new LogRecord(Level.SEVERE,ioExceptionEvent.getIoException()
            .getMessage())));
  }


  /**
   * {@link PropertiesFile#getProperties()} must not return null.
   */
  @Test
  public void testGetProperties()
  {
    final PropertiesFile propertiesFile =
        new PropertiesFile(getExistingFileName());
    propertiesFile.addIOExceptionListener(this);
    propertiesFile.addFileNotFoundExceptionListener(this);
    Assert.assertNotNull(propertiesFile.getProperties());
  }


  /**
   * {@code new PropertiesFile("non-existent-file")} must throw a
   * {@code PropertiesFileNotFoundException}.
   */
  @Test
  public void testNoSuchFile()
  {
    final PropertiesFile propertiesFile =
        new PropertiesFile(getNonExistingFileName());
    propertiesFile.addFileNotFoundExceptionListener(this);
    Assert.assertTrue(propertiesFile.getProperties() == null);
  }


  private String getExistingFileName()
  {
    return PropertiesFileTest.COMMAND_LINE_OPTIONS_PROPERTIES_RESOURCE_NAME;
  }


  private String getNonExistingFileName()
  {
    return "noSuchFile.properties";
  }
}
