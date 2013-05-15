/*
 * NodeRatingStatistics.java
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
package org.reco4j.model;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class NodeRatingStatistics
{
  private int ratingCount = 0;
  private double ratingSum = 0.0;
  private double ratingAvg = 0.0;
  private double pseudoAvg = 0.0;

  public void addRating(double rating)
  {
    ratingCount++;
    ratingSum += rating;
  }

  public void setStatistics()
  {
    if (ratingCount > 0)
    {
      setRatingAvg(ratingSum / ratingCount);
      setPseudoAvg(ratingSum + ((3.23 * 25) / (ratingCount + 25)));
    }
  }

  public int getRatingCount()
  {
    return ratingCount;
  }

  public void setRatingCount(int RatingCount)
  {
    this.ratingCount = RatingCount;
  }

  public double getRatingSum()
  {
    return ratingSum;
  }

  public void setRatingSum(double RatingSum)
  {
    this.ratingSum = RatingSum;
  }

  public double getRatingAvg()
  {
    return ratingAvg;
  }

  public void setRatingAvg(double RatingAvg)
  {
    this.ratingAvg = RatingAvg;
  }

  public double getPseudoAvg()
  {
    return pseudoAvg;
  }

  public void setPseudoAvg(double PseudoAvg)
  {
    this.pseudoAvg = PseudoAvg;
  }
}
