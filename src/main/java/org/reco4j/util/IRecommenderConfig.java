/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.util;

import org.reco4j.graph.IGraphConfig;

/**
 *
 * @author giuri
 */
public interface IRecommenderConfig
{
  public int getRecommenderType();

  public IGraphConfig getGraphConfig();

  /*
   * Due to the RecommenderEvaluator, we need the following common properties
   * on all recommender config;
   */
  public String getUserType();

  public String getItemType();

  public String getItemIdentifierName();

  public String getUserIdentifierName();

  public String getEdgeRankValueName();
}
