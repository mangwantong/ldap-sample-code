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
package samplecode;


import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.Validator;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * TODO
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 23, 2011")
@CodeVersion("1.0")
public final class SkeletonTool
        extends LDAPCommandLineTool
{

  /**
   * The description of this tool; used in help and diagnostic output
   * and for other purposes.
   */
  public static final String TOOL_DESCRIPTION = "";



  /**
   * The name of this tool; used in help and diagnostic output and for
   * other purposes.
   */
  public static final String TOOL_NAME = "";



  /**
   * TODO: Provide a comment for this method.
   * 
   * @param args
   */
  public static void main(final String[] args)
  {
    final OutputStream outStream = System.out;
    final OutputStream errStream = System.err;
    final Formatter formatter = new MinimalLogFormatter();
    final SkeletonTool tool = SkeletonTool.newSkeletonTool(outStream,errStream,formatter);
    final ResultCode resultCode = tool.runTool(args);
    if(resultCode != null)
    {
      final String msg =
              String.format("%s has completed processing. The result code was: %s.",
                      tool.getToolName(),resultCode.toString());
      final LogRecord logRecord = new LogRecord(Level.INFO,msg);
      final String text = formatter.format(logRecord);
      tool.out(text);
    }
  }



  /**
   * Creates a new {@code SkeletonTool} object with the specified error
   * stream and output stream and logging formatter.
   * 
   * @param outStream
   *          The stream to which information output is transmitted.
   * @param errStream
   *          The stream to which error output are transmitted.
   * @param formatter
   *          The formatter used to format information and error output.
   * @return a new SkeletonTool
   */
  public static SkeletonTool newSkeletonTool(final OutputStream outStream,
          final OutputStream errStream,final Formatter formatter)
  {
    Validator.ensureNotNull(outStream,errStream,formatter);
    return new SkeletonTool(outStream,errStream,formatter);
  }



  @Override
  public void addNonLDAPArguments(final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    commandLineOptions =
            SkeletonToolCommandLineOptions.newSkeletonToolCommandLineOptions(argumentParser);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {
    final ResultCode resultCode = ResultCode.SUCCESS;
    return resultCode;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    return SkeletonTool.TOOL_DESCRIPTION;
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    return SkeletonTool.TOOL_NAME;
  }



  /**
   * Initializes the new {@code SkeletonTool} object with output stream
   * {@code System.out}. error stream {@code System.err}, and a minimal
   * log formatter.
   */
  public SkeletonTool()
  {
    this(System.out,System.err,new MinimalLogFormatter());
  }



  /**
   * Initializes the new {@code SkeletonTool} object with the specified
   * error stream and output stream and logging formatter.
   * 
   * @param outStream
   *          The stream to which information output is transmitted.
   * @param errStream
   *          The stream to which error output are transmitted.
   * @param formatter
   *          The formatter used to format information and error output.
   */
  public SkeletonTool(
          final OutputStream outStream,final OutputStream errStream,final Formatter formatter)
  {
    super(outStream,errStream);
    this.formatter = formatter;
  }



  // Handles command line argument processing for this tool.
  @SuppressWarnings("unused")
  private SkeletonToolCommandLineOptions commandLineOptions;



  // The formatter used to format information and error output.
  @SuppressWarnings("unused")
  private final Formatter formatter;
}


/**
 * Provides support for command line arguments required by this tool.
 */
final class SkeletonToolCommandLineOptions
        extends CommandLineOptions
{

  public static SkeletonToolCommandLineOptions newSkeletonToolCommandLineOptions(
          final ArgumentParser argumentParser) throws ArgumentException
  {
    Validator.ensureNotNull(argumentParser);
    return new SkeletonToolCommandLineOptions(argumentParser);
  }



  private SkeletonToolCommandLineOptions(
          final ArgumentParser argumentParser)
          throws ArgumentException
  {
    super(argumentParser);
  }
}
