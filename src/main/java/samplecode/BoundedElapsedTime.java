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
 * Calculates elapsed time from the time it is instantiated and reports
 * a upper-bounded elapsed time upon request.
 */
@Author("terry.gardner@unboundid.com")
@Since("Dec 23, 2011")
@CodeVersion("1.0")
public final class BoundedElapsedTime
{


  private final long timeBeforeSearchInMillis = System.currentTimeMillis();


  /**
   * Reports the elapsed time since this object was instantiated bounded
   * by {@code upperBound}.
   * 
   * @param upperBound
   *          the upper limit of of the elapsed time.
   * @return elapsed time since this object was instantiated bounded by
   *         {@code upperBound}.
   */
  public long getBoundedElapsedTime(final long upperBound)
  {
    final long timeAfterSearchInMillis = System.currentTimeMillis();
    long boundedElapsedTime;
    if(timeAfterSearchInMillis >= this.timeBeforeSearchInMillis)
    {
      long elapsedTime =
          timeAfterSearchInMillis - this.timeBeforeSearchInMillis;
      if(elapsedTime > upperBound)
      {
        elapsedTime = 0;
      }
      boundedElapsedTime = upperBound - elapsedTime;
    }
    else
    {
      boundedElapsedTime = upperBound;
    }
    return boundedElapsedTime;
  }


  @Override
  public String toString()
  {
    return "BoundedElapsedTime [timeBeforeSearchInMillis=" +
        this.timeBeforeSearchInMillis + "]";
  }
}
