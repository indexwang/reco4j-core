/*
 * TimeReportUtility.java
 * 
 * Copyright (C) 2013 Alessandro Negro <alessandro.negro at reco4j.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.reco4j.util;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class TimeReportUtility
{
  private long startTime;
  private long endTimeTime;
  private long totalTime;
  private long count;
  private long min;
  private long max;
  private String name;
  
  public TimeReportUtility(String name)
  {
    this.name = name;    
    totalTime = 0;
    min = Long.MAX_VALUE;
    max = Long.MIN_VALUE;
    count = 0;
  }
  
  public void start()
  {
    startTime = System.currentTimeMillis();
  }
  public void stop()
  {
    endTimeTime = System.currentTimeMillis();
    long diff = endTimeTime - startTime;
    totalTime += diff;
    count++;
    if (diff < min)
      min = diff;
    if (diff > max)
      max = diff;
    //System.out.println("Timer " + name + ": " + diff);
    startTime = System.currentTimeMillis();
  }
  public void printStatistics()
  {
    long med = totalTime/count;
    StringBuilder output = new StringBuilder("Timer");
    output.append(" ").append(name).append(" -");
    output.append(" totalTime: ").append(totalTime);
    output.append(" count: ").append(count);
    output.append(" min: ").append(min);
    output.append(" max: ").append(max);
    output.append(" med: ").append(med);
    System.out.println(output.toString());
    
  }
}
