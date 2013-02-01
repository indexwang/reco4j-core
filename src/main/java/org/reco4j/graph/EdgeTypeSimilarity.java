/*
 * EdgeTypeSimilarity.java
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

import org.reco4j.util.RecommenderPropertiesHandle;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class EdgeTypeSimilarity implements IEdgeType
{

  @Override
  public int getType()
  {
    return IEdgeType.EDGE_TYPE_SIMILARITY;
  }

  @Override
  public String getEdgeName()
  {
    return RecommenderPropertiesHandle.getInstance().getEdgeSimilarityName();
  }
  
}
