/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

/**
 *
 * @author ale
 */
public class ModelBase implements IModel
{
  protected String name = "default";
  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public void setName(String name)
  {
    this.name = name;
  }
  
}
