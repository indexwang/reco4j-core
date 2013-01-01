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
public class ContentBasedRecommender extends BasicRecommender
{

  @Override
  public void buildRecommender(IGraph learningDataSet)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void updateRecommender(IGraph learningDataSet)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Rating> recommend(INode node)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public double estimateRating(INode user, INode source, IEdgeType edgeType, String edgeRankValueName)
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
