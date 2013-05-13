/*
 * Utility.java
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
package org.reco4j.util;

import java.util.ArrayList;
import java.util.Iterator;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class Utility
{
  public static ArrayList<Rating> cutList(ArrayList<Rating> recommendations, int recoNumber)
  {
    ArrayList<Rating> recommendationsRes = new ArrayList<Rating>();

    int step = 0;
    for (Rating rate : recommendations)
    {
      if (step > recoNumber)
        break;
      step++;
      recommendationsRes.add(step++, rate);
    }
    return recommendationsRes;
  }

//  public static HashMap<String, Rating> getKNNRow(ArrayList<Rating> recommendations, String itemIdentifierName, int kvalue)
//  {
//    HashMap<String, Rating> knnRow = new HashMap<String, Rating>();
//
//    int step = 0;
//    for (Rating rate : recommendations)
//    {
//      if (step > kvalue)
//        break;
//      step++;
//      knnRow.put(rate.getItem().getProperty(itemIdentifierName), rate);
//    }
//    return knnRow;
//  }
//  
//  public static FastByIDMap<Rating> getFastKNNRow(ArrayList<Rating> recommendations, String itemIdentifierName, int kvalue)
//  {
//    FastByIDMap<Rating> knnRow = new FastByIDMap<Rating>();
//
//    int step = 0;
//    for (Rating rate : recommendations)
//    {
//      if (step > kvalue)
//        break;
//      step++;
//      String itemId = rate.getItem().getProperty(itemIdentifierName);
//      knnRow.put(Long.parseLong(itemId), rate);
//    }
//    return knnRow;
//  }

  public static void orderedInsert(ArrayList<Rating> recommendations, double estimatedRating, INode item)
  {
    //Da sistemare per prevedere il caso in cui ci siano più edge type che stiamo considerando
    //e quindi lo stesso nodo potrebbe comparire più volte
    int index = 0;
    Iterator<Rating> recIterator = recommendations.iterator();
    while (recIterator.hasNext() && recIterator.next().getRate() > estimatedRating)
    {
      index++;
      continue;
    }
    Rating rate = new Rating(item, estimatedRating);
    recommendations.add(index, rate);
  }
  public static void orderedInsert(ArrayList<Rating> recommendations, double estimatedRating, INode item, int size)
  {
    int index = 0;
    Iterator<Rating> recIterator = recommendations.iterator();
    while (recIterator.hasNext() && recIterator.next().getRate() > estimatedRating)
    {
      index++;
      if (index > size)
        return;
      else
        continue;
    }
    Rating rate = new Rating(item, estimatedRating);
    recommendations.add(index, rate);
    if (recommendations.size() > size)
      recommendations.remove(size);
  }
}
