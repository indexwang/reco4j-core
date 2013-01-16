/*
 * RecommenderEvaluator.java
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
import org.reco4j.util.RecommenderPropertiesHandle;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class RecommenderEvaluator
{
  public static void evaluateRecommender(IGraph testDataSet, IRecommender recommender)
  {
    int n = 0;
    double numerator = 0.0;
    List<INode> users = testDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getUserType());
    for (INode user : users)
    {
      System.out.println("User: " + user.getProperty(RecommenderPropertiesHandle.getInstance().getUserIdentifierName()));
      List<IEdge> ranks = user.getOutEdge(EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_TEST_RANK));
      if (ranks.isEmpty())
        continue;
      for (IEdge rank : ranks)
      {
        StringBuilder output = new StringBuilder();
        output.append("item: " + rank.getSource().getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName()));
        double estimatedRating = recommender.estimateRating(user, rank.getSource(), EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK),
                                                         RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());
        if (estimatedRating > 0)
        {
          n++;
          double realValue = Double.parseDouble(rank.getProperty(RecommenderPropertiesHandle.getInstance().getEdgeRankValueName()));
          double difference = realValue - estimatedRating;
          numerator = numerator + Math.abs(difference);
          output.append(" n: ").append(n).append(" numerator: ").append(numerator).append(" estimatedRating: ").append(estimatedRating).append(" realValue: ").append(realValue).append(" difference: ").append(difference);
          System.out.println(output);

        }
        System.out.flush();
      }

    }
    double mae = numerator / (double)(n);
    System.out.println("MAE: " + mae);
  }

  public static void oldevaluateRecommender(IGraph testDataSet, IRecommender reco)
  {
    int hitRecommed = 0;
    int userNumber = 0;
    List<INode> users = testDataSet.getNodesByType(RecommenderPropertiesHandle.getInstance().getUserType());
    for (INode user : users)
    {
      List<IEdge> ranks = user.getOutEdge(EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_TEST_RANK));
      if (ranks.isEmpty())
        continue;
      userNumber++;
      List<Rating> recommendtations = reco.recommend(user);
      System.out.println("Recommendation for User with code: "
                         + user.getProperty(RecommenderPropertiesHandle.getInstance().getUserIdentifierName()));
      int hit = 0;
      for (Rating rate : recommendtations)
      {
        for (IEdge edge : ranks)
        {
          String recommendedId = rate.getItem().getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
          String testRankId = edge.getSource().getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
          if (recommendedId.equalsIgnoreCase(testRankId));
          hit++;

        }


      }
      System.out.println("Hit Number for user "
                         + user.getProperty(RecommenderPropertiesHandle.getInstance().getUserIdentifierName())
                         + " = " + hit);
      if (hit > 0)
        hitRecommed++;
    }
    System.out.println("Hit percentage: " + (userNumber > 0 ? (double) hitRecommed / (double) userNumber : 0));
  }
}
