/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.test;

class TestDataPairing<T> implements DataPairing<T> {

  TestDataPairing(String string, final T value) {
    this.string = string;
    this.value = value;
  }



  final String string;



  public String getString() {
    return this.string;
  }



  final T value;



  public T getValue() {
    return this.value;
  }
}
