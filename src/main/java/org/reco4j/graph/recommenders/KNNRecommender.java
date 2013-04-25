/*
 * KNNRecommender.java
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
import java.util.logging.Logger;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.graph.UserItemDataset;
import org.reco4j.graph.similarity.ISimilarity;
import org.reco4j.graph.similarity.SimilarityFactory;
import org.reco4j.util.TimeReportUtility;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class KNNRecommender
        extends BasicRecommender<ICollaborativeFilteringRecommenderConfig>
{

  private static final Logger logger = Logger.getLogger(KNNRecommender.class.getName());
  private ISimilarity similarityFunction;
  private UserItemDataset userItemDataset;
  private KNNModel model;
  private IUserRecommender recommender;
  private KNNPredictor1 predictor;

  //protected ArrayList<IEdgeType> edges;
  //
  public KNNRecommender(ICollaborativeFilteringRecommenderConfig config)
  {
    super(config);
    similarityFunction = SimilarityFactory.getSimilarityClass(getConfig().getSimilarityConfig());
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
    timeReport.start();
    userItemDataset = new UserItemDataset();
    userItemDataset.init(learningDataSet, getConfig().getItemType(), getConfig().getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, getConfig().getGraphConfig()), getConfig().getEdgeRankValueName());

    KNNModelBuilder knnModelBuilder = new KNNModelBuilder(userItemDataset, getConfig().getItemIdentifierName(), rankEdgeType, similarityFunction, EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig()), getConfig().getRecalculateSimilarity());
    model = knnModelBuilder.build();

    timeReport.stop();
  }

  @Override
  public void loadRecommender(IGraph modelGraph)
  {
    KNNModelLoader knnModelLoader = new KNNModelLoader(modelGraph, getConfig().getItemIdentifierName(), similarityFunction, EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig()));
    model = knnModelLoader.build();
    model.logKNN();
  }

  @Override
  public List<Rating> recommend(INode user)
  {
    return getRecommender().userRecommend(user);
  }

  @Override
  public double estimateRating(INode user, INode item)
  {
    return getPredictor().predictRating(user, item);
  }

  @Override
  public void updateRecommender(IEdge newEdge)
  {
    throw new UnsupportedOperationException();
    // TODO
//    if (newEdge.getProperty(getConfig().getEdgeRankValueName()) == null)
//      return;
//    INode dest = newEdge.getDestination();
//    HashMap<String, INode> commonNodes = dest.getCommonNodes(rankEdgeType, getConfig().getItemIdentifierName());
//    for (INode item : commonNodes.values())
//    {
//      String itemId = item.getProperty(getConfig().getItemIdentifierName());
//      if (itemId == null)
//        throw new RuntimeException("Items don't have the 'id' property!");
//      HashMap<String, Rating> knnRow = getKnnRow(itemId);
//      findNearestNeighbour(item, rankEdgeType, knnRow, true);
//    }
  }

  private IUserRecommender getRecommender()
  {
    if (recommender == null)
      recommender = new ItemFullScanUserRecommender(getConfig().getRecoNumber(), getPredictor(), rankEdgeType, userItemDataset.getItemList().values());

    return recommender;
  }

  private IPredictor getPredictor()
  {
    if (predictor == null)
    {
      if (userItemDataset == null || model == null)
        throw new IllegalStateException("Cannot build predictor: userItemDataset == null || model == null");
      
      predictor = new KNNPredictor1(userItemDataset, model, getConfig().getItemIdentifierName());
    }

    return predictor;
  }
}
