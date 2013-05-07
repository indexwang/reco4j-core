/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.filter;

import java.util.concurrent.ConcurrentHashMap;
import org.reco4j.graph.INode;

/**
 *
 * @author ale
 */
public interface IPostFilter
{
  public ConcurrentHashMap<Long, INode> getItemNodesMap();  
}
