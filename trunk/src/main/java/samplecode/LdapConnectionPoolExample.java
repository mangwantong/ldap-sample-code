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


import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionPool;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.Validator;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * An example of how to create and use a connection pool.
 */
@Author("terry.gardner@unboundID.com")
@Since("Oct 31, 2011")
@CodeVersion("1.2")
final class LdapConnectionPoolExample
{

  public LDAPConnectionPool connectionPool(final String hostname,final int port,
          final int initialConnections,final int maxConnections) throws LDAPException
  {
    Validator.ensureNotNull(hostname);
    if(port <= 0)
    {
      final StringBuilder builder = new StringBuilder(port);
      builder.append(" is an illegal value for port.");
      throw new IllegalArgumentException(builder.toString());
    }
    if(initialConnections <= 0)
    {
      final StringBuilder builder = new StringBuilder(initialConnections);
      builder.append(" is an illegal value for initialConnections.");
      throw new IllegalArgumentException(builder.toString());
    }
    if(maxConnections <= 0)
    {
      final StringBuilder builder = new StringBuilder(maxConnections);
      builder.append(" is an illegal value for maxConnections.");
      throw new IllegalArgumentException(builder.toString());
    }
    final LDAPConnection ldapConnection = new LDAPConnection(hostname,port);
    return new LDAPConnectionPool(ldapConnection,initialConnections,maxConnections);
  }



  private LdapConnectionPoolExample()
  {
    throw new UnsupportedOperationException(getClass().getCanonicalName() +
            " cannot be instantiated.");
  }

}
