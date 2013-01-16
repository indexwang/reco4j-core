/*
 * SimilarityFactory.java
 * 
 * Copyright (C) 2012 Alessandro Negro <alessandro.negro at reco4j.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.reco4j.graph.similarity;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class SimilarityFactory
{
  
  public final static int EUCLIDEAN_SIM = 0;
  public final static int EUCLIDEAN_NORM_SIM = 1;
  public final static int JACCARD_SIM = 2;
  public final static int COSINE_SIM = 3;
  public final static int BINARY_JACCARD_SIM = 4;

  public static ISimilarity getSimilarityClass(int simMethod)
  {
    ISimilarity sim = null;
    switch (simMethod)
    {
      case EUCLIDEAN_SIM:
        sim = EuclideanSimilarity.getInstance();
        break;
      case EUCLIDEAN_NORM_SIM:
        sim = EuclideanSimilarityNormalized.getInstance();
        break;
      case JACCARD_SIM:
        sim = JaccardSimilarity.getInstance();
        break;
      case COSINE_SIM:
        sim = CosineSimilarity.getInstance();
        break;
      case BINARY_JACCARD_SIM:
        //sim = new BinaryJaccardSimilarity();
        break;
    }

    return sim;
  }

}
