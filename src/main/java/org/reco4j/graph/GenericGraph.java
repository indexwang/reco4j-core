/*
 * GenericGraph.java
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
import java.util.Properties;

/**
 *
 * @author ale
 */
public class GenericGraph implements IGraph
{

  @Override
  public void setProperties(Properties properties)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void loadGraph()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<INode> getNeighbours(List<IEdgeType> edgesType)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<INode> getNodesByInEdge(IEdgeType edgesType)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<INode> getNodesByType(String type)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addEdge(INode x, INode y, IEdgeType similarityEdgeType, String propertyName, String value)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setEdgeProperty(IEdge edge, String propertyName, String value)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<IEdge> getEdgesByType(IEdgeType edgesType)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public HashMap<String, INode> getNodesMapByType(String type, String identifier)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void getNodesByType(String type, IGraphCallable<INode> callback)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
