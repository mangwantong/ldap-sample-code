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


import java.util.Properties;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Support for property-backed data values where the type of value is
 * {@link Integer}.
 */
@Since("14-Jun-2012")
@CodeVersion("1.0")
@Author("Terry Gardner")
public class IntegerPropertyValue
        extends PropertyValue<Integer>
{

  @Override
  public Integer getValue()
  {
    final String propAsString = properties.getProperty(key);
    Integer value;
    if(propAsString != null)
    {
      try
      {
        value = Integer.parseInt(propAsString);
      }
      catch(final NumberFormatException nex)
      {
        value = defaultValue;
      }
    }
    else
    {
      value = defaultValue;
    }
    return value;
  }



  /**
   * Creates a {@code IntegerPropertyValue} with default state.
   * 
   * @param properties
   *          the properties that back the data value; not permitted to
   *          be {@code null}
   * 
   * @param key
   *          the key associated with the value; not permitted to be
   *          {@code null}
   * 
   * @param defaultValue
   *          returned from {@link IntegerPropertyValue#getValue()} when
   *          the {@code key} is not associated with a value, that is,
   *          the {@code key} does not exist.
   */
  public IntegerPropertyValue(
          final Properties properties,final String key,final Integer defaultValue)
  {
    super(properties,key,defaultValue);
  }

}
