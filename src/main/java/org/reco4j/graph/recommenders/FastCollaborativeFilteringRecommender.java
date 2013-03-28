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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.reco4j.graph.*;
import org.reco4j.graph.similarity.ISimilarity;
import org.reco4j.graph.similarity.SimilarityFactory;
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

  public FastCollaborativeFilteringRecommender(ICollaborativeFilteringRecommenderConfig config)
  {
    super(config);
    knn = new FastByIDMap<FastByIDMap<Rating>>();
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    setLearningDataSet(learningDataSet);
    createKNN();
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    setLearningDataSet(learningDataSet);
    loadKNN();
  }

  @Override
  public void updateRecommender(IGraph learningDataSet)
  {
    //Questo metodo insieme a quello di store consentono di storicizzare e quindi aggiornare 
    //il motore delle raccomandazioni
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Rating> recommend(final INode userNode)
  {
    final ArrayList<Rating> recommendations = new ArrayList<Rating>();
    learningDataSet.getNodesByType(getConfig().getItemType(),
                                   new IGraphCallable<INode>()
    {
      @Override
      public void call(INode item)
      {
        if (item.isConnected(userNode, rankEdgeType))
          return;
        double estimatedRating = estimateRating(userNode, item);
        Utility.orderedInsert(recommendations, estimatedRating, item);
      }
    });

    ArrayList<Rating> result = Utility.cutList(recommendations,
                                               getConfig().getRecoNumber());
    return result;
  }

  private void createKNN()
  {
    TimeReportUtility timeReport = new TimeReportUtility("createKNN");

    FastIDSet nodes = learningDataSet.getNodesIdByType(getConfig().getItemType());
    for (long id : nodes)
    {
      //Inserire il multithread
      timeReport.start();
      FastByIDMap<Rating> knnRow = getKnnRow(id);
      foundNearestNeighbour(id, rankEdgeType, knnRow);
      //printKnnRow(id);
      timeReport.stop();
    }
    timeReport.printStatistics();
  }

  private void foundNearestNeighbour(long itemId, final IEdgeType edgeType, FastByIDMap<Rating> knnRow)
  {
    foundNearestNeighbour(itemId, edgeType, knnRow, false);
  }

  private void foundNearestNeighbour(long itemId, final IEdgeType edgeType, final FastByIDMap<Rating> knnRow, final boolean rewrite)
  {
    final INode item = learningDataSet.getItemNodeById(itemId);


    FastIDSet nodes = item.getCommonNodeIds(edgeType);
    logger.log(Level.INFO, "foundNearestNeighbour: {0}, CommonNodes Size: " + nodes.size(), item.getProperty(getConfig().getItemIdentifierName()));
    for (long otherItemId : nodes)
    {
      if (!rewrite && knnRow.get(otherItemId) != null)
        continue;
      if (itemId == otherItemId)
        continue;
      INode otherItem = learningDataSet.getItemNodeById(otherItemId);
      double similarityValue = calculateSimilarity(item, otherItem, edgeType);
      if (similarityValue > 0)
      {
        knnRow.put(otherItemId, new Rating(otherItem, similarityValue));
        if (!rewrite)
          getKnnRow(otherItemId).put(itemId, new Rating(item, similarityValue));
      }
    }
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
      FastByIDMap<Rating> recommendationsSource = getKnnRow(Long.parseLong(sourceItemId));
      FastByIDMap<Rating> recommendationsDestination = getKnnRow(Long.parseLong(destinationItemId));
      String similarityValueStr = alreadyCalulatedEdge.getPermissiveProperty(similarityFunction.getClass().getName());
      if (similarityValueStr == null)
        continue;
      double similarityValue = Double.parseDouble(similarityValueStr);
      if (similarityValue > 0)
      {
        Rating rateSource = new Rating(alreadyCalulatedEdge.getSource(), similarityValue);
        Rating rateDestination = new Rating(alreadyCalulatedEdge.getDestination(), similarityValue);
        recommendationsSource.put(Long.parseLong(destinationItemId), rateDestination);
        recommendationsDestination.put(Long.parseLong(sourceItemId), rateSource);
      }
    }
    printKNN(knn);
  }

  private void printKNN(FastByIDMap<FastByIDMap<Rating>> knnMatrix)
  {
    final LongPrimitiveIterator rowKeySetIterator = knnMatrix.keySetIterator();
    while (rowKeySetIterator.hasNext())
    {
      Long rowItem = rowKeySetIterator.nextLong();
      System.out.print("Key: " + rowItem + " - ");
      FastByIDMap<Rating> row = knnMatrix.get(rowItem);
      final LongPrimitiveIterator columnKeySetIterator = row.keySetIterator();
      while (columnKeySetIterator.hasNext())
      {
        Long item = columnKeySetIterator.next();
        Rating rate = row.get(item);
        System.out.print(" " + item + "(" + rate.getRate() + ") ");
      }
      System.out.println();
    }
  }

  @Override
  protected double calculateEstimatedRating(INode item, INode user, IEdgeType rankType, String propertyName) throws RuntimeException
  {

    double estimatedRating = 0.0;
    double similaritySum = 0.0;
    double weightedRatingSum = 0.0;
    if (!knn.containsKey(item.getId()))
      return 0.0;
    FastByIDMap<Rating> rowItem = knn.get(item.getId());
    final LongPrimitiveIterator rowKeySetIterator = rowItem.keySetIterator();
    while (rowKeySetIterator.hasNext())
    {
      Rating rate = rowItem.get(rowKeySetIterator.next());
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
    if (newEdge.getProperty(getConfig().getEdgeRankValueName()) == null)
      return;
    INode dest = newEdge.getDestination();
    dest.iterateOnCommonNodes(rankEdgeType, new IGraphCallable<INode>()
    {
      @Override
      public void call(INode item)
      {
        String itemId = item.getProperty(getConfig().getItemIdentifierName());
        if (itemId == null)
          throw new RuntimeException("Items don't have the 'id' property!");
        FastByIDMap<Rating> knnRow = getKnnRow(Long.parseLong(itemId));
        foundNearestNeighbour(item.getId(), rankEdgeType, knnRow, true);
      }
    });
  }

  private void printKnnRow(long itemId)
  {
    FastByIDMap<Rating> knnRow = getKnnRow(itemId);
    final LongPrimitiveIterator columnKeySetIterator = knnRow.keySetIterator();
    while (columnKeySetIterator.hasNext())
    {
      Long item = columnKeySetIterator.next();
      Rating rate = knnRow.get(item);
      logger.log(Level.INFO, " {0}({1}) ", new Object[]
      {
        rate.getItem().getProperty(getConfig().getItemIdentifierName()), rate.getRate()
      });
    }
    System.out.println();
  }
}
