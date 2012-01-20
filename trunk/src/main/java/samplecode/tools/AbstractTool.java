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
package samplecode.tools;


import com.unboundid.ldap.sdk.ResultCode;
import com.unboundid.util.LDAPCommandLineTool;
import com.unboundid.util.MinimalLogFormatter;
import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;


import java.io.OutputStream;
import java.util.logging.Formatter;


import samplecode.CommandLineOptions;


/**
 * A minimal implementation of the {@code LDAPCommandLineTool} class.
 */
public abstract class AbstractTool extends
    LDAPCommandLineTool
{

  /**
   * Manages command line arguments
   */
  protected CommandLineOptions commandLineOptions;



  /**
   * maximum amount of time to spend waiting for a response from the
   * server
   */
  protected long responseTimeoutMillis;



  /** Whether the tool is verbose during execution */
  protected boolean verbose;



  /**
   * Formats {@code LogRecord} for display.
   */
  private final Formatter formatter = new MinimalLogFormatter();



  /**
   * @param outStream
   * @param errStream
   */
  protected AbstractTool(
      final OutputStream outStream,final OutputStream errStream)
  {
    super(outStream,errStream);
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public void addNonLDAPArguments(final ArgumentParser arg0) throws ArgumentException
  {
    throw new UnsupportedOperationException();
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public ResultCode doToolProcessing()
  {
    throw new UnsupportedOperationException();
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolDescription()
  {
    throw new UnsupportedOperationException();
  }



  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolName()
  {
    throw new UnsupportedOperationException();
  }



  /**
   * @return the formatter
   */
  protected Formatter getFormatter()
  {
    return formatter;
  }



  /**
   * @return the column width of the introduction text.
   */
  protected int getIntroductionColumnWidth()
  {
    return 72;
  }



  /**
   * @return the indentation of the introduction text.
   */
  protected int getIntroductionIndentation()
  {
    return 0;
  }



  /**
   * @return the text to be used for the introduction string.
   */
  protected String getIntroductionString()
  {
    return String.format("%s: %s",getToolName(),getToolDescription());
  }



  /**
   * Display introductory matter. This implementation display the name
   * of the tool and the tool description.
   */
  protected void introduction()
  {
    wrapOut(getIntroductionIndentation(),getIntroductionColumnWidth(),getToolName() + ": " +
                                                                      getToolDescription());
    out();
  }



  protected void setFieldsFromCommandLineOptions()
  {
    responseTimeoutMillis = commandLineOptions.getMaxResponseTimeMillis();
    verbose = commandLineOptions.isVerbose();
  }
}
