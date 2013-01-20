/*
 * CollaborativeFilteringRecommender.java
 * 
 * Copyright (C) 2012 Alessandro Negro <alessandro.negro at reco4j.org>
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
package org.reco4j.graph.recommenders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.graph.similarity.ISimilarity;
import org.reco4j.graph.similarity.SimilarityFactory;
import org.reco4j.util.RecommenderPropertiesHandle;
import org.reco4j.util.TimeReportUtility;
import org.reco4j.util.Utility;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class CollaborativeFilteringRecommender extends BasicRecommender
{
  protected static final Logger logger = Logger.getLogger(CollaborativeFilteringRecommender.class.getName());
  protected HashMap<String, HashMap<String, Rating>> knn;
  //protected ArrayList<IEdgeType> edges;

  public CollaborativeFilteringRecommender()
  {
    knn = new HashMap<String, HashMap<String, Rating>>();
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
    timeReport.start();
    setLearningDataSet(learningDataSet);
    createKNN(RecommenderPropertiesHandle.getInstance().getDistanceAlgorithm());
    timeReport.stop();
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    setLearningDataSet(learningDataSet);
    loadKNN(RecommenderPropertiesHandle.getInstance().getDistanceAlgorithm());
    printKNN(knn);
  }

  @Override
  public void updateRecommender(IGraph learningDataSet)
  {
    //Questo metodo insieme a quello di store consentono di storicizzare e quindi aggiornare 
    //il motore delle raccomandazioni
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Rating> recommend(INode user)
  {
    ArrayList<Rating> recommendations = new ArrayList<Rating>();

    for (INode item : learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType()))
    {
      if (item.isConnected(user, edgeType))
        continue;
      double estimatedRating = estimateRating(user, item, edgeType,
                                              RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());
      Utility.orderedInsert(recommendations, estimatedRating, item, RecommenderPropertiesHandle.getInstance().getRecoNumber());
    }
    return recommendations;
  }

  private void createKNN(int distMethod)
  {
    TimeReportUtility timeReport = new TimeReportUtility("createKNN");
    timeReport.start();

    for (INode item : learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType()))
    {
      String itemId = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
      if (itemId == null)
        throw new RuntimeException("Items don't have the 'id' property!");
      HashMap<String, Rating> knnRow = getKnnRow(itemId);
      foundNearestNeighbour(item, edgeType, distMethod, knnRow);
    }
    timeReport.stop();
  }

  private void foundNearestNeighbour(INode item, IEdgeType edgeType, int distMethod, HashMap<String, Rating> knnRow)
  {
    logger.info("foundNearestNeighbour: " + item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()));
    HashMap<Long, INode> nodesByInEdge = item.getCommonNodes(edgeType);
    String itemId = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
    for (INode otherItem : nodesByInEdge.values())
    {
      String otherItemId = otherItem.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
      if (knnRow.get(otherItemId) != null)
        continue;
      if (itemId == null || itemId.isEmpty() || otherItemId == null || otherItemId.isEmpty())
        throw new RuntimeException("Items don't have the 'id' property!");
      if (itemId.equalsIgnoreCase(otherItemId))
        continue;
      double similarityValue = calculateSimilarity(item, otherItem, edgeType, distMethod);
      //Take into consideration that the similarityValue is simmetric
      if (similarityValue > 0)
      {
        knnRow.put(otherItemId, new Rating(otherItem, similarityValue));
        HashMap<String, Rating> otherKnnRow = getKnnRow(otherItemId);
        otherKnnRow.put(itemId, new Rating(item, similarityValue));
      }
    }
    /*System.out.print(itemId + ":> ");
    for (String key : knnRow.keySet())
      System.out.print(key + "["+ knnRow.get(key).getRate() +"] ");
    System.out.println();*/
  }

  @Override
  public double estimateRating(INode user, INode item, IEdgeType rankType, String propertyName)
  {
    /*ISimilarity simFunction = SimilarityFactory.getSimilarityClass(RecommenderPropertiesHandle.getInstance().getDistanceAlgorithm());
     IEdgeType estimatedRatingEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_ESTIMATED_RATING);
     IEdge alreadyCalulatedEdge = user.getEdge(item, estimatedRatingEdgeType);
     String edgeEstimationPropertyName = CollaborativeFilteringRecommender.class.getName() + "-" + simFunction.getClass().getName();
    
     if (alreadyCalulatedEdge != null
     && alreadyCalulatedEdge.getPermissiveProperty(edgeEstimationPropertyName) != null)
     {
     //System.out.println("Esiste già... ... riciclo!");
     BigDecimal value = new BigDecimal(alreadyCalulatedEdge.getProperty(edgeEstimationPropertyName));
     return value;
     }*/
    double estimatedRating = calculateEstimatedRating(item, user, rankType, propertyName);

    //learningDataSet.addEdge(user, item, estimatedRatingEdgeType, edgeEstimationPropertyName, estimatedRating.toString());
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

  private void loadKNN(int distanceAlgorithm)
  {
    ISimilarity simFunction = SimilarityFactory.getSimilarityClass(distanceAlgorithm);
    IEdgeType edgeType1 = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY);
    List<IEdge> edgesByType = learningDataSet.getEdgesByType(edgeType1);
    for (IEdge alreadyCalulatedEdge : edgesByType)
    {
      logger.info("alreadyCalulatedEdge"); //Valutare se la simmetria fa si che ci sia solo un arco tra due item
      String sourceItemId = alreadyCalulatedEdge.getSource().getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
      String destinationItemId = alreadyCalulatedEdge.getDestination().getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
      HashMap<String, Rating> recommendationsSource = getKnnRow(sourceItemId);
      HashMap<String, Rating> recommendationsDestination = getKnnRow(destinationItemId);
      double similarityValue = Double.parseDouble(alreadyCalulatedEdge.getProperty(simFunction.getClass().getName()));
      if (similarityValue > 0)
      {
        Rating rateSource = new Rating(alreadyCalulatedEdge.getSource(), similarityValue);
        Rating rateDestination = new Rating(alreadyCalulatedEdge.getDestination(), similarityValue);
        recommendationsSource.put(destinationItemId, rateDestination);
        recommendationsDestination.put(sourceItemId, rateSource);
      }
    }
  }

  protected double calculateSimilarity(INode item, INode otherItem, IEdgeType edgeType, int distMethod)
  {
    ISimilarity simFunction = SimilarityFactory.getSimilarityClass(distMethod);
    IEdgeType similarityEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY);
    IEdge alreadyCalulatedEdge = null;
    if (!RecommenderPropertiesHandle.getInstance().getRecalculateSimilarity())
    {
      alreadyCalulatedEdge = item.getEdge(otherItem, similarityEdgeType);
      if (alreadyCalulatedEdge != null
          && alreadyCalulatedEdge.getPermissiveProperty(simFunction.getClass().getName()) != null)
      {
        double value = Double.parseDouble(alreadyCalulatedEdge.getProperty(simFunction.getClass().getName()));
        return value;
      }
    }

    double similarityValue = simFunction.getSimilarity(item, otherItem, edgeType, learningDataSet);
    //Introdurre una coda di valori da inserire per toglierla dal processo di calcolo
    if (!RecommenderPropertiesHandle.getInstance().getRecalculateSimilarity())
    {
      if (alreadyCalulatedEdge != null)
        learningDataSet.setEdgeProperty(alreadyCalulatedEdge, simFunction.getClass().getName(), Double.toString(similarityValue));
      else
        learningDataSet.addEdge(item, otherItem, similarityEdgeType, simFunction.getClass().getName(), Double.toString(similarityValue));
    }
    return similarityValue;
  }

  private void printKNN(HashMap<String, HashMap<String, Rating>> knnMatrix)
  {
    for (String rowItem : knnMatrix.keySet())
    {
      //System.out.print("Key: " + rowItem + " - ");
      StringBuilder out = new StringBuilder("Key: ");
      out.append(rowItem).append(" - ");
      HashMap<String, Rating> row = knnMatrix.get(rowItem);
      for (String item : row.keySet())
      {
        Rating rate = row.get(item);
        out.append(" ").append(item).append("(").append(rate.getRate()).append(") ");
      }
      logger.info(out.toString());
    }
  }

  private double calculateEstimatedRating(INode item, INode user, IEdgeType rankType, String propertyName) throws RuntimeException
  {

    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;
    //Da verificare
    //if (user.getItemRating(item) != null)
    //  estimatedRating = user.getItemRating(item).getRate();
    String itemIdentifierName = RecommenderPropertiesHandle.getInstance().getItemIdentifierName();
    String id = item.getProperty(itemIdentifierName);
    if (!knn.containsKey(id))
      return 0.0;
    for (Rating rate : knn.get(id).values())
    {
      IEdge edge = user.getEdge(rate.getItem(), rankType);
      if (edge == null)
        continue;
      double uRate = getUserRate(edge, propertyName, rankType);
      double similarityBetweenItem = rate.getRate();
      weightedRatingSum += uRate * similarityBetweenItem;
      similaritySum += similarityBetweenItem;
    }
    if (similaritySum > 0)
      estimatedRating = weightedRatingSum / similaritySum;
    return estimatedRating;
  }

  private HashMap<String, Rating> getKnnRow(String itemId)
  {
    HashMap<String, Rating> knnRow = knn.get(itemId);
    if (knnRow == null)
    {
      knnRow = new HashMap<String, Rating>();
      knn.put(itemId, knnRow);
    }
    return knnRow;
  }
}
