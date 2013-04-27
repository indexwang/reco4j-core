/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.graph.UserItemDataset;

/**
 *
 * @author giuri
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
