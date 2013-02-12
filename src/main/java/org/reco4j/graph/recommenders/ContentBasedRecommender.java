/*
 * ContentBasedRecommender.java
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
package org.reco4j.graph.recommenders;

import java.util.List;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class ContentBasedRecommender extends BasicRecommender
{

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateRecommender(IGraph learningDataSet)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Rating> recommend(INode node)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double estimateRating(INode user, INode source)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
