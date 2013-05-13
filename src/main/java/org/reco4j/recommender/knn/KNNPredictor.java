/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender.knn;

import org.reco4j.graph.IEdge;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import org.reco4j.recommender.IPredictor;

/**
 *
 * @author giuri
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
