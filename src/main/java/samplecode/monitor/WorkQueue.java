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
package samplecode.monitor;


import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPSearchException;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.util.Validator;


import java.util.Map;


import samplecode.AbstractLdapSearchSupport;
import samplecode.Author;
import samplecode.CodeVersion;
import samplecode.SampleCodeCollectionUtils;
import samplecode.Since;
import samplecode.Singleton;


/**
 * <b>usage example:</b> <blockquote>
 * 
 * <pre>
 * 
 * 
 * 
 * 
 * 
 * 
 * final WorkQueue wq = WorkQueue.newWorkQueue(this.ldapConnection);
 * 
 * 
 * final Map&lt;Attribute,String[]&gt; mapOfWorkQueueAttributes = wq
 *     .getWorkQueueRawAttributeValues();
 * </pre>
 * 
 * </blockquote>
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 27, 2011")
@CodeVersion("1.2")
@Singleton
public final class WorkQueue
    extends AbstractLdapSearchSupport
{


  /**
   * The average work queue size attribute type
   */
  public static final String ATTR_NAME_AVERAGE_QUEUE_SIZE =
      "average-queue-size";


  /**
   * The average worker thread percent busy attribute type
   */
  public static final String ATTR_NAME_AVERAGE_WORKER_THREAD_PERCENT_BUSY =
      "average-worker-thread-percent-busy";


  /**
   * cn
   */
  public static final String ATTR_NAME_CN = "cn";


  /**
   * Current administrative session queue size
   */
  public static final String ATTR_NAME_CURRENT_ADMINISTRATIVE_SESSION_QUEUE_SIZE =
      "current-administrative-session-queue-size";


  /**
   * Current queue size.
   */
  public static final String ATTR_NAME_CURRENT_QUEUE_SIZE =
      "current-queue-size";


  /**
   * Current worker thread percent busy
   */
  public static final String ATTR_NAME_CURRENT_WORKER_THREAD_PERCENT_BUSY =
      "current-worker-thread-percent-busy";


  /**
   * maxmimum size of the administrative session queue.
   */
  public static final String ATTR_NAME_MAX_ADMINISTRATVE_SESSION_QUEUE_SIZE =
      "max-administrative-session-queue-size";


  /**
   * max queue size
   */
  public static final String ATTR_NAME_MAX_QUEUE_SIZE = "max-queue-size";


  /**
   * max worker thread percent busy
   */
  public static final String ATTR_NAME_MAX_WORKER_THREAD_PERCENT_BUSY =
      "max-worker-thread-percent-busy";


  /**
   * Number of administrative session worker threads
   */
  public static final String ATTR_NAME_NUM_ADMINISTRATIVE_SESSION_WORKER_THREADS =
      "num-administrative-session-worker-threads";


  /**
   * Number of busy administrative session worker threads
   */
  public static final String ATTR_NAME_NUM_BUSY_ADMINISTRATIVE_SESSION_WORKER_THREADS =
      "num-busy-administrative-session-worker-threads";


  /**
   * Number of busy worker threads
   */
  public static final String ATTR_NAME_NUM_BUSY_WORKER_THREADS =
      "num-busy-worker-threads";


  /**
   * Number of worker threads.
   */
  public static final String ATTR_NAME_NUM_WORKER_THREADS =
      "num-worker-threads";


  /**
   * recent average queue size.
   */
  public static final String ATTR_NAME_RECENT_AVERAGE_QUEUE_SIZE =
      "recent-average-queue-size";


  /**
   * recent worker thread percent busy
   */
  public static final String ATTR_NAME_RECENT_WORKER_THREAD_PERCENT_BUSY =
      "recent-worker-thread-percent-busy";


  /**
   * rejected because the queue became full
   */
  public static final String ATTR_NAME_REJECTED_COUNT = "rejected-count";


  /**
   * The relative distinguished name of the work queue monitor entry.
   */
  public static final String WORK_QUEUE_RDN = "cn=work queue";


  // singleton instance
  private static WorkQueue instance = null;


  /**
   * gets an instance of {@code WorkQueue}.
   * 
   * @return a {@code WorkQueue} object.
   */
  public static WorkQueue getInstance()
  {
    if(WorkQueue.instance == null)
    {
      WorkQueue.instance = new WorkQueue();
    }
    return WorkQueue.instance;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getBaseObject()
  {
    return WorkQueue.WORK_QUEUE_RDN + "," + "cn=monitor";
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Filter getFilter() throws LDAPException
  {
    return Filter.createEqualityFilter("objectClass",
        "ds-unboundid-work-queue-monitor-entry");
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
        WorkQueue.ATTR_NAME_AVERAGE_QUEUE_SIZE,
        WorkQueue.ATTR_NAME_AVERAGE_WORKER_THREAD_PERCENT_BUSY,
        WorkQueue.ATTR_NAME_CN,
        WorkQueue.ATTR_NAME_CURRENT_ADMINISTRATIVE_SESSION_QUEUE_SIZE,
        WorkQueue.ATTR_NAME_CURRENT_QUEUE_SIZE,
        WorkQueue.ATTR_NAME_CURRENT_WORKER_THREAD_PERCENT_BUSY,
        WorkQueue.ATTR_NAME_MAX_ADMINISTRATVE_SESSION_QUEUE_SIZE,
        WorkQueue.ATTR_NAME_MAX_QUEUE_SIZE,
        WorkQueue.ATTR_NAME_MAX_WORKER_THREAD_PERCENT_BUSY,
        WorkQueue.ATTR_NAME_NUM_ADMINISTRATIVE_SESSION_WORKER_THREADS,
        WorkQueue.ATTR_NAME_NUM_BUSY_ADMINISTRATIVE_SESSION_WORKER_THREADS,
        WorkQueue.ATTR_NAME_NUM_BUSY_WORKER_THREADS,
        WorkQueue.ATTR_NAME_NUM_WORKER_THREADS,
        WorkQueue.ATTR_NAME_RECENT_AVERAGE_QUEUE_SIZE,
        WorkQueue.ATTR_NAME_RECENT_WORKER_THREAD_PERCENT_BUSY,
        WorkQueue.ATTR_NAME_REJECTED_COUNT,
    };
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public SearchScope getScope()
  {
    return SearchScope.BASE;
  }


  /**
   * Return the current average queue size.
   * 
   * @param ldapConnection
   *          a connection to the LDAp server.
   * @return average work queue size.
   * @throws LDAPException
   * @throws LDAPSearchException
   */
  public int getWorkQueueAverageSize(final LDAPConnection ldapConnection)
      throws LDAPSearchException,LDAPException
  {
    Validator.ensureNotNullWithMessage(ldapConnection,
        "ldapConnection is not permitted to be null.");

    return getIntAttributeValue(ldapConnection,
        WorkQueue.ATTR_NAME_AVERAGE_QUEUE_SIZE);
  }


  /**
   * Return the current average worker thread percent busy.
   * 
   * @param ldapConnection
   *          a connection to the LDAP server.
   * @return average work queue size.
   * @throws LDAPException
   * @throws LDAPSearchException
   */
  public int getWorkQueueAverageWorkerThreadPercentBusy(
      final LDAPConnection ldapConnection) throws LDAPSearchException,
      LDAPException
  {
    Validator.ensureNotNullWithMessage(ldapConnection,
        "ldapConnection is not permitted to be null.");

    return getIntAttributeValue(ldapConnection,
        WorkQueue.ATTR_NAME_AVERAGE_WORKER_THREAD_PERCENT_BUSY);
  }


  /**
   * @param ldapConnection
   *          a connection to the LDAP server.
   * @return current administrative queue size
   * @throws LDAPException
   * @throws LDAPSearchException
   */
  public int getWorkQueueCurrentAdministrativeQueueSize(
      final LDAPConnection ldapConnection) throws LDAPSearchException,
      LDAPException
  {
    Validator.ensureNotNullWithMessage(ldapConnection,
        "ldapConnection is not permitted to be null.");

    return getIntAttributeValue(ldapConnection,
        WorkQueue.ATTR_NAME_CURRENT_ADMINISTRATIVE_SESSION_QUEUE_SIZE);
  }


  /**
   * <b>usage example:</b> <blockquote>
   * 
   * <pre>
   * 
   * 
   * 
   * 
   * 
   * 
   * final WorkQueue wq = WorkQueue.newWorkQueue(this.ldapConnection);
   * 
   * 
   * final Map&lt;Attribute,String[]&gt; mapOfWorkQueueAttributes = wq
   *     .getWorkQueueRawAttributeValues();
   * </pre>
   * 
   * </blockquote>
   * 
   * @param ldapConnection
   *          a connection to the LDAP server.
   * @return a {@code Map} of the raw attribute values from the
   *         {@code work queue monitor} wherein the map keys are
   *         {@code Attribute} and the values are {@code String[]}.
   * @throws LDAPException
   * @throws LDAPSearchException
   */
  public Map<String,String[]> getWorkQueueRawAttributeValues(
      final LDAPConnection ldapConnection) throws LDAPSearchException,
      LDAPException
  {
    Validator.ensureNotNullWithMessage(ldapConnection,
        "ldapConnection is not permitted to be null.");


    SearchResult searchResult;
    searchResult = doSearch(ldapConnection,newSearchRequest());
    final Map<String,String[]> map = SampleCodeCollectionUtils.newHashMap();
    if(searchResult.getEntryCount() == 1)
    {
      final SearchResultEntry e = searchResult.getSearchEntries().get(0);
      for(final String attrName : getRequestedAttributes())
      {
        final Attribute attribute = e.getAttribute(attrName);
        if(attribute != null)
        {
          map.put(attribute.getBaseName(),attribute.getValues());
        }
      }
    }
    return map;

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


  private SearchResult doSearch(final LDAPConnection ldapConnection,
      final SearchRequest searchRequest) throws LDAPSearchException
  {
    return ldapConnection.search(searchRequest);
  }


  private int getIntAttributeValue(final LDAPConnection ldapConnection,
      final String attributeName) throws LDAPSearchException,LDAPException
  {
    int value;
    final String[] values =
        getValuesByAttributeName(ldapConnection,attributeName);
    if(values != null)
    {
      try
      {
        value = Integer.parseInt(values[0]);
      }
      catch(final Exception ex)
      {
        ex.printStackTrace();
        value = -1;
      }
    }
    else
    {
      value = -1;
    }
    return value;
  }


  private String[] getValuesByAttributeName(
      final LDAPConnection ldapConnection,final String attributeName)
      throws LDAPSearchException,LDAPException
  {
    Validator.ensureNotNull(attributeName);
    final Map<String,String[]> map =
        getWorkQueueRawAttributeValues(ldapConnection);
    return map.get(attributeName);
  }


}
