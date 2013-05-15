/*
 * UserItemDataset.java
 * 
 * Copyright (C) 2013 Alessandro Negro <alessandro.negro at reco4j.org>
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
package org.reco4j.dataset;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.model.NodeRatingStatistics;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class UserItemDataset
{

  protected IGraph graph;
  protected String itemType;
  protected String userType;
  //
  protected IEdgeType ratingEdgeType;
  protected String ratingValueEdgePropertyName;
  //
  protected ConcurrentHashMap<Long, INode> itemList;
  protected ConcurrentHashMap<Long, INode> userList;
  protected FastIDSet itemIdList;
  protected List<IEdge> ratingList;
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
  
  public ConcurrentHashMap<Long, INode> getItemListForRecommendation()
  {
    if (itemList == null)
      itemList = graph.getNodesMapByType(itemType);
      
    return itemList;
  }

  /**
   * @return the userList
   */
  public ConcurrentHashMap<Long, INode> getUserListForRecommendation()
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
    if (itemIdList == null)
      itemIdList = graph.getNodesIdByType(itemType);
    
    return itemIdList;
  }

  public INode getItemNodeById(long itemId)
  {
    return graph.getItemNodeById(itemId);
  }

  public FastIDSet getCommonNodeIds(INode item)
  {
    return item.getCommonNodeIds(ratingEdgeType);
  }
  
  public ConcurrentHashMap<Long, INode> getCommonNodes(INode item, String identifier)
  {
    return item.getCommonNodes(ratingEdgeType, identifier);
  }
}
