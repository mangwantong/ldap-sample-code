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


import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;


/**
 * defines methods that retrieve data requred for a search.
 */
public interface LdapSearchSupport
{


  /**
   * Get the base object for creating a search request.
   * 
   * @return the base object in the form of a {@code String}.
   */
  String getBaseObject();


  /**
   * Get the filter for creating a search request.
   * 
   * @return the filter.
   * @throws LDAPException
   */
  Filter getFilter() throws LDAPException;


  /**
   * Get the the list of requested attributes used for creating a search
   * request.
   * 
   * @return the filter.
   */
  String[] getRequestedAttributes();


  /**
   * Get the scope for creating a search request.
   * 
   * @return the scope.
   */
  SearchScope getScope();


  /**
   * Creates a new {@code SerachRequest} at the discretion of the
   * implementor.
   * 
   * @return a new {@code SerachRequest}.
   * @throws LDAPException
   *           if the search fails
   */
  SearchRequest newSearchRequest() throws LDAPException;
}
