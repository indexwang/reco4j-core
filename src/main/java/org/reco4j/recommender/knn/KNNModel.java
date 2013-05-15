/*
 * KNNModel.java
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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import org.reco4j.graph.INode;
import org.reco4j.model.Rating;
import org.reco4j.model.ModelBase;
import org.reco4j.util.Utility;

/**
 *
 * @author Luigi Giuri < luigi.giuri at reco4j.org >
 */
public class KNNModel extends ModelBase implements IKNNModel
{

  public static final String KNN_DEFAULT_MODEL_NAME = "knn_default";
  private String itemIdentifierNodePropertyName;

  public KNNModel()
  {
    name = KNN_DEFAULT_MODEL_NAME;
  }

  public KNNModel(String itemIdentifierNodePropertyName)
  {
    name = KNN_DEFAULT_MODEL_NAME;
    this.itemIdentifierNodePropertyName = itemIdentifierNodePropertyName;
  }
  private static final Logger logger = Logger.getLogger(KNNModel.class.getName());
  //
  private HashMap<String, HashMap<String, Rating>> knn = new HashMap<String, HashMap<String, Rating>>();
  //

  public boolean containsItem(String id)
  {
    return knn.containsKey(id);
  }

  public Collection<Rating> getItemRatings(String id)
  {
    return knn.get(id).values();
  }

  public HashMap<String, Rating> getKnnRow(String itemId)
  {
    HashMap<String, Rating> knnRow = knn.get(itemId);
    if (knnRow == null)
    {
      knnRow = new HashMap<String, Rating>();
      knn.put(itemId, knnRow);
    }
    return knnRow;
  }

//  private void printKnnRow(String itemId)
//  {
//    HashMap<String, Rating> row = getKnnRow(itemId);
//    for (String item : row.keySet())
//    {
//      Rating rate = row.get(item);
//      logger.log(Level.INFO, " {0}({1}) ", new Object[]
//      {
//        rate.getItem().getProperty(getConfig().getItemIdentifierName()), rate.getRate()
//      });
//    }
//  }
  public void logKNN()
  {
    for (String rowItem : knn.keySet())
    {
      //System.out.print("Key: " + rowItem + " - ");
      StringBuilder out = new StringBuilder("Key: ");
      out.append(rowItem).append(" - ");
      HashMap<String, Rating> row = knn.get(rowItem);
      for (String item : row.keySet())
      {
        Rating rate = row.get(item);
        out.append(" ").append(item).append("(").append(rate.getRate()).append(") ");
      }
      logger.info(out.toString());
    }
  }

  /**
   *
   * @param item
   * @param cardinality
   * @return
   */
  @Override
  public List<Rating> getSimilarItem(INode item, int cardinality)
  {
    ArrayList<Rating> similarItems = new ArrayList<Rating>();
    for (Rating rating : knn.get(item.getProperty(itemIdentifierNodePropertyName)).values())
      Utility.orderedInsert(similarItems, rating.getRate(), rating.getItem(), cardinality);
    return similarItems;
  }
}
