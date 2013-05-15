/*
 * KNNFastModel.java
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
package org.reco4j.recommender.knn;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.model.ModelBase;
import org.reco4j.util.Utility;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class KNNFastModel extends ModelBase implements IKNNModel
{

  private static final Logger logger = Logger.getLogger(KNNFastModel.class.getName());
  //
  private FastByIDMap<FastByIDMap<Rating>> knn = new FastByIDMap<FastByIDMap<Rating>>();
  //

  public KNNFastModel()
  {
    this.name = KNNModel.KNN_DEFAULT_MODEL_NAME;
  }

  public boolean containsItem(Long id)
  {
    return knn.containsKey(id);
  }

  public FastByIDMap<Rating> getItemRatings(Long id)
  {
    return knn.get(id);
  }

  public FastByIDMap<Rating> getKnnRow(Long itemId)
  {
    FastByIDMap<Rating> knnRow = knn.get(itemId);
    if (knnRow == null)
    {
      knnRow = new FastByIDMap<Rating>();
      knn.put(itemId, knnRow);
    }
    return knnRow;
  }

  public void logKNN()
  {
    final LongPrimitiveIterator rowKeySetIterator = knn.keySetIterator();
    while (rowKeySetIterator.hasNext())
    {
      Long rowItem = rowKeySetIterator.nextLong();
      StringBuilder out = new StringBuilder("Key: ");
      out.append(rowItem).append(" - ");
      FastByIDMap<Rating> row = knn.get(rowItem);
      final LongPrimitiveIterator columnKeySetIterator = row.keySetIterator();
      while (columnKeySetIterator.hasNext())
      {
        Long item = columnKeySetIterator.next();
        Rating rate = row.get(item);
        out.append(" ").append(item).append("(").append(rate.getRate()).append(") ");
      }
      logger.info(out.toString());
    }
  }

  @Override
  public List<Rating> getSimilarItem(INode item, int cardinality)
  {
    ArrayList<Rating> similarItems = new ArrayList<Rating>();
    FastByIDMap<Rating> row = knn.get(item.getId());
    final LongPrimitiveIterator columnKeySetIterator = row.keySetIterator();
    while (columnKeySetIterator.hasNext())
    {
      Long itemId = columnKeySetIterator.next();
      Rating rate = row.get(itemId);
      Utility.orderedInsert(similarItems, rate.getRate(), rate.getItem(), cardinality);
    }
    return similarItems;
  }
}
