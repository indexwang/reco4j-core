/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.similarity;

/**
 *
 * @author giuri
 */
public abstract class BasicSimilarity<TConfig extends ISimilarityConfig>
  implements ISimilarity
{
  private TConfig config;

  public BasicSimilarity(TConfig config)
  {
    this.config = config;
  }

  /**
   * @return the config
   */
  public TConfig getConfig()
  {
    return config;
  }
}
