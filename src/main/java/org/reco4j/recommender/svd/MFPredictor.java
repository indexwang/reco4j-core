/*
 * MFPredictor.java
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

import org.reco4j.graph.INode;
import org.reco4j.recommender.IPredictor;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class MFPredictor
  implements IPredictor
{
  private IFeatureMatrixModel<? extends IFeatureMatrix> model;

  public MFPredictor(IFeatureMatrixModel<? extends IFeatureMatrix> model)
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
