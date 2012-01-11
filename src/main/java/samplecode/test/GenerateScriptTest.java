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


import java.io.File;
import java.io.IOException;
import java.util.Properties;


import junit.framework.Assert;


import org.junit.Before;
import org.junit.Test;


import com.unboundid.util.Validator;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.PropertiesFile;
import samplecode.PropertiesFileNotFoundException;
import samplecode.ScriptGenerator;
import samplecode.Since;


/**
 * test the generation of scripts used to launch the launchable classes.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.2")
public final class GenerateScriptTest
{


  // The name of a launchable class.
  private String className;


  // The class to use when executing the script.
  private String classpath;


  // The directory in which to place generated scripts
  private String directory;


  // JVM options
  private String spaceSeparatedJVMOptions;


  /**
   * load data from properties file.
   * 
   * @throws IOException
   * @throws PropertiesFileNotFoundException
   */
  @Before
  public void getProperties() throws IOException,
      PropertiesFileNotFoundException
  {
    final PropertiesFile indexPackagePropertiesFile =
        PropertiesFile.of(TestUtils
            .getIndexPackagePropertiesResourceName());


    className =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getClassnameKeyName());

    classpath =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getScriptClasspathKeyName());


    directory =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getScriptDirectoryKeyName());


    spaceSeparatedJVMOptions =
        indexPackagePropertiesFile.getProperties().getProperty(
            TestUtils.getJVMOptionsKeyName());


    final Properties systemProperties = System.getProperties();
    String propertyValue =
        systemProperties.getProperty(TestUtils.getClassnameKeyName());
    if(propertyValue != null)
    {
      className = propertyValue;
    }
    propertyValue =
        systemProperties.getProperty(TestUtils.getScriptClasspathKeyName());
    if(propertyValue != null)
    {
      classpath = propertyValue;
    }
    propertyValue =
        systemProperties.getProperty(TestUtils.getScriptDirectoryKeyName());
    if(propertyValue != null)
    {
      directory = propertyValue;
    }
    propertyValue =
        systemProperties.getProperty(TestUtils.getJVMOptionsKeyName());
    if(propertyValue != null)
    {
      spaceSeparatedJVMOptions = propertyValue;
    }


    Validator.ensureNotNull(className,directory,classpath);


    if(spaceSeparatedJVMOptions == null)
    {
      spaceSeparatedJVMOptions = "-d64 -client -Xmx256m -Xms32m";
    }


    System.out.println(this);
  }


  /**
   * edge-to-edge test of script generation.
   * 
   * @throws IOException
   */
  @Test
  public void testGenerateScriptEdgeToEdge() throws IOException
  {
    final ScriptGenerator scriptGenerator =
        new ScriptGenerator(className,classpath,spaceSeparatedJVMOptions,
            directory);
    final File script = scriptGenerator.generateScript();
    Assert.assertNotNull(script);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return String
        .format(
            "GenerateScriptTest [className=%s, classpath=%s, directory=%s, spaceSeparatedJVMOptions=%s]",
            className,classpath,directory,spaceSeparatedJVMOptions);
  }
}
