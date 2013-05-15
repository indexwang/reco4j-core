/*
 * RecommenderEngine.java
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
package org.reco4j.engine;

import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reco4j.graph.IGraph;
import org.reco4j.recommender.IRecommender;
import org.reco4j.recommender.RecommendersFactory;
import org.reco4j.util.RecommenderPropertiesHandle;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class RecommenderEngine
{
  private static final Logger logger = Logger.getLogger(RecommenderEngine.class.getName());
  public static IRecommender buildRecommender(IGraph learningDataSet, Properties properties)
  {
    IRecommender recommender = createRecommender(properties);
    logger.log(Level.INFO, "Build recommendre start: {0}", new Timestamp(System.currentTimeMillis()));
    recommender.buildRecommender(learningDataSet);
    logger.log(Level.INFO, "Build recommendre end: {0}", new Timestamp(System.currentTimeMillis()));
    return recommender;
  }

  public static IRecommender loadRecommender(IGraph learningDataSet, Properties properties)
  {
    IRecommender recommender = createRecommender(properties);
    logger.log(Level.INFO, "Load recommender start: {0}", new Timestamp(System.currentTimeMillis()));
    recommender.loadRecommender(learningDataSet);
    logger.log(Level.INFO, "Load recommender end: {0}", new Timestamp(System.currentTimeMillis()));
    return recommender;
  }

  public static IRecommender createRecommender(Properties properties)
  {
    RecommenderPropertiesHandle.getInstance().setProperties(properties);
    IRecommender recommender = RecommendersFactory.getRecommender(RecommenderPropertiesHandle.getInstance());
    return recommender;
  }
}
