package org.reco4j.recommender.svd;

// /*
// * MFRecommender.java
// * 
// * Copyright (C) 2013 Alessandro Negro <alessandro.negro at reco4j.org>
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//package org.reco4j.graph.recommenders;
//
//import java.sql.Timestamp;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import org.reco4j.graph.*;
//import org.reco4j.util.TimeReportUtility;
//import org.reco4j.util.Utility;
//
///**
// *
// ** @author Alessandro Negro <alessandro.negro at reco4j.org>
// */
//public class MFRecommender
//  extends BasicRecommender<IMFRecommenderConfig>
//{
//  private static final int MIN_EPOCHS = 120; //120           // Minimum number of epochs per feature
//  private static final int MAX_EPOCHS = 200;           // Max epochs per feature
//  private static final double K = 0.015;         // Regularization parameter used to minimize over-fitting
//  private static final double LRATE = 0.001;         // Learning rate parameter
//  private static final double MIN_IMPROVEMENT = 0.0001;        // Minimum improvement required to continue current feature
//  private ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>> itemFeatures;
//  private ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>> userFeatures;
//  private ConcurrentHashMap<Long, INode> itemList;
//  private ConcurrentHashMap<Long, INode> userList;
//  private List<IEdge> ratingList;
//  private int maxFeatures;
//  private int ratingCount;
//
//  public MFRecommender(IMFRecommenderConfig config)
//  {
//    super(config);
//  }
//
//  @Override
//  public void buildRecommender(IGraph learningDataSet)
//  {
//    this.learningDataSet = learningDataSet;
//    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
//    timeReport.start();
//    init();
//    calcMetrics();
//    calcFeatures();
//    //calcFeaturesByUser();
//    timeReport.stop();
//    timeReport.printStatistics();
//  }
//
//  @Override
//  public List<Rating> recommend(INode user)
//  {
//    ArrayList<Rating> recommendations = new ArrayList<Rating>();
//
//    for (INode item : learningDataSet.getNodesByType(getConfig().getItemType()))
//    {
//      if (item.isConnected(user, rankEdgeType))
//        continue;
//      double estimatedRating = estimateRating(user, item);
//      Utility.orderedInsert(recommendations, estimatedRating, item, getConfig().getRecoNumber());
//    }
//    return recommendations;
//  }
//
//  @Override
//  public double estimateRating(INode user, INode item)
//  {
//    double sum = 1;
//    for (int f = 0; f < maxFeatures; f++)
//    {
//      sum += itemFeatures.get(f).get(item.getId())
//             * userFeatures.get(f).get(user.getId());
//      if (sum > 5)
//        sum = 5.0;
//      if (sum < 1)
//        sum = 1.0;
//    }
//    return sum;
//  }
//
//  private void init()
//  {
//    itemFeatures = new ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>>();
//    userFeatures = new ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>>();
//    itemList = learningDataSet.getNodesMapByType(
//      getConfig().getItemType());
//    userList = learningDataSet.getNodesMapByType(
//      getConfig().getUserType());
//    ratingList = learningDataSet.getEdgesByType(EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, getConfig().getGraphConfig()));
//    ratingCount = ratingList.size();
//
//    maxFeatures = getConfig().getMaxFeatures();
//    double featureInitValue = getConfig().getFeatureInitValue();
//
//    for (int i = 0; i < maxFeatures; i++)
//    {
//      ConcurrentHashMap<Long, Double> qi = new ConcurrentHashMap<Long, Double>();
//      for (INode item : itemList.values())
//      {
//        qi.put(item.getId(), featureInitValue);
//        if (item.getExtendedInfos() == null)
//        {
//          ExtendedNodeInfos info = new ExtendedNodeInfos();
//          info.setRatingCount(0);
//          info.setRatingSum(0.0);
//          info.setRatingAvg(0.0);
//          info.setPseudoAvg(0.0);
//          item.setExtendedInfos(info);
//        }
//      }
//      itemFeatures.put(i, qi);
//      ConcurrentHashMap<Long, Double> pu = new ConcurrentHashMap<Long, Double>();
//      for (INode user : userList.values())
//      {
//        pu.put(user.getId(), featureInitValue);
//        if (user.getExtendedInfos() == null)
//        {
//          ExtendedNodeInfos info = new ExtendedNodeInfos();
//          info.setRatingCount(0);
//          info.setRatingSum(0.0);
//          info.setRatingAvg(0.0);
//          info.setPseudoAvg(0.0);
//          user.setExtendedInfos(info);
//        }
//      }
//      userFeatures.put(i, pu);
//    }
//    System.out.println("itemList: \t" + itemList.size());
//    System.out.println("userList: \t" + userList.size());
//    System.out.println("ratingList: \t" + ratingList.size());
//
//  }
//
//  private void calcMetrics()
//  {
//    for (IEdge rating : ratingList)
//    {
//      double realValue = Double.parseDouble(rating.getProperty(getConfig().getEdgeRankValueName()));
//
//      ExtendedEdgeInfos exEdgeInfos = new ExtendedEdgeInfos();
//      rating.setExtendedInfos(exEdgeInfos);
//
//      INode item = itemList.get(rating.getDestination().getId());
//
//      ExtendedNodeInfos itemExtInfos = (ExtendedNodeInfos) item.getExtendedInfos();
//      itemExtInfos.setRatingCount(itemExtInfos.getRatingCount() + 1);
//      itemExtInfos.setRatingSum(itemExtInfos.getRatingSum() + realValue);
//
//      INode user = userList.get(rating.getSource().getId());
//
//      ExtendedNodeInfos userExtInfos = (ExtendedNodeInfos) user.getExtendedInfos();
//      userExtInfos.setRatingCount(userExtInfos.getRatingCount() + 1);
//      userExtInfos.setRatingSum(userExtInfos.getRatingSum() + realValue);
//    }
//    for (INode item : itemList.values())
//    {
//      ExtendedNodeInfos itemExtInfos = (ExtendedNodeInfos) item.getExtendedInfos();
//      if (itemExtInfos.getRatingCount() > 0)
//      {
//        itemExtInfos.setRatingAvg(itemExtInfos.getRatingSum() / itemExtInfos.getRatingCount());
//        itemExtInfos.setPseudoAvg(itemExtInfos.getRatingSum() + ((3.23 * 25) / (itemExtInfos.getRatingCount() + 25)));
//      }
//    }
//  }
//
//  private void calcFeatures()
//  {
//    int cnt = 0;
//
//    double rmse_last = 2.0;
//    double rmse = 2.0;
//    System.out.println("maxFeatures: " + maxFeatures);
//    for (int f = 0; f < maxFeatures; f++)
//    {
//      //System.out.println("Calculating feature: " + f + " start: " + new Timestamp(System.currentTimeMillis()));
//      for (int e = 0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
//      {
//        //System.out.println(" e: " + e + " RMSE: " + rmse + " RMSE_LAST: " + rmse_last + " " + new Timestamp(System.currentTimeMillis()));
//        cnt++;
//        double sq = 0;
//        rmse_last = rmse;
//
//        ConcurrentHashMap<Long, Double> itemFeature = itemFeatures.get(f);
//        ConcurrentHashMap<Long, Double> userFeature = userFeatures.get(f);
//        for (IEdge rating : ratingList)
//        {
//          INode item = rating.getDestination();
//          INode user = rating.getSource();
//
//          // Predict rating and calc error
//          double p = predictRating(item, user, f, rating, true);
//          double ratingValue = Double.parseDouble(rating.getProperty(getConfig().getEdgeRankValueName()));
//
//
//          double err;
//          err = ratingValue - p;
//          sq += err * err;
//          //System.out.println("P: " + p.doubleValue() + " R: " + ratingValue.doubleValue() + " err: " + err + " sq: " + sq);
//
//          // Cache off old feature values
//          double mf = itemFeature.get(item.getId()).doubleValue();
//          double cf = userFeature.get(user.getId()).doubleValue();
//
//
//
//          double newCf = cf + (LRATE * (err * mf - K * cf)); //0.001 * ((0.099) - 0.0015))
//          userFeature.put(user.getId(), newCf);
//          double newMf = mf + (LRATE * (err * cf - K * mf));
//          itemFeature.put(item.getId(), newMf);
//        }
//        rmse = Math.sqrt(sq / (double) ratingList.size());
//      }
//      //System.out.println("RMSE: " + rmse);
//      for (IEdge rating : ratingList)
//        ((ExtendedEdgeInfos) rating.getExtendedInfos()).setCache(predictRating(rating.getDestination(), rating.getSource(), f, rating, false));
//    }
//  }
//
//  private void calcFeaturesByUser()
//  {
//    int cnt = 0;
//
//    double rmse_last = 2.0;
//    double rmse = 2.0;
//    System.out.println("maxFeatures: " + maxFeatures);
//    for (INode user : userList.values())
//    {
//      System.out.println("User: " + user.getProperty(getConfig().getUserIdentifierName()));
//      for (int f = 0; f < maxFeatures; f++)
//      {
//        System.out.println("Calculating feature: " + f + " start: " + new Timestamp(System.currentTimeMillis()));
//        for (int e = 0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
//        {
//          //System.out.println(" e: " + e + " RMSE: " + rmse + " RMSE_LAST: " + rmse_last + " " + new Timestamp(System.currentTimeMillis()));
//          cnt++;
//          double sq = 0;
//          rmse_last = rmse;
//
//          ConcurrentHashMap<Long, Double> itemFeature = itemFeatures.get(f);
//          ConcurrentHashMap<Long, Double> userFeature = userFeatures.get(f);
//          for (IEdge rating : ratingList)
//          {
//            INode item = rating.getDestination();
//            if (rating.getSource().getId() != user.getId())
//              continue;
//
//
//            // Predict rating and calc error
//            double p = predictRating(item, user, f, rating, true);
//            double ratingValue = Double.parseDouble(rating.getProperty(getConfig().getEdgeRankValueName()));
//
//
//            double err;
//            err = ratingValue - p;
//            sq += err * err;
//            //System.out.println("P: " + p.doubleValue() + " R: " + ratingValue.doubleValue() + " err: " + err + " sq: " + sq);
//
//            // Cache off old feature values
//            double mf = itemFeature.get(item.getId()).doubleValue();
//            double cf = userFeature.get(user.getId()).doubleValue();
//
//
//
//            double newCf = cf + (LRATE * (err * mf - K * cf)); //0.001 * ((0.099) - 0.0015))
//            userFeature.put(user.getId(), newCf);
//            double newMf = mf + (LRATE * (err * cf - K * mf));
//            itemFeature.put(item.getId(), newMf);
//          }
//          rmse = Math.sqrt(sq / (double) ratingList.size());
//        }
//        System.out.println("RMSE: " + rmse);
//        for (IEdge rating : ratingList)
//          ((ExtendedEdgeInfos) rating.getExtendedInfos()).setCache(predictRating(rating.getDestination(), rating.getSource(), f, rating, false));
//      }
//    }
//  }
//
//  private double predictRating(INode movie, INode user, int f, IEdge rating, boolean bTrailing)
//  {
//    double sum = ((ExtendedEdgeInfos) rating.getExtendedInfos()).getCache() > 0.0 ? ((ExtendedEdgeInfos) rating.getExtendedInfos()).getCache() : 1;
//    sum = sum + (itemFeatures.get(f).get(movie.getId())
//                 * (userFeatures.get(f).get(user.getId())));
//    if (sum > 5)
//      sum = 5;
//    if (sum < 1)
//      sum = 1.0;
//    return sum;
//  }
//}
//
//class ExtendedNodeInfos
//{
//  int RatingCount;
//  double RatingSum;
//  double RatingAvg;
//  double PseudoAvg;
//
//  public int getRatingCount()
//  {
//    return RatingCount;
//  }
//
//  public void setRatingCount(int RatingCount)
//  {
//    this.RatingCount = RatingCount;
//  }
//
//  public double getRatingSum()
//  {
//    return RatingSum;
//  }
//
//  public void setRatingSum(double RatingSum)
//  {
//    this.RatingSum = RatingSum;
//  }
//
//  public double getRatingAvg()
//  {
//    return RatingAvg;
//  }
//
//  public void setRatingAvg(double RatingAvg)
//  {
//    this.RatingAvg = RatingAvg;
//  }
//
//  public double getPseudoAvg()
//  {
//    return PseudoAvg;
//  }
//
//  public void setPseudoAvg(double PseudoAvg)
//  {
//    this.PseudoAvg = PseudoAvg;
//  }
//}
//
//class ExtendedEdgeInfos
//{
//  double cache;
//
//  public ExtendedEdgeInfos()
//  {
//    cache = 0.0;
//  }
//
//  public synchronized double getCache()
//  {
//    return cache;
//  }
//
//  public synchronized void setCache(double cache)
//  {
//    this.cache = cache;
//  }
//}
//
///*
//
// //=============================================================================
// //
// // SVD Sample Code
// //
// // Copyright (C) 2007 Timely Development (www.timelydevelopment.com)
// //
// // Special thanks to Simon Funk and others from the Netflix Prize contest 
// // for providing pseudo-code and tuning hints.
// //
// // Feel free to use this code as you wish as long as you include 
// // these notices and attribution. 
// //
// // Also, if you have alternative types of algorithms for accomplishing 
// // the same goal and would like to contribute, please share them as well :)
// //
// // STANDARD DISCLAIMER:
// //
// // - THIS CODE AND INFORMATION IS PROVIDED "AS IS" WITHOUT WARRANTY
// // - OF ANY KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT
// // - LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR
// // - FITNESS FOR A PARTICULAR PURPOSE.
// //
// //=============================================================================
//
// #define WIN32_LEAN_AND_MEAN
// #include <windows.h>
// #include <stdio.h>
// #include <math.h>
// #include <tchar.h>
// #include <map>
// using namespace std;
//
// //===================================================================
// //
// // Constants and Type Declarations
// //
// //===================================================================
// #define TRAINING_PATH   L"C:\Netflix\training_set*.txt"
// #define TRAINING_FILE   L"C:\Netflix\training_set\%s"
// #define FEATURE_FILE    L"C:\Netflix\features.txt"
// #define TEST_PATH       L"C:\Netflix\%s"
// #define PREDICTION_FILE L"C:\Netflix\prediction.txt"
//
// #define MAX_RATINGS     100480508     // Ratings in entire training set (+1)
// #define MAX_CUSTOMERS   480190        // Customers in the entire training set (+1)
// #define MAX_MOVIES      17771         // Movies in the entire training set (+1)
// #define MAX_FEATURES    64            // Number of features to use 
// #define MIN_EPOCHS      120           // Minimum number of epochs per feature
// #define MAX_EPOCHS      200           // Max epochs per feature
//
// #define MIN_IMPROVEMENT 0.0001        // Minimum improvement required to continue current feature
// #define INIT            0.1           // Initialization value for features
// #define LRATE           0.001         // Learning rate parameter
// #define K               0.015         // Regularization parameter used to minimize over-fitting
//
// typedef unsigned char BYTE;
// typedef map<int, int> IdMap;
// typedef IdMap::iterator IdItr;
//
// struct Movie
// {
// int         RatingCount;
// int         RatingSum;
// double      RatingAvg;            
// double      PseudoAvg;            // Weighted average used to deal with small movie counts 
// };
//
// struct Customer
// {
// int         CustomerId;
// int         RatingCount;
// int         RatingSum;
// };
//
// struct Data
// {
// int         CustId;
// short       MovieId;
// BYTE        Rating;
// float       Cache;
// };
//
// class Engine 
// {
// private:
// int             m_nRatingCount;                                 // Current number of loaded ratings
// Data            m_aRatings[MAX_RATINGS];                        // Array of ratings data
// Movie           m_aMovies[MAX_MOVIES];                          // Array of movie metrics
// Customer        m_aCustomers[MAX_CUSTOMERS];                    // Array of customer metrics
// float           m_aMovieFeatures[MAX_FEATURES][MAX_MOVIES];     // Array of features by movie (using floats to save space)
// float           m_aCustFeatures[MAX_FEATURES][MAX_CUSTOMERS];   // Array of features by customer (using floats to save space)
// IdMap           m_mCustIds;                                     // Map for one time translation of ids to compact array index
//
// inline double   PredictRating(short movieId, int custId, int feature, float cache, bool bTrailing=true);
// inline double   PredictRating(short movieId, int custId);
//
// bool            ReadNumber(wchar_t* pwzBufferIn, int nLength, int &nPosition, wchar_t* pwzBufferOut);
// bool            ParseInt(wchar_t* pwzBuffer, int nLength, int &nPosition, int& nValue);
// bool            ParseFloat(wchar_t* pwzBuffer, int nLength, int &nPosition, float& fValue);
//
// public:
// Engine(void);
// ~Engine(void) { };
//
// void            CalcMetrics();
// void            CalcFeatures();
// void            LoadHistory();
// void            ProcessTest(wchar_t* pwzFile);
// void            ProcessFile(wchar_t* pwzFile);
// };
//
//
// //===================================================================
// //
// // Program Main
// //
// //===================================================================
// int _tmain(int argc, _TCHAR* argv[])
// {
// Engine* engine = new Engine();
//
// engine->LoadHistory();
// engine->CalcMetrics();
// engine->CalcFeatures();
// engine->ProcessTest(L"qualifying.txt");
//
// wprintf(L"\nDone\n");
// getchar();
//    
// return 0;
// }
//
//
// //===================================================================
// //
// // Engine Class 
// //
// //===================================================================
//
// //-------------------------------------------------------------------
// // Initialization
// //-------------------------------------------------------------------
//
// Engine::Engine(void)
// {
// m_nRatingCount = 0;
//
// for (int f=0; f<MAX_FEATURES; f++)
// {
// for (int i=0; i<MAX_MOVIES; i++) m_aMovieFeatures[f][i] = (float)INIT;
// for (int i=0; i<MAX_CUSTOMERS; i++) m_aCustFeatures[f][i] = (float)INIT;
// }
// }
//
// //-------------------------------------------------------------------
// // Calculations - This Paragraph contains all of the relevant code
// //-------------------------------------------------------------------
//
// //
// // CalcMetrics
// // - Loop through the history and pre-calculate metrics used in the training 
// // - Also re-number the customer id's to fit in a fixed array
// //
// void Engine::CalcMetrics()
// {
// int i, cid;
// IdItr itr;
//
// wprintf(L"\nCalculating intermediate metrics\n");
//
// // Process each row in the training set
// for (i=0; i<m_nRatingCount; i++)
// {
// Data* rating = m_aRatings + i;
//
// // Increment movie stats
// m_aMovies[rating->MovieId].RatingCount++;
// m_aMovies[rating->MovieId].RatingSum += rating->Rating;
//        
// // Add customers (using a map to re-number id's to array indexes) 
// itr = m_mCustIds.find(rating->CustId); 
// if (itr == m_mCustIds.end())
// {
// cid = 1 + (int)m_mCustIds.size();
//
// // Reserve new id and add lookup
// m_mCustIds[rating->CustId] = cid;
//
// // Store off old sparse id for later
// m_aCustomers[cid].CustomerId = rating->CustId;
//
// // Init vars to zero
// m_aCustomers[cid].RatingCount = 0;
// m_aCustomers[cid].RatingSum = 0;
// }
// else
// {
// cid = itr->second;
// }
//
// // Swap sparse id for compact one
// rating->CustId = cid;
//
// m_aCustomers[cid].RatingCount++;
// m_aCustomers[cid].RatingSum += rating->Rating;
// }
//
// // Do a follow-up loop to calc movie averages
// for (i=0; i<MAX_MOVIES; i++)
// {
// Movie* movie = m_aMovies+i;
// movie->RatingAvg = movie->RatingSum / (1.0 * movie->RatingCount);
// movie->PseudoAvg = (3.23 * 25 + movie->RatingSum) / (25.0 + movie->RatingCount);
// }
// }
//
// //
// // CalcFeatures
// // - Iteratively train each feature on the entire data set
// // - Once sufficient progress has been made, move on
// //
// void Engine::CalcFeatures()
// {
// int f, e, i, custId, cnt = 0;
// Data* rating;
// double err, p, sq, rmse_last, rmse = 2.0;
// short movieId;
// float cf, mf;
//
// for (f=0; f<MAX_FEATURES; f++)
// {
// wprintf(L"\n--- Calculating feature: %d ---\n", f);
//
// // Keep looping until you have passed a minimum number 
// // of epochs or have stopped making significant progress 
// for (e=0; (e < MIN_EPOCHS) || (rmse <= rmse_last - MIN_IMPROVEMENT); e++)
// {
// cnt++;
// sq = 0;
// rmse_last = rmse;
//
// for (i=0; i<m_nRatingCount; i++)
// {
// rating = m_aRatings + i;
// movieId = rating->MovieId;
// custId = rating->CustId;
//
// // Predict rating and calc error
// p = PredictRating(movieId, custId, f, rating->Cache, true);
// err = (1.0 * rating->Rating - p);
// sq += err*err;
//                
// // Cache off old feature values
// cf = m_aCustFeatures[f][custId];
// mf = m_aMovieFeatures[f][movieId];
//
// // Cross-train the features
// m_aCustFeatures[f][custId] += (float)(LRATE * (err * mf - K * cf));
// m_aMovieFeatures[f][movieId] += (float)(LRATE * (err * cf - K * mf));
// }
//            
// rmse = sqrt(sq/m_nRatingCount);
//                  
// wprintf(L"     <set x='%d' y='%f' />\n",cnt,rmse);
// }
//
// // Cache off old predictions
// for (i=0; i<m_nRatingCount; i++)
// {
// rating = m_aRatings + i;
// rating->Cache = (float)PredictRating(rating->MovieId, rating->CustId, f, rating->Cache, false);
// }            
// }
// }
//
// //
// // PredictRating
// // - During training there is no need to loop through all of the features
// // - Use a cache for the leading features and do a quick calculation for the trailing
// // - The trailing can be optionally removed when calculating a new cache value
// //
// double Engine::PredictRating(short movieId, int custId, int feature, float cache, bool bTrailing)
// {
// // Get cached value for old features or default to an average
// double sum = (cache > 0) ? cache : 1; //m_aMovies[movieId].PseudoAvg; 
//
// // Add contribution of current feature
// sum += m_aMovieFeatures[feature][movieId] * m_aCustFeatures[feature][custId];
// if (sum > 5) sum = 5;
// if (sum < 1) sum = 1;
//
// // Add up trailing defaults values
// if (bTrailing)
// {
// sum += (MAX_FEATURES-feature-1) * (INIT * INIT);
// if (sum > 5) sum = 5;
// if (sum < 1) sum = 1;
// }
//
// return sum;
// }
//
// //
// // PredictRating
// // - This version is used for calculating the final results
// // - It loops through the entire list of finished features
// //
// double Engine::PredictRating(short movieId, int custId)
// {
// double sum = 1; //m_aMovies[movieId].PseudoAvg;
//
// for (int f=0; f<MAX_FEATURES; f++) 
// {
// sum += m_aMovieFeatures[f][movieId] * m_aCustFeatures[f][custId];
// if (sum > 5) sum = 5;
// if (sum < 1) sum = 1;
// }
//
// return sum;
// }
//
// //-------------------------------------------------------------------
// // Data Loading / Saving
// //-------------------------------------------------------------------
//
// //
// // LoadHistory
// // - Loop through all of the files in the training directory
// //
// void Engine::LoadHistory()
// {
// WIN32_FIND_DATA FindFileData;
// HANDLE hFind;
// bool bContinue = true;
// int count = 0; // TEST
//
// // Loop through all of the files in the training directory
// hFind = FindFirstFile(TRAINING_PATH, &FindFileData);
// if (hFind == INVALID_HANDLE_VALUE) return;
//    
// while (bContinue) 
// {
// this->ProcessFile(FindFileData.cFileName);
// bContinue = (FindNextFile(hFind, &FindFileData) != 0);
//
// //if (++count > 999) break; // TEST: Uncomment to only test with the first X movies
// } 
//
// FindClose(hFind);
// }
//
// //
// // ProcessFile
// // - Load a history file in the format:
// //
// //   <MovieId>:
// //   <CustomerId>,<Rating>
// //   <CustomerId>,<Rating>
// //   ...
// void Engine::ProcessFile(wchar_t* pwzFile)
// {
// FILE *stream;
// wchar_t pwzBuffer[1000];
// wsprintf(pwzBuffer,TRAINING_FILE,pwzFile);
// int custId, movieId, rating, pos = 0;
//    
// wprintf(L"Processing file: %s\n", pwzBuffer);
//
// if (_wfopen_s(&stream, pwzBuffer, L"r") != 0) return;
//
// // First line is the movie id
// fgetws(pwzBuffer, 1000, stream);
// ParseInt(pwzBuffer, (int)wcslen(pwzBuffer), pos, movieId);
// m_aMovies[movieId].RatingCount = 0;
// m_aMovies[movieId].RatingSum = 0;
//
// // Get all remaining rows
// fgetws(pwzBuffer, 1000, stream);
// while ( !feof( stream ) )
// {
// pos = 0;
// ParseInt(pwzBuffer, (int)wcslen(pwzBuffer), pos, custId);
// ParseInt(pwzBuffer, (int)wcslen(pwzBuffer), pos, rating);
//        
// m_aRatings[m_nRatingCount].MovieId = (short)movieId;
// m_aRatings[m_nRatingCount].CustId = custId;
// m_aRatings[m_nRatingCount].Rating = (BYTE)rating;
// m_aRatings[m_nRatingCount].Cache = 0;
// m_nRatingCount++;
//
// fgetws(pwzBuffer, 1000, stream);
// }
//
// // Cleanup
// fclose( stream );
// }
//
// //
// // ProcessTest
// // - Load a sample set in the following format
// //
// //   <Movie1Id>:
// //   <CustomerId>
// //   <CustomerId>
// //   ...
// //   <Movie2Id>:
// //   <CustomerId>
// //
// // - And write results:
// //
// //   <Movie1Id>:
// //   <Rating>
// //   <Raing>
// //   ...
// void Engine::ProcessTest(wchar_t* pwzFile)
// {
// FILE *streamIn, *streamOut;
// wchar_t pwzBuffer[1000];
// int custId, movieId, pos = 0;
// double rating;
// bool bMovieRow;
//
// wsprintf(pwzBuffer, TEST_PATH, pwzFile);
// wprintf(L"\n\nProcessing test: %s\n", pwzBuffer);
//
// if (_wfopen_s(&streamIn, pwzBuffer, L"r") != 0) return;
// if (_wfopen_s(&streamOut, PREDICTION_FILE, L"w") != 0) return;
//
// fgetws(pwzBuffer, 1000, streamIn);
// while ( !feof( streamIn ) )
// {
// bMovieRow = false;
// for (int i=0; i<(int)wcslen(pwzBuffer); i++)
// {
// bMovieRow |= (pwzBuffer[i] == 58); 
// }
//
// pos = 0;
// if (bMovieRow)
// {
// ParseInt(pwzBuffer, (int)wcslen(pwzBuffer), pos, movieId);
//
// // Write same row to results
// fputws(pwzBuffer,streamOut); 
// }
// else
// {
// ParseInt(pwzBuffer, (int)wcslen(pwzBuffer), pos, custId);
// custId = m_mCustIds[custId];
// rating = PredictRating(movieId, custId);
//
// // Write predicted value
// swprintf(pwzBuffer,1000,L"%5.3f\n",rating);
// fputws(pwzBuffer,streamOut);
// }
//
// //wprintf(L"Got Line: %d %d %d \n", movieId, custId, rating);
// fgetws(pwzBuffer, 1000, streamIn);
// }
//
// // Cleanup
// fclose( streamIn );
// fclose( streamOut );
// }
//
// //-------------------------------------------------------------------
// // Helper Functions
// //-------------------------------------------------------------------
// bool Engine::ReadNumber(wchar_t* pwzBufferIn, int nLength, int &nPosition, wchar_t* pwzBufferOut)
// {
// int count = 0;
// int start = nPosition;
// wchar_t wc = 0;
//
// // Find start of number
// while (start < nLength)
// {
// wc = pwzBufferIn[start];
// if ((wc >= 48 && wc <= 57) || (wc == 45)) break;
// start++;
// }
//
// // Copy each character into the output buffer
// nPosition = start;
// while (nPosition < nLength && ((wc >= 48 && wc <= 57) || wc == 69  || wc == 101 || wc == 45 || wc == 46))
// {
// pwzBufferOut[count++] = wc;
// wc = pwzBufferIn[++nPosition];
// }
//
// // Null terminate and return
// pwzBufferOut[count] = 0;
// return (count > 0);
// }
//
// bool Engine::ParseFloat(wchar_t* pwzBuffer, int nLength, int &nPosition, float& fValue)
// {
// wchar_t pwzNumber[20];
// bool bResult = ReadNumber(pwzBuffer, nLength, nPosition, pwzNumber);
// fValue = (bResult) ? (float)_wtof(pwzNumber) : 0;
// return false;
// }
//
// bool Engine::ParseInt(wchar_t* pwzBuffer, int nLength, int &nPosition, int& nValue)
// {
// wchar_t pwzNumber[20];
// bool bResult = ReadNumber(pwzBuffer, nLength, nPosition, pwzNumber);
// nValue = (bResult) ? _wtoi(pwzNumber) : 0;
// return bResult;
// }
//    
//
//
// */
