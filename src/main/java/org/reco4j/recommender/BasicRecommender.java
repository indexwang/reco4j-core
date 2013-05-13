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
package org.reco4j.recommender;

import org.reco4j.graph.EdgeTypeFactory;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.util.IRecommenderConfig;

/**
 *
 * This class is the class that implements some basic method of the recommender.
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public abstract class BasicRecommender<TConfig extends IRecommenderConfig>
  implements IRecommender<TConfig>
{
  protected IEdgeType rankEdgeType;
  private TConfig config;
  protected String modelName;
  
  @Override
  public void setModelName(String modelName)
  {
    this.modelName = modelName;
  }
    
  public BasicRecommender(TConfig config)
  {
    this.config = config;
    rankEdgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, config.getGraphConfig());
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    //do nothing
  }

  @Override
  public void updateRecommender(IEdge newEdge, int operation)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * @return the config
   */
  @Override
  public TConfig getConfig()
  {
    return config;
  }
}
