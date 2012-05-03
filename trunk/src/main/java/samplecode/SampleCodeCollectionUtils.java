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


import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


import samplecode.annotation.Author;
import samplecode.annotation.CodeVersion;
import samplecode.annotation.Since;


/**
 * Utility methods. This class cannot be instantiated.
 * <p>
 * Includes methods for
 * <ul>
 * <li>Converting delimited string into a list of strings.</li>
 * <li>Creating a {@link List} in a type-safe way.</li>
 * <li>Creating a {@link Map} in a type-safe way.</li>
 * </ul>
 * 
 * @author Terry.Gardner@UnboundID.COM
 * @since 1.0
 */
@Author("terry.gardner@unboundid.com")
@Since("01-Jan-2008")
@CodeVersion("1.2")
public final class SampleCodeCollectionUtils
{

  /**
   * Given a {@link String} that consists of strings delimited by a
   * comma, returns a list wherein each member of the list was a string
   * delimited by a comma in the original string.
   * 
   * @param csv
   *          A comma-separated list of strings.
   * @return Never returns {@code null}, that is, a list is always
   *         returned (but it might be empty.)
   */
  public static List<String> csvToList(final String csv)
  {

    List<String> result;
    if(csv != null)
    {
      result = SampleCodeCollectionUtils.newArrayList();
      final StringTokenizer t = new StringTokenizer(csv,",");
      while(t.hasMoreTokens())
      {
        result.add(t.nextToken());
      }
    }
    else
    {
      result = Collections.emptyList();
    }
    return result;
  }



  /**
   * Returns a new {@link ArrayList} in a type-safe way.
   * 
   * @param <T>
   *          The type of each element of the list.
   * @return A new ArrayList.
   */
  public static <T> List<T> newArrayList()
  {

    return new ArrayList<T>();
  }



  /**
   * Returns a new {@link ArrayList} in a type-safe way.
   * 
   * @param <T>
   *          The type of each element of the list.
   * @param size
   *          The initial size of the list.
   * @return A new ArrayList.
   */
  public static <T> List<T> newArrayList(final int size)
  {

    return new ArrayList<T>(size);
  }



  /**
   * Returns a new {@link ConcurrentHashMap} in a type-safe way.
   * 
   * @param <K>
   *          The type of the keys in the map.
   * @param <V>
   *          The type of values in the map.
   * @return A new ConcurrentHashMap.
   */
  public static <K,V> Map<K,V> newConcurrentHashMap()
  {

    return new ConcurrentHashMap<K,V>();
  }



  /**
   * Returns a new {@link EnumMap} in a type-safe way.
   * 
   * @param <K>
   *          The type of the keys in the map.
   * @param <V>
   *          The type of values in the map.
   * @param keyClass
   *          the class object of the key type for this enum map.
   * @return A new enum map.
   */
  public static <K extends Enum<K>,V> Map<K,V> newEnumMap(final Class<K> keyClass)
  {
    return new EnumMap<K,V>(keyClass);
  }



  /**
   * Returns a new {@link HashMap} in a type-safe way.
   * 
   * @param <K>
   *          The type of the keys in the map.
   * @param <V>
   *          The type of values in the map.
   * @return A new HashMap.
   */
  public static <K,V> Map<K,V> newHashMap()
  {

    return new HashMap<K,V>();
  }



  /**
   * Returns a new {@link HashSet} in a type-safe way.
   * 
   * @param <E>
   *          The type of the objects in the set.
   * @return A new HashSet.
   */
  public static <E> Set<E> newHashSet()
  {
    return new HashSet<E>();
  }



  /**
   * Returns a new {@link HashSet} in a type-safe way.
   * 
   * @param <E>
   *          The type of the objects in the set.
   * @param c
   *          a collection of objects that will be used to create the
   *          set.
   * @return A new HashSet.
   */
  public static <E> Set<E> newHashSet(final Collection<E> c)
  {
    return new HashSet<E>(c);
  }



  /**
   * Returns a new {@link HashSet} in a type-safe way.
   * 
   * @param <E>
   *          The type of the objects in the set.
   * @return A new HashSet.
   */
  public static <E> Set<E> newSortedSet()
  {
    return new ConcurrentSkipListSet<E>();
  }



  private SampleCodeCollectionUtils()
  {
    // This block deliberately left empty.
  }
}
