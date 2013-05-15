/*
 * KNNFastPredictor.java
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

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import org.reco4j.recommender.IPredictor;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class KNNFastPredictor
  implements IPredictor
{
  private UserItemDataset userItemDataset;
  private KNNFastModel model;
  private String itemIdentifierNodePropertyName;

  public KNNFastPredictor(UserItemDataset userItemDataset, KNNFastModel model)
  {
    this.userItemDataset = userItemDataset;
    this.model = model;
  }

  @Override
  public double predictRating(INode user, INode item)
  {
    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;

    if (!model.containsItem(item.getId()))
      return 0.0;
    FastByIDMap<Rating> rowItem = model.getItemRatings(item.getId());
    final LongPrimitiveIterator rowKeySetIterator = rowItem.keySetIterator();
    while (rowKeySetIterator.hasNext())
    {
      Rating rate = rowItem.get(rowKeySetIterator.next());
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
