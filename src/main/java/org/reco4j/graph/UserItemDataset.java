/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

/**
 *
 * @author giuri
 */
public class UserItemDataset
{

  private IGraph graph;
  private String itemType;
  private String userType;
  //
  private IEdgeType ratingEdgeType;
  private String ratingValueEdgePropertyName;
  //
  private ConcurrentHashMap<Long, INode> itemList;
  private ConcurrentHashMap<Long, INode> userList;
  private List<IEdge> ratingList;
  //

  public void init(IGraph graph, String itemType, String userType, IEdgeType ratingEdgeType, String ratingValueEdgePropertyName)
  {
    this.graph = graph;
    this.itemType = itemType;
    this.userType = userType;
    this.ratingEdgeType = ratingEdgeType;
    this.ratingValueEdgePropertyName = ratingValueEdgePropertyName;
  }

  private void initRatingStatistics()
  {
    initNodeListRatingStatistics(getUserList());
    initNodeListRatingStatistics(getItemList());
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
    initRatingStatistics();
    
    for (IEdge rating : getRatingList())
    {
      double realValue = getRating(rating);

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
    if (itemList == null)
      itemList = graph.getNodesMapByType(itemType);
      
    return itemList;
  }

  /**
   * @return the userList
   */
  public ConcurrentHashMap<Long, INode> getUserList()
  {
    if (userList == null)
      userList = graph.getNodesMapByType(userType);
    
    return userList;
  }

  /**
   * @return the ratingList
   */
  public List<IEdge> getRatingList()
  {
    if (ratingList == null)
      ratingList = graph.getEdgesByType(ratingEdgeType);
    
    return ratingList;
  }

  public IEdge getRatingEdge(INode user, INode item)
  {
    return user.getEdge(item, ratingEdgeType);
  }

  public double getRating(IEdge rating) throws NumberFormatException
  {
    final String propertyValue = rating.getProperty(ratingValueEdgePropertyName);
    if (propertyValue == null)
      throw new RuntimeException("Properties : " + ratingValueEdgePropertyName + " not found on edge of id: " + rating.getId());
    double realValue = Double.parseDouble(propertyValue);
    return realValue;
  }

  public FastIDSet getItemIdList()
  {
    return graph.getNodesIdByType(itemType);
  }

  public INode getItemNodeById(long itemId)
  {
    return graph.getItemNodeById(itemId);
  }
}
