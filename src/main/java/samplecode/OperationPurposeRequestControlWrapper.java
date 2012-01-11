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


import java.lang.reflect.InvocationTargetException;


import com.unboundid.ldap.sdk.Control;


/**
 * Provides clients with a method to obtain an OperationPurposeRequest
 * control, and throws an exception if that control is not available.
 */
class OperationPurposeRequestControlWrapper {


  /**
   * Creates a new instance of
   * {@code OperationPurposeRequestControlWrapper} initialized with the
   * specified parameters.
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
   *         {@code OperationPurposeRequestControlWrapper}.
   * @throws ClassNotFoundException
   */
  static OperationPurposeRequestControlWrapper
      newOperationPurposeRequestControlWrapper(final boolean isCritical,
          final String applicationName,final String applicationVersion,
          final int codeLocationFrames,final String requestPurpose)
          throws ClassNotFoundException,InstantiationException,
          IllegalAccessException,SecurityException,IllegalArgumentException,
          NoSuchMethodException,InvocationTargetException {
    return new OperationPurposeRequestControlWrapper(isCritical,
        applicationName,applicationVersion,codeLocationFrames,requestPurpose);
  }


  private final Control operationPurposeRequestControl;


  Control getOperationPurposeRequestControl() {
    return operationPurposeRequestControl;
  }


  /**
   * Provides a service whereby the LDAP client can check whether the
   * operation purpose request control is supported by the API.
   * 
   * @param isCritical
   *          Indicates whether the control should be considered
   *          critical.
   * @param applicationName
   *          The name of the application generating the associated
   *          request. It may be {@code null} if this should not be
   *          included in the control.
   * @param applicationVersion
   *          Information about the version of the application
   *          generating the associated request. It may be {@code null}
   *          if this should not be included in the control.
   * @param codeLocationFrames
   *          Indicates that the code location should be automatically
   *          generated with a condensed stack trace for the current
   *          thread, using the specified number of stack frames. A
   *          value that is less than or equal to zero indicates an
   *          unlimited number of stack frames should be included.
   * @param requestPurpose
   *          A string identifying the purpose of the associated
   *          request. It may be {@code null} if this should not be
   *          included in the control.
   * @throws ClassNotFoundException
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * @throws NoSuchMethodException
   * @throws InvocationTargetException
   */
  private OperationPurposeRequestControlWrapper(
      final boolean isCritical,final String applicationName,
      final String applicationVersion,final int codeLocationFrames,
      final String requestPurpose)
      throws ClassNotFoundException,InstantiationException,
      IllegalAccessException,SecurityException,IllegalArgumentException,
      NoSuchMethodException,InvocationTargetException {
    final OperationPurposeRequestControlSupportedClass supportedClass =
        OperationPurposeRequestControlSupportedClass.newSupportedClass(
            isCritical,applicationName,applicationVersion,codeLocationFrames,
            requestPurpose);
    operationPurposeRequestControl = supportedClass.newInstance();
  }


}
