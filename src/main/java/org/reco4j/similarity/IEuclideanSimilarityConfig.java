/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.similarity;

/**
 * This interface is valid for both EuclideanSimilarity and
 * EuclideanNormalizedSimilarity.
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public interface IEuclideanSimilarityConfig
  extends ISimilarityConfig
{
  public String getEdgeRankValueName();
}
