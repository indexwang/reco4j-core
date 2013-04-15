/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.HashMap;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;

/**
 *
 * @author giuri
 */
public class NMPredictor1
        implements IPredictor
{

  private String itemIdentifierName;
  private HashMap<String, HashMap<String, Rating>> knn;
  protected IEdgeType rankEdgeType;
  private String edgeRankValueName;

  @Override
  public double predictRating(INode user, INode item)
  {
    return calculateEstimatedRating(item, user, rankEdgeType, edgeRankValueName);
  }

  protected double calculateEstimatedRating(INode item, INode user, IEdgeType rankType, String edgeRankValueName) throws RuntimeException
  {

    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;
    //Da verificare
    //if (user.getItemRating(item) != null)
    //  estimatedRating = user.getItemRating(item).getRate();
    String id = item.getProperty(itemIdentifierName);
    if (!knn.containsKey(id))
      return 0.0;
    for (Rating rate : knn.get(id).values())
    {
      IEdge edge = user.getEdge(rate.getItem(), rankType);
      if (edge == null)
        continue;
      double uRate = getUserRate(edge, edgeRankValueName, rankType);
      double similarityBetweenItem = rate.getRate();
      weightedRatingSum += uRate * similarityBetweenItem;
      similaritySum += similarityBetweenItem;
    }
    if (similaritySum > 0)
      estimatedRating = weightedRatingSum / similaritySum;
    return estimatedRating;
  }

  private double getUserRate(IEdge edge, String propertyName, IEdgeType rankType) throws RuntimeException
  {
    String propertyValue = edge.getProperty(propertyName);
    if (propertyValue == null)
      throw new RuntimeException("Properties : " + propertyName + " not found on edge of type: " + rankType.getType());
    double uRate = Double.parseDouble(propertyValue);
    return uRate;
  }
}
