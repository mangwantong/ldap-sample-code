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

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.SearchResultListener;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.cli.CommandLineOptions;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * An implementation of {@code SearchResultListener} that provides
 * methods to associate an LDAPConnection and command line arguments
 * with extending classes.
 * <p/>
 * example usage: <blockquote>
 * <p/>
 * <pre>
 * class LocalSearchResultListener
 *         extends AbstractSearchResultListener
 * {
 *
 *   LocalSearchResultListener(
 *           final LDAPConnection ldapConnection,final CommandLineOptions
 * commandLineOptions)
 *   {
 *     Validator.ensureNotNull(ldapConnection,commandLineOptions);
 *     setLDAPConnection(ldapConnection);
 *     setCommandLineOptions(commandLineOptions);
 *   }
 *
 *
 *
 *   &#064;Override
 *   public void searchReferenceReturned(final SearchResultReference
 * searchResultReference)
 *   {
 *     Validator.ensureNotNull(searchResultReference);
 *     System.out.println(searchResultReference);
 *   }
 *
 *
 *
 *   &#064;Override
 *   public void searchEntryReturned(final SearchResultEntry searchResultEntry)
 *   {
 *     Validator.ensureNotNull(searchResultEntry);
 *     System.out.println(searchResultEntry);
 *   }
 * }
 * </pre>
 * </blockquote>
 *
 * @see CommandLineOptions
 * @see LDAPConnection
 */
@SuppressWarnings("serial")
@Author("terry.gardner@unboundid.com")
@Since("Dec 18, 2011")
@CodeVersion("1.3")
public abstract class AbstractSearchResultListener
  implements SearchResultListener {

  /**
   * Sets the command line arguments processor object associated with
   * the {@code AbstractSearchResultListener}.
   *
   * @param commandLineOptions
   *   The command line arguments processor; cannot be
   *   {@code null}.
   *
   * @return The resulting {@code AbstractSearchResultListener} object.
   */
  public AbstractSearchResultListener setCommandLineOptions(
    final CommandLineOptions commandLineOptions) {
    ensureNotNull(commandLineOptions);
    this.commandLineOptions = commandLineOptions;
    return this;
  }



  /**
   * Sets the connection to the directory server associated with the
   * {@code AbstractSearchResultListener}.
   *
   * @param ldapConnection
   *   The connection to the directory server, cannot be
   *   {@code null} .
   *
   * @return The resulting {@code AbstractSearchResultListener} object.
   */
  public AbstractSearchResultListener
  setLDAPConnection(final LDAPConnection ldapConnection) {
    ensureNotNull(ldapConnection);
    this.ldapConnection = ldapConnection;
    return this;
  }



  /**
   * Retrieves the connection to the directory server associated with
   * the {@code AbstractSearchResultListener}.
   *
   * @return The connection to the directory server.
   */
  public LDAPConnection getLDAPConnection() {
    return ldapConnection;
  }



  /**
   * Retrieves the command line arguments processor object associated
   * with the {@code AbstractSearchResultListener}.
   *
   * @return The command line arguments processor.
   */
  protected CommandLineOptions getCommandLineOptions() {
    return commandLineOptions;
  }



  private CommandLineOptions commandLineOptions;


  private LDAPConnection ldapConnection;

}
