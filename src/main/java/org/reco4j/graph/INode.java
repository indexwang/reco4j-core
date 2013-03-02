/*
 * INode.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public interface INode
{
  public long getId();
  public void setProperty(String name, String value);
  public String getProperty(String name);
  public Boolean isConnected(INode node, IEdgeType edgeType);
  public Boolean isConnected(INode node, List<IEdgeType> edgeTypes);
  public Boolean isConnectedIn(INode node, List<IEdgeType> edgeTypes);
  public Boolean isConnectedOut(INode node, List<IEdgeType> edgeTypes);
  public IEdge getEdge(INode node, IEdgeType edgeType);
  public List<IEdge> getInEdge(IEdgeType edgeType);
  public List<IEdge> getOutEdge(IEdgeType edgeType);
  public void iterateOnEdge(IEdgeType edgeType, IGraphCallable<IEdge> callback);
  public HashMap<String, INode> getCommonNodes(IEdgeType edgeType, String identifier);
  public FastIDSet getCommonNodeIds(IEdgeType edgeType);
  public void iterateOnCommonNodes(IEdgeType edgeType, IGraphCallable<INode> callback);
  public int getInEdgeNumber(IEdgeType edgeType);
  public Object getExtendedInfos();
  public void setExtendedInfos(Object infos);
  public ArrayList<Rating> getRatingsFromUser();
  public ArrayList<Rating> getRatingsForItem();
}
