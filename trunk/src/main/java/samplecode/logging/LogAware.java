/*
 * Copyright 2012 UnboundID Corp. All Rights Reserved.
 */
package samplecode.logging;

import org.apache.commons.logging.Log;

/**
 * defines services for clients that need logging
 */
public interface LogAware
{
  /** @return the logger */
  Log getLogger();
}
