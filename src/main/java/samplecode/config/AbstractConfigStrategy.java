/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */
package samplecode.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import samplecode.logging.LogAware;

public abstract class AbstractConfigStrategy<T> implements ConfigStrategy<T>,LogAware
{

  protected AbstractConfigStrategy()
  {
    logger = LogFactory.getLog(getClass());
  }

  private final Log logger;

  /**
   * retrieves the logger
   *
   * @return the logger
   */
  @Override
  public Log getLogger()
  {
    return logger;
  }

}
