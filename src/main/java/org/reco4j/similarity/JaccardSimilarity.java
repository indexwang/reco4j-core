/*
 * JaccardSimilarity.java
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

package org.reco4j.similarity;

import java.util.List;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class JaccardSimilarity 
  extends BasicSimilarity<ISimilarityConfig>
{
  public JaccardSimilarity(ISimilarityConfig config)
  {
    super(config);
  }
  
  @Override
  public double getSimilarity(INode x, INode y, IEdgeType edgeType)
  {
    int commonUsers = 0;
    List<IEdge> xInEdge;
    xInEdge = x.getInEdge(edgeType);
    //TODO: Improve using the graph capabilities
    for (IEdge edge : xInEdge)
      if (edge.getSource().isConnected(y, edgeType))
        commonUsers++;
    
//    System.out.println("x: " + x.getProperty("movieId"));
//    System.out.println("y: " + y.getProperty("movieId"));
    int xInEdgeSize = xInEdge.size();
    int yInEdgeNumber = y.getInEdgeNumber(edgeType);
    
    int totalUsers = xInEdgeSize + yInEdgeNumber - commonUsers;
    double sim = 0.0;
    if (commonUsers > 0)
      sim = (double) commonUsers / (double) totalUsers;
    return sim;
  }
}
