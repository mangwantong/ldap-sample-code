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

package samplecode.auth;


import com.unboundid.ldap.sdk.*;
import com.unboundid.util.args.*;
import java.util.Collection;
import samplecode.annotation.*;
import samplecode.listener.*;
import samplecode.tools.AbstractTool;
import samplecode.util.SampleCodeCollectionUtils;


/**
 * Provides a demonstration of the Who Am I? extended operation and the
 * {@code AuthorizationIdentityRequestControl}.
 *
 * @author Terry Gardner
 */
@Author("terry.gardner@unboundid.com")
@Since("27-Nov-2011")
@CodeVersion("4.1")
@Launchable
public final class AuthDemo extends AbstractTool
{

  /**
   * The authorization ID.
   */
  private String authId;


  /**
   * Support for authorized identity operations.
   */
  private AuthorizedIdentity authorizedIdentity;


  /**
   * String representation of messages that provide informative or
   * instructional messages.
   */
  private String msg;




  public AuthDemo()
  {
    addLdapExceptionListener(new DefaultLdapExceptionListener(getLogger()));
  }




  @Override
  public String toString()
  {
    StringBuilder sb = new StringBuilder("AuthDemo{");
    sb.append("authId='").append(authId).append('\'');
    sb.append(", authorizedIdentity=").append(authorizedIdentity);
    sb.append(", msg='").append(msg).append('\'');
    sb.append('}');
    return sb.toString();
  }




  /**
   * Adds {@code --bindDN} to the required argument set
   */
  @Override
  protected void addArguments(final ArgumentParser argumentParser)
    throws ArgumentException
  {
    Argument requiredArgument = commandLineOptions.getBindDnArgument();
    Collection<Argument> requiredArgumentSet = SampleCodeCollectionUtils.newArrayList();
    requiredArgumentSet.add(requiredArgument);
    requiredArgument = commandLineOptions.getBindPasswordArgument();
    requiredArgumentSet.add(requiredArgument);
    argumentParser.addRequiredArgumentSet(requiredArgumentSet);
  }




  @Override
  protected ResultCode executeToolTasks()
  {
    try
    {
      getLDAPConnections();
      getAuthorizedIdentity();
      demonstrateWhoAmI();
      demonstrateAuthorizationIdentityRequestControl();
    }
    catch(LDAPException ldapException)
    {
      return ldapException.getResultCode();
    }
    finally
    {
      ldapConnection.close();
    }
    return ResultCode.SUCCESS;
  }




  /**
   * {@inheritDoc}
   */
  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "AuthDemo.properties";
  }




  /**
   * Demonstrate the use of the {@code AuthorizationIdentityRequestControl}.
   */
  public void demonstrateAuthorizationIdentityRequestControl()
  {
    String bindDn = commandLineOptions.getBindDn().toString();
    String bindPassword = commandLineOptions.getBindPassword();
    long responseTimeMillis = getResponseTimeMillis();
    authId =
      authorizedIdentity.getAuthorizationIdentityFromBindRequest(bindDn,
                                                                 bindPassword,responseTimeMillis);
    if(authId != null)
    {
      msg = String.format("AuthorizationID from the " +
                        "AuthorizationIdentityResponseControl: '%s'",authId);
      getLogger().info(msg);
    }
  }




  public void demonstrateWhoAmI() throws LDAPException
  {
    /*
     * Demonstrate the user of the Who Am I? extended operation. This
     * procedure requires creating a WhoAmIExtendedRequest object and
     * using processExtendedOperation to transmit it.
     */
    if(isVerbose())
    {
      verbose("Getting the authorization identity using the Who Am I? " +
                "extended request.");
    }
    authId = authorizedIdentity.getAuthorizationIdentityWhoAmIExtendedOperation();
    if(authId != null && authId.length() > 0)
    {
      msg = String.format("AuthorizationID from the Who am I? extended request: " +
                            "'%s'",authId);
      getLogger().info(msg);
    }
  }




  private void getAuthorizedIdentity()
  {
    /*
     * Instantiate the object which provides methods to get the
     * authorization identity. Add an instance of the default
     * LDAP Exception Listener to the authorized identity provider.
     */
    authorizedIdentity = new AuthorizedIdentity(ldapConnection);
    LdapExceptionListener listener = new DefaultLdapExceptionListener(getLogger());
    authorizedIdentity.addLdapExceptionListener(listener);
    if(isVerbose())
    {
      verbose("Created the authorized identity object: " + authorizedIdentity);
    }
  }




  /**
   * Obtains a pool of connections to the LDAP server from the
   * LDAPCommandLineTool services,this requires specifying a
   * connection to the LDAP server,a number of initial connections
   * (--initialConnections) in the pool,and the maximum number of
   * connections (--maxConnections) that the pool should create.
   */
  private void getLDAPConnections() throws LDAPException
  {
    ldapConnection = connectToServer();
    ldapConnectionPool = getLdapConnectionPool(ldapConnection);
  }
}
