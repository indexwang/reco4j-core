/*
 * MFFeatureMatrix.java
 * 
 * Copyright (C) 2013 Alessandro Negro <alessandro.negro at reco4j.org>
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
package org.reco4j.recommender.svd;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
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
