/*
 * ISimilarityConfig.java
 * 
 * Copyright (C) 2013 Alessandro Negro <alessandro.negro at reco4j.org>
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

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
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
