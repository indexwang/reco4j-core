/*
 * BasicNode.java
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

import org.reco4j.model.Rating;
import java.util.ArrayList;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public abstract class BasicNode implements INode
{
  private Object infos;
  @Override
  public Object getExtendedInfos()
  {
    return infos;
  }

  @Override
  public void setExtendedInfos(Object infos)
  {
    this.infos = infos;
  }
  
  @Override
  public ArrayList<Rating> getRatingsFromUser(IGraphConfig config)
  {
    final ArrayList<Rating> ratingList = new ArrayList<Rating>();
    final String edgeRankValueName = config.getEdgeRankValueName();
    final INode thisNode = this;
    this.iterateOnEdge(EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, config), new IGraphCallable<IEdge>()
    {
      @Override
      public void call(IEdge rating)
      {
        INode item = rating.getDestination();
        String value = rating.getProperty(edgeRankValueName);
        double rate = Double.parseDouble(value);
        Rating pref = new Rating(thisNode, item, rate, null);
        ratingList.add(pref);
      }
    });
    return ratingList;
  }
  
  @Override
  public ArrayList<Rating> getRatingsForItem(IGraphConfig config)
  {
    final ArrayList<Rating> ratingList = new ArrayList<Rating>();
    final String edgeRankValueName = config.getEdgeRankValueName();
    final INode thisNode = this;
    this.iterateOnEdge(EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK, config), new IGraphCallable<IEdge>()
    {
      @Override
      public void call(IEdge rating)
      {
        INode node = rating.getSource();
        String value = rating.getProperty(edgeRankValueName);
        double rate = Double.parseDouble(value);
        Rating pref = new Rating(node, thisNode, rate, null);
        ratingList.add(pref);
      }
    });
    return ratingList;
  }
}
