/*
 * Copyright 2008-2012 UnboundID Corp. All Rights Reserved.
 */
package samplecode.ldif;

import samplecode.listener.ProgressEvent;

public class LdifLoadProgressEvent implements ProgressEvent<String>
{

  /**
   * Creates a {@code LdifLoadProgressEvent} with default state.
   *
   * @param progressMessage
   */
  public LdifLoadProgressEvent(final String progressMessage)
  {
    this.progressMessage = progressMessage;
  }

  // The progress message from the client loading LDIF from a file.
  private final String progressMessage;

  /**
   * @return the progressMessage
   */
  @Override
  public final String getProgressMessage()
  {
    return progressMessage;
  }

}
