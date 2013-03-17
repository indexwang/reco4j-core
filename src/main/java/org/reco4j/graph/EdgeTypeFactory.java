/*
 * EdgeTypeFactory.java
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

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class EdgeTypeFactory
{
  public static IEdgeType getEdgeType(int edgeType, IGraphConfig graphConfig)
  {
    switch (edgeType)
    {
      case IEdgeType.EDGE_TYPE_RANK:
        return new EdgeTypeRank(graphConfig);
      case IEdgeType.EDGE_TYPE_TEST_RANK:
        return new EdgeTypeTestRank(graphConfig);
      case IEdgeType.EDGE_TYPE_SIMILARITY:
        return new EdgeTypeSimilarity(graphConfig);
      case IEdgeType.EDGE_TYPE_ESTIMATED_RATING:
        return new EdgeTypeEstimatedRating(graphConfig);
      default:
        throw new RuntimeException("Edge Type Not supported yet!");
    }
  }
}
