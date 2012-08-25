/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */
package samplecode.modify;

import com.unboundid.ldap.sdk.LDAPException;

public class ModifyException extends LDAPException
{
  public ModifyException(LDAPException ex)
  {
    super(ex);
  }
}
