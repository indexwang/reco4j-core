/*
 * RecommenderPropertiesHandle.java
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
package org.reco4j.util;

import java.util.Properties;
import org.reco4j.graph.IGraphConfig;
import org.reco4j.recommender.knn.ICollaborativeFilteringRecommenderConfig;
import org.reco4j.recommender.svd.IMFRecommenderConfig;
import org.reco4j.recommender.mahout.IMahoutRecommenderConfig;
import org.reco4j.similarity.ICosineSimilarityConfig;
import org.reco4j.similarity.IEuclideanSimilarityConfig;
import org.reco4j.similarity.ISimilarityConfig;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class RecommenderPropertiesHandle
  implements
  IPropertiesHandle,
  IRecommenderConfig, ICollaborativeFilteringRecommenderConfig, IMFRecommenderConfig, IMahoutRecommenderConfig,
  ISimilarityConfig, ICosineSimilarityConfig, IEuclideanSimilarityConfig,
  IGraphConfig
{
  protected Properties properties;
  //Properties name on the properties file
  protected final static String PROTERTY_NAME_K_VALUE = "KValue";
  protected final static String PROTERTY_NAME_RECOMMENDED_ITEMS = "RecoNumber";
  protected final static String PROPERTY_NAME_NODE_IDENTIFIER = "NodeIdentifier";
  protected final static String PROPERTY_NAME_NODE_USER_IDENTIFIER = "userIdentifier";
  protected final static String PROPERTY_NAME_NODE_ITEM_IDENTIFIER = "itemIdentifier";
  protected final static String PROPERTY_NAME_RANK_VALUE = "RankValueIdentifier";
  protected final static String PROPERTY_NAME_DISTANCE_ALGORITHM = "DistanceAlgorithm";
  protected final static String PROPERTY_NAME_EDGE_RANK = "rankEdgeIdentifier";
  protected final static String PROPERTY_NAME_EDGE_TEST_RANK = "testRankEdgeIdentifier";
  protected final static String PROPERTY_NAME_EDGE_SIMILARITY = "similarityEdgeIdentifier";
  protected final static String PROPERTY_NAME_EDGE_ESTIMATED_RATING = "estimatedRatingIdentifier";
  protected final static String PROPERTY_NAME_RECALCULATE_SIMILARITY = "recalculateSimilarity";
  protected final static String PROPERTY_NAME_RECOMMENDER_TYPE = "recommenderType";
  protected final static String PROPERTY_NAME_MAXFEATURES = "maxFeatures";
  protected final static String PROPERTY_NAME_FEATURE_INIT_VALUE = "featureInitValue";
  protected final static String PROPERTY_NAME_USER_TYPE = "userType";
  protected final static String PROPERTY_NAME_ITEM_TYPE = "itemType";
  protected static final String PROPERTY_NAME_MAX_PREFERENCE_VALUE = "maxPreference";
  protected static final String PROPERTY_NAME_MIN_PREFERENCE_VALUE = "minPreference";
  protected static final String PROPERTY_NAME_NODE_TYPE = "nodeType";
  //Default value for properties name on node or edges
  protected final static String PROPERTY_NODE_IDENTIFIER = "id"; //Prendere anche da properties file
  protected final static String PROPERTY_ITEM_IDENTIFIER = "itemId"; //Prendere anche da properties file
  protected final static String PROPERTY_USER_IDENTIFIER = "userId"; //Prendere anche da properties file
  protected final static String PROPERTY_RANK_VALUE_NAME = "RankValue"; //Prendere anche da properties file
  protected final static String PROPERTY_EDGE_RANK_IDENTIFIER = "rated";
  protected final static String PROPERTY_EDGE_TEST_RANK_IDENTIFIER = "ratedTest";
  protected final static String PROPERTY_EDGE_SIMILARITY_IDENTIFIER = "similarity";
  protected final static String PROPERTY_EDGE_ESTIMATED_RATING_IDENTIFIER = "estimatedRating";
  protected final static String PROPERTY_USER_TYPE = "User";
  protected final static String PROPERTY_ITEM_TYPE = "Movie";
  protected final static String PROPERTY_NODE_TYPE = "type";
  protected final static boolean PROPERTY_RECALCULATE_SIMILARITY = false;
  protected final static int PROPERTY_RECOMMENDER_TYPE = 1;
  protected final static int PROPERTY_MAXFEATURES = 20;
  protected final static double PROPERTY_FEATURE_INIT_VALUE = 0.1;
  protected final static double PROPERTY_MAX_PREFERENCE_VALUE = 5.0;
  protected final static double PROPERTY_MIN_PREFERENCE_VALUE = 1.0;
  //Default Value for Recommender
  protected static int PROPERTY_K_VALUE = 25;
  protected static int PROPERTY_RECO_NUMBER = 10;
  private static RecommenderPropertiesHandle theInstance = new RecommenderPropertiesHandle();

  protected RecommenderPropertiesHandle()
  {
  }

  public static RecommenderPropertiesHandle getInstance()
  {
    return theInstance;
  }

  @Override
  public void setProperties(Properties properties)
  {
    this.properties = properties;
  }

  @Override
  public int getKValue()
  {
    int k_value = Integer.parseInt(getProperty(PROTERTY_NAME_K_VALUE, "-1"));

    if (k_value > 0)
      return k_value;
    else
      return PROPERTY_K_VALUE; //Default value
  }

  @Override
  public int getRecoNumber()
  {
    int reco_number_v = Integer.parseInt(getProperty(PROTERTY_NAME_RECOMMENDED_ITEMS, "-1"));

    if (reco_number_v > 0)
      return reco_number_v;
    else
      return PROPERTY_RECO_NUMBER; //Default value
  }

  public String getNodeIdentifierName()
  {
    return PROPERTY_NODE_IDENTIFIER; //Default value
  }

  @Override
  public String getItemIdentifierName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_NODE_ITEM_IDENTIFIER, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_ITEM_IDENTIFIER;
  }

  @Override
  public String getUserIdentifierName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_NODE_USER_IDENTIFIER, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_USER_IDENTIFIER;
  }

  @Override
  public String getEdgeRankValueName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_RANK_VALUE, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_RANK_VALUE_NAME; //Default value
  }

  @Override
  public String getItemType()
  {
    String rankValueName = getProperty(PROPERTY_NAME_ITEM_TYPE, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_ITEM_TYPE; //Default value
  }

  @Override
  public String getUserType()
  {
    String rankValueName = getProperty(PROPERTY_NAME_USER_TYPE, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_USER_TYPE; //Default value
  }

  @Override
  public String getEdgeRankName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_RANK, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_RANK_IDENTIFIER; //Default value
  }

  @Override
  public String getEdgeTestRankName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_TEST_RANK, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_TEST_RANK_IDENTIFIER; //Default value
  }

  @Override
  public String getEdgeEstimatedRatingName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_ESTIMATED_RATING, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_ESTIMATED_RATING_IDENTIFIER; //Default value
  }

  public String getProperty(String name, String defaultValue)
  {
    if (properties != null)
    {
      return properties.getProperty(name, defaultValue);
    }
    else
    {
      throw new RuntimeException("Recommender properties is not set!");
    }
  }

  @Override
  public int getSimilarityType()
  {
    int distanceAlg = Integer.parseInt(getProperty(PROPERTY_NAME_DISTANCE_ALGORITHM, "-1"));

    if (distanceAlg > 0)
      return distanceAlg;
    else
      return ISimilarityConfig.SIMILARITY_TYPE_EUCLIDEAN; //Default value
  }

  @Override
  public ISimilarityConfig getSimilarityConfig()
  {
    return this;
  }

  @Override
  public IGraphConfig getGraphConfig()
  {
    return this;
  }

  @Override
  public String getEdgeSimilarityName()
  {
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_SIMILARITY, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_SIMILARITY_IDENTIFIER; //Default value
  }

  @Override
  public boolean getRecalculateSimilarity()
  {
    String rankValueName = getProperty(PROPERTY_NAME_RECALCULATE_SIMILARITY, null);
    if (rankValueName != null)
      return Boolean.valueOf(rankValueName).booleanValue();
    else
      return PROPERTY_RECALCULATE_SIMILARITY; //Default value
  }

  @Override
  public int getRecommenderType()
  {
    String recommenderTypeValue = getProperty(PROPERTY_NAME_RECOMMENDER_TYPE, null);
    if (recommenderTypeValue != null)
      return Integer.valueOf(recommenderTypeValue).intValue();
    else
      return PROPERTY_RECOMMENDER_TYPE; //Default value
  }

  @Override
  public int getMaxFeatures()
  {
    String maxFeatureValue = getProperty(PROPERTY_NAME_MAXFEATURES, null);
    if (maxFeatureValue != null)
      return Integer.valueOf(maxFeatureValue).intValue();
    else
      return PROPERTY_MAXFEATURES; //Default value
  }

  @Override
  public double getFeatureInitValue()
  {
    String featureInitValue = getProperty(PROPERTY_NAME_FEATURE_INIT_VALUE, null);
    if (featureInitValue != null)
      return Double.parseDouble(featureInitValue);
    else
      return PROPERTY_FEATURE_INIT_VALUE; //Default value
  }

  @Override
  public double getMaxPreferenceValue()
  {
    String maxPreferenceValue = getProperty(PROPERTY_NAME_MAX_PREFERENCE_VALUE, null);
    if (maxPreferenceValue != null)
      return Double.parseDouble(maxPreferenceValue);
    else
      return PROPERTY_MAX_PREFERENCE_VALUE; //Default value
  }

  @Override
  public double getMinPreferenceValue()
  {
    String minPreferenceValue = getProperty(PROPERTY_NAME_MIN_PREFERENCE_VALUE, null);
    if (minPreferenceValue != null)
      return Double.parseDouble(minPreferenceValue);
    else
      return PROPERTY_MIN_PREFERENCE_VALUE; //Default value
  }

  public String getNodeTypeName()
  {
    String nodeType = getProperty(PROPERTY_NAME_NODE_TYPE, null);
    if (nodeType != null)
      return nodeType;
    else
      return PROPERTY_NODE_TYPE; //Default value
  }
}
