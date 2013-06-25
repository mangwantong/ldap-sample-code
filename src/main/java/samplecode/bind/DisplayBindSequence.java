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

package samplecode.bind;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.ldap.sdk.SimpleBindRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedRequest;
import com.unboundid.ldap.sdk.extensions.WhoAmIExtendedResult;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import samplecode.cli.CommandLineOptions;
import samplecode.tools.AbstractTool;


/**
 * Display the authorization identities of a connection during the course of
 * BIND requests
 */
public final class DisplayBindSequence extends AbstractTool {

  /**
   * create and run the {@code DisplayBindSequence} demonstration.
   */
  public static void main(final String[] args) {
    final LDAPCommandLineTool tool = new DisplayBindSequence();
    final ResultCode resultCode = tool.runTool(args);
    if(resultCode.intValue() != 0) {
      System.exit(resultCode.intValue());
    }
  }



  /**
   * Adds tool-specific arguments.
   *
   * @param argumentParser
   *   The argument parser provided by {@code CommandLineTool}
   */
  @Override
  protected void addArguments(final ArgumentParser argumentParser)
    throws ArgumentException {
    argumentParser.addRequiredArgumentSet(
      argumentParser.getNamedArgument(CommandLineOptions.ARG_NAME_BIND_DN),
      argumentParser.getNamedArgument(CommandLineOptions
        .ARG_NAME_BIND_PASSWORD));
  }



  @Override
  protected ResultCode executeToolTasks() {


    LDAPConnection ldapConnection;
    try {
      final String format = "%-64s '%s'";

      /*
       * Connect to directory server, do not authenticate the connection
       */
      ldapConnection =
        new LDAPConnection(commandLineOptions.getHostname(),
          commandLineOptions.getPort());

      try {

        WhoAmIExtendedResult whoAmIExtendedResult =
          (WhoAmIExtendedResult) ldapConnection.processExtendedOperation(new
            WhoAmIExtendedRequest());
        String msg =
          String.format(format,"Authorization identity after initial connection",
            whoAmIExtendedResult.getAuthorizationID());
        out(msg);

      /*
      * Authenticate (simple bind) using the distinguished name and password specified
      * by the --bindDn and --bindPassword command line options.
      */
        ldapConnection.bind(new SimpleBindRequest(commandLineOptions.getBindDn().toString(),
          commandLineOptions.getBindPassword()));

        whoAmIExtendedResult =
          (WhoAmIExtendedResult) ldapConnection.processExtendedOperation(new
            WhoAmIExtendedRequest());
        msg =
          String.format(format,"Authorization identity after simple bind",
            whoAmIExtendedResult.getAuthorizationID());
        out(msg);

      /*
      * Transmit a bind request to the server that will not succeed. The
      * authentication state will be set to unauthenticated.
      */
        try {
          ldapConnection.bind(new SimpleBindRequest("x","x"));
        } catch(LDAPException ldapException) {
          // this block deliberately left empty
        }

        whoAmIExtendedResult =
          (WhoAmIExtendedResult) ldapConnection.processExtendedOperation(new
            WhoAmIExtendedRequest());
        msg =
          String.format(format,"Authorization identity after unsuccessful " +
            "authentication" + " attempt",whoAmIExtendedResult.getAuthorizationID());
        out(msg);

      /*
      * "Reset" the authorization identity of the connection by transmitting
      * a bind request with a zero-length (empty) distinguished name and
      * empty password.
      */
        ldapConnection.bind(new SimpleBindRequest("",""));

        whoAmIExtendedResult =
          (WhoAmIExtendedResult) ldapConnection.processExtendedOperation(new
            WhoAmIExtendedRequest());
        msg =
          String.format(format,"Authorization identity after reset",
            whoAmIExtendedResult.getAuthorizationID());
        out(msg);

      /*
      * Authenticate (simple bind) using the distinguished name and password specified
      * by the --bindDn and --bindPassword command line options.
      */
        ldapConnection.bind(new SimpleBindRequest(commandLineOptions.getBindDn().toString(),
          commandLineOptions.getBindPassword()));

        whoAmIExtendedResult =
          (WhoAmIExtendedResult) ldapConnection.processExtendedOperation(new
            WhoAmIExtendedRequest());
        msg =
          String.format(format,"Authorization identity after simple bind",
            whoAmIExtendedResult.getAuthorizationID());
        out(msg);
      } catch(final LDAPException ldapException) {
        err(ldapException);
        return ldapException.getResultCode();
      } finally {
        ldapConnection.close();
      }
    } catch(final LDAPException ldapException) {
      err(ldapException);
      return ldapException.getResultCode();
    }

    ldapConnection.close();
    return ResultCode.SUCCESS;
  }



  @Override
  protected String classSpecificPropertiesResourceName() {
    return "DisplayBindSequence.properties";
  }

}
