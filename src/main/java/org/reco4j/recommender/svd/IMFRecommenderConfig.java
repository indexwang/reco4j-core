/*
 * IMFRecommenderConfig.java
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
package org.reco4j.recommender.svd;

import org.reco4j.util.IRecommenderConfig;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public interface IMFRecommenderConfig
  extends IRecommenderConfig
{
  @Override
  public String getUserType();

  @Override
  public String getItemType();

  @Override
  public String getUserIdentifierName();

  @Override
  public String getEdgeRankValueName();

  public int getRecoNumber();

  public int getMaxFeatures();

  public double getFeatureInitValue();
}
