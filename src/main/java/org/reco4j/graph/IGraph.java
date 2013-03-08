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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

/**
 * This interface represents a Graph. Subclass of this instance can use different
 * mechanism to handle the graph data (for instance: neo4j, infiniteGraph, dbms).
 * Also the rank is represented as a link between the party and the object ranked
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public interface IGraph
{
  public void setProperties(Properties properties);
  
  public INode addNode(Map<String, String> properties);
  public void setNodeProperty(INode node, String propertyName, String value);
  
  public void addEdge(INode x, INode y, IEdgeType similarityEdgeType, String propertyName, String value);
  public void setEdgeProperty(IEdge edge, String propertyName, String value);
  
  public List<INode> getNodesByInEdge(IEdgeType edgesType);
  public List<INode> getNodesByType(String type);
  public void getNodesByType(String type, IGraphCallable<INode> callback);
  public HashMap<String, INode> getNodesMapByType(String type, String identifier);
  
  public List<IEdge> getEdgesByType(IEdgeType edgesType);
  
  public INode getUserNodeById(long id);
  public INode getItemNodeById(long id);
  public INode getNodeById(long id);
  
  public int getNodesNumberByType(String type);
  
  public FastIDSet getEdgesIdByType(IEdgeType edgeType);
  public FastIDSet getNodesIdByType(String type);
}
