/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.test;

class TestDataPairing<T> implements DataPairing<T>
{

  private final String string;

  private final T value;






  TestDataPairing(String string,  T value)
  {
    this.string = string;
    this.value = value;
  }






  public String getString()
  {
    return this.string;
  }






  public T getValue()
  {
    return this.value;
  }
}
