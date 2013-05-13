/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.util.Utility;

/**
 *
 * @author giuri
 */
public class ItemFullScanUserRecommender
        implements IUserRecommender
{

  private int recoNumber;
  private IPredictor predictor;
  protected IEdgeType rankEdgeType;
  private Collection<INode> items;

  public ItemFullScanUserRecommender(int recoNumber, IPredictor predictor, IEdgeType rankEdgeType, Collection<INode> items)
  {
    this.recoNumber = recoNumber;
    this.predictor = predictor;
    this.rankEdgeType = rankEdgeType;
    this.items = items;
  }

  @Override
  public List<Rating> userRecommend(INode user)
  {

    ArrayList<Rating> recommendations = new ArrayList<Rating>();

    for (INode item : items) // learningDataSet.getNodesByType(getConfig().getItemType()))
    {
      if (item.isConnected(user, rankEdgeType))
        continue;
      double estimatedRating = predictor.predictRating(user, item);
      Utility.orderedInsert(recommendations, estimatedRating, item, recoNumber);
    }
    return recommendations;
  }
}
