/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

/**
 *
 * @author giuri
 */
public interface IFeatureMatrixModel<T extends IFeatureMatrix>
{

  /**
   * @return the featuresCount
   */
  int getFeaturesCount();

  /**
   * @return the itemFeatures
   */
  T getItemFeatures();

  /**
   * @return the userFeatures
   */
  T getUserFeatures();
  
}
