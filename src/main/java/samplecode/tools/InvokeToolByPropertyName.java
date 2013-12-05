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
import java.io.OutputStream;
import samplecode.annotation.*;

/**
 * Invokes a class that extends the {@code LDAPCommandLineTool} class
 * where the class name is stored in system properties.
 *
 * @author Terry J. Gardner
 */
@Since("Dec 11, 2011")
@CodeVersion("1.1")
public final class InvokeToolByPropertyName
{


  private static final String HELPFUL_USAGE_MSG =
    "Creates a class, which  must exist and must extend the " +
      "LDAPCommandLineTool  class, and invokes the runTool(args) method. " +
      "You must provide the  classname by setting the 'classname' system " +
      "property. For  example, java -cp your-classpath samplecode.tools " +
      ".InvokeToolByPropertyName -Dclassname=samplecode.Classname [args]";


  private static final String INVOKABLE_CLASSNAME_PROP_NAME = "classname";


  private final String classname;


  InvokeToolByPropertyName(final String invokeableClassname)
  {
    if(invokeableClassname == null)
    {
      final String msg =
        "invokeableClassname violates the contract of this method (it was null).";
      throw new NullPointerException(msg);
    }
    this.classname = invokeableClassname;
  }


  /**
   * Given a system property "classname" creates that class and invokes the runTool method.
   *
   * @param args Command line arguments, less the JVM-specific arguments.
   *             One of these arguments should set the system property
   *             {@code classname}.
   */
  public static void main(final String... args)
  {
    String classname = System.getProperty(INVOKABLE_CLASSNAME_PROP_NAME);
    if(classname == null)
    {
      System.err.printf("No classname was found in system properties.\n");
      return;
    }
    InvokeToolByPropertyName invokeToolByPropertyName =
      new InvokeToolByPropertyName(classname);
    OutputStream outStream = System.out;
    try
    {
      ResultCode resultCode = invokeToolByPropertyName.runTool(args);
      if(resultCode != null && !resultCode.equals(ResultCode.SUCCESS))
      {
        System.exit(resultCode.intValue());
      }
    }
    catch(Exception e)
    {
      System.err.println(e.getMessage());
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
      (Class<? extends LDAPCommandLineTool>)Class.forName(classname);
    LDAPCommandLineTool tool = cl.newInstance();
    return tool.runTool(args);
  }


}
