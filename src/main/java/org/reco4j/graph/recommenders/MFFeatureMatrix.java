/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author giuri
 */
public class MFFeatureMatrix implements IFeatureMatrix
{
  private ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>> features = new ConcurrentHashMap<Integer, ConcurrentHashMap<Long, Double>>();
  
  public ConcurrentHashMap<Long, Double> getFeatureItemMap(Integer feature)
  {
    ConcurrentHashMap<Long, Double> result = features.get(feature);
    
    if (result == null)
    {
      // TODO synchronize if needed
      result = new ConcurrentHashMap<Long, Double>();
      features.put(feature, result);
    }
    
    return result;
  }
  
  public void setFeature(Integer feature, Long item, Double value)
  {
    getFeatureItemMap(feature).put(item, value);
  }
  
  @Override
  public Double getFeature(Integer feature, Long item)
  {
    return getFeatureItemMap(feature).get(item);
  }
}
