/*
 * ICollaborativeFilteringRecommenderConfig.java
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
package org.reco4j.recommender.knn;

import org.reco4j.similarity.ISimilarityConfig;
import org.reco4j.util.IRecommenderConfig;

/**
 * This interface is valid for both CollaborativeFilteringRecommender and
 * FastCollaborativeFilteringRecommender.
 * 
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public interface ICollaborativeFilteringRecommenderConfig
  extends IRecommenderConfig
{
  @Override
  public String getItemType();

  @Override
  public String getItemIdentifierName();

  @Override
  public String getEdgeRankValueName();

  public ISimilarityConfig getSimilarityConfig();

  public int getRecoNumber();

  public boolean getRecalculateSimilarity();
}
