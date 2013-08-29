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

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Invokes a class that extends the {@code LDAPCommandLineTool} class
 * where the class name is stored in system properties.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 11, 2011")
@CodeVersion("1.0")
final class InvokeToolByPropertyName
{


  private static final String HELPFUL_USAGE_MSG;


  private static final String INVOKABLE_CLASSNAME_PROP_NAME;


  private final String classname;


  static
  {
    HELPFUL_USAGE_MSG =
      "Creates a class, which  must exist and must extend the " +
        "LDAPCommandLineTool  class, and invokes the runTool(args) method. " +
        "You must provide the  classname by setting the 'classname' system " +
        "property. For  example, java -cp your-classpath samplecode.tools " +
        ".InvokeToolByPropertyName -Dclassname=samplecode.Classname [args]";
    INVOKABLE_CLASSNAME_PROP_NAME = "classname";
  }


  InvokeToolByPropertyName(final String invokeableClassname)
  {
    Validator.ensureNotNull(invokeableClassname);
    this.classname = invokeableClassname;
  }


  /**
   * usage: <blockquote>
   * <p/>
   * <pre>
   * java -cp your-classpath samplecode.tools.InvokeToolByPropertyName \
   *   -classname=samplecode.Classname [args]
   * </pre>
   * <p/>
   * </blockquote>
   *
   * @param args Command line arguments, less the JVM-specific arguments.
   *             One of these arguments should set the system property
   *             {@code classname}.
   */
  public static void main(final String... args)
  {
    String classname =
      System.getProperty(InvokeToolByPropertyName.INVOKABLE_CLASSNAME_PROP_NAME);
    if (classname == null)
    {
      return;
    }
    InvokeToolByPropertyName invokeToolByPropertyName =
      new InvokeToolByPropertyName(classname);
    OutputStream outStream = System.out;
    try
    {
      ResultCode resultCode = invokeToolByPropertyName.runTool(args);
      if (resultCode != null)
      {
        StringBuilder builder =
          new StringBuilder(invokeToolByPropertyName.getClass().getCanonicalName());
        builder.append(" has completed processing. The result code was: ");
        builder.append(resultCode);
        LogRecord logRecord = new LogRecord(Level.INFO, builder.toString());
        String msg = new MinimalLogFormatter().format(logRecord);
        outStream.write(msg.getBytes());
      }
    }
    catch(SecurityException e)
    {
      String msg =
        String.format("An SecurityException resulted from an " + "attempt to create the" +
          " class '%s'.\n" + "The class must have a zero-argument constructor" +
          ".\n\n%s", classname, InvokeToolByPropertyName
          .HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch(IllegalArgumentException e)
    {
      String msg =
        String.format("An SecurityException resulted from an " + "attempt to create the" +
          " class '%s'.\n" + "The class must have a zero-argument constructor" +
          ".\n\n%s", classname, InvokeToolByPropertyName
          .HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch(ClassNotFoundException e)
    {
      String msg =
        String.format("An SecurityException resulted from an " + "attempt to create the" +
          " class '%s'.\n" + "The class must have a zero-argument constructor" +
          ".\n\n%s", classname, InvokeToolByPropertyName
          .HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch(InstantiationException instantiationException)
    {
      String msg =
        String.format("An Instantiation exception resulted from an " + "attempt to " +
          "create the class '%s'.\n" + "The class must have a zero-argument " +
          "constructor.\n\n%s", classname,
          InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch(IllegalAccessException e)
    {
      String msg =
        String.format("An SecurityException resulted from an " + "attempt to create the" +
          " class '%s'.\n" + "The class must have a zero-argument constructor" +
          ".\n\n%s", classname, InvokeToolByPropertyName
          .HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch(IOException e)
    {
      String msg =
        String.format("An SecurityException resulted from an " + "attempt to create the" +
          " class '%s'.\n" + "The class must have a zero-argument constructor" +
          ".\n\n%s", classname, InvokeToolByPropertyName
          .HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
  }


  ResultCode runTool(String... args) throws
    ClassNotFoundException,
    SecurityException,
    IllegalArgumentException,
    InstantiationException,
    IllegalAccessException
  {
    @SuppressWarnings("unchecked")
    Class<? extends LDAPCommandLineTool> cl =
      (Class<? extends LDAPCommandLineTool>) Class.forName(classname);
    LDAPCommandLineTool tool = cl.newInstance();
    return tool.runTool(args);
  }


}
