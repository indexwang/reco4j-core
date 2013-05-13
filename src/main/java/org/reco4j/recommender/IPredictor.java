/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.recommender;

import org.reco4j.graph.INode;

/**
 *
 * @author giuri
 */
public interface IPredictor
{

  double predictRating(INode user, INode item);
}
