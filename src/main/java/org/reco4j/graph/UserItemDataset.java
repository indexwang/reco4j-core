/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author giuri
 */
public class UserItemDataset
{

  private ConcurrentHashMap<Long, INode> itemList;
  private ConcurrentHashMap<Long, INode> userList;
  private List<IEdge> ratingList;
  //
  private String edgeRankValueName;

  public void init(IGraph graph, String itemType, String userType, IEdgeType edgeType, String edgeRankValueName)
  {
    userList = graph.getNodesMapByType(userType);
    itemList = graph.getNodesMapByType(itemType);
    ratingList = graph.getEdgesByType(edgeType);
    this.edgeRankValueName = edgeRankValueName;

    initNodeListRatingStatistics(userList);
    initNodeListRatingStatistics(itemList);
  }

  private static void initNodeListRatingStatistics(final ConcurrentHashMap<Long, INode> nodeList)
  {
    for (INode node : nodeList.values())
      initNodeRatingStatistics(node);
  }

  private static void initNodeRatingStatistics(INode node)
  {
    if (node.getExtendedInfos() == null)
      node.setExtendedInfos(new NodeRatingStatistics());
  }

  public void setNodeRatingStatistics()
  {
    for (IEdge rating : getRatingList())
    {
      double realValue = Double.parseDouble(rating.getProperty(edgeRankValueName));

      INode item = getItemList().get(rating.getDestination().getId());
      ((NodeRatingStatistics) item.getExtendedInfos()).addRating(realValue);

      INode user = getUserList().get(rating.getSource().getId());
      ((NodeRatingStatistics) user.getExtendedInfos()).addRating(realValue);
    }

    for (INode item : getItemList().values())
    {
      ((NodeRatingStatistics) item.getExtendedInfos()).setStatistics();
    }
  }

  /**
   * @return the itemList
   */
  public ConcurrentHashMap<Long, INode> getItemList()
  {
    return itemList;
  }

  /**
   * @return the userList
   */
  public ConcurrentHashMap<Long, INode> getUserList()
  {
    return userList;
  }

  /**
   * @return the ratingList
   */
  public List<IEdge> getRatingList()
  {
    return ratingList;
  }
}
