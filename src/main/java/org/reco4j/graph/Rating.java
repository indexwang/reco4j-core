/*
 * Rating.java
 * 
 * Copyright (C) 2012 Alessandro Negro <alessandro.negro at reco4j.org>
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
package org.reco4j.graph;

import java.sql.Timestamp;
import org.apache.mahout.cf.taste.model.Preference;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class Rating implements Preference
{
  protected INode item = null;
  protected INode user = null;  
  protected double rate;  
  protected Timestamp ratingDate = null;

  public Rating(INode item, double rate)
  {
    this(null, item, rate, null);
  }
  public Rating(INode user, INode item, double rate, Timestamp ratingDate)
  {
    this.user = user;
    this.item = item;
    this.rate = rate;
    this.ratingDate = ratingDate;
  }
  

  public INode getItem()
  {
    return item;
  }

  public void setItem(INode item)
  {
    this.item = item;
  }

  public INode getUser()
  {
    return user;
  }

  public void setUser(INode user)
  {
    this.user = user;
  }

  public double getRate()
  {
    return rate;
  }

  public void setRate(double rate)
  {
    this.rate = rate;
  }

  public Timestamp getRatingDate()
  {
    return ratingDate;
  }

  public void setRatingDate(Timestamp ratingDate)
  {
    this.ratingDate = ratingDate;
  }

  @Override
  public long getUserID()
  {
    return user.getId();
  }

  @Override
  public long getItemID()
  {
    return item.getId();
  }

  @Override
  public float getValue()
  {
    return (float)rate;
  }

  @Override
  public void setValue(float value)
  {
    rate = (double)value;
  }
}
