/*
 * FastCollaborativeFilteringRecommender.java
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
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.IGraphCallable;
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
public class FastCollaborativeFilteringRecommender extends CollaborativeFilteringRecommender
{
  protected static final Logger logger = Logger.getLogger(FastCollaborativeFilteringRecommender.class.getName());
  protected FastByIDMap<FastByIDMap<Rating>> knn;
  
  public FastCollaborativeFilteringRecommender()
  {
    super();
    knn = new FastByIDMap<FastByIDMap<Rating>>();
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
  public List<Rating> recommend(final INode user)
  {
    final ArrayList<Rating> recommendations = new ArrayList<Rating>();
    learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType(),
                                   new IGraphCallable<INode>()
    {
      @Override
      public void call(INode item)
      {
        if (item.isConnected(user, edgeType))
          return;
        double estimatedRating = estimateRating(user, item, edgeType, RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());
        Utility.orderedInsert(recommendations, estimatedRating, item);
      }
    });

    ArrayList<Rating> result = Utility.cutList(recommendations,
                                               RecommenderPropertiesHandle.getInstance().getRecoNumber());
    return result;
  }

  private void createKNN(final int distMethod)
  {
    TimeReportUtility timeReport = new TimeReportUtility("createKNN");
    timeReport.start();
    learningDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getItemType(),
                                   new IGraphCallable<INode>()
    {
      @Override
      public void call(INode item)
      {
        String itemId = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
        if (itemId == null || itemId.isEmpty())
          throw new RuntimeException("Items don't have the 'id' property or the content is null!");
        FastByIDMap<Rating> knnRow = getKnnRow(Long.parseLong(itemId));
        foundNearestNeighbour(item, edgeType, distMethod, knnRow);
      }
    });
    timeReport.stop();
  }

  private void foundNearestNeighbour(final INode item, final IEdgeType edgeType, final int distMethod, FastByIDMap<Rating> knnRow)
  {
    foundNearestNeighbour(item, edgeType, distMethod, knnRow, false);
  }

  private void foundNearestNeighbour(final INode item, final IEdgeType edgeType, final int distMethod, final FastByIDMap<Rating> knnRow, final boolean rewrite)
  {
    logger.log(Level.INFO, "foundNearestNeighbour: {0}", item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()));
    item.iterateOnCommonNodes(edgeType, new IGraphCallable<INode>()
    {
      @Override
      public void call(INode otherItem)
      {
        String otherItemId = otherItem.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
        //logger.log(Level.INFO, "otherItem: {0}", otherItemId);
        if (!rewrite && knnRow.get(Long.parseLong(otherItemId)) != null)
          return;
        String itemId = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
        if (itemId == null || itemId.isEmpty() || otherItemId == null || otherItemId.isEmpty())
          throw new RuntimeException("Items don't have the 'id' property!");
        if (itemId.equalsIgnoreCase(otherItemId))
          return;
        double similarityValue = calculateSimilarity(item, otherItem, edgeType, distMethod);
        if (similarityValue > 0)
        {
          knnRow.put(Long.parseLong(otherItemId), new Rating(otherItem, similarityValue));
          if (!rewrite)
          {
            FastByIDMap<Rating> otherKnnRow = getKnnRow(Long.parseLong(otherItemId));
            otherKnnRow.put(Long.parseLong(itemId), new Rating(item, similarityValue));
          }
        }
      }
    });
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

  private FastByIDMap<FastByIDMap<Rating>> loadKNN(int distanceAlgorithm)
  {
    FastByIDMap<FastByIDMap<Rating>> knnMatrix = new FastByIDMap<FastByIDMap<Rating>>();
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
      FastByIDMap<Rating> knnRow = Utility.getFastKNNRow(recommendations, RecommenderPropertiesHandle.getInstance().getKValue());
      String itemId = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
      knnMatrix.put(Long.parseLong(itemId), knnRow);
    }
    printKNN(knnMatrix);
    return knnMatrix;
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

  private void printKNN(FastByIDMap<FastByIDMap<Rating>> knnMatrix)
  {
    while (knnMatrix.keySetIterator().hasNext())
    {
      Long rowItem = knnMatrix.keySetIterator().nextLong();
      System.out.print("Key: " + rowItem + " - ");
      FastByIDMap<Rating> row = knnMatrix.get(rowItem);
      while (row.keySetIterator().hasNext())
      {
        Long item = row.keySetIterator().next();
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
    String itemIdentifierName = RecommenderPropertiesHandle.getInstance().getItemIdentifierName();
    String id = item.getProperty(itemIdentifierName);
    FastByIDMap<Rating> rowItem = knn.get(Long.parseLong(id));
    while (rowItem.keySetIterator().hasNext())
    {
      Rating rate = rowItem.get(rowItem.keySetIterator().next());
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

  private FastByIDMap<Rating> getKnnRow(long itemId)
  {
    FastByIDMap<Rating> knnRow = knn.get(itemId);
    if (knnRow == null)
    {
      knnRow = new FastByIDMap<Rating>();
      knn.put(itemId, knnRow);
    }
    return knnRow;
  }

  @Override
  public void updateRecommender(IEdge newEdge)
  {
    if (newEdge.getProperty(RecommenderPropertiesHandle.getInstance().getEdgeRankValueName()) == null)
      return;
    INode dest = newEdge.getDestination();
    dest.iterateOnCommonNodes(edgeType, new IGraphCallable<INode>()
    {
      @Override
      public void call(INode item)
      {
        String itemId = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
        if (itemId == null)
          throw new RuntimeException("Items don't have the 'id' property!");
        FastByIDMap<Rating> knnRow = getKnnRow(Long.parseLong(itemId));
        foundNearestNeighbour(item, edgeType, RecommenderPropertiesHandle.getInstance().getDistanceAlgorithm(), knnRow, true);
      }
    });
  }
}
