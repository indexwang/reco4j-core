/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
 * @author giuri
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
