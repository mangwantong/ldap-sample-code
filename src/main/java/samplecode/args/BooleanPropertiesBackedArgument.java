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

package samplecode.args;


import com.unboundid.util.args.ArgumentException;
import com.unboundid.util.args.BooleanArgument;

import java.util.ResourceBundle;

import static com.unboundid.util.Validator.ensureNotNull;


/**
 * Provide support for a {@link PropertiesBackedArgument} whose value is
 * an {@link Boolean}.
 */
public class BooleanPropertiesBackedArgument
        extends PropertiesBackedArgument<BooleanArgument,Boolean> {



  private BooleanPropertiesBackedArgument(
          final ResourceBundle resourceBundle, final String basePropertyName) {
    super(resourceBundle,basePropertyName);
  }



  /**
   * @param resourceBundle
   *         the resource bundle from which the command line argument
   *         are taken
   * @param basePropertyName
   *         the base property name
   *
   * @return a new {@code StringPropertiesBackedArgument} object.
   */
  public static BooleanPropertiesBackedArgument
    newBooleanPropertiesBackedArgument(
          final ResourceBundle resourceBundle, final String basePropertyName) {
    return new BooleanPropertiesBackedArgument(resourceBundle,basePropertyName);
  }



  /**
   * @return the {@code value} as an {@link Integer}.
   */
  @Override
  protected Boolean convertString(final String value) {
    ensureNotNull(value);

    return Boolean.valueOf(value);
  }







  @Override
  public BooleanArgument getArgument() throws ArgumentException {
    return new BooleanArgument(getShortIdentifier(),getLongIdentifier(),
                               getMaxOccurrences(),getDescription());
  }



}
