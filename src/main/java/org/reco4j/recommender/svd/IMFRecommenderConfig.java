/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender.svd;

import org.reco4j.util.IRecommenderConfig;

/**
 *
 * @author giuri
 */
public interface IMFRecommenderConfig
  extends IRecommenderConfig
{
  @Override
  public String getUserType();

  @Override
  public String getItemType();

  @Override
  public String getUserIdentifierName();

  @Override
  public String getEdgeRankValueName();

  public int getRecoNumber();

  public int getMaxFeatures();

  public double getFeatureInitValue();
}
