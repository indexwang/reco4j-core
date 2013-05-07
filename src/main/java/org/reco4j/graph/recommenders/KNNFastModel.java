/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.reco4j.graph.recommenders;

import java.util.logging.Logger;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.reco4j.graph.Rating;

/**
 *
 * @author giuri
 */
public class KNNFastModel extends ModelBase
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
}
