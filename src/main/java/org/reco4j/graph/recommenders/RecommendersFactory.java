/*
 * Copyright 2011 Wirex s.r.l.
 * All rights reserved.
 * Wirex proprietary/confidential. Use is subject to license terms.
 */
package org.reco4j.graph.recommenders;

/**
 *
 * @author ale
 */
public class RecommendersFactory
{
  public final static int RECOMMENDER_TYPE_COLLABORATIVE = 1;
  public final static int RECOMMENDER_TYPE_MATRIXFACTORIZATION = 2;
  
  public static IRecommender getRecommender(int recommenderType)
  {
    switch (recommenderType)
    {
      case RECOMMENDER_TYPE_COLLABORATIVE:
        return new CollaborativeFilteringRecommender();
      case RECOMMENDER_TYPE_MATRIXFACTORIZATION:
        return new MFRecommender();      
      default:
        throw new UnsupportedOperationException("Unsupported recommender type: " + recommenderType);
    }    
  }
}
