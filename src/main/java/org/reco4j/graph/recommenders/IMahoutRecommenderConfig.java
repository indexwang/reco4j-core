/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import org.reco4j.util.IRecommenderConfig;

/**
 *
 * @author giuri
 */
public interface IMahoutRecommenderConfig
  extends IRecommenderConfig
{
  @Override
  public String getUserType();

  @Override
  public String getItemType();

  @Override
  public String getUserIdentifierName();

  @Override
  public String getItemIdentifierName();

  @Override
  public String getEdgeRankValueName();

  public int getKValue();

  public double getMaxPreferenceValue();

  public double getMinPreferenceValue();
}
