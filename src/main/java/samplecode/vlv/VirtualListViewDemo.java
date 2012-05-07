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
package samplecode.vlv;


import com.unboundid.asn1.ASN1OctetString;
import com.unboundid.ldap.sdk.Control;
import com.unboundid.ldap.sdk.Filter;
import com.unboundid.ldap.sdk.LDAPConnectionOptions;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SearchRequest;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.SearchScope;
import com.unboundid.ldap.sdk.UnsolicitedNotificationHandler;
import com.unboundid.ldap.sdk.controls.ServerSideSortRequestControl;
import com.unboundid.ldap.sdk.controls.SortKey;
import com.unboundid.ldap.sdk.controls.VirtualListViewRequestControl;
import com.unboundid.ldap.sdk.controls.VirtualListViewResponseControl;


import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.AttributeNotSupportedException;
import samplecode.BasicLdapEntryDisplay;
import samplecode.BasicToolCompletedProcessing;
import samplecode.DefaultUnsolicitedNotificationHandler;
import samplecode.LdapEntryDisplay;
import samplecode.SupportedFeature;
import samplecode.SupportedFeatureException;
import samplecode.SupportedUserAttribute;
import samplecode.ToolCompletedProcessing;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;
import samplecode.listener.LdapExceptionListener;
import samplecode.listener.ObservedByLdapExceptionListener;
import samplecode.tools.AbstractTool;


/**
 * Demonstrates the virtual list view request control such as command
 * line launch facilities and command line argument processing
 * facilities.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 4, 2011")
@CodeVersion("1.3")
public final class VirtualListViewDemo
        extends AbstractTool
        implements LdapExceptionListener,ObservedByLdapExceptionListener
{


  /**
   * The description of the tool; this is used in self-documentation and
   * help text.
   */
  private static final String TOOL_DESCRIPTION = "Provides a demonstration of the use of the "
          + "virtual list view request and response controls. "
          + "The VLV request and response controls are similar to "
          + "the simple paged results request control except "
          + "LDAP clients may access arbitrary pages of data.";



  /**
   * The name of the tool; this is used in self-documentation and help
   * text.
   */
  private static final String TOOL_NAME = "VirtualListViewDemo";



  /**
   * <blockquote>
   * 
   * <pre>
   * Provides a demonstration of the use of the virtual list view request and
   * response controls. The VLV request and response controls are similar to the
   * simple paged results request control except LDAP clients may access arbitrary
   * pages of data.
   * 
   * Usage:  VirtualListViewDemo {options}
   * 
   * Available options include:
   * -h, --hostname {host}
   *     The IP address or resolvable name to use to connect to the directory
   *     server.  If this is not provided, then a default value of 'localhost' will
   *     be used.
   * -p, --port {port}
   *     The port to use to connect to the directory server.  If this is not
   *     provided, then a default value of 389 will be used.
   * -D, --bindDN {dn}
   *     The DN to use to bind to the directory server when performing simple
   *     authentication.
   * -w, --bindPassword {password}
   *     The password to use to bind to the directory server when performing simple
   *     authentication or a password-based SASL mechanism.
   * -j, --bindPasswordFile {path}
   *     The path to the file containing the password to use to bind to the
   *     directory server when performing simple authentication or a password-based
   *     SASL mechanism.
   * -Z, --useSSL
   *     Use SSL when communicating with the directory server.
   * -q, --useStartTLS
   *     Use StartTLS when communicating with the directory server.
   * -X, --trustAll
   *     Trust any certificate presented by the directory server.
   * -K, --keyStorePath {path}
   *     The path to the file to use as the key store for obtaining client
   *     certificates when communicating securely with the directory server.
   * -W, --keyStorePassword {password}
   *     The password to use to access the key store contents.
   * -u, --keyStorePasswordFile {path}
   *     The path to the file containing the password to use to access the key store
   *     contents.
   * --keyStoreFormat {format}
   *     The format (e.g., jks, jceks, pkcs12, etc.) for the key store file.
   * -P, --trustStorePath {path}
   *     The path to the file to use as trust store when determining whether to
   *     trust a certificate presented by the directory server.
   * -T, --trustStorePassword {password}
   *     The password to use to access the trust store contents.
   * -U, --trustStorePasswordFile {path}
   *     The path to the file containing the password to use to access the trust
   *     store contents.
   * --trustStoreFormat {format}
   *     The format (e.g., jks, jceks, pkcs12, etc.) for the trust store file.
   * -N, --certNickname {nickname}
   *     The nickname (alias) of the client certificate in the key store to present
   *     to the directory server for SSL client authentication.
   * -o, --saslOption {name=value}
   *     A name-value pair providing information to use when performing SASL
   *     authentication.
   * -b, --baseObject {distinguishedName}
   *     The base object used in the search request.
   * --reportInterval {positive-integer}
   *     The report interval in milliseconds.
   * --reportCount {positive-integer}
   *     Specifies the maximum number of reports. This command line argument is
   *     applicable to tools that display repeated reports. The time between
   *     repeated reports is specified by the --reportInterval command line
   *     argument.
   * -a, --attribute {attribute name or type}
   *     The attribute used in the search request or other request. This command
   *     line argument is not required, and can be specified multiple times. If this
   *     command line argument is not specified, the value '*' is used.
   * -f, --filter {filter}
   *     The search filter used in the search request.
   * -i, --initialConnections {positiveInteger}
   *     The number of initial connections to establish to directory server when
   *     creating the connection pool.
   * -m, --maxConnections {positiveInteger}
   *     The maximum number of connections to establish to directory server when
   *     creating the connection pool.
   * -s, --scope {searchScope}
   *     The scope of the search request; allowed values are BASE, ONE, and SUB
   * --sizeLimit {positiveInteger}
   *     The search size limit
   * --timeLimit {positiveInteger}
   *     The search time limit
   * --pageSize {positiveInteger}
   *     The search page size
   * -H, -?, --help
   *     Display usage information for this program.
   * </pre>
   * 
   * </blockquote>
   * 
   * @param args
   *          command line arguments, less the JVM arguments.
   */
  public static void main(final String... args)
  {
    final PrintStream outStream = System.out;
    final PrintStream errStream = System.err;
    final VirtualListViewDemo demo = new VirtualListViewDemo(outStream,errStream);
    final ResultCode resultCode = demo.runTool(args);
    if(resultCode != null)
    {
      final ToolCompletedProcessing c = new BasicToolCompletedProcessing(demo,resultCode);
      c.displayMessage(outStream,errStream);
    }
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode executeToolTasks()
  {
    ResultCode resultCode = ResultCode.SUCCESS;

    introduction();

    /*
     * Obtain a pool of connections to the LDAP server from the
     * LDAPCommandLineTool services,this requires specifying a
     * connection to the LDAP server,a number of initial connections
     * (--initialConnections) in the pool,and the maximum number of
     * connections (--maxConnections) that the pool should create.
     */
    try
    {
      ldapConnection = connectToServer();
      ldapConnectionPool = getLdapConnectionPool(ldapConnection);
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      return ldapException.getResultCode();
    }

    try
    {

      /*
       * Handles unsolicited notifications from the directory server.
       */
      final UnsolicitedNotificationHandler unsolicitedNotificationHandler =
              new DefaultUnsolicitedNotificationHandler(this);

      final LDAPConnectionOptions ldapConnectionOptions =
              commandLineOptions.newLDAPConnectionOptions();
      ldapConnectionOptions.setUnsolicitedNotificationHandler(unsolicitedNotificationHandler);
      ldapConnection.setConnectionOptions(ldapConnectionOptions);

      /*
       * Determine whether the VirtualListViewRequestControl and the
       * ServerSideSortRequestControl are supported by the server to
       * which this LDAP client is connected.
       */
      final SupportedFeature supportedFeature =
              SupportedFeature.newSupportedFeature(ldapConnection);
      String controlOID = ServerSideSortRequestControl.SERVER_SIDE_SORT_REQUEST_OID;
      supportedFeature.isControlSupported(controlOID);
      controlOID = VirtualListViewRequestControl.VIRTUAL_LIST_VIEW_REQUEST_OID;
      supportedFeature.isControlSupported(controlOID);

      /*
       * Use the attribute specified by the --attribute command line
       * argument (which may appear multiple times) to create sort keys
       * for a new ServerSideSortRequestControl object. Before creating
       * the new sort key, check that the attribute is supported by the
       * server schema.
       */
      final String[] requestedAttributes = commandLineOptions.getRequestedAttributes();
      final SortKey[] sortKeys = new SortKey[requestedAttributes.length];
      int i = 0;
      for(final String a : requestedAttributes)
      {
        SupportedUserAttribute.getInstance().supported(ldapConnection,a);
        sortKeys[i] = new SortKey(a);
        ++i;
      }
      final ServerSideSortRequestControl sortRequest =
              new ServerSideSortRequestControl(sortKeys);

      /*
       * Construct a search request from the parameter to the
       * --baseObject, --scope, --filter, --sizeLimit, --timeLimit, and
       * --requestedAttribute command line arguments. Note that all
       * search requests should include and client-requested size limit
       * and time limit.
       */
      final String baseObject = commandLineOptions.getBaseObject();
      final SearchScope scope = commandLineOptions.getSearchScope();
      final Filter filter = commandLineOptions.getFilter();
      final SearchRequest searchRequest =
              new SearchRequest(baseObject,scope,filter,requestedAttributes);
      final int sizeLimit = commandLineOptions.getSizeLimit();
      searchRequest.setSizeLimit(sizeLimit);
      final int timeLimit = commandLineOptions.getTimeLimit();
      searchRequest.setTimeLimitSeconds(timeLimit);

      int targetOffset = 1;
      int contentCount = 0;
      ASN1OctetString contextID = null;
      final int beforeCount = 0;
      final int afterCount = 9;
      do
      {
        final VirtualListViewRequestControl vlvRequest =
                new VirtualListViewRequestControl(targetOffset,beforeCount,afterCount,
                        contentCount,contextID);
        searchRequest.setControls(new Control[]
        {
                sortRequest, vlvRequest
        });
        final SearchResult searchResult = ldapConnection.search(searchRequest);

        /*
         * Display the results of the search.
         */
        if(searchResult.getResultCode().equals(ResultCode.SUCCESS) &&
                (searchResult.getEntryCount() > 0))
        {
          for(final SearchResultEntry entry : searchResult.getSearchEntries())
          {
            final LdapEntryDisplay ldapEntryDisplay = new BasicLdapEntryDisplay(entry);
            ldapEntryDisplay.display();
          }
        }

        contentCount = -1;
        final VirtualListViewResponseControl c =
                VirtualListViewResponseControl.get(searchResult);
        if(c != null)
        {
          contentCount = c.getContentCount();
          contextID = c.getContextID();
        }
        targetOffset += 10;
      }
      while(targetOffset <= contentCount);

      ldapConnection.close();
    }
    catch(final LDAPException ldapException)
    {
      fireLdapExceptionListener(ldapConnection,ldapException);
      resultCode = ldapException.getResultCode();
    }
    catch(final SupportedFeatureException supportedFeatureException)
    {
      // a request control is not supported by this server.
      logger.log(Level.SEVERE,supportedFeatureException.getMessage());
      resultCode = ResultCode.UNWILLING_TO_PERFORM;
    }
    catch(final AttributeNotSupportedException attributeNotSupportedException)
    {
      // An attribute was not defined
      final String msg =
              String.format("attribute '%s' is not supported, "
                      + "that is, is not defined in the server schema.",
                      attributeNotSupportedException.getAttributeName());
      logger.log(Level.SEVERE,msg);
      resultCode = ResultCode.PROTOCOL_ERROR;
    }
    return resultCode;
  }



  @Override
  public Logger getLogger()
  {
    return Logger.getLogger(getClass().getName());
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return VirtualListViewDemo.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return VirtualListViewDemo.TOOL_NAME;
  }



  /**
   * Prepares {@code VirtualListViewDemo} for use by a client - the
   * {@code System.out} and {@code System.err OutputStreams} are used.
   */
  public VirtualListViewDemo()
  {
    this(System.out,System.err);
  }



  private VirtualListViewDemo(
          final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



}
