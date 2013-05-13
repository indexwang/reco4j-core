/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender.knn;

import java.util.List;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;

/**
 *
 * @author ale
 */
public interface IKNNModel
{
  public List<Rating> getSimilarItem(INode item, int cardinality);
}
