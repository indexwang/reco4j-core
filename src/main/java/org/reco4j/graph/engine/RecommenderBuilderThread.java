/*
 * RecommenderThread.java
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
package org.reco4j.graph.engine;

import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.recommenders.IRecommender;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class RecommenderBuilderThread extends Thread
{
  private static final Logger logger = Logger.getLogger(RecommenderEngine.class.getName());
  private IGraph learningDataSet;
  private IRecommender recommender;

  public RecommenderBuilderThread(IRecommender recommender, IGraph learningDataSet)
  {
    this.learningDataSet = learningDataSet;
    this.recommender = recommender;
  }

  @Override
  public void run()
  {
    logger.log(Level.INFO, ">>>>>>>>>>>>>>>>>>>>: Load recommender start: {0}", new Timestamp(System.currentTimeMillis()));
    recommender.loadRecommender(learningDataSet);
    logger.log(Level.INFO, ">>>>>>>>>>>>>>>>>>>>:Load recommender end: {0}", new Timestamp(System.currentTimeMillis()));
    logger.log(Level.INFO, ">>>>>>>>>>>>>>>>>>>>:Build recommendre start: {0}", new Timestamp(System.currentTimeMillis()));
    recommender.buildRecommender(learningDataSet);
    logger.log(Level.INFO, ">>>>>>>>>>>>>>>>>>>>:Build recommendre end: {0}", new Timestamp(System.currentTimeMillis()));
  }
}
