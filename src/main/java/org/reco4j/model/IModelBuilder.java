/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.model;

import org.reco4j.graph.IEdge;

/**
 *
 * @author giuri
 */
public interface IModelBuilder<TModel>
{
  TModel build();
  void update(IEdge edge, int operation);
  /**
   * This method must be used after a load from database so that the model can be updated
   * @param model
   */
  
  void setModel(TModel model);
}
