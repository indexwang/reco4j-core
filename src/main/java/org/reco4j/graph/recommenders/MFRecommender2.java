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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.reco4j.graph.*;
import org.reco4j.util.TimeReportUtility;
import org.reco4j.util.Utility;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class MFRecommender2
  extends BasicRecommender<IMFRecommenderConfig>
{

  private static final int MIN_EPOCHS = 120; //120           // Minimum number of epochs per feature
  private static final int MAX_EPOCHS = 200;           // Max epochs per feature
  private static final double K = 0.015;         // Regularization parameter used to minimize over-fitting
  private static final double LRATE = 0.001;         // Learning rate parameter
  private static final double MIN_IMPROVEMENT = 0.0001;        // Minimum improvement required to continue current feature
  //
  //
  private UserItemDataset userItemDataset;
  private MFModel model;
  private IPredictor predictor;
  private int maxFeatures;

  public MFRecommender2(IMFRecommenderConfig config)
  {
    super(config);
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    this.learningDataSet = learningDataSet;
    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
    timeReport.start();
    init();
    calcMetrics();
    calcFeatures();
    predictor = new MFPredictor1(model);
    //calcFeaturesByUser();
    timeReport.stop();
    timeReport.printStatistics();
  }

  @Override
  public void updateRecommender(IGraph learningDataSet)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Rating> recommend(INode user)
  {
    final int recoNumber = getConfig().getRecoNumber();

    ArrayList<Rating> recommendations = new ArrayList<Rating>();

    for (INode item : userItemDataset.getItemList().values()) // learningDataSet.getNodesByType(getConfig().getItemType()))
    {
      if (item.isConnected(user, rankEdgeType))
        continue;
      double estimatedRating = estimateRating(user, item);
      Utility.orderedInsert(recommendations, estimatedRating, item, recoNumber);
    }
    return recommendations;
  }

  @Override
  public double estimateRating(INode user, INode item)
  {
    return predictor.predictRating(user.getId(), item.getId());
  }

  private void init()
  {
    userItemDataset = new UserItemDataset();
    userItemDataset.init(learningDataSet, getConfig().getItemType(), getConfig().getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, getConfig().getGraphConfig()), getConfig().getEdgeRankValueName());

    // TODO init with constructor or setter
    maxFeatures = getConfig().getMaxFeatures();

    model = new MFModel();
    model.init(maxFeatures, getConfig().getFeatureInitValue(), userItemDataset.getUserList(), userItemDataset.getItemList());
  }

  private void calcMetrics()
  {
    userItemDataset.setNodeRatingStatistics();
  }

  private void calcFeatures()
  {
    initEdgePredictRatingCache();

    int cnt = 0;

    double rmse_last = 2.0;
    double rmse = 2.0;
    final int ratingListSize = userItemDataset.getRatingList().size();

    System.out.println("maxFeatures: " + maxFeatures);
    for (int feature = 0; feature < maxFeatures; feature++)
    {
      //System.out.println("Calculating feature: " + f + " start: " + new Timestamp(System.currentTimeMillis()));
      for (int e = 0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
      {
        //System.out.println(" e: " + e + " RMSE: " + rmse + " RMSE_LAST: " + rmse_last + " " + new Timestamp(System.currentTimeMillis()));
        cnt++;
        double sq = 0;
        rmse_last = rmse;

        ConcurrentHashMap<Long, Double> itemFeature = model.getItemFeatures().getFeatureItemMap(feature);
        ConcurrentHashMap<Long, Double> userFeature = model.getUserFeatures().getFeatureItemMap(feature);
        for (IEdge rating : userItemDataset.getRatingList())
        {
          // Predict rating and calc error
          double p = predictFeatureRating(feature, rating, true);
          double ratingValue = Double.parseDouble(rating.getProperty(getConfig().getEdgeRankValueName()));

          double err;
          err = ratingValue - p;
          sq += err * err;
          //System.out.println("P: " + p.doubleValue() + " R: " + ratingValue.doubleValue() + " err: " + err + " sq: " + sq);

          INode item = rating.getDestination();
          INode user = rating.getSource();

          // Cache off old feature values
          double mf = itemFeature.get(item.getId()).doubleValue();
          double cf = userFeature.get(user.getId()).doubleValue();



          double newCf = cf + (LRATE * (err * mf - K * cf)); //0.001 * ((0.099) - 0.0015))
          userFeature.put(user.getId(), newCf);
          double newMf = mf + (LRATE * (err * cf - K * mf));
          itemFeature.put(item.getId(), newMf);
        }
        rmse = Math.sqrt(sq / (double) ratingListSize);
      }
      //System.out.println("RMSE: " + rmse);
      for (IEdge rating : userItemDataset.getRatingList())
        ((EdgePredictRatingCache) rating.getExtendedInfos()).setCache(predictFeatureRating(feature, rating, false));
    }

//    cleanupEdgePredictRatingCache();
  }

  private double predictFeatureRating(int f, IEdge rating, boolean bTrailing)
  {
    INode item = rating.getDestination();
    INode user = rating.getSource();

    final double cache = ((EdgePredictRatingCache) rating.getExtendedInfos()).getCache();
    double sum = cache > 0.0 ? cache : 1;
    sum = sum + (model.getItemFeatures().getFeature(f, item.getId()) * model.getUserFeatures().getFeature(f, user.getId()));
    if (sum > 5)
      sum = 5;
    if (sum < 1)
      sum = 1.0;
    return sum;
  }

  private void initEdgePredictRatingCache()
  {
    for (IEdge rating : userItemDataset.getRatingList())
      rating.setExtendedInfos(new EdgePredictRatingCache());
  }

  private void cleanupEdgePredictRatingCache()
  {
    for (IEdge rating : userItemDataset.getRatingList())
      rating.setExtendedInfos(null);
  }

  private static class EdgePredictRatingCache
  {

    private double cache;

    public EdgePredictRatingCache()
    {
      cache = 0.0;
    }

    public synchronized double getCache()
    {
      return cache;
    }

    public synchronized void setCache(double cache)
    {
      this.cache = cache;
    }
  }
}
