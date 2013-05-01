/*
 * RecommendersFactory.java
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

import org.reco4j.util.IRecommenderConfig;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class RecommendersFactory
{
  public final static int RECOMMENDER_TYPE_COLLABORATIVE = 1;
  public final static int RECOMMENDER_TYPE_MATRIXFACTORIZATION = 2;
  public final static int RECOMMENDER_TYPE_FASTCOLLABORATIVE = 3;
  public final static int RECOMMENDER_TYPE_MAHOUT = 4;
  
  public static IRecommender getRecommender(IRecommenderConfig config)
  {
    int recommenderType = config.getRecommenderType();
  
    switch (recommenderType)
    {
      case RECOMMENDER_TYPE_COLLABORATIVE:
        return new KNNRecommender((ICollaborativeFilteringRecommenderConfig) config);
      case RECOMMENDER_TYPE_FASTCOLLABORATIVE:
        return new KNNFastRecommender((ICollaborativeFilteringRecommenderConfig) config);
      case RECOMMENDER_TYPE_MATRIXFACTORIZATION:
        return new MFRecommender2((IMFRecommenderConfig) config);      
      case RECOMMENDER_TYPE_MAHOUT:
        return new MahoutRecommender((IMahoutRecommenderConfig) config);      
      default:
        throw new RuntimeException("Bad recommender type: " + recommenderType);
      //Aggiungere meccanismo di loading mediante ServiceLoader
    }    
  }
}
