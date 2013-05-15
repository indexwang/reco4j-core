/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph;

/**
 *
 *** @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public interface IGraphConfig
{
  public String getEdgeRankValueName();

  public String getEdgeEstimatedRatingName();

  public String getEdgeRankName();

  public String getEdgeSimilarityName();

  public String getEdgeTestRankName();
}
