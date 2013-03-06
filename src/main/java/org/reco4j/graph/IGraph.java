/*
 * IGraph.java
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
package org.reco4j.graph;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

/**
 * This interface represents a Graph. Subclass of this instance can use different
 * mechanism to handle the graph data (for instance: neo4j, infiniteGraph, dbms).
 * Also the rank is represented as a link between the party and the object ranked
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public interface IGraph
{
  /**
   * This method return a list of node's neighbour connected by an edge of the specified edge types
   * @param edgesType the list of edge of types
   * @return A list of Neighbour
   */
  public void addEdge(INode x, INode y, IEdgeType similarityEdgeType, String propertyName, String value);
  public void setEdgeProperty(IEdge edge, String propertyName, String value);
  public List<INode> getNeighbours(List<IEdgeType> edgesType);
  public List<INode> getNodesByInEdge(IEdgeType edgesType);
  public List<INode> getNodesByType(String type);
  public void getNodesByType(String type, IGraphCallable<INode> callback);
  public ConcurrentHashMap<Long, INode> getNodesMapByType(String type);
  public List<IEdge> getEdgesByType(IEdgeType edgesType);
  public void setProperties(Properties properties);
  public void loadGraph();  
  public INode getUserNodeById(long id);
  public INode getItemNodeById(long id);
  public INode getNodeById(long id);
  public int getNodesNumberByType(String type);
  public FastIDSet getEdgesIdByType(IEdgeType edgeType);
  public FastIDSet getNodesIdByType(String type);
}
