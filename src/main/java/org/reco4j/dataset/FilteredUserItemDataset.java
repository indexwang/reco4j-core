/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.dataset;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.reco4j.filter.IPostFilter;
import org.reco4j.filter.IPreFilter;
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.recommender.knn.ICollaborativeFilteringRecommenderConfig;

/**
 *
 * @author ale
 */
public class FilteredUserItemDataset extends UserItemDataset
{
  private IPreFilter preFilter;
  private IPostFilter postFilter;
  
  public void init(IGraph graph, ICollaborativeFilteringRecommenderConfig config, IPreFilter filter)
  {
    init(graph, config.getItemType(), config.getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, config.getGraphConfig()), config.getEdgeRankValueName(), filter, null);
  }
  
  public void init(IGraph graph, ICollaborativeFilteringRecommenderConfig config, IPreFilter filter, IPostFilter postFilter)
  {
    init(graph, config.getItemType(), config.getUserType(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, config.getGraphConfig()), config.getEdgeRankValueName(), filter, postFilter);
  }
  
  public void init(IGraph graph, String itemType, String userType, IEdgeType ratingEdgeType, String ratingValueEdgePropertyName, IPreFilter filter, IPostFilter postFilter)
  {
    super.init(graph, itemType, userType, ratingEdgeType, ratingValueEdgePropertyName);
    this.preFilter = filter;
    this.postFilter = postFilter;
    filter.setGraph(graph);
    if (postFilter != null)
      this.postFilter.setGraph(graph);
  }
  /**
   * @return the itemList
   */
  @Override
  public ConcurrentHashMap<Long, INode> getItemList()
  {
    if (itemList == null)
      itemList = preFilter.getItemNodesMap();
      
    return itemList;
  }

  /**
   * @return the userList
   */
  @Override
  public ConcurrentHashMap<Long, INode> getUserList()
  {
    if (userList == null)
      userList = preFilter.getUserNodesMap();
    
    return userList;
  }

  /**
   * @return the ratingList
   */
  @Override
  public List<IEdge> getRatingList()
  {
    if (ratingList == null)
      ratingList = preFilter.getRatingList();
    
    return ratingList;
  }


  @Override
  public FastIDSet getCommonNodeIds(INode item)
  {
    return preFilter.getCommonNodeIds(item);
  }
  
  @Override
  public ConcurrentHashMap<Long, INode> getCommonNodes(INode item, String identifier)
  {
    return preFilter.getCommonNodes(item);
  }
  
  
  @Override
  public FastIDSet getItemIdList()
  {
    if (itemIdList == null)
      itemIdList = preFilter.getItemNodesId();
    
    return itemIdList;
  }
  
  @Override
  public ConcurrentHashMap<Long, INode> getItemListForRecommendation()
  {
    if (itemList == null)
    {
      if (postFilter != null)
        itemList = postFilter.getItemNodesMap();
      else
        itemList = preFilter.getItemNodesMap();
    } 
    return itemList;
  }

}
