/*
 * ItemFullScanUserRecommender.java
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
package org.reco4j.recommender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.util.Utility;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class ItemFullScanUserRecommender
        implements IUserRecommender
{

  private int recoNumber;
  private IPredictor predictor;
  protected IEdgeType rankEdgeType;
  private Collection<INode> items;

  public ItemFullScanUserRecommender(int recoNumber, IPredictor predictor, IEdgeType rankEdgeType, Collection<INode> items)
  {
    this.recoNumber = recoNumber;
    this.predictor = predictor;
    this.rankEdgeType = rankEdgeType;
    this.items = items;
  }

  @Override
  public List<Rating> userRecommend(INode user)
  {

    ArrayList<Rating> recommendations = new ArrayList<Rating>();

    for (INode item : items) // learningDataSet.getNodesByType(getConfig().getItemType()))
    {
      if (item.isConnected(user, rankEdgeType))
        continue;
      double estimatedRating = predictor.predictRating(user, item);
      Utility.orderedInsert(recommendations, estimatedRating, item, recoNumber);
    }
    return recommendations;
  }
}
