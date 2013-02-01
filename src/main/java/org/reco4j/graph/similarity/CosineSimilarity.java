/*
 * CosineSimilarity.java
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
package org.reco4j.graph.similarity;

import java.util.HashMap;
import java.util.List;
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.util.RecommenderPropertiesHandle;
import org.reco4j.util.Utility;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class CosineSimilarity implements ISimilarity
{
  private static CosineSimilarity theInstance = new CosineSimilarity();

  private CosineSimilarity()
  {
  }

  public static CosineSimilarity getInstance()
  {
    return theInstance;
  }

  @Override
  public double getSimilarity(INode x, INode y, IEdgeType edgeType, IGraph dataSet)
  {
    HashMap<String, INode> totalUserMap = new HashMap<String, INode>();
    HashMap<String, INode> totalXUserMap = new HashMap<String, INode>();
    HashMap<String, INode> totalYUserMap = new HashMap<String, INode>();
    List<IEdge> xInEdge = x.getInEdge(edgeType);

    for (IEdge edge : xInEdge)
    {
      Utility.insertUserOnce(totalUserMap, edge.getSource());
      Utility.insertUserOnce(totalXUserMap, edge.getSource());
    }

    List<IEdge> yInEdge = y.getInEdge(edgeType);
    for (IEdge edge : yInEdge)
    {
      Utility.insertUserOnce(totalUserMap, edge.getSource());
      Utility.insertUserOnce(totalYUserMap, edge.getSource());
    }

    int userTotalNumber = totalUserMap.size();
    int[] xVector = new int[userTotalNumber];
    int[] yVector = new int[userTotalNumber];

    int pos = 0;
    for (INode user : totalUserMap.values())
    {
      if (totalXUserMap.containsKey(user.getProperty(RecommenderPropertiesHandle.getInstance().getUserIdentifierName())))
        xVector[pos] = 1;
      else
        xVector[pos] = 0;

      if (totalYUserMap.containsKey(user.getProperty(RecommenderPropertiesHandle.getInstance().getUserIdentifierName())))
        yVector[pos] = 1;
      else
        yVector[pos] = 0;
      pos++;
    }
    return calculateCosineValue(xVector, yVector);
  }

  private double calculateCosineValue(int[] xVector, int[] yVector)
  {
    double a = (double) getDotProduct(xVector, yVector);
    double b = getNorm(xVector) * getNorm(yVector);
    
    if (b > 0)
      return a / b;
    else
      return 0;
  }

  private int getDotProduct(int[] xVector, int[] yVector)
  {
    int sum = 0;
    for (int i = 0, n = xVector.length; i < n; i++)
    {
      sum += xVector[i] * yVector[i];
    }
    return sum;
  }

  private double getNorm(int[] xVector)
  {
    int sum = 0;
    for (int i = 0, n = xVector.length; i < n; i++)
    {
      sum += xVector[i] * xVector[i];
    }
    return Math.sqrt(sum);
  }
}
