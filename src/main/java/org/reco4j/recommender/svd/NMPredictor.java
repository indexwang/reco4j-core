/*
 * NMPredictor.java
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
package org.reco4j.recommender.svd;

import java.util.HashMap;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.recommender.IPredictor;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class NMPredictor
        implements IPredictor
{

  private String itemIdentifierName;
  private HashMap<String, HashMap<String, Rating>> knn;
  protected IEdgeType rankEdgeType;
  private String edgeRankValueName;

  @Override
  public double predictRating(INode user, INode item)
  {
    return calculateEstimatedRating(item, user, rankEdgeType, edgeRankValueName);
  }

  protected double calculateEstimatedRating(INode item, INode user, IEdgeType rankType, String edgeRankValueName) throws RuntimeException
  {

    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;
    //Da verificare
    //if (user.getItemRating(item) != null)
    //  estimatedRating = user.getItemRating(item).getRate();
    String id = item.getProperty(itemIdentifierName);
    if (!knn.containsKey(id))
      return 0.0;
    for (Rating rate : knn.get(id).values())
    {
      IEdge edge = user.getEdge(rate.getItem(), rankType);
      if (edge == null)
        continue;
      double uRate = getUserRate(edge, edgeRankValueName, rankType);
      double similarityBetweenItem = rate.getRate();
      weightedRatingSum += uRate * similarityBetweenItem;
      similaritySum += similarityBetweenItem;
    }
    if (similaritySum > 0)
      estimatedRating = weightedRatingSum / similaritySum;
    return estimatedRating;
  }

  private double getUserRate(IEdge edge, String propertyName, IEdgeType rankType) throws RuntimeException
  {
    String propertyValue = edge.getProperty(propertyName);
    if (propertyValue == null)
      throw new RuntimeException("Properties : " + propertyName + " not found on edge of type: " + rankType.getType());
    double uRate = Double.parseDouble(propertyValue);
    return uRate;
  }
}
