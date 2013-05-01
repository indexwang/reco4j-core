/*
 * IRecommender.java
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
import org.reco4j.graph.IEdge;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;
import org.reco4j.graph.UserItemDataset;
import org.reco4j.util.IRecommenderConfig;

/**
 *
 * This interface is the interface for any recommender type. Any recommender
 * MUST subclass this instance and implements it method to be used. *
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public interface IRecommender<TConfig extends IRecommenderConfig>
{
  public TConfig getConfig();

  /**
   * This method set the properties of the recommender. Each type of recommender
   * has it's own properties, but the great part should have the inromation
   * about the persistent storage
   *
   * @param properties Properties
   */
  //public void setProperties(Properties properties);
  //
  /**
   * This method, starting from a learning dataset, build the recommender. The
   * implementation of this method is related to the particular algorithms used
   * to recommend.
   *
   * @param learningDataSet: the IGraph that contains the data that have to be
   * used for instruct the recommender
   */
  public void buildRecommender(IGraph learningDataSet);
  
  public void buildRecommender(IGraph learningDataSet, UserItemDataset dataset);

  /**
   * This method, starting from a newEdge, update the recommender. It consider
   * the old recommender and update only the data that changed according to to
   * concept of commonode
   *
   * @param newEdge: the newEdge added to the learningGraph
   */
  public void updateRecommender(IEdge newEdge);

  /**
   * This method load the recommender info from the database
   *
   * @param learningDataSet: the IGraph that contains the data that have to be
   * used for instruct the recommender
   *
   */
  public void loadRecommender(IGraph learningDataSet);

  /**
   * Pensare anche ad un lazy recommender che dato il nodo di cui deve calcolare
   * le raccomandazioni calcola il tutto considerando quel sottoalbero
   */
  public List<Rating> recommend(INode userNode);

  public double estimateRating(INode user, INode source);
}
