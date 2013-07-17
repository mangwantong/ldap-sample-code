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


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Defines the services that must be provided by classes that aware of
 * response controls that are included in the response to LDAP requests.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 6, 2011")
@CodeVersion("1.2")
public interface ResponseControlAware {

  /**
   * Whether to invoke the {@code processResponseControl} method. LDAP
   * clients must check the output of the {@code invoke} method before
   * invoking {@code processResponseControl}, otherwise
   * {@code processResponseControl} is not safe to invoke.
   *
   * @return An indication of whether to invoke the
   *         {@code processResponseControl} method.
   */
  boolean invoke();


  /**
   * Services a response control {@code control} that has been attached
   * by the server to an LDAP response. LDAP clients must check the
   * output of the {@code invoke} method before invoking
   * {@code processResponseControl}, otherwise
   * {@code processResponseControl} is not safe to invoke.
   *
   * @param <T>
   *   The type of result.
   * @param ldapResult
   *   The result of an LDAP operation. If the {@code ldapResult}
   *   object is {@code null}, no action is taken and no
   *   exception is thrown.
   *
   * @throws LDAPException
   *   When an exception occurs in the course of handling the
   *   control.
   */
  <T extends LDAPResult> void processResponseControl(T ldapResult) throws LDAPException;
}
