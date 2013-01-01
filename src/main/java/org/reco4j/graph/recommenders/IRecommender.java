/*
 * Copyright 2011 Wirex s.r.l.
 * All rights reserved.
 * Wirex proprietary/confidential. Use is subject to license terms.
 */
package org.reco4j.graph.recommenders;

import java.util.List;
import org.reco4j.graph.IEdgeType;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;

/**
 *
 * @author ale
 */
public interface IRecommender
{
  
  /**
   * This method set the properties of the recommender.
   * Each type of recommender has it's own properties,
   * but the great part should have the inromation about the persistent storage
   * @param properties Properties
   */
  //public void setProperties(Properties properties);
  /**
   * This method, starting from a learning dataset, build the recommender. 
   * The implementation of this method is related to the particular alghorims used
   * to recommend.    *
   * @param learningDataSet: the IGraph that contains the data that have to be
   * used for instruct the recommender
   */
  public void buildRecommender(IGraph learningDataSet);

  /**
   * This method, starting from a learning dataset, update the recommender. 
   * It consider the old recommender and update only the data that changed
   * @param learningDataSet: the IGraph that contains the data that have to be
   * used for instruct the recommender
   */
  public void updateRecommender(IGraph learningDataSet);

  /**
   * This method store recommender info in a persistent storage (db).
   * The way in which the recommender is stored depends on the type of recommender.
   */
  public void storeRecommender();

  /**
   * This method load the recommender info from the database
   */
  public void loadRecommender(IGraph learningDataSet);

  /**
   * Pensare anche ad un lazy recommender che dato il nodo di cui deve calcolare 
   * le raccomandazioni calcola il tutto considerando quel sottoalbero
   */
  public List<Rating> recommend(INode node);

  public double estimateRating(INode user, INode source, IEdgeType edgeType, String edgeRankValueName);
}
