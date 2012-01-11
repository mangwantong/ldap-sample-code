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


import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import com.unboundid.util.Validator;


import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.Since;


/**
 * This class cannot be instantiated and contains only static methods.
 * Uses properties file {@code indexPackage.properties}.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 28, 2011")
@CodeVersion("1.0")
public final class TestUtils
{


  /**
   * key to the JVM options specified in a java properties file.
   */
  public static final String KEY_JVM_OPTIONS = "jvmOptions";


  /**
   * Transmits the provided message on the {@code printStream}.
   * 
   * @param printStream
   *          the stream upon which to transmit the message.
   * @param helpfulMessage
   *          the message
   */
  public static void displayHelpfulMessage(final PrintStream printStream,
      final Object helpfulMessage)
  {
    Validator.ensureNotNull(printStream,helpfulMessage);
    if(printStream != null && helpfulMessage != null)
    {
      final LogRecord record =
          new LogRecord(Level.FINE,helpfulMessage.toString());
      printStream.println(DataUsedForTestingPurposesOnly.getFormatter().format(
          record));
    }
  }


  /**
   * Formats the output of {@link Object#toString()} using the provided
   * {@code formatString} and returns the result.
   * 
   * @param formatString
   * @param o
   * @return o.toString() formatted using {@code formatString}.
   */
  public static String formatHelpFulMessage(final String formatString,
      final Object o)
  {
    Validator.ensureNotNull(formatString,o);
    return String.format(formatString,o.toString());
  }


  /**
   * retrieves the key whose value is the backEnd ID, i.e., "userRoot".
   * 
   * @return key whose value is the backEnd ID
   */
  public static String getBackendIdKeyName()
  {
    return "backendId";
  }


  /**
   * retrieves the key whose value is the base DN, i.e.,
   * "DC=example,DC=com"
   * 
   * @return key whose value is the base DN
   */
  public static String getBaseDnKeyName()
  {
    return "baseDn";
  }


  /**
   * retrieves the key whose value is a launchable classname, i.e.,
   * "samplecode.AuthDemo".
   * 
   * @return key whose value is a launchable classname
   */
  public static String getClassnameKeyName()
  {
    return "launchableClassname";
  }


  /**
   * retrieves the key whose value is the databaseName, i.e.,
   * "sn.equality".
   * 
   * @return key whose value is the databaseName
   */
  public static String getDatabaseNameKeyName()
  {
    return "databaseName";
  }


  /**
   * Retrieves the key associated with a value that describes the path
   * to dbtest.
   * 
   * @return key to dbtest path.
   */
  public static String getDbtestPathKeyName()
  {
    return "dbtestPath";
  }


  /**
   * Retrieves the key associated with a value that describes an entry
   * DN.
   * 
   * @return an entry name, which must be a valid distinguished name.
   */
  public static String getEntryDnKey()
  {
    return "entryDn";
  }


  /**
   * Retrieves the key associated with a value that describes the
   * existing password.
   * 
   * @return the existing password
   */
  public static String getExistingPasswordKey()
  {
    return "existingPassword";
  }


  /**
   * retrieves the name of the properties file used for index package
   * testing. The name must refer to a Java properties file on the
   * classPath.
   * 
   * @return name of the properties resource
   */
  public static String getIndexPackagePropertiesResourceName()
  {
    return "indexPackage.properties";
  }


  /**
   * retrieves the key associated with the JVM options in the properties
   * file.
   * 
   * @return key to the JVM options in the properties file.
   */
  public static String getJVMOptionsKeyName()
  {
    return TestUtils.KEY_JVM_OPTIONS;
  }


  /**
   * Retrieves the key associated with a value that describes the
   * existing password.
   * 
   * @return the existing password
   */
  public static String getNewPasswordKey()
  {
    return "newPassword";
  }


  /**
   * Retrieves the key associated with a value that indeicates the
   * number of treads to use.
   * 
   * @return key for number of threads.
   */
  public static String getNumThreadsKeyName()
  {
    return "numThreads";
  }


  /**
   * Retrieves the key associated with a value that describes the
   * class-path to use when executing the script.
   * 
   * @return shell script class-path properties key
   */
  public static String getScriptClasspathKeyName()
  {
    return "classpath";
  }


  /**
   * Retrieves the key associated with a value that describes the
   * directory in which shell scripts are dropped.
   * 
   * @return shell script directory properties key
   */
  public static String getScriptDirectoryKeyName()
  {
    return "scriptDirectory";
  }


  /**
   * This class cannot be instantiated and contains only static methods.
   */
  private TestUtils()
  {
    // This block deliberately left empty.
  }

}
