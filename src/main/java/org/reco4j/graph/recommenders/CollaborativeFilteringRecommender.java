/*
 * Copyright 2011 Wirex s.r.l.
 * All rights reserved.
 * Wirex proprietary/confidential. Use is subject to license terms.
 */
package org.reco4j.graph.recommenders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.session.RecommenderSessionManager;
import org.reco4j.graph.similarity.ISimilarity;
import org.reco4j.graph.similarity.SimilarityFactory;
import org.reco4j.util.RecommenderPropertiesHandle;
import org.reco4j.util.Utility;

/**
 *
 * @author ale
 */
public class CollaborativeFilteringRecommender extends BasicRecommender
{
  protected Logger logger = Logger.getLogger(CollaborativeFilteringRecommender.class);
  protected HashMap<String, HashMap<String, Rating>> knn;
  //protected ArrayList<IEdgeType> edges;  
  protected IEdgeType edgeType;

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    setLearningDataSet(learningDataSet);
    RecommenderSessionManager.getInstance().setRankValueProprertyName(
      RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());//Da spostare eventualmente
    //edges = new ArrayList<IEdgeType>();
    //edges.add(EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK)); //Prendere da file di properties
    edgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK);
    knn = createKNN(RecommenderPropertiesHandle.getInstance().getDistanceAlgorithm());
    /*for (String id : knn.keySet())
     {
     StringBuilder row = new StringBuilder("ItemId: ");
     row.append(id).append(" > ");
     for (String itemId : knn.get(row).keySet())
     {
     row.append(itemId).append(":").append(knn.get(row).get(itemId).getRate());
     }
     System.out.println(row);
     }*/
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    setLearningDataSet(learningDataSet);
    RecommenderSessionManager.getInstance().setRankValueProprertyName(
      RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());//Da spostare eventualmente
    edgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK);

    knn = loadKNN(RecommenderPropertiesHandle.getInstance().getDistanceAlgorithm());
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
      Utility.orderedInsert(recommendations, estimatedRating, item);
    }
    recommendations = Utility.cutList(recommendations,
                                      RecommenderPropertiesHandle.getInstance().getRecoNumber());
    return recommendations;
  }

  private HashMap<String, HashMap<String, Rating>> createKNN(int distMethod)
  {
    //System.out.println("Creating KNN ...");
    HashMap<String, HashMap<String, Rating>> knnMatrix = new HashMap<String, HashMap<String, Rating>>();

    for (INode item : learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType()))
    {
      if (item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()) == null)
        throw new RuntimeException("Items don't have the 'id' property!");
      HashMap<String, Rating> knnRow = foundNearestNeighbour(item, edgeType, distMethod);
      knnMatrix.put(item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()), knnRow);
    }
    //System.out.println("... fine Creating KNN");
    return knnMatrix;
  }

  private HashMap<String, Rating> foundNearestNeighbour(INode item, IEdgeType edgeType, int distMethod)
  {
    System.out.println("foundNearestNeighbour: " + item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()));
    HashMap<String, Rating> knnRow = new HashMap<String, Rating>();
    //long startGetCommonNodes = System.currentTimeMillis();
    //List<INode> nodesByInEdge = learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType());
    List<INode> nodesByInEdge = item.getCommonNodes(edgeType);
    //long endGetCommonNodes = System.currentTimeMillis();
    //System.out.println("CommonNodes ("+ (endGetCommonNodes - startGetCommonNodes) +"): " + nodesByInEdge.size());
    for (INode otherItem : nodesByInEdge)
    {
      if (item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()) == null
          || otherItem.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()) == null)
        throw new RuntimeException("Items don't have the 'id' property!");
//      System.out.println("foundNearestNeighbour item: " + item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()));
//      System.out.println("foundNearestNeighbour otherItem: " + otherItem.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()));
      if (item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()).equalsIgnoreCase(otherItem.getProperty(
        RecommenderPropertiesHandle.getInstance().getItemIdentifierName()))) //Migliorabile e generalizabile
        continue;
      double similarityValue = calculateSimilarity(item, otherItem, edgeType, distMethod);
      if (similarityValue > 0)
        knnRow.put(otherItem.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()), new Rating(otherItem, similarityValue));

    }
    return knnRow;
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

  private HashMap<String, HashMap<String, Rating>> loadKNN(int distanceAlgorithm)
  {
    HashMap<String, HashMap<String, Rating>> knnMatrix = new HashMap<String, HashMap<String, Rating>>();
    ISimilarity simFunction = SimilarityFactory.getSimilarityClass(distanceAlgorithm);
    for (INode item : learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType()))
    {
      ArrayList<Rating> recommendations = new ArrayList<Rating>();
      IEdgeType similarityEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY);
      List<IEdge> edgeList = item.getOutEdge(similarityEdgeType);
      for (IEdge alreadyCalulatedEdge : edgeList)
      {
        double similarityValue = Double.parseDouble(alreadyCalulatedEdge.getProperty(simFunction.getClass().getName()));
        if (similarityValue > 0)
          Utility.orderedInsert(recommendations, similarityValue, alreadyCalulatedEdge.getSource());
      }
      HashMap<String, Rating> knnRow = Utility.getKNNRow(recommendations, RecommenderPropertiesHandle.getInstance().getKValue());
      knnMatrix.put(item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()), knnRow);
    }
    printKNN(knnMatrix);
    return knnMatrix;
  }

  private double calculateSimilarity(INode item, INode otherItem, IEdgeType edgeType, int distMethod)
  {
    ISimilarity simFunction = SimilarityFactory.getSimilarityClass(distMethod);

    IEdgeType similarityEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY);
    IEdge alreadyCalulatedEdge = item.getEdge(otherItem, similarityEdgeType);
    if (alreadyCalulatedEdge != null
        && alreadyCalulatedEdge.getPermissiveProperty(simFunction.getClass().getName()) != null
        && !RecommenderPropertiesHandle.getInstance().getRecalculateSimilarity())
    {
      double value = Double.parseDouble(alreadyCalulatedEdge.getProperty(simFunction.getClass().getName()));
      return value;
    }

    double similarityValue = simFunction.getSimilarity(item, otherItem, edgeType, learningDataSet);
    
    //Qui intervenire controllando se esiste l'edge basta solo aggiungere la proprietà
    if (alreadyCalulatedEdge != null)
    {
      //System.out.println("esiste, aggiungo proprietà");
      learningDataSet.setEdgeProperty(alreadyCalulatedEdge, simFunction.getClass().getName(), Double.toString(similarityValue));
    }
    else
    {
      learningDataSet.addEdge(item, otherItem, similarityEdgeType, simFunction.getClass().getName(), Double.toString(similarityValue));
    }
    return similarityValue;
  }

  private void printKNN(HashMap<String, HashMap<String, Rating>> knnMatrix)
  {
    for (String rowItem : knnMatrix.keySet())
    {
      System.out.print("Key: " + rowItem + " - ");
      HashMap<String, Rating> row = knnMatrix.get(rowItem);
      for (String item : row.keySet())
      {
        Rating rate = row.get(item);
        System.out.print(" " + item + "(" + rate.getRate() + ") ");
      }
      System.out.println();
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
      estimatedRating = weightedRatingSum/similaritySum;
    return estimatedRating;
  }
}
