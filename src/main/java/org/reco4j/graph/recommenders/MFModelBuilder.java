/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.concurrent.ConcurrentHashMap;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.INode;
import org.reco4j.graph.UserItemDataset;
import org.reco4j.util.TimeReportUtility;

/**
 *
 * @author giuri
 */
public class MFModelBuilder
        implements IModelBuilder<MFModel>
{

  private static final int MIN_EPOCHS = 120; //120           // Minimum number of epochs per feature
  private static final int MAX_EPOCHS = 200;           // Max epochs per feature
  private static final double K = 0.015;         // Regularization parameter used to minimize over-fitting
  private static final double LRATE = 0.001;         // Learning rate parameter
  private static final double MIN_IMPROVEMENT = 0.0001;        // Minimum improvement required to continue current feature
  //
  private UserItemDataset userItemDataset;
  //
  private int maxFeatures;
  private double featureInitValue;
  //
  private MFModel model;

  public MFModelBuilder(UserItemDataset userItemDataset, int maxFeatures, double featureInitValue)
  {
    this.userItemDataset = userItemDataset;
    this.maxFeatures = maxFeatures;
    this.featureInitValue = featureInitValue;
  }

  @Override
  public MFModel build()
  {
    init();
    calcMetrics();
    calcFeatures();
    //calcFeaturesByUser();
    return this.model;
  }

  private void init()
  {
    model = new MFModel();
    model.init(maxFeatures, featureInitValue, userItemDataset.getUserList(), userItemDataset.getItemList());
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
          double ratingValue = userItemDataset.getRating(rating);

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
