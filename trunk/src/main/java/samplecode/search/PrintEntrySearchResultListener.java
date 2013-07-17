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

package samplecode.search;


import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchResultReference;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


/**
 * A search result listener which prints search result entries or search
 * result preferences.
 */
@SuppressWarnings("serial")
@Author("terry.gardner@unboundid.com")
@Since("Dec 18, 2011")
@CodeVersion("1.0")
@NotMutable
public class PrintEntrySearchResultListener
  extends AbstractSearchResultListener {

  /**
   * {@inheritDoc}
   */
  @Override
  public void searchEntryReturned(final SearchResultEntry searchResultEntry) {
    Validator.ensureNotNull(searchResultEntry);
    final String msg =
      String.format("dn: %s attributes: %s",searchResultEntry.getDN(),
        searchResultEntry.getAttributes());
    final LogRecord record = new LogRecord(Level.INFO,msg);
    displayStream.println(formatter.format(record));
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void searchReferenceReturned(final SearchResultReference searchResultReference) {
    Validator.ensureNotNull(searchResultReference);
    final String msg =
      String.format("referral URLs: %s",
        Arrays.asList(searchResultReference.getReferralURLs()));
    final LogRecord record = new LogRecord(Level.INFO,msg);
    displayStream.println(formatter.format(record));
  }



  /**
   * Create a {@code PrintEntrySearchResultListener} with default state.
   */
  public PrintEntrySearchResultListener() {
    // This block deliberately left empty.
  }



  // text is transmitted via this stream
  private final PrintStream displayStream = System.out;


  // format text in a standardized format.
  private final Formatter formatter = new MinimalLogFormatter();
}
