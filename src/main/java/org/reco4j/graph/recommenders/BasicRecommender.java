/*
 * BasicRecommender.java
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
import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.session.RecommenderSessionManager;
import org.reco4j.util.RecommenderPropertiesHandle;

/**
 * 
 * This class is the class that implements some basic method of the recommender.
 * 
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public abstract class BasicRecommender implements IRecommender
{
  protected IGraph learningDataSet;
  protected IEdgeType edgeType;
  
  public BasicRecommender()
  {
    RecommenderSessionManager.getInstance().setRankValueProprertyName(
      RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());
    edgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK);
  }
  
  public IGraph getLearningDataSet()
  {
    return learningDataSet;
  }

  public void setLearningDataSet(IGraph learningDataSet)
  {
    this.learningDataSet = learningDataSet;
  }

  @Override
  public void storeRecommender()
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    //do nothing
  }

  @Override
  public void updateRecommender(IEdge newEdge)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }    
}
