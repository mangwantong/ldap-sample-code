/*
 * Copyright 2012 UnboundID Corp. All Rights Reserved.
 */

package samplecode.config;

/**
 * defines configuration strategy services
 */
public interface ConfigStrategy<T> {

  /**
   * @return a configuration
   */
  T createConfiguration();

}
