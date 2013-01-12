/*
 * RecommenderPropertiesHandle.java
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
package org.reco4j.util;

import java.util.Properties;
import org.reco4j.graph.similarity.SimilarityFactory;

/**
 *
 * @author ale
 */
public class RecommenderPropertiesHandle implements IPropertiesHandle
{
  protected Properties properties;
  //Properties name on the properties file
  public final static String PROTERTY_NAME_K_VALUE = "KValue";
  public final static String PROTERTY_NAME_RECOMMENDED_ITEMS = "RecoNumber";
  public final static String PROPERTY_NAME_NODE_IDENTIFIER = "NodeIdentifier";
  public final static String PROPERTY_NAME_NODE_USER_IDENTIFIER = "userIdentifier";
  public final static String PROPERTY_NAME_NODE_ITEM_IDENTIFIER = "itemIdentifier";
  public final static String PROPERTY_NAME_RANK_VALUE = "RankValueIdentifier";
  public final static String PROPERTY_NAME_DISTANCE_ALGORITHM = "DistanceAlgorithm";
  public final static String PROPERTY_NAME_EDGE_RANK = "rankEdgeIdentifier";
  public final static String PROPERTY_NAME_EDGE_TEST_RANK = "testRankEdgeIdentifier";
  public final static String PROPERTY_NAME_EDGE_SIMILARITY = "similarityEdgeIdentifier";
  public final static String PROPERTY_NAME_EDGE_ESTIMATED_RATING = "estimatedRatingIdentifier";
  public final static String PROPERTY_NAME_RECALCULATE_SIMILARITY = "recalculateSimilarity";
  public final static String PROPERTY_NAME_RECOMMENDER_TYPE = "recommenderType";
  public final static String PROPERTY_NAME_MAXFEATURES = "maxFeatures";
  public final static String PROPERTY_NAME_FEATURE_INIT_VALUE = "featureInitValue";
  
  //Default value for properties name on node or edges
  public final static String PROPERTY_NODE_IDENTIFIER = "id"; //Prendere anche da properties file
  public final static String PROPERTY_ITEM_IDENTIFIER = "itemId"; //Prendere anche da properties file
  public final static String PROPERTY_USER_IDENTIFIER = "userId"; //Prendere anche da properties file
  public final static String PROPERTY_RANK_VALUE_NAME = "RankValue"; //Prendere anche da properties file
  public final static String PROPERTY_EDGE_RANK_IDENTIFIER = "rated";
  public final static String PROPERTY_EDGE_TEST_RANK_IDENTIFIER = "ratedTest";
  public final static String PROPERTY_EDGE_SIMILARITY_IDENTIFIER = "similarity";
  public final static String PROPERTY_EDGE_ESTIMATED_RATING_IDENTIFIER = "estimatedRating";
  public final static boolean PROPERTY_RECALCULATE_SIMILARITY = false;
  public final static int PROPERTY_RECOMMENDER_TYPE = 1;
  public final static int PROPERTY_MAXFEATURES = 64;
  public final static double PROPERTY_FEATURE_INIT_VALUE = 0.1;
  
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

  public int getKValue()
  {
    int k_value = Integer.parseInt(getProperty(PROTERTY_NAME_K_VALUE, "-1"));

    if (k_value > 0)
      return k_value;
    else
      return PROPERTY_K_VALUE; //Default value
  }

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
    //Consentire il loading da properties file
    return PROPERTY_NODE_IDENTIFIER; //Default value
  }
  
  public String getItemIdentifierName()
  {
    //Consentire il loading da properties file
    //Consentire il loading da properties file
    String rankValueName = getProperty(PROPERTY_NAME_NODE_ITEM_IDENTIFIER, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_ITEM_IDENTIFIER;
  }
  
  public String getUserIdentifierName()
  {
    //Consentire il loading da properties file
    //Consentire il loading da properties file
    String rankValueName = getProperty(PROPERTY_NAME_NODE_USER_IDENTIFIER, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_USER_IDENTIFIER;
  }

  public String getEdgeRankValueName()
  {
    //Consentire il loading da properties file
    String rankValueName = getProperty(PROPERTY_NAME_RANK_VALUE, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_RANK_VALUE_NAME; //Default value
  }
  
  public String getItemType()
  {
    return new String("Movie");
  }
  
  public String getUserType()
  {
    return new String("User");
  }
  public String getEdgeRankName()
  {
    //Consentire il loading da properties file
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_RANK, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_RANK_IDENTIFIER; //Default value
  }
  
  public String getEdgeTestRankName()
  {
    //Consentire il loading da properties file
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_TEST_RANK, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_TEST_RANK_IDENTIFIER; //Default value
  }
  
  public String getEdgeEstimatedRatingName()
  {
    //Consentire il loading da properties file
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

  public int getDistanceAlgorithm()
  {
    int distanceAlg = Integer.parseInt(getProperty(PROPERTY_NAME_DISTANCE_ALGORITHM, "-1"));

    if (distanceAlg > 0)
      return distanceAlg;
    else
      return SimilarityFactory.EUCLIDEAN_SIM; //Default value
  }
  public String getEdgeSimilarityName()
  {
    //Consentire il loading da properties file
    String rankValueName = getProperty(PROPERTY_NAME_EDGE_SIMILARITY, null);
    if (rankValueName != null)
      return rankValueName;
    else
      return PROPERTY_EDGE_SIMILARITY_IDENTIFIER; //Default value
  }
  
  public boolean getRecalculateSimilarity()
  {
    String rankValueName = getProperty(PROPERTY_NAME_RECALCULATE_SIMILARITY, null);
    if (rankValueName != null)
      return Boolean.valueOf(rankValueName).booleanValue();
    else
      return PROPERTY_RECALCULATE_SIMILARITY; //Default value
  }

  public int getRecommenderType()
  {
    //Consentire il loading da properties file
    String recommenderTypeValue = getProperty(PROPERTY_NAME_RECOMMENDER_TYPE, null);
    if (recommenderTypeValue != null)
      return Integer.valueOf(recommenderTypeValue).intValue();
    else
      return PROPERTY_RECOMMENDER_TYPE; //Default value
  }

  public int getMaxFeatures()
  {
    //Consentire il loading da properties file
    String maxFeatureValue = getProperty(PROPERTY_NAME_MAXFEATURES, null);
    if (maxFeatureValue != null)
      return Integer.valueOf(maxFeatureValue).intValue();
    else
      return PROPERTY_MAXFEATURES; //Default value
  }

  public double getFeatureInitValue()
  {
    //Consentire il loading da properties file
    String featureInitValue = getProperty(PROPERTY_NAME_FEATURE_INIT_VALUE, null);
    if (featureInitValue != null)
      return Double.parseDouble(featureInitValue);
    else
      return PROPERTY_FEATURE_INIT_VALUE; //Default value
  }
}
