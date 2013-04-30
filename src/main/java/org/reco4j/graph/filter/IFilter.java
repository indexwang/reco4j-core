/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.filter;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;

/**
 *
 * @author ale
 */
public interface IFilter
{
  public void setGraph(IGraph graph);
  public ConcurrentHashMap<Long, INode> getItemNodesMap();
  public ConcurrentHashMap<Long, INode> getUserNodesMap();
  public List<IEdge> getRatingList();
  public FastIDSet getCommonNodeIds(INode item);
  public FastIDSet getItemNodesId();
}
