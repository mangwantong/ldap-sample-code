/*
 * Copyright 2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.test;

/**
 * provides key-value (data pairing) services to clients
 */
interface DataPairing<T> {

  /**
   * @return the key
   */
  String getString();

  /**
   * @return the value
   */
  T getValue();
}
