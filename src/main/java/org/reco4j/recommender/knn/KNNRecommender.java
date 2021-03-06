/*
 * KNNRecommender.java
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

import java.util.List;
import java.util.logging.Logger;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import org.reco4j.model.IModel;
import org.reco4j.similarity.ISimilarity;
import org.reco4j.similarity.SimilarityFactory;
import org.reco4j.recommender.BasicRecommender;
import org.reco4j.recommender.IPredictor;
import org.reco4j.recommender.IUserRecommender;
import org.reco4j.recommender.ItemFullScanUserRecommender;
import org.reco4j.util.TimeReportUtility;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class KNNRecommender
        extends BasicRecommender<ICollaborativeFilteringRecommenderConfig>
{

  private static final Logger logger = Logger.getLogger(KNNRecommender.class.getName());
  protected ISimilarity similarityFunction;
  protected UserItemDataset userItemDataset;
  private KNNModel model;
  protected IUserRecommender recommender;
  private KNNPredictor predictor;
  private KNNModelBuilder knnModelBuilder;
  

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
    UserItemDataset userItemDS = new UserItemDataset();
    userItemDS.init(learningDataSet, getConfig().getItemType(), getConfig().getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, getConfig().getGraphConfig()), getConfig().getEdgeRankValueName());
    buildRecommender(learningDataSet, userItemDS);
  }
  
  @Override
  public void buildRecommender(IGraph learningDataSet, UserItemDataset userItemDataset)
  {
    this.userItemDataset = userItemDataset;
    knnModelBuilder = new KNNModelBuilder(userItemDataset, getConfig().getItemIdentifierName(), rankEdgeType, similarityFunction, EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig()), getConfig().getRecalculateSimilarity());
    if (modelName != null && !modelName.isEmpty())
      knnModelBuilder.setModelName(modelName); 
    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
    timeReport.start();
    model = knnModelBuilder.build();
    timeReport.stop();
    timeReport.printStatistics();
  }

  @Override
  public void loadRecommender(IGraph modelGraph)
  {
    KNNModelLoader knnModelLoader = new KNNModelLoader(modelGraph, getConfig().getItemIdentifierName(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig()), modelName);
    model = knnModelLoader.build();
    knnModelBuilder = new KNNModelBuilder(userItemDataset, getConfig().getItemIdentifierName(), rankEdgeType, similarityFunction, EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig()), getConfig().getRecalculateSimilarity());
    if (modelName != null && !modelName.isEmpty())
      knnModelBuilder.setModelName(modelName);    
    knnModelBuilder.setModel(model);
    
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
  public void updateRecommender(IEdge newEdge, int operation)
  {
    knnModelBuilder.update(newEdge, operation);
  }

  private IUserRecommender getRecommender()
  {
    if (recommender == null)
      recommender = new ItemFullScanUserRecommender(getConfig().getRecoNumber(), getPredictor(), rankEdgeType, userItemDataset.getItemListForRecommendation().values());

    return recommender;
  }

  private IPredictor getPredictor()
  {
    if (predictor == null)
    {
      if (userItemDataset == null || model == null)
        throw new IllegalStateException("Cannot build predictor: userItemDataset == null || model == null");
      
      predictor = new KNNPredictor(userItemDataset, model, getConfig().getItemIdentifierName());
    }

    return predictor;
  }



  @Override
  public IModel getModel()
  {
    return model;
  }
}
