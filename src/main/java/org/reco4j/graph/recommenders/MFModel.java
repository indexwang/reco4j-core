/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.concurrent.ConcurrentHashMap;
import org.reco4j.graph.INode;

/**
 *
 * @author giuri
 */
public class MFModel 
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
