/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.List;
import org.reco4j.graph.INode;
import org.reco4j.graph.Rating;

/**
 *
 * @author giuri
 */
public interface IUserRecommender
{

  public List<Rating> userRecommend(INode userNode);
}
