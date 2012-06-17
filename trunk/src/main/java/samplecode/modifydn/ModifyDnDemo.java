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
package samplecode.modifydn;


import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.LDAPResult;
import com.unboundid.ldap.sdk.ModifyDNRequest;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.BooleanArgument;
import com.unboundid.util.args.StringArgument;


import java.util.logging.Level;
import java.util.logging.Logger;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Launchable;
import samplecode.annotation.Since;
import samplecode.exception.ExceptionMsgFactory;
import samplecode.tools.AbstractTool;


/** Demonstrates how to use the modify DN request */
@Author("terry.gardner@unboundID.com")
@Since("Oct 30, 2011")
@CodeVersion("1.5")
@Launchable
public final class ModifyDnDemo
        extends AbstractTool
{



  /**
   * The long identifier of the argument used to specify to delete the
   * old RDN
   */
  private static final String ARG_NAME_DELETE_OLD_RDN = "deleteOldRdn";



  /**
   * The long identifier of the argument used to specify an existing
   * distinguished name
   */
  private static final String ARG_NAME_EXISTING_DN = "existingDn";



  /**
   * The long identifier of the argument used to specify the new
   * distinguished name
   */
  private static final String ARG_NAME_NEW_DN = "newDn";



  /**
   * The long identifier of the argument used to specify the new
   * superior distinguished name
   */
  private static final String ARG_NAME_NEW_SUPERIOR_DN = "newSuperiorDn";



  /** Demonstrate the tool */
  public static void main(final String... args)
  {
    new ModifyDnDemo().runTool(args);
  }



  @Override
  public Logger getLogger()
  {
    return Logger.getLogger(getClass().getSimpleName());
  }



  @Override
  protected void addArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    super.addArguments(argumentParser);

    /** add --existingDn command line argument */
    argumentParser.addArgument(new StringArgument(null,ModifyDnDemo.ARG_NAME_EXISTING_DN,true,
            1,"dn","The existing DN","cn=old,dc=example,dc=com"));

    /** add --newDn command line argument */
    argumentParser.addArgument(new StringArgument(null,ModifyDnDemo.ARG_NAME_NEW_DN,true,1,
            "dn","The new DN","cn=new,dc=example,dc=com"));

    /** add --deleteOldRdn command line argument */
    argumentParser.addArgument(new BooleanArgument(null,ModifyDnDemo.ARG_NAME_DELETE_OLD_RDN,0,
            "whether to delete the old RDN"));

    /** add --newSuperiorDn command line argument */
    argumentParser.addArgument(new StringArgument(null,ModifyDnDemo.ARG_NAME_NEW_SUPERIOR_DN,
            false,1,"dn","The new superior DN"));
  }



  @Override
  protected String classSpecificPropertiesResourceName()
  {
    return "ModifyDnDemo.properties";
  }



  @Override
  protected ResultCode executeToolTasks()
  {
    final String existingDn = (String)commandLineOptions.get(ModifyDnDemo.ARG_NAME_EXISTING_DN);
    final String newDn = (String)commandLineOptions.get(ModifyDnDemo.ARG_NAME_NEW_DN);
    final boolean deleteOldRdn =
            (Boolean)commandLineOptions.get(ModifyDnDemo.ARG_NAME_DELETE_OLD_RDN);
    final String newSuperiorDn =
            (String)commandLineOptions.get(ModifyDnDemo.ARG_NAME_NEW_SUPERIOR_DN);
    final ModifyDNRequest modifyDnRequest =
            new ModifyDNRequest(existingDn,newDn,deleteOldRdn,newSuperiorDn);
    LDAPResult ldapResult;
    try
    {
      ldapResult = getConnection().modifyDN(modifyDnRequest);
      ldapConnection.close();
    }
    catch(final LDAPException exception)
    {
      getLogger().log(Level.SEVERE,ExceptionMsgFactory.getLdapExceptionMsg(exception).msg());
      return exception.getResultCode();
    }
    return ldapResult.getResultCode();
  }
}
