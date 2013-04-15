/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import org.reco4j.graph.INode;

/**
 *
 * @author giuri
 */
public class MFPredictor1
  implements IPredictor
{
  private IFeatureMatrixModel<? extends IFeatureMatrix> model;

  public MFPredictor1(IFeatureMatrixModel<? extends IFeatureMatrix> model)
  {
    this.model = model;
  }

  @Override
  public double predictRating(INode user, INode item)
  {
    Long userId = user.getId();
    Long itemId = item.getId();
    
    double sum = 1;
    for (int f = 0; f < model.getFeaturesCount(); f++)
    {
      sum += model.getItemFeatures().getFeature(f, itemId)
             * model.getUserFeatures().getFeature(f, userId);
      if (sum > 5)
        sum = 5.0;
      if (sum < 1)
        sum = 1.0;
    }
    return sum;
  }
}
