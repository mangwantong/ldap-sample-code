/*
 * Copyright 2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.memory;

import com.unboundid.ldap.sdk.Entry;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;


/**
 * TODO: Complete this Javadoc
 */
public interface InMemoryDirectoryServerListener {

  /**
   * This method is notified when a noteworthy event occurs with an
   * entry.
   *
   * @param ldapConnectionPool
   *   A valid {@code LDAPConnectionPool}.
   * @param entry
   *   A directory server entry.
   *
   * @return The result code from any action that was taken by the
   *         method.
   */
  ResultCode entryAction(LDAPConnectionPool ldapConnectionPool, Entry entry)
    throws LDAPException;
}
