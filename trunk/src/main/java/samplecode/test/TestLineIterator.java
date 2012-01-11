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


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;
import samplecode.io.StringLineIterator;


/**
 * tests the {@code StringLineIterator} class.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 30, 2011")
@CodeVersion("1.0")
public final class TestLineIterator
{


  private InputStream inputStream;


  /**
   * closes the input stream when the test is complete.
   * 
   * @throws IOException
   */
  @After
  public void closeFile() throws IOException
  {
    inputStream.close();
  }


  /**
   * opens the file before tetsing commences.
   * 
   * @throws FileNotFoundException
   */
  @Before
  public void openFile() throws FileNotFoundException
  {
    inputStream = new FileInputStream(getFilename());
  }


  /**
   * exercises the {@code StringLineIterator}.
   */
  @Test
  public void testLineIterator()
  {
    final StringLineIterator lineIterator =
        new StringLineIterator(inputStream,null);
    final Iterator<String> iterator = lineIterator.iterator();
    while(iterator.hasNext())
    {
      final String line = iterator.next();
      TestUtils.displayHelpfulMessage(System.out,line);
    }
  }


  private String getFilename()
  {
    return "/etc/hosts";
  }
}
