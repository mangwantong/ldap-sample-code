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


/**
 * Defines services which must be provided by the descriptive name
 * builder, which is a realization of the Builder design pattern.
 * <p>
 * example usage: <blockquote>
 * 
 * <pre>
 * class ExampleClass {
 *   pbulic static class Builder implements DescriptiveNameBuilder&lt;ExampleClass&gt; {
 *     int intValue;
 *     public Builder intValue(int intValue) {
 *       this.intValue = intValue
 *     }
 *     public ExampleClass build() {
 *       return new ExampleClass(this);
 *     }
 *   }
 *   private final int intValue;
 *   private ExampleClass(Builder builder) {
 *     this.intValue = builder.intValue;
 *   }
 * }
 * </pre>
 * </blockquote>
 * 
 * @param <T>
 *          The type of object built by the Builder.
 */
@Author("terry.gardner@unboundid.com")
@Since("Jan 01, 2008")
@CodeVersion("1.2")
interface DescriptiveNameBuilder<T> {


  /**
   * Build a new instance of type {@code T}.
   * 
   * @return a new instance of type {@code T}.
   */
  T build();
}
