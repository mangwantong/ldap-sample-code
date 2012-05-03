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


import com.unboundid.util.NotMutable;
import com.unboundid.util.Validator;


import java.util.List;
import java.util.Map;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * TODO
 * 
 * @param <T>
 */
@NotMutable
@Since("1.1, 04-Oct-2011")
class MapArgMsgr<T>
{

  /**
   * @return The name.
   */
  public String name()
  {
    return name;
  }



  /**
   * @return The value
   */
  public T value()
  {
    return value;
  }



  /**
   * @param name
   * @param value
   */
  public MapArgMsgr(
          final String name,final T value)
  {
    if(name == null)
    {
      throw new NullPointerException(); // TODO
    }
    if(value == null)
    {
      throw new NullPointerException(); // TODO
    }

    this.name = name;
    this.value = value;
  }



  private final String name;



  private final T value;
}


/**
 * This class cannot be instantiated; attempts to instantiate this class
 * will result in an {@link UnsupportedOperationException} being thrown
 * from the constructor.
 */
@Author("Terry.Gardner@UnboundID.COM")
@Since("Oct 16, 2011")
@CodeVersion("1.2")
final class SampleCodeClassUtils
{

  /**
   * A mapped new line.
   */
  public static final Map<String,Object> NEW_LINE = SampleCodeClassUtils.mapArg("","\n");



  /**
   * The default format string; column one is 16 characters wide and
   * column 2 is a String of unspecified length.
   */
  final static String DEFAULT_FORMAT = "%1$-48s %2$s\n";



  /**
   * The map key to the object's name
   */
  static final String KEY_FORMAT_OBJECT_NAME = "objectName";



  /**
   * The map key to the object's value
   */
  static final String KEY_FORMAT_OBJECT_VALUE = "objectValue";



  /**
   * @param builder
   * @param o
   */
  public static void appendHashCodeString(final StringBuilder builder,final Object o)
  {
    SampleCodeClassUtils.appendHashCodeString(builder,o,null);
  }



  /**
   * @param builder
   * @param o
   * @param format
   *          defaults to "%-32s 0x%x\n"
   */
  public static void appendHashCodeString(final StringBuilder builder,final Object o,
          String format)
  {
    if(builder == null)
    {
      throw new NullPointerException();
    }
    if(o == null)
    {
      throw new NullPointerException();
    }
    if(format == null)
    {
      format = "%-32s 0x%x\n";
    }

    builder.append(String.format(format,"hashCode",Integer.valueOf(o.hashCode())));
  }



  /**
   * Maps the argument {@code value} by its key {@code name}.
   * 
   * @param <T>
   * @param fieldName
   * @param value
   * 
   * @return a mapped object.
   */
  public static <T> Map<String,Object> mapArg(final String fieldName,final T value)
  {
    final Map<String,Object> m = SampleCodeCollectionUtils.newHashMap();
    m.put(SampleCodeClassUtils.KEY_FORMAT_OBJECT_NAME,fieldName);
    m.put(SampleCodeClassUtils.KEY_FORMAT_OBJECT_VALUE,value);
    return m;
  }



  /**
   * @param msgrs
   * 
   * @return A list of the msgr objects
   */
  public static List<Map<String,Object>> mapArgs(final List<MapArgMsgr<?>> msgrs)
  {

    Validator.ensureNotNull(msgrs);

    final List<Map<String,Object>> mapArgs = SampleCodeCollectionUtils.newArrayList();
    for(final MapArgMsgr<?> m : msgrs)
    {
      mapArgs.add(SampleCodeClassUtils.mapArg(m.name(),m.value()));
    }
    return mapArgs;
  }



  private SampleCodeClassUtils()
  {
    throw new UnsupportedOperationException(); // TODO
  }

}
