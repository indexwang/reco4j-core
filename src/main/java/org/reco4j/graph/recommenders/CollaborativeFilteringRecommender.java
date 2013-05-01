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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.graph.UserItemDataset;
import org.reco4j.graph.similarity.ISimilarity;
import org.reco4j.graph.similarity.SimilarityFactory;
import org.reco4j.util.TimeReportUtility;
import org.reco4j.util.Utility;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class CollaborativeFilteringRecommender
  extends BasicRecommender<ICollaborativeFilteringRecommenderConfig>
{
  private static final Logger logger = Logger.getLogger(CollaborativeFilteringRecommender.class.getName());
  protected IGraph learningDataSet;
  private HashMap<String, HashMap<String, Rating>> knn;
  protected ISimilarity similarityFunction;

  //protected ArrayList<IEdgeType> edges;
  //
  public CollaborativeFilteringRecommender(ICollaborativeFilteringRecommenderConfig config)
  {
    super(config);
    similarityFunction = SimilarityFactory.getSimilarityClass(getConfig().getSimilarityConfig());
    knn = new HashMap<String, HashMap<String, Rating>>();
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    TimeReportUtility timeReport = new TimeReportUtility("buildRecommender");
    timeReport.start();
    setLearningDataSet(learningDataSet);
    createKNN();
    timeReport.stop();
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    setLearningDataSet(learningDataSet);
    loadKNN();
    printKNN(knn);
  }

  protected void setLearningDataSet(IGraph learningDataSet)
  {
    this.learningDataSet = learningDataSet;
  }
  
  @Override
  public List<Rating> recommend(INode userNode)
  {
    final int recoNumber = getConfig().getRecoNumber();

    ArrayList<Rating> recommendations = new ArrayList<Rating>();

    for (INode item : learningDataSet.getNodesByType(getConfig().getItemType()))
    {
      if (item.isConnected(userNode, rankEdgeType))
        continue;
      double estimatedRating = estimateRating(userNode, item);
      Utility.orderedInsert(recommendations, estimatedRating, item, recoNumber);
    }
    return recommendations;
  }

  private void createKNN()
  {
    TimeReportUtility timeReport = new TimeReportUtility("createKNN");


    for (INode item : learningDataSet.getNodesByType(getConfig().getItemType()))
    {
      timeReport.start();
      String itemId = item.getProperty(getConfig().getItemIdentifierName());
      if (itemId == null)
        throw new RuntimeException("Items don't have the 'id' property!");
      HashMap<String, Rating> knnRow = getKnnRow(itemId);
      findNearestNeighbour(item, rankEdgeType, knnRow);
      //printKnnRow(itemId);
      timeReport.stop();
    }
    timeReport.printStatistics();
  }

  private void findNearestNeighbour(INode item, IEdgeType rankEdgeType, HashMap<String, Rating> knnRow)
  {
    findNearestNeighbour(item, rankEdgeType, knnRow, false);
  }

  private void findNearestNeighbour(INode item, IEdgeType rankEdgeType, HashMap<String, Rating> knnRow, boolean rewrite)
  {
    HashMap<String, INode> nodesByInEdge = item.getCommonNodes(rankEdgeType, getConfig().getItemIdentifierName());
    logger.log(Level.INFO, "foundNearestNeighbour: {0}, CommonNodes Size: " + nodesByInEdge.size(), item.getProperty(getConfig().getItemIdentifierName()));
    String itemId = item.getProperty(getConfig().getItemIdentifierName());
    for (INode otherItem : nodesByInEdge.values())
    {
      String otherItemId = otherItem.getProperty(getConfig().getItemIdentifierName());
      if (!rewrite && knnRow.get(otherItemId) != null)
        continue;
      if (itemId == null || itemId.isEmpty() || otherItemId == null || otherItemId.isEmpty())
        throw new RuntimeException("Items don't have the 'id' property!");
      if (itemId.equalsIgnoreCase(otherItemId))
        continue;
      double similarityValue = calculateSimilarity(item, otherItem, rankEdgeType);
      if (similarityValue > 0)
      {
        knnRow.put(otherItemId, new Rating(otherItem, similarityValue));
        if (!rewrite)
        {
          HashMap<String, Rating> otherKnnRow = getKnnRow(otherItemId);
          otherKnnRow.put(itemId, new Rating(item, similarityValue));
        }
      }
    }
    /*System.out.print(itemId + ":> ");
     for (String key : knnRow.keySet())
     System.out.print(key + "["+ knnRow.get(key).getRate() +"] ");
     System.out.println();*/
  }

  protected double calculateSimilarity(INode item, INode otherItem, IEdgeType rankEdgeType)
  {
//    ISimilarity similarityFunction = SimilarityFactory.getSimilarityClass(similarityConfig);
    IEdgeType similarityEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig());
    IEdge alreadyCalulatedEdge = null;
    if (!getConfig().getRecalculateSimilarity())
    {
      alreadyCalulatedEdge = item.getEdge(otherItem, similarityEdgeType);
      if (alreadyCalulatedEdge != null
          && alreadyCalulatedEdge.getPermissiveProperty(similarityFunction.getClass().getName()) != null)
      {
        double value = Double.parseDouble(alreadyCalulatedEdge.getProperty(similarityFunction.getClass().getName()));
        return value;
      }
    }

    double similarityValue = similarityFunction.getSimilarity(item, otherItem, rankEdgeType);
    //Introdurre una coda di valori da inserire per toglierla dal processo di calcolo
    if (!getConfig().getRecalculateSimilarity())
    {
      if (alreadyCalulatedEdge != null)
        alreadyCalulatedEdge.setProperty(similarityFunction.getClass().getName(), Double.toString(similarityValue));
      else
        learningDataSet.addEdge(item, otherItem, similarityEdgeType, similarityFunction.getClass().getName(), Double.toString(similarityValue));
    }
    return similarityValue;
  }

  @Override
  public double estimateRating(INode user, INode item)
  {
    /*ISimilarity simFunction = SimilarityFactory.getSimilarityClass(getConfig().getSimilarityType());
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
    double estimatedRating = calculateEstimatedRating(item, user, rankEdgeType, getConfig().getEdgeRankValueName());

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

  private void loadKNN()
  {
    IEdgeType edgeType1 = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig());
    List<IEdge> edgesByType = learningDataSet.getEdgesByType(edgeType1);
    for (IEdge alreadyCalulatedEdge : edgesByType)
    {
      String sourceItemId = alreadyCalulatedEdge.getSource().getProperty(getConfig().getItemIdentifierName());
      String destinationItemId = alreadyCalulatedEdge.getDestination().getProperty(getConfig().getItemIdentifierName());
      HashMap<String, Rating> recommendationsSource = getKnnRow(sourceItemId);
      HashMap<String, Rating> recommendationsDestination = getKnnRow(destinationItemId);
      String similarityValueStr = alreadyCalulatedEdge.getPermissiveProperty(similarityFunction.getClass().getName());
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
    printKNN(knn);
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

  protected double calculateEstimatedRating(INode item, INode user, IEdgeType rankType, String edgeRankValueName) throws RuntimeException
  {

    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;
    //Da verificare
    //if (user.getItemRating(item) != null)
    //  estimatedRating = user.getItemRating(item).getRate();
    String itemIdentifierName = getConfig().getItemIdentifierName();
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

  @Override
  public void updateRecommender(IEdge newEdge)
  {
    if (newEdge.getProperty(getConfig().getEdgeRankValueName()) == null)
      return;
    INode dest = newEdge.getDestination();
    HashMap<String, INode> commonNodes = dest.getCommonNodes(rankEdgeType, getConfig().getItemIdentifierName());
    for (INode item : commonNodes.values())
    {
      String itemId = item.getProperty(getConfig().getItemIdentifierName());
      if (itemId == null)
        throw new RuntimeException("Items don't have the 'id' property!");
      HashMap<String, Rating> knnRow = getKnnRow(itemId);
      findNearestNeighbour(item, rankEdgeType, knnRow, true);
    }
  }

  private void printKnnRow(String itemId)
  {
    HashMap<String, Rating> row = getKnnRow(itemId);
    for (String item : row.keySet())
    {
      Rating rate = row.get(item);
      logger.log(Level.INFO, " {0}({1}) ", new Object[]
      {
        rate.getItem().getProperty(getConfig().getItemIdentifierName()), rate.getRate()
      });
    }

  }

  @Override
  public void buildRecommender(IGraph learningDataSet, UserItemDataset dataset)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
