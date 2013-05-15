/*
 * MFModel.java
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
import org.reco4j.graph.INode;
import org.reco4j.model.ModelBase;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class MFModel extends ModelBase
  implements IFeatureMatrixModel<MFFeatureMatrix>
{

  private int featuresCount;
  private MFFeatureMatrix userFeatures;
  private MFFeatureMatrix itemFeatures;
  
  public void init(int featuresCount, double featureInitValue, ConcurrentHashMap<Long, INode> userList, ConcurrentHashMap<Long, INode> itemList)
  {
    this.featuresCount = featuresCount;
    userFeatures = new MFFeatureMatrix();
    itemFeatures = new MFFeatureMatrix();
    
    for (int feature = 0; feature < featuresCount; feature++)
    {
      initFeature(feature, featureInitValue, userFeatures, userList);
      initFeature(feature, featureInitValue, itemFeatures, itemList);
    }
  }

  private void initFeature(Integer feature, Double featureInitValue, final MFFeatureMatrix featureMatrix, final ConcurrentHashMap<Long, INode> nodeList)
  {
    ConcurrentHashMap<Long, Double> qi = featureMatrix.getFeatureItemMap(feature);
    for (INode node : nodeList.values())
      qi.put(node.getId(), featureInitValue);
  }

  /**
   * @return the featuresCount
   */
  @Override
  public int getFeaturesCount()
  {
    return featuresCount;
  }

  /**
   * @return the userFeatures
   */
  @Override
  public MFFeatureMatrix getUserFeatures()
  {
    return userFeatures;
  }

  /**
   * @return the itemFeatures
   */
  @Override
  public MFFeatureMatrix getItemFeatures()
  {
    return itemFeatures;
  }
}
