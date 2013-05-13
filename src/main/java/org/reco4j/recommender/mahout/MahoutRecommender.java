/*
 * Neo4jMahoutRecommender.java
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
package org.reco4j.recommender.mahout;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.NonNegativeQuadraticOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.Optimizer;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.reco4j.graph.IGraph;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.dataset.UserItemDataset;
import org.reco4j.model.IModel;
import org.reco4j.recommender.BasicRecommender;
import org.reco4j.recommender.mahout.Reco4jMahoutDataModel;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class MahoutRecommender
  extends BasicRecommender<IMahoutRecommenderConfig>
{
  private Recommender mahoutRecommender;
  private IGraph learningDataSet;

  public MahoutRecommender(IMahoutRecommenderConfig config)
  {
    super(config);
  }

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    this.learningDataSet = learningDataSet;
    
    Reco4jMahoutDataModel datamodel = new Reco4jMahoutDataModel(learningDataSet, getConfig());
    //usare classforname per caricare dinamicamente le classi del recommender dopo e delle similitudini
    ItemSimilarity similarity = new LogLikelihoodSimilarity(datamodel);
    Optimizer optimizer = new NonNegativeQuadraticOptimizer();
    mahoutRecommender = new KnnItemBasedRecommender(datamodel, similarity, optimizer, getConfig().getKValue());
  }

  @Override
  public void loadRecommender(IGraph learningDataSet)
  {
    //Do nothing
  }

  @Override
  public List<Rating> recommend(INode userNode)
  {
    List<Rating> result = new ArrayList<Rating>();
    String identifier = userNode.getProperty(getConfig().getUserIdentifierName());
    try
    {
      List<RecommendedItem> recommendation = mahoutRecommender.recommend(Long.parseLong(identifier), 10);
      for (RecommendedItem reco : recommendation)
      {
        INode item = learningDataSet.getItemNodeById(reco.getItemID());
        double value = (double) reco.getValue();
        result.add(new Rating(item, value));
      }
    }
    catch (TasteException ex)
    {
      Logger.getLogger(MahoutRecommender.class.getName()).log(Level.SEVERE, "Error while recommending", ex);
    }
    return result;
  }

  @Override
  public double estimateRating(INode user, INode source)
  {
    try
    {
      float estimatePreference = mahoutRecommender.estimatePreference(user.getId(), source.getId());
      return (double) estimatePreference;
    }
    catch (TasteException ex)
    {
      Logger.getLogger(MahoutRecommender.class.getName()).log(Level.SEVERE, "Error while estimating rating", ex);
      return -1;
    }
  }

  @Override
  public void buildRecommender(IGraph learningDataSet, UserItemDataset dataset)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public IModel getModel()
  {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }
}
