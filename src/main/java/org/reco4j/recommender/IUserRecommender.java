/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender;

import java.util.List;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public interface IUserRecommender
{

  public List<Rating> userRecommend(INode userNode);
}
