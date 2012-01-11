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


import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;


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


  /**
   * usage: <blockquote>
   * 
   * <pre>
   * java -cp your-classpath samplecode.InvokeToolByPropertyName \
   *   -DinvokeableClassname=samplecode.Classname [args]
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *          Command line arguments, less the JVM-specific arguments.
   *          One of these arguments should set the system property
   *          {@code invokeableClassname}.
   */
  public static void main(final String... args)
  {
    final String invokeableClassname =
        System
            .getProperty(InvokeToolByPropertyName.INVOKABLE_CLASSNAME_PROP_NAME);
    Validator.ensureNotNull(invokeableClassname);
    final InvokeToolByPropertyName invokeToolByPropertyName =
        new InvokeToolByPropertyName(invokeableClassname);
    final OutputStream outStream = System.out;
    try
    {
      final ResultCode resultCode = invokeToolByPropertyName.runTool(args);
      if(resultCode != null)
      {
        final StringBuilder builder =
            new StringBuilder(invokeToolByPropertyName.getClass()
                .getCanonicalName());
        builder.append(" has completed processing. The result code was: ");
        builder.append(resultCode);
        final LogRecord logRecord =
            new LogRecord(Level.INFO,builder.toString());
        final String msg = new MinimalLogFormatter().format(logRecord);
        outStream.write(msg.getBytes());
      }
    }
    catch (final SecurityException e)
    {
      final String msg =
          String.format("An SecurityException resulted from an "
              + "attempt to create the class '%s'.\n"
              + "The class must have a zero-argument constructor.\n\n%s",
              invokeableClassname,InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch (final IllegalArgumentException e)
    {
      final String msg =
          String.format("An SecurityException resulted from an "
              + "attempt to create the class '%s'.\n"
              + "The class must have a zero-argument constructor.\n\n%s",
              invokeableClassname,InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch (final ClassNotFoundException e)
    {
      final String msg =
          String.format("An SecurityException resulted from an "
              + "attempt to create the class '%s'.\n"
              + "The class must have a zero-argument constructor.\n\n%s",
              invokeableClassname,InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch (final InstantiationException instantiationException)
    {
      final String msg =
          String.format("An Instantiation exception resulted from an "
              + "attempt to create the class '%s'.\n"
              + "The class must have a zero-argument constructor.\n\n%s",
              invokeableClassname,InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch (final IllegalAccessException e)
    {
      final String msg =
          String.format("An SecurityException resulted from an "
              + "attempt to create the class '%s'.\n"
              + "The class must have a zero-argument constructor.\n\n%s",
              invokeableClassname,InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
    catch (final IOException e)
    {
      final String msg =
          String.format("An SecurityException resulted from an "
              + "attempt to create the class '%s'.\n"
              + "The class must have a zero-argument constructor.\n\n%s",
              invokeableClassname,InvokeToolByPropertyName.HELPFUL_USAGE_MSG);
      System.err.println(msg);
    }
  }


  static
  {
    HELPFUL_USAGE_MSG =
        "Creates a class, which  must exist and must extend the "
            + "LDAPCommandLineTool class, and invokes the runTool(args) method.\n"
            + "You must provide the classname by setting the 'invokableClassname' "
            + "system property. for example,\n\n"
            + "java -cp your-classpath samplecode.InvokeToolByPropertyName "
            + "-DinvokeableClassname=samplecode.Classname [args]\n\n";
    INVOKABLE_CLASSNAME_PROP_NAME = "invokableClassname";
  }


  private final String invokeableClassname;


  private InvokeToolByPropertyName(
      final String invokeableClassname)
  {
    Validator.ensureNotNull(invokeableClassname);
    this.invokeableClassname = invokeableClassname;
  }


  private ResultCode runTool(final String... args)
      throws ClassNotFoundException,SecurityException,IllegalArgumentException,
      InstantiationException,IllegalAccessException
  {
    @SuppressWarnings("unchecked")
    final Class<? extends LDAPCommandLineTool> cl =
        (Class<? extends LDAPCommandLineTool>)Class
            .forName(invokeableClassname);
    final LDAPCommandLineTool tool = cl.newInstance();
    return tool.runTool(args);
  }
}
