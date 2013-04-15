/*
 * MFRecommender.java
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
package org.reco4j.graph.recommenders;

import java.util.List;
import org.reco4j.graph.*;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class MFRecommender2
  extends BasicRecommender<IMFRecommenderConfig>
{
  private UserItemDataset userItemDataset;
  private MFModel model;
  private IUserRecommender recommender;

  public MFRecommender2(IMFRecommenderConfig config)
  {
    super(config);
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    userItemDataset = new UserItemDataset();
    userItemDataset.init(learningDataSet, getConfig().getItemType(), getConfig().getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, getConfig().getGraphConfig()), getConfig().getEdgeRankValueName());

    MFModelBuilder builder = new MFModelBuilder(userItemDataset, getConfig().getMaxFeatures(), getConfig().getFeatureInitValue());
    model = builder.build();
  }

  @Override
  public List<Rating> recommend(INode user)
  {
    return getRecommender().userRecommend(user);
//    
//    final int recoNumber = getConfig().getRecoNumber();
//
//    ArrayList<Rating> recommendations = new ArrayList<Rating>();
//
//    for (INode item : userItemDataset.getItemList().values()) // learningDataSet.getNodesByType(getConfig().getItemType()))
//    {
//      if (item.isConnected(user, rankEdgeType))
//        continue;
//      double estimatedRating = getPredictor().predictRating(user.getId(), item.getId());
//      Utility.orderedInsert(recommendations, estimatedRating, item, recoNumber);
//    }
//    return recommendations;
  }

  @Override
  public double estimateRating(INode user, INode item)
  {
    return 0; // getPredictor().predictRating(user.getId(), item.getId());
  }
//
//  private void calcMetrics()
//  {
//    userItemDataset.setNodeRatingStatistics();
//  }

  private IUserRecommender getRecommender()
  {
    if (recommender == null)
      recommender = new ItemFullScanUserRecommender(getConfig().getRecoNumber(), new MFPredictor1(model), rankEdgeType, userItemDataset.getItemList().values());
    
    return recommender;
  }

}
