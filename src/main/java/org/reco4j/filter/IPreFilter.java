/*
 * IPreFilter.java
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
package org.reco4j.filter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public interface IPreFilter
{
  public void setGraph(IGraph graph);
  public ConcurrentHashMap<Long, INode> getItemNodesMap();
  public ConcurrentHashMap<Long, INode> getUserNodesMap();
  public List<IEdge> getRatingList();
  public FastIDSet getCommonNodeIds(INode item);
  public ConcurrentHashMap<Long, INode> getCommonNodes(INode item);
  public FastIDSet getItemNodesId();
}
