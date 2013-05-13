/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender.svd;

/**
 *
 * @author giuri
 */
public interface IFeatureMatrix
{

  Double getFeature(Integer feature, Long item);
}
