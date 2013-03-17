/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph;

/**
 *
 * @author giuri
 */
public abstract class BasicEdgeType
  implements IEdgeType
{
  private String edgeName;

  /**
   * @return the edgeName
   */
  @Override
  public final String getEdgeName()
  {
    return edgeName;
  }

  /**
   * @param edgeName the edgeName to set
   */
  protected void setEdgeName(String edgeName)
  {
    this.edgeName = edgeName;
  }
}
