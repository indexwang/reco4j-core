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

package org.reco4j.similarity;

import static org.reco4j.similarity.ISimilarityConfig.*;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class SimilarityFactory
{
  public static ISimilarity getSimilarityClass(ISimilarityConfig config)
  {
    ISimilarity sim;
    
    int similarityType = config.getSimilarityType();
    
    // Safe casts due to similarityType
    switch (similarityType)
    {
      case SIMILARITY_TYPE_EUCLIDEAN:
        sim = new EuclideanSimilarity((IEuclideanSimilarityConfig) config);
        break;
      case SIMILARITY_TYPE_EUCLIDEAN_NORMALIZED:
        sim = new EuclideanSimilarityNormalized((IEuclideanSimilarityConfig) config);
        break;
      case SIMILARITY_TYPE_JACCARD:
        sim = new JaccardSimilarity(config);
        break;
      case SIMILARITY_TYPE_COSINE:
        sim = new CosineSimilarity((ICosineSimilarityConfig) config);
        break;
//      case SIMILARITY_TYPE_BINARY_JACCARD:
//        //sim = new BinaryJaccardSimilarity();
//        break;
      default:
        throw new RuntimeException("Bad similarity type: " + similarityType);
    }

    return sim;
  }
}
