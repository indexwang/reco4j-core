/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender.knn;

import org.reco4j.similarity.ISimilarityConfig;
import org.reco4j.util.IRecommenderConfig;

/**
 * This interface is valid for both CollaborativeFilteringRecommender and
 * FastCollaborativeFilteringRecommender.
 * 
 * @author giuri
 */
public interface ICollaborativeFilteringRecommenderConfig
  extends IRecommenderConfig
{
  @Override
  public String getItemType();

  @Override
  public String getItemIdentifierName();

  @Override
  public String getEdgeRankValueName();

  public ISimilarityConfig getSimilarityConfig();

  public int getRecoNumber();

  public boolean getRecalculateSimilarity();
}
