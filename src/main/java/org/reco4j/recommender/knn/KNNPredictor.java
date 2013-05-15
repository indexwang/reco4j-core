/*
 * KNNPredictor.java
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
package org.reco4j.recommender.knn;

import org.reco4j.graph.IEdge;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import org.reco4j.recommender.IPredictor;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class KNNPredictor
  implements IPredictor
{
  private UserItemDataset userItemDataset;
  private KNNModel model;
  private String itemIdentifierNodePropertyName;

  public KNNPredictor(UserItemDataset userItemDataset, KNNModel model, String itemIdentifierNodePropertyName)
  {
    this.userItemDataset = userItemDataset;
    this.model = model;
    this.itemIdentifierNodePropertyName = itemIdentifierNodePropertyName;
  }

  @Override
  public double predictRating(INode user, INode item)
  {
    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;

    //Da verificare
    //if (user.getItemRating(item) != null)
    //  estimatedRating = user.getItemRating(item).getRate();

    String id = item.getProperty(itemIdentifierNodePropertyName);
    if (!model.containsItem(id))
      return 0.0;
    
    for (Rating rate : model.getItemRatings(id))
    {
      IEdge edge = userItemDataset.getRatingEdge(user, rate.getItem());
      if (edge == null)
        continue;
      double uRate = userItemDataset.getRating(edge);
      double similarityBetweenItem = rate.getRate();
      weightedRatingSum += uRate * similarityBetweenItem;
      similaritySum += similarityBetweenItem;
    }
    
    if (similaritySum > 0)
      estimatedRating = weightedRatingSum / similaritySum;
    
    return estimatedRating;
  }
}
