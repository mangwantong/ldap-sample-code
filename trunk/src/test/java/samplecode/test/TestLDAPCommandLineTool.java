/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.test;

import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


/**
 * provides support for basic LDAP command line tools; a convenient
 * way to get the basic {@code LDAPCommandLineTool} command line
 * arguments used in testing.
 */
class TestLDAPCommandLineTool extends LDAPCommandLineTool {

  TestLDAPCommandLineTool() {
    super(System.out,System.err);
  }



  /**
   * Performs the core set of processing for this tool.
   *
   * @return A result code that indicates whether the processing completed
   *         successfully.
   */
  @Override
  public ResultCode doToolProcessing() {
    return ResultCode.SUCCESS;
  }



  /**
   * Retrieves a human-readable description for this tool.
   *
   * @return A human-readable description for this tool.
   */
  @Override
  public String getToolDescription() {
    return "generates the basic LDAP command line tool arguments";
  }



  /**
   * Retrieves the name of this tool.  It should be the name of the command
   * used
   * to invoke this tool.
   *
   * @return The name for this tool.
   */
  @Override
  public String getToolName() {
    return "Test Tool";
  }



  /**
   * Adds the arguments needed by this command-line tool to the provided
   * argument parser which are not related to connecting or authenticating to
   * the directory server.
   *
   * @param parser
   *   The argument parser to which the arguments should be added.
   *
   * @throws com.unboundid.util.args.ArgumentException
   *   If a problem occurs while adding the arguments.
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser parser)
    throws ArgumentException {
    // this block deliberately left blank
  }
}
