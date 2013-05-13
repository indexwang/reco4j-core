/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author giuri
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
