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
package org.reco4j.recommender.svd;

import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import java.util.List;
import org.reco4j.graph.*;
import org.reco4j.model.IModel;
import org.reco4j.recommender.BasicRecommender;
import org.reco4j.recommender.IPredictor;
import org.reco4j.recommender.IUserRecommender;
import org.reco4j.recommender.ItemFullScanUserRecommender;
import org.reco4j.util.TimeReportUtility;

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
  private MFPredictor1 predictor;

  public MFRecommender2(IMFRecommenderConfig config)
  {
    super(config);
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    UserItemDataset userItemDS = new UserItemDataset();
    userItemDS.init(learningDataSet, getConfig().getItemType(), getConfig().getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, getConfig().getGraphConfig()), getConfig().getEdgeRankValueName());
    buildRecommender(learningDataSet, userItemDS);
  }
  
  @Override
  public void buildRecommender(IGraph learningDataSet, UserItemDataset dataset)
  {
    userItemDataset = dataset;
    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
    timeReport.start();
    MFModelBuilder builder = new MFModelBuilder(userItemDataset, getConfig().getMaxFeatures(), getConfig().getFeatureInitValue());
    model = builder.build();
    timeReport.stop();
    timeReport.printStatistics();
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
      if (model == null)
        throw new IllegalStateException("Cannot build predictor: model == null");
      
      predictor = new MFPredictor1(model);
    }

    return predictor;
  }

  @Override
  public IModel getModel()
  {
    return model;
  }
}
