/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.filter;

import java.util.concurrent.ConcurrentHashMap;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;

/**
 *
 * @author ale
 */
public interface IPostFilter
{
  public void setGraph(IGraph graph);
  public ConcurrentHashMap<Long, INode> getItemNodesMap();  
}
