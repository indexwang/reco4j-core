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
 * @author ale
 */
public class FilteredUserItemDataset extends UserItemDataset
{
  /**
   * @return the itemList
   */
  @Override
  public ConcurrentHashMap<Long, INode> getItemList()
  {
    if (itemList == null)
      itemList = graph.getNodesMapByType(itemType);
      
    return itemList;
  }

  /**
   * @return the userList
   */
  @Override
  public ConcurrentHashMap<Long, INode> getUserList()
  {
    if (userList == null)
      userList = graph.getNodesMapByType(userType);
    
    return userList;
  }

  /**
   * @return the ratingList
   */
  @Override
  public List<IEdge> getRatingList()
  {
    if (ratingList == null)
      ratingList = graph.getEdgesByType(ratingEdgeType);
    
    return ratingList;
  }

  @Override
  public IEdge getRatingEdge(INode user, INode item)
  {
    return user.getEdge(item, ratingEdgeType);
  }

  @Override
  public double getRating(IEdge rating) throws NumberFormatException
  {
    final String propertyValue = rating.getProperty(ratingValueEdgePropertyName);
    if (propertyValue == null)
      throw new RuntimeException("Properties : " + ratingValueEdgePropertyName + " not found on edge of id: " + rating.getId());
    double realValue = Double.parseDouble(propertyValue);
    return realValue;
  }

  @Override
  public FastIDSet getItemIdList()
  {
    return graph.getNodesIdByType(itemType);
  }

  @Override
  public INode getItemNodeById(long itemId)
  {
    return graph.getItemNodeById(itemId);
  }

  @Override
  public FastIDSet getCommonNodeIds(INode item)
  {
    return item.getCommonNodeIds(ratingEdgeType);
  }
}
