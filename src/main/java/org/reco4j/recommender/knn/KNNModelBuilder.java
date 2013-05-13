/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender.knn;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import org.reco4j.similarity.ISimilarity;
import org.reco4j.model.IModelBuilder;
import org.reco4j.util.TimeReportUtility;

/**
 *
 * @author giuri
 */
public class KNNModelBuilder
  implements IModelBuilder<KNNModel>
{
  private static final Logger logger = Logger.getLogger(KNNModelBuilder.class.getName());
  //
  private UserItemDataset userItemDataset;
  //
  private String itemIdentifierNodePropertyName;
  private IEdgeType rankEdgeType;
  private ISimilarity similarityFunction;
  private IEdgeType similarityEdgeType;
  private boolean recalculateSimilarity;
  //
  private KNNModel model;

  public KNNModelBuilder(UserItemDataset userItemDataset, String itemIdentifierNodePropertyName, IEdgeType rankEdgeType, ISimilarity similarityFunction, IEdgeType similarityEdgeType, boolean recalculateSimilarity)
  {
    this.userItemDataset = userItemDataset;
    this.itemIdentifierNodePropertyName = itemIdentifierNodePropertyName;
    this.rankEdgeType = rankEdgeType;
    this.similarityFunction = similarityFunction;
    this.similarityEdgeType = similarityEdgeType;
    this.recalculateSimilarity = recalculateSimilarity;
    model = new KNNModel(itemIdentifierNodePropertyName);
  }
  

  public void setModelName(String modelName)
  {
    this.model.setName(modelName);
  }  
  
  @Override
  public KNNModel build()
  {
    createKNN(userItemDataset.getItemList());
    return this.model;
  }

  private void createKNN(ConcurrentHashMap<Long, INode> itemList)
  {
    TimeReportUtility timeReport = new TimeReportUtility("createKNN");


    for (INode item : itemList.values())
    {
      timeReport.start();
      String itemId = item.getProperty(itemIdentifierNodePropertyName);
      if (itemId == null)
        throw new RuntimeException("Items don't have the 'id' property!");
      HashMap<String, Rating> knnRow = model.getKnnRow(itemId);
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
    ConcurrentHashMap<Long, INode> nodesByInEdge = userItemDataset.getCommonNodes(item, itemIdentifierNodePropertyName);
    logger.log(Level.INFO, "foundNearestNeighbour: {0}, CommonNodes Size: " + nodesByInEdge.size(), item.getProperty(itemIdentifierNodePropertyName));
    String itemId = item.getProperty(itemIdentifierNodePropertyName);
    for (INode otherItem : nodesByInEdge.values())
    {
      String otherItemId = otherItem.getProperty(itemIdentifierNodePropertyName);
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
          HashMap<String, Rating> otherKnnRow = model.getKnnRow(otherItemId);
          otherKnnRow.put(itemId, new Rating(item, similarityValue));
        }
      }
    }
    /*System.out.print(itemId + ":> ");
     for (String key : knnRow.keySet())
     System.out.print(key + "["+ knnRow.get(key).getRate() +"] ");
     System.out.println();*/
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
        double value = Double.parseDouble(alreadyCalulatedEdge.getProperty(similarityFunction.getClass().getName()));
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

  @Override
  public void update(IEdge newEdge, int operation)
  {
    INode dest = newEdge.getDestination();  
    findNearestNeighbour(dest, rankEdgeType, model.getKnnRow(dest.getProperty(itemIdentifierNodePropertyName)), true);
    ConcurrentHashMap<Long, INode> commonNodes = userItemDataset.getCommonNodes(dest, itemIdentifierNodePropertyName);
    for (INode item : commonNodes.values())
    {
      String itemId = item.getProperty(itemIdentifierNodePropertyName);
      if (itemId == null)
        throw new RuntimeException("Items don't have the 'id' property!");
      HashMap<String, Rating> knnRow = model.getKnnRow(itemId);
      findNearestNeighbour(item, rankEdgeType, knnRow, true);
    }
  }

  @Override
  public void setModel(KNNModel model)
  {
    this.model = model;
  }
}
