/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph;

import org.reco4j.graph.filter.IFilter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

/**
 *
 * @author ale
 */
public class FilteredUserItemDataset extends UserItemDataset
{
  private IFilter filter;
  
  public void init(IGraph graph, String itemType, String userType, IEdgeType ratingEdgeType, String ratingValueEdgePropertyName, IFilter filter)
  {
    this.graph = graph;
    this.itemType = itemType;
    this.userType = userType;
    this.ratingEdgeType = ratingEdgeType;
    this.ratingValueEdgePropertyName = ratingValueEdgePropertyName;
    this.filter = filter;
    filter.setGraph(graph);
  }
  /**
   * @return the itemList
   */
  @Override
  public ConcurrentHashMap<Long, INode> getItemList()
  {
    if (itemList == null)
      itemList = filter.getItemNodesMap();
      
    return itemList;
  }

  /**
   * @return the userList
   */
  @Override
  public ConcurrentHashMap<Long, INode> getUserList()
  {
    if (userList == null)
      userList = filter.getUserNodesMap();
    
    return userList;
  }

  /**
   * @return the ratingList
   */
  @Override
  public List<IEdge> getRatingList()
  {
    if (ratingList == null)
      ratingList = filter.getRatingList();
    
    return ratingList;
  }


  @Override
  public FastIDSet getCommonNodeIds(INode item)
  {
    return filter.getCommonNodeIds(item);
  }
  
  
  @Override
  public FastIDSet getItemIdList()
  {
    if (itemIdList == null)
      itemIdList = filter.getItemNodesId();
    
    return itemIdList;
  }

}
