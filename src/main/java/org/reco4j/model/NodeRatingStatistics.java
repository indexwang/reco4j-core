/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.model;

/**
 *
 * @author giuri
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
