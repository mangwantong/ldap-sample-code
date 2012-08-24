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

import com.unboundid.util.Validator;
import com.unboundid.util.args.Argument;
import com.unboundid.util.args.ArgumentException;
import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Provides support for command line options (arguments) that are backed
 * by a resource bundle which are provided by the client. <blockquote>
 * <p/>
 * <pre>
 * hostnameShortIdentifier = h
 * hostnameDescription     = the hostname or IP address of a server
 * hostnameIsRequired       = false
 * hostnameMaxOccurrences  = 1
 * </pre>
 * <p/>
 * </blockquote>
 * <p/>
 * Keys to property values are based on a name. The name, which is known
 * as the {@code basePropertyName}, has appended to it a string that
 * identifies the parameter as listed in the javadoc for the UnboundID
 * LDAP SDK. The {@code basePropertyName} is used as the
 * "long identifier".
 * <p/>
 * In the following, the {@code basePropertyName} is given as
 * {@code "hostname"}.
 * <p/>
 * Table of Strings Appended
 * <table>
 * <tr>
 * <td>string appended</td>
 * <td>description</td>
 * </tr>
 * <tr>
 * <td>{@code "description"}</td>
 * <td>a string that when appended to the basePropertyName results in
 * the key to the property that is the description of the hostname
 * command line argument.</td>
 * </tr>
 * <tr>
 * <td>{@code "shortIdentifier"}</td>
 * <td>a string that when appended to the basePropertyName results in
 * the key to the property that is the short identifier of the hostname
 * command line argument.</td>
 * </tr>
 * </table>
 *
 * @param <T>
 */
@Since("15-Jun-2012") @CodeVersion("1.0") @Author("Terry Gardner")
public abstract class PropertiesBackedArgument<T extends Argument, E>
{

  /**
   * @param resourceBundle   used to determine the values used for the command line
   *                         argument parameters.
   * @param basePropertyName the base name of keys associated with properties
   */
  protected PropertiesBackedArgument(final ResourceBundle resourceBundle,
          final String basePropertyName)
  {
    Validator.ensureNotNullWithMessage(resourceBundle, "fatal: resourceBundle was null.");
    Validator.ensureNotNullWithMessage(basePropertyName, "fatal: basePropertyName was null.");
    this.resourceBundle = resourceBundle;
    this.basePropertyName = basePropertyName;
  }

  /**
   * @return the value associated with the short identifier key, or
   *         {@code null}
   */
  protected Character getShortIdentifier()
  {
    Character shortIdentifier;
    try
    {
      final String s = resourceBundle.getString(getShortIdentifierKey());
      if(s.length() == 0)
      {
        shortIdentifier = null;
      }
      else
      {
        shortIdentifier =
                Character.valueOf(resourceBundle.getString(getShortIdentifierKey()).charAt(0));
      }
    }
    catch(final MissingResourceException x)
    {
      shortIdentifier = null;
    }
    return shortIdentifier;
  }

  /**
   * @return the key associated with the value of the short identifier
   *         for the command line argument.
   */
  protected String getShortIdentifierKey()
  {
    return basePropertyName + "ShortIdentifier";
  }

  /**
   * @return a default value for this argument
   */
  protected E getDefaultValue()
  {
    E defaultValue;
    try
    {
      defaultValue = convertString(resourceBundle.getString(getDefaultValueKey()));
    }
    catch(final MissingResourceException x)
    {
      defaultValue = null;
    }
    return defaultValue;
  }

  protected abstract E convertString(String value);

  /**
   * @return the key associated with the value of the argument default
   *         value
   */
  protected String getDefaultValueKey()
  {
    return basePropertyName + "DefaultValue";
  }

  /**
   * @return a human-readable description for this argument
   */
  protected String getDescription()
  {
    String description;
    try
    {
      description = resourceBundle.getString(getDescriptionKey());
    }
    catch(final MissingResourceException x)
    {
      description = this.basePropertyName;
    }
    return description;
  }

  /**
   * @return the key associated with the value of the argument
   *         description
   */
  protected String getDescriptionKey()
  {
    return basePropertyName + "Description";
  }

  /**
   * @return the long identifier for this argument
   */
  protected String getLongIdentifier()
  {
    String longIdentifier;
    try
    {
      longIdentifier = resourceBundle.getString(getLongIdentifierKey());
    }
    catch(final MissingResourceException x)
    {
      longIdentifier = this.basePropertyName;
    }
    return longIdentifier;
  }

  /**
   * @return the key associated with the value of the argument
   *         LongIdentifier
   */
  protected String getLongIdentifierKey()
  {
    return basePropertyName + "LongIdentifier";
  }

  /**
   * @return the value place-holder for the command line argument
   */
  protected String getValuePlaceholder()
  {
    String valuePlaceholder;
    try
    {
      valuePlaceholder = resourceBundle.getString(getValuePlaceholderKey());
    }
    catch(final MissingResourceException x)
    {
      valuePlaceholder = this.basePropertyName;
    }
    return valuePlaceholder;
  }

  /**
   * @return the key associated with the value of the value place-holder
   *         for the command line argument.
   */
  protected String getValuePlaceholderKey()
  {
    return basePropertyName + "ValuePlaceholder";
  }

  /**
   * @return the value associated with the maximum number of times this
   *         argument may be provided on the command line. A value less
   *         than or equal to zero indicates that it may be present any
   *         number of times
   */
  protected int getMaxOccurrences()
  {
    int maxOccurrences;
    try
    {
      maxOccurrences = Integer.parseInt(resourceBundle.getString(getMaxOccurrencesKey()));
    }
    catch(final MissingResourceException x)
    {
      maxOccurrences = 0;
    }
    catch(final NumberFormatException nex)
    {
      maxOccurrences = 0;
    }
    return maxOccurrences;
  }

  /**
   * @return the key associated with the value of the argument max
   *         occurrences
   */
  protected String getMaxOccurrencesKey()
  {
    return basePropertyName + "MaxOccurrences";
  }

  @Override
  public String toString()
  {
    final StringBuilder buf = new StringBuilder();

    buf.append("basePropertyName: ");
    buf.append(basePropertyName);
    buf.append("\n");

    buf.append("shortIdentifier: ");
    buf.append(getShortIdentifier());
    buf.append("\n");

    buf.append("longIdentifier: ");
    buf.append(getLongIdentifier());
    buf.append("\n");

    buf.append("isRequired: ");
    buf.append(isRequired());
    buf.append("\n");

    buf.append("maxOccurrences: ");
    buf.append(getMaxOccurrences());
    buf.append("\n");

    buf.append("valuePlaceholder: ");
    buf.append(getValuePlaceholder());
    buf.append("\n");

    buf.append("description: ");
    buf.append(getDescription());
    buf.append("\n");

    buf.append("defaultValue: ");
    buf.append(getDefaultValue());
    buf.append("\n");

    return buf.toString();
  }

  /**
   * @return an indication of whether this argument is required to be
   *         provided
   */
  protected boolean isRequired()
  {
    boolean isRequired;
    try
    {
      isRequired = resourceBundle.getString(getIsRequiredKey()).equals("true");
    }
    catch(final MissingResourceException x)
    {
      isRequired = false;
    }
    return isRequired;
  }

  protected String getIsRequiredKey()
  {
    return this.basePropertyName + "IsRequired";
  }

  /**
   * @return the argument
   */
  public abstract T getArgument() throws ArgumentException;

  private final ResourceBundle resourceBundle;

  protected final ResourceBundle getResourceBundle()
  {
    return this.resourceBundle;
  }

  private final String basePropertyName;

  protected final String getBasePropertyName()
  {
    return this.basePropertyName;
  }

}
