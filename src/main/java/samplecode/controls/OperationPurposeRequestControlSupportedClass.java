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
package samplecode.controls;


import com.unboundid.ldap.sdk.Control;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


import samplecode.SupportedClass;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides a way to create the operation purpose request control by
 * using reflection instead of referring to the class using imports.
 * <p>
 * <b>Example usage</b> <blockquote>
 * 
 * <pre>
 * 
 * 
 * 
 * 
 * 
 * OperationPurposeRequestControlSupportedClass supCl =
 *         OperationPurposeRequestControlSupportedClass.newSupportedClass(isCritical,
 *                 applicationName,applicationVersion,codeLocationFrames,requestPurpose);
 * 
 * 
 * 
 * OperationPurposeRequestControl control = supCl.newInstance();
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 10, 2011")
@CodeVersion("1.1")
class OperationPurposeRequestControlSupportedClass
        implements SupportedClass
{

  /**
   * The fully-qualified class name of the operation purpose request
   * control, which is only available in the commercial edition of the
   * LDAP SDK.
   */
  private static final String OPERATION_PURPOSE_REQUEST_CONTROL_CLASSNAME;



  /**
   * Creates a new instance of
   * {@code OperationPurposeRequestControlSupportedClass} initialized
   * with the specified parameters.
   * 
   * @param isCritical
   *          Whether the control should be considered critical.
   * @param applicationName
   *          The name of the application generating the request, or
   *          {@code null}.
   * @param applicationVersion
   *          The version of the application generating the request, or
   *          {@code null}.
   * @param codeLocationFrames
   *          The number of code location frames.
   * @param requestPurpose
   *          The purpose of the request, or {@code null}.
   * @return a new and distinct instance of
   *         {@code OperationPurposeRequestControlSupportedClass}.
   * @throws ClassNotFoundException
   */
  static OperationPurposeRequestControlSupportedClass newSupportedClass(
          final boolean isCritical,final String applicationName,
          final String applicationVersion,final int codeLocationFrames,
          final String requestPurpose) throws ClassNotFoundException
  {
    return new OperationPurposeRequestControlSupportedClass(isCritical,applicationName,
            applicationVersion,codeLocationFrames,requestPurpose);
  }


  static
  {
    OPERATION_PURPOSE_REQUEST_CONTROL_CLASSNAME =
            "com.unboundid.ldap.sdk.unboundidds.controls.OperationPurposeRequestControl";
  }



  @Override
  public Control newInstance() throws InstantiationException,IllegalAccessException,
          SecurityException,NoSuchMethodException,IllegalArgumentException,
          InvocationTargetException
  {
    final Constructor<? extends Control> ctor =
            cl.getConstructor(String.class,String.class,int.class,String.class);
    return ctor.newInstance(applicationName,applicationVersion,codeLocationFrames,
            requestPurpose);
  }



  @Override
  public String toString()
  {
    return "OperationPurposeRequestControlSupportedClass [" +
            (applicationName != null ? "applicationName=" + applicationName + ", " : "") +
            (applicationVersion != null ? "applicationVersion=" + applicationVersion + ", "
                    : "") + (cl != null ? "cl=" + cl + ", " : "") + "codeLocationFrames=" +
            codeLocationFrames + ", isCritical=" + isCritical + ", " +
            (requestPurpose != null ? "requestPurpose=" + requestPurpose : "") + "]";
  }



  @SuppressWarnings("unchecked")
  private OperationPurposeRequestControlSupportedClass(
          final boolean isCritical,final String applicationName,
          final String applicationVersion,final int codeLocationFrames,
          final String requestPurpose)
          throws ClassNotFoundException
  {
    cl =
            (Class<? extends Control>)Class
                    .forName(OperationPurposeRequestControlSupportedClass.OPERATION_PURPOSE_REQUEST_CONTROL_CLASSNAME);
    this.isCritical = isCritical;
    this.applicationName = applicationName;
    this.applicationVersion = applicationVersion;
    this.codeLocationFrames = codeLocationFrames;
    this.requestPurpose = requestPurpose;
  }



  private final String applicationName;



  private final String applicationVersion;



  private final Class<? extends Control> cl;



  private final int codeLocationFrames;



  private final boolean isCritical;



  private final String requestPurpose;
}
