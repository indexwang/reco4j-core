/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.similarity;

/**
 *
 * @author giuri
 */
public interface ISimilarityConfig
{
  public static final int SIMILARITY_TYPE_EUCLIDEAN = 0;
  public static final int SIMILARITY_TYPE_EUCLIDEAN_NORMALIZED = 1;
  public static final int SIMILARITY_TYPE_JACCARD = 2;
  public static final int SIMILARITY_TYPE_COSINE = 3;
  public static final int SIMILARITY_TYPE_BINARY_JACCARD = 4;

  public int getSimilarityType();
}
