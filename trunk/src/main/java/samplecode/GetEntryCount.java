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
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.Validator;


import java.util.Vector;


import samplecode.listener.LdapExceptionEvent;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.LdapSearchExceptionEvent;
import samplecode.listener.LdapSearchExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.listener.ObservedByLdapSearchExceptionListener;


/**
 * Provides services related to entry counts and indexes.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 29, 2011")
@CodeVersion("1.2")
public class GetEntryCount
    extends AbstractLdapSearchSupport
    implements Comparable<GetEntryCount>,ObservedByLdapSearchExceptionListener,
    ObservedByLdapExceptionListener
{


  // the attribute name (type), i.e., 'cn'
  private final String attributeType;


  // value of the attribute, used in the filter
  private final String attributeValue;


  // the base object to use in the search
  private final String baseObject;


  private int count;


  // the index type, i.e., 'substring'
  private final String indexType;


  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapExceptionListener> ldapExceptionListeners =
      new Vector<LdapExceptionListener>();


  /**
   * interested parties to {@code LdapExceptionEvents}
   */
  private volatile Vector<LdapSearchExceptionListener> ldapSearchExceptionListeners =
      new Vector<LdapSearchExceptionListener>();


  private final SearchScope scope;


  /**
   * @param baseObject
   * @param scope
   * @param attributeType
   * @param indexType
   * @param attributeValue
   */
  public GetEntryCount(
      final String baseObject,final SearchScope scope,
      final String attributeType,final String indexType,
      final String attributeValue)
  {


    Validator.ensureNotNullWithMessage(baseObject,
        "GetEntryCount requires a non-null base object, "
            + "for example, 'dc=example,dc=com'");


    Validator.ensureNotNullWithMessage(scope,
        "GetEntryCount requires a non-null scope");


    Validator.ensureNotNullWithMessage(attributeType,
        "GetEntryCount requires a non-null indexed attribute type/name");


    Validator.ensureNotNullWithMessage(indexType,
        "GetEntryCount requires a non-null indexed index type, "
            + "for example, 'substring'");


    Validator.ensureNotNullWithMessage(attributeValue,
        "GetEntryCount requires a non-null attribute value.");


    this.baseObject = baseObject;
    this.scope = scope;
    this.attributeType = attributeType;
    this.indexType = indexType;
    this.attributeValue = attributeValue;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapExceptionListener(
      final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.add(ldapExceptionListener);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void addLdapSearchExceptionListener(
      final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.add(ldapSearchExceptionListener);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final GetEntryCount o)
  {
    int diff;


    diff = attributeType.compareTo(o.attributeType);
    if(diff != 0)
    {
      return diff;
    }


    diff = attributeValue.compareTo(o.attributeValue);
    if(diff != 0)
    {
      return diff;
    }


    diff = baseObject.compareTo(o.baseObject);
    if(diff != 0)
    {
      return diff;
    }


    diff = indexType.compareTo(o.indexType);
    if(diff != 0)
    {
      return diff;
    }


    diff = scope.intValue() - o.scope.intValue();
    if(diff != 0)
    {
      return diff;
    }


    return 0;
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapExceptionListener(final LDAPConnection ldapConnection,
      final LDAPException ldapException)
  {
    Vector<LdapExceptionListener> copy;
    synchronized(this)
    {
      copy = (Vector<LdapExceptionListener>)ldapExceptionListeners.clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapExceptionEvent ev =
        new LdapExceptionEvent(this,ldapConnection,ldapException);
    for(final LdapExceptionListener l : copy)
    {
      l.ldapRequestFailed(ev);
    }
  }


  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public void fireLdapSearchExceptionListener(
      final LDAPConnection ldapConnection,
      final LDAPSearchException ldapSearchException)
  {
    Vector<LdapSearchExceptionListener> copy;
    synchronized(this)
    {
      copy =
          (Vector<LdapSearchExceptionListener>)ldapSearchExceptionListeners
              .clone();
    }
    if(copy.size() == 0)
    {
      return;
    }
    final LdapSearchExceptionEvent ev =
        new LdapSearchExceptionEvent(this,ldapConnection,ldapSearchException);
    for(final LdapSearchExceptionListener l : copy)
    {
      l.searchRequestFailed(ev);
    }
  }


  /**
   * @return the attributeType
   */
  public final String getAttributeType()
  {
    return attributeType;
  }


  /**
   * @return the attributeValue
   */
  public final String getAttributeValue()
  {
    return attributeValue;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getBaseObject()
  {
    return baseObject;
  }


  /**
   * retrieves the number of entries.
   * 
   * @return number of entries
   */
  public int getCount()
  {
    return count;
  }


  /**
   * retrieves a count of entries
   * 
   * @param ldapConnection
   *          connection to LDAP server, not permitted to be
   *          {@code null}.
   * @return entry count
   */
  public int getEntryCount(final LDAPConnection ldapConnection)
  {
    Validator.ensureNotNullWithMessage(ldapConnection,
        "the object referencing a connection to the "
            + "LDAP server is not permitted to be null.");
    try
    {
      return ldapConnection.search(newSearchRequest()).getEntryCount();
    }
    catch(final LDAPSearchException ldapSearchException)
    {
      fireLdapSearchExceptionListener(ldapConnection,ldapSearchException);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
    }
    return 0;
  }


  /**
   * {@inheritDoc}
   * <p>
   * FIXME
   */
  @Override
  public Filter getFilter() throws LDAPException
  {
    Filter filter;
    if(indexType.equalsIgnoreCase("equality"))
    {
      filter = Filter.createEqualityFilter(attributeType,attributeValue);
    }
    else if(indexType.equalsIgnoreCase("substring"))
    {
      filter = Filter.createSubstringFilter(attributeType,null,new String[]
      {
        attributeValue
      },null);
    }
    else
    {
      throw new IllegalStateException(indexType + " not yet handled");
    }
    return filter;
  }


  /**
   * @return the indexType
   */
  public final String getIndexType()
  {
    return indexType;
  }


  @Override
  public LDAPConnection getLdapConnection() throws LDAPException
  {
    // TODO: This block deliberately left empty
    return null;

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String[] getRequestedAttributes()
  {
    return new String[]
    {
      SearchRequest.NO_ATTRIBUTES
    };
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SearchScope getScope()
  {
    return scope;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SearchRequest newSearchRequest() throws LDAPException
  {
    final SearchRequest req =
        new SearchRequest(getBaseObject(),getScope(),getFilter(),
            getRequestedAttributes());
    return req;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapExceptionListener(
      final LdapExceptionListener ldapExceptionListener)
  {
    if(ldapExceptionListener != null)
    {
      ldapExceptionListeners.remove(ldapExceptionListener);
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public synchronized void removeLdapSearchExceptionListener(
      final LdapSearchExceptionListener ldapSearchExceptionListener)
  {
    if(ldapSearchExceptionListener != null)
    {
      ldapSearchExceptionListeners.remove(ldapSearchExceptionListener);
    }
  }


  /**
   * @param count
   *          the number of entries
   */
  public void setCount(final int count)
  {
    this.count = count;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString()
  {
    return "GetEntryCount [" +
        (attributeType != null ? "attributeType=" + attributeType + ", " : "") +
        (attributeValue != null ? "attributeValue=" + attributeValue + ", "
            : "") +
        (baseObject != null ? "baseObject=" + baseObject + ", " : "") +
        (indexType != null ? "indexType=" + indexType + ", " : "") +
        (scope != null ? "scope=" + scope : "") + "]";
  }

}
