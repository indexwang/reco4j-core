/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.graph.UserItemDataset;
import org.reco4j.graph.similarity.ISimilarity;
import org.reco4j.util.TimeReportUtility;

/**
 *
 * @author giuri
 */
public class KNNFastModelBuilder
  implements IModelBuilder<KNNFastModel> 
{
  private static final Logger logger = Logger.getLogger(KNNFastModelBuilder.class.getName());
  //
  private UserItemDataset userItemDataset;
  //
  private String itemIdentifierNodePropertyName;
  private IEdgeType rankEdgeType;
  private ISimilarity similarityFunction;
  private IEdgeType similarityEdgeType;
  private boolean recalculateSimilarity;
  //
  private KNNFastModel model = new KNNFastModel();

  public KNNFastModelBuilder(UserItemDataset userItemDataset, String itemIdentifierNodePropertyName, IEdgeType rankEdgeType, ISimilarity similarityFunction, IEdgeType similarityEdgeType, boolean recalculateSimilarity)
  {
    this.userItemDataset = userItemDataset;
    this.itemIdentifierNodePropertyName = itemIdentifierNodePropertyName;
    this.rankEdgeType = rankEdgeType;
    this.similarityFunction = similarityFunction;
    this.similarityEdgeType = similarityEdgeType;
    this.recalculateSimilarity = recalculateSimilarity;
  }
  public void setModelName(String name)
  {
    this.model.setName(name);
  }
  
  @Override
  public KNNFastModel build()
  {
    System.out.println("Model name: " + model.getName());
    createKNN(userItemDataset.getItemIdList());
    return this.model;
  }

  private void createKNN(FastIDSet nodes)
  {
    TimeReportUtility timeReport = new TimeReportUtility("createKNN");

    for (long id : nodes)  
    {
      timeReport.start();
      FastByIDMap<Rating> knnRow = model.getKnnRow(id);
      findNearestNeighbour(id, rankEdgeType, knnRow);
      timeReport.stop();
    }
    timeReport.printStatistics();
  }

  private void findNearestNeighbour(long itemId, final IEdgeType edgeType, FastByIDMap<Rating> knnRow)
  {
    findNearestNeighbour(itemId, edgeType, knnRow, false);
  }

  private void findNearestNeighbour(long itemId, final IEdgeType edgeType, final FastByIDMap<Rating> knnRow, final boolean rewrite)
  {
    final INode item = userItemDataset.getItemNodeById(itemId);
    FastIDSet nodes = userItemDataset.getCommonNodeIds(item);
    logger.log(Level.INFO, "foundNearestNeighbour: {0}, CommonNodes Size: " + nodes.size(), item.getProperty(itemIdentifierNodePropertyName));
    for (long otherItemId : nodes)
    {
      if (!rewrite && knnRow.get(otherItemId) != null)
        continue;
      if (itemId == otherItemId)
        continue;
      INode otherItem = userItemDataset.getItemNodeById(otherItemId);
      double similarityValue = calculateSimilarity(item, otherItem, edgeType);
      if (similarityValue > 0)
      {
        knnRow.put(otherItemId, new Rating(otherItem, similarityValue));
        if (!rewrite)
        {
          FastByIDMap<Rating> otherKnnRow = model.getKnnRow(otherItemId);
          otherKnnRow.put(itemId, new Rating(item, similarityValue));
        }
      }
    }
  }

  private double calculateSimilarity(INode item, INode otherItem, IEdgeType rankEdgeType)
  {
//    IEdgeType similarityEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_SIMILARITY, getConfig().getGraphConfig());
    IEdge alreadyCalulatedEdge = null;
//    getConfig().getRecalculateSimilarity();
    if (!recalculateSimilarity)
    {
      alreadyCalulatedEdge = item.getEdge(otherItem, similarityEdgeType);
      if (alreadyCalulatedEdge != null
          && alreadyCalulatedEdge.getPermissiveProperty(model.getName()) != null)
      {
        double value = Double.parseDouble(alreadyCalulatedEdge.getProperty(model.getName()));
        return value;
      }
    }

    double similarityValue = similarityFunction.getSimilarity(item, otherItem, rankEdgeType);
    //Introdurre una coda di valori da inserire per toglierla dal processo di calcolo
    if (!recalculateSimilarity)
    {
      if (alreadyCalulatedEdge != null)
        alreadyCalulatedEdge.setProperty(model.getName(), Double.toString(similarityValue));
      else
        item.addOutEdgeWithProperty(similarityEdgeType, otherItem, model.getName(), Double.toString(similarityValue));
    }
    return similarityValue;
  }
}
