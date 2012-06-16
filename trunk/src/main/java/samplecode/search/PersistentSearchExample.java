/*
 * Copyright 2008-2011 UnboundID Corp. All Rights Reserved.
 */
/*
 * Copyright (C) 2008-2011 UnboundID Corp. This program is free
 * software; you can redistribute it and/or modify it under the terms of
 * the GNU General Public License (GPLv2 only) or the terms of the GNU
 * Lesser General Public License (LGPLv2.1 only) as published by the
 * Free Software Foundation. This program is distributed in the hope
 * that it will be useful,but WITHOUT ANY WARRANTY; without even the
 * implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details. You
 * should have received a copy of the GNU General Public License along
 * with this program; if not,see <http://www.gnu.org/licenses>.
 */


package samplecode.search;


import com.unboundid.ldap.sdk.AsyncRequestID;
import com.unboundid.ldap.sdk.AsyncSearchResultListener;
import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.ldap.sdk.controls.PersistentSearchChangeType;
import com.unboundid.ldap.sdk.controls.PersistentSearchRequestControl;


import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.DefaultUnsolicitedNotificationHandler;
import samplecode.SupportedFeature;
import samplecode.SupportedFeatureException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.tools.AbstractTool;


/**
 * Provides services useful for demonstrating a persistent search. This
 * class can be launched from the command line.
 */
@Author("terry.gardner@unboundid.com")
@Since("Oct 13,2011")
@CodeVersion("1.29")
@Launchable
public final class PersistentSearchExample
        extends AbstractTool
{



  /**
   * @param args
   *          Command line arguments as supported by
   *          {@code LDAPCommandLineTool} with help from
   *          {@code CommandLineOptions}.
   */
  public static void main(final String... args)
  {
    final OutputStream outStream = System.out;
    final OutputStream errStream = System.err;
    final PersistentSearchExample persistentSearchExample =
            new PersistentSearchExample(outStream,errStream);
    final ResultCode resultCode = persistentSearchExample.runTool(args);
    if(resultCode != null)
    {
      final StringBuilder builder = new StringBuilder(persistentSearchExample.getToolName());
      builder.append(" has completed processing. The result code was: ");
      builder.append(resultCode);
      persistentSearchExample.out(builder.toString());
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    introduction();
    try
    {
      demonstratePersistentSearch();
    }
    catch(final LDAPException ldapException)
    {
      getLogger().log(Level.SEVERE,ldapException.getMessage());
      return ldapException.getResultCode();
    }

    ldapConnection.close();
    return ResultCode.SUCCESS;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "PersistentSearchExample.properties";
  }



  @Override
  protected UnsolicitedNotificationHandler getUnsolicitedNotificationHandler()
  {
    return new samplecode.DefaultUnsolicitedNotificationHandler(this);
  }



  /**
   * Issues a persistent search request and displays results as they are
   * returned from the server.
   */
  private ResultCode demonstratePersistentSearch() throws LDAPException
  {

    /*
     * Handles unsolicited notifications from the directory server.
     */
    final UnsolicitedNotificationHandler unsolicitedNotificationHandler =
            new DefaultUnsolicitedNotificationHandler(this);

    /*
     * Connect to the directory server:
     */
    ldapConnection = getConnection();
    final LDAPConnectionOptions ldapConnectionOptions =
            commandLineOptions.newLDAPConnectionOptions();
    ldapConnectionOptions.setUnsolicitedNotificationHandler(unsolicitedNotificationHandler);
    ldapConnection.setConnectionOptions(ldapConnectionOptions);

    /*
     * Check that the persistent search request control is supported by
     * the server to which this client is connected.
     */
    final String controlOID = PersistentSearchRequestControl.PERSISTENT_SEARCH_REQUEST_OID;
    final SupportedFeature supported = SupportedFeature.newSupportedFeature(ldapConnection);
    try
    {
      supported.isControlSupported(controlOID);
    }
    catch(final SupportedFeatureException ex)
    {
      getLogger().log(Level.SEVERE,ex.getMessage());
      return ResultCode.UNWILLING_TO_PERFORM;
    }

    /*
     * Create the search request:
     */
    final SearchRequest searchRequest =
            new SearchRequest(asyncSearchListener,commandLineOptions.getBaseObject(),
                    commandLineOptions.getSearchScope(),commandLineOptions.getFilter(),
                    commandLineOptions.getRequestedAttributes().toArray(new String[0]));
    final int sizeLimit = commandLineOptions.getSizeLimit();
    searchRequest.setSizeLimit(sizeLimit);
    final int timeLimit = commandLineOptions.getTimeLimit();
    searchRequest.setTimeLimitSeconds(timeLimit);

    /*
     * Add the persistent search request control.
     */
    final boolean changesOnly = true;
    final boolean returnECs = true;
    final boolean isCritical = true;
    final PersistentSearchRequestControl control =
            new PersistentSearchRequestControl(PersistentSearchChangeType.allChangeTypes(),
                    changesOnly,returnECs,isCritical);
    searchRequest.addControl(control);

    /*
     * Transmit the search request:
     */
    final SearchResult searchResult = ldapConnection.search(searchRequest);

    return searchResult.getResultCode();
  }



  /**
   * Prepares {@code PersistentSearchExample} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public PersistentSearchExample()
  {
    this(System.out,System.err);
  }



  private PersistentSearchExample(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



  @SuppressWarnings("serial")
  private final AsyncSearchResultListener asyncSearchListener = new AsyncSearchResultListener()
  {

    @Override
    public void searchEntryReturned(final SearchResultEntry searchResultEntry)
    {
      final StringBuilder builder = new StringBuilder(">>>>\nsearch entry returned\n");
      builder.append(String.format("%-12s %s\n","DN:",searchResultEntry.getDN()));
      builder.append(String.format("%-12s %s\n","searchResult:",
              searchResultEntry.toLDIFString()));
      System.out.println(builder.toString());
    }



    @Override
    public void searchReferenceReturned(final SearchResultReference searchReferenceReturned)
    {
      final String msg = "searchReferenceReturned not yet supported.";
      throw new UnsupportedOperationException(msg);
    }



    @Override
    public void searchResultReceived(final AsyncRequestID requestId,
            final SearchResult searchResult)
    {
      final StringBuilder builder = new StringBuilder("search result received\n");
      builder.append(String.format("%-12s %s\n","requestId:",requestId));
      builder.append(String.format("%-12s %s\n","searchResult:",searchResult));
      out(builder.toString());
    }
  };



  /**
   * The connection to the directory server.
   */
  private LDAPConnection ldapConnection;

}
