/*
 * KNNModelLoader.java
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
package org.reco4j.recommender.knn;

import java.util.HashMap;
import java.util.List;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.model.Rating;
import org.reco4j.model.IModelBuilder;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class KNNModelLoader
  implements IModelBuilder<KNNModel>
{
  private IGraph modelGraph;
  //
  private String itemIdentifierNodePropertyName;
  private String modelName;
  private IEdgeType similarityEdgeType;
  //
  private KNNModel model = new KNNModel();

  public KNNModelLoader(IGraph modelGraph, String itemIdentifierNodePropertyName, IEdgeType similarityEdgeType, String modelName)
  {
    this.modelGraph = modelGraph;
    this.itemIdentifierNodePropertyName = itemIdentifierNodePropertyName;
    this.modelName = modelName;
    this.similarityEdgeType = similarityEdgeType;
  }
  
  @Override
  public KNNModel build()
  {
    loadKNN();
    return this.model;
  }
  
  private void loadKNN()
  {
    List<IEdge> edgesByType = modelGraph.getEdgesByType(similarityEdgeType);
    for (IEdge alreadyCalulatedEdge : edgesByType)
    {
      String sourceItemId = alreadyCalulatedEdge.getSource().getProperty(itemIdentifierNodePropertyName);
      String destinationItemId = alreadyCalulatedEdge.getDestination().getProperty(itemIdentifierNodePropertyName);
      HashMap<String, Rating> recommendationsSource = model.getKnnRow(sourceItemId);
      HashMap<String, Rating> recommendationsDestination = model.getKnnRow(destinationItemId);
      String similarityValueStr = alreadyCalulatedEdge.getPermissiveProperty(modelName);
      if (similarityValueStr == null)
        continue;
      double similarityValue = Double.parseDouble(similarityValueStr);
      if (similarityValue > 0)
      {
        Rating rateSource = new Rating(alreadyCalulatedEdge.getSource(), similarityValue);
        Rating rateDestination = new Rating(alreadyCalulatedEdge.getDestination(), similarityValue);
        recommendationsSource.put(destinationItemId, rateDestination);
        recommendationsDestination.put(sourceItemId, rateSource);
      }
    }
  }

  @Override
  public void update(IEdge edge, int operation)
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public void setModel(KNNModel model)
  {
    this.model = model;
  }
}
