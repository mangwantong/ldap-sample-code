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

import com.unboundid.ldap.sdk.*;
import com.unboundid.ldap.sdk.controls.ContentSyncRequestControl;
import com.unboundid.ldap.sdk.controls.ContentSyncRequestMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.listener.DefaultLdapExceptionListener;
import samplecode.listener.LdapExceptionListener;
import samplecode.tools.AbstractTool;

import java.util.List;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * A launchable demo of the content sync mechanism
 * defined in RFC4533.
 */
@Author("terry.gardner@unboundid.com")
@Since("May 22, 2013")
@CodeVersion("1.0")
@Launchable
public final class ContentSyncDemo extends AbstractTool {

  //----------------------------------------------------------------------------

  public static void main(final String... args) {
    final AbstractTool tool = new ContentSyncDemo();
    final Log logger = LogFactory.getLog(ContentSyncDemo.class);
    final LdapExceptionListener ldapExceptionListener =
      new DefaultLdapExceptionListener(logger);
    tool.addLdapExceptionListener(ldapExceptionListener);
    final ResultCode resultCode = tool.runTool(args);
    if(!resultCode.equals(ResultCode.SUCCESS)) {
      final int exitIntValue = resultCode.intValue();
      System.exit(exitIntValue);
    }
  }


  static class PrintingIntermediateResponseListener
    implements IntermediateResponseListener {

    @Override
    public void intermediateResponseReturned(final IntermediateResponse intermediateResponse) {
      ensureNotNull(intermediateResponse);

      System.out.println(intermediateResponse);
    }
  }


  static class PrintingSearchResultListener implements SearchResultListener {

    @Override
    public void searchEntryReturned
      (final SearchResultEntry searchEntry) {
      ensureNotNull(searchEntry);

      System.out.println(searchEntry);
    }



    @Override
    public void searchReferenceReturned
      (final SearchResultReference searchReference) {
    }
  }



  @Override
  protected ResultCode executeToolTasks() {
    ResultCode resultCode = ResultCode.OTHER;

    LDAPConnection ldapConnection = null;
    try {
      ldapConnection = connectToServer();

      final SearchResultListener searchResultListener =
        new PrintingSearchResultListener();

      /*
       * Create a search request using the command
       * line arguments
       */
      final String baseObject = commandLineOptions.getBaseObject();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final List<String> attributes =
        commandLineOptions.getRequestedAttributes();
      final String[] requestedAttributes = new String[attributes.size()];
      attributes.toArray(requestedAttributes);
      final SearchRequest searchRequest =
        new SearchRequest(searchResultListener,baseObject,
                          scope,filter,requestedAttributes);
      searchRequest.setTimeLimitSeconds(0);
      searchRequest.setResponseTimeoutMillis(0);
      final IntermediateResponseListener irListener =
        new PrintingIntermediateResponseListener();
      searchRequest.setIntermediateResponseListener(irListener);

      final Control control =
        new ContentSyncRequestControl(ContentSyncRequestMode.REFRESH_AND_PERSIST);
      searchRequest.addControl(control);

      final SearchResult searchResult = ldapConnection.search(searchRequest);

    } catch(final LDAPException ldapException) {
      if(ldapConnection != null) {
        this.fireLdapExceptionListener(ldapConnection,ldapException);
      }
      resultCode = ldapException.getResultCode();
    } finally {
      if(ldapConnection != null) {
        ldapConnection.close();
      }
    }

    return resultCode;
  }



  @Override
  protected String classSpecificPropertiesResourceName() {
    return "ContentSyncDemo.properties";
  }
}
