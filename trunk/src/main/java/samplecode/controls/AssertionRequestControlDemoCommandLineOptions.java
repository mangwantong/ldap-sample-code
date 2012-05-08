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


import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.ArgumentParser;
import com.unboundid.util.args.StringArgument;


import samplecode.CommandLineOptions;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Provides command line options services.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 25, 2011")
@CodeVersion("1.1")
class AssertionRequestControlDemoCommandLineOptions
        extends CommandLineOptions
{

  /**
   * Retrieves the value specified by command line argument for the
   * value to which an attribute is set.
   * 
   * @return attribute value.
   */
  String getNewAttributeValue()
  {
    final StringArgument stringArgument =
            (StringArgument)getArgumentParser().getNamedArgument(
                    AssertionRequestControlDemo.ARG_NAME_NEW_ATTRIBUTE_VALUE);
    return stringArgument.getValue();
  }



  /**
   * Prepares the command line options processor and makes the new
   * attribute value command line option available.
   * 
   * @param argumentParser
   *          The argument processor from the
   *          {@code LDAPCommandLineTool}
   * @throws ArgumentException
   *           If an error occurs constructing or adding an argument to
   *           the argument parser.
   */
  AssertionRequestControlDemoCommandLineOptions(
          final ArgumentParser argumentParser)
          throws ArgumentException
  {

    super(argumentParser);

    /*
     * Add to the command line argument parser the command line argument
     * that specifies a new value the attribute that is named by the
     * --attribute command line argument.
     */
    final Character shortIdentifier = AssertionRequestControlDemo.SHORT_ID_NEW_ATTRIBUTE_VALUE;
    final String longIdentifier = AssertionRequestControlDemo.ARG_NAME_NEW_ATTRIBUTE_VALUE;
    final boolean isRequired = true;
    final int maxOccurrences = 1;
    final String valuePlaceholder = "{attribute-value}";
    final String description =
            "The value to which the attribute specified by "
                    + "--attribute final command line argument final is set.";
    final StringArgument stringArgument =
            new StringArgument(shortIdentifier,longIdentifier,isRequired,maxOccurrences,
                    valuePlaceholder,description);
    argumentParser.addArgument(stringArgument);
  }
}
