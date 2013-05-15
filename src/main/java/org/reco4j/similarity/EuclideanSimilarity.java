/*
 * EuclideanSimilarity.java
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
public class EuclideanSimilarity 
  extends BasicSimilarity<IEuclideanSimilarityConfig>
{
  public EuclideanSimilarity(IEuclideanSimilarityConfig config)
  {
    super(config);
  }
  
  @Override
  public double getSimilarity(INode x, INode y, IEdgeType edgeType)
  {
    
    int commonItems = 0;
    double sim = 0.0;
    List<IEdge> inEdges = x.getInEdge(edgeType);
    
    for (IEdge edge : inEdges)
    {
      IEdge otherRating = edge.getDestination().getEdge(y, edgeType);
      if (otherRating != null)
      {
        commonItems++;
        String propertyName = getConfig().getEdgeRankValueName();
        double edgeRating = Double.parseDouble(edge.getProperty(propertyName));
        double otherEdgeRating = Double.parseDouble(otherRating.getProperty(propertyName));
        sim += Math.pow(edgeRating - otherEdgeRating, 2);
      }
    }

    if (commonItems > 0)
    {
      sim = Math.sqrt(sim/(double)commonItems);
      sim = 1.0 - Math.tanh(sim);
    }    
    return sim;
  }
}
