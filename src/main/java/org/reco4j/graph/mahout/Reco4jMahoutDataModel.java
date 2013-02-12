/*
 * RecommenderNeo4jEngineManager.java
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
package org.reco4j.graph.mahout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import org.apache.mahout.cf.taste.common.Refreshable;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastIDSet;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.GenericItemPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.reco4j.graph.*;
import org.reco4j.util.RecommenderPropertiesHandle;

/**
 * Follow:
 * https://builds.apache.org/job/Mahout-Quality/javadoc/org/apache/mahout/cf/taste/model/DataModel.html
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class Reco4jMahoutDataModel implements DataModel
{

  private IGraph learningDataset;
  private LongPrimitiveIterator userIds;
  private int userCount = -1;
  private LongPrimitiveIterator itemIds;
  private int itemCount = -1;
  IEdgeType edgeType;

  public Reco4jMahoutDataModel(IGraph learningDataset)
  {
    this.learningDataset = learningDataset;
    edgeType = EdgeTypeFactory.getEdgeType(IEdgeType.EDGE_TYPE_RANK);
  }

  @Override
  public LongPrimitiveIterator getUserIDs() throws TasteException
  {
    if (userIds != null)
      return userIds;
    String userType = RecommenderPropertiesHandle.getInstance().getUserType();
    userIds = getNodesByType(userType, RecommenderPropertiesHandle.getInstance().getUserIdentifierName());
    return userIds;
  }

  @Override
  public LongPrimitiveIterator getItemIDs() throws TasteException
  {
    if (itemIds != null)
      return itemIds;
    String itemType = RecommenderPropertiesHandle.getInstance().getItemType();
    itemIds = getNodesByType(itemType, RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
    return itemIds;
  }

  @Override
  public PreferenceArray getPreferencesFromUser(long userID) throws TasteException
  {
    final INode user = learningDataset.getUserNodeById(userID);
    ArrayList<Rating> ratingList = user.getRatingsFromUser();
    return new GenericUserPreferenceArray(ratingList);
  }

  @Override
  public FastIDSet getItemIDsFromUser(long userID) throws TasteException
  {
    INode user = learningDataset.getUserNodeById(userID);
    final FastIDSet result = new FastIDSet();
    user.iterateOnEdge(edgeType, new IGraphCallable<IEdge>()
    {

      @Override
      public void call(IEdge rate)
      {
        INode item = rate.getDestination();
        String idStr = item.getProperty(RecommenderPropertiesHandle.getInstance().getItemIdentifierName());
        result.add(Long.parseLong(idStr));
      }
    });
    return result;
  }

  @Override
  public PreferenceArray getPreferencesForItem(long itemID) throws TasteException
  {
    final INode user = learningDataset.getItemNodeById(itemID);
    ArrayList<Rating> ratingList = user.getRatingsForItem();
    return new GenericItemPreferenceArray(ratingList);
  }

  @Override
  public Float getPreferenceValue(long userID, long itemID) throws TasteException
  {
    INode user = learningDataset.getUserNodeById(userID);
    INode item = learningDataset.getItemNodeById(itemID);
    if (user ==null || item == null)
      return null;
    IEdge edge = user.getEdge(item, edgeType);
    String property = edge.getProperty(RecommenderPropertiesHandle.getInstance().getEdgeRankValueName());
    return Float.parseFloat(property);
  }

  @Override
  public Long getPreferenceTime(long userID, long itemID) throws TasteException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public int getNumItems() throws TasteException
  {
    if (itemCount < 0)
      itemCount = learningDataset.getNodesNumberByType(RecommenderPropertiesHandle.getInstance().getItemType());
    return itemCount;
  }

  @Override
  public int getNumUsers() throws TasteException
  {
    if (userCount < 0)
      userCount = learningDataset.getNodesNumberByType(RecommenderPropertiesHandle.getInstance().getUserType());
    return userCount;
  }

  @Override
  public int getNumUsersWithPreferenceFor(long itemID) throws TasteException
  {
    INode item = learningDataset.getItemNodeById(itemID);
    List<IEdge> inEdges = item.getInEdge(edgeType);
    return inEdges.size();
  }

  @Override
  public int getNumUsersWithPreferenceFor(long itemID1, long itemID2) throws TasteException
  {
    int count = 0;
    INode item1 = learningDataset.getItemNodeById(itemID1);
    INode item2 = learningDataset.getItemNodeById(itemID2);
    List<IEdge> inEdges = item1.getInEdge(edgeType);
    for (IEdge edge : inEdges)
      if (edge.getSource().isConnected(item2, edgeType))
        count++;
    return count;
  }

  @Override
  public void setPreference(long userID, long itemID, float value) throws TasteException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void removePreference(long userID, long itemID) throws TasteException
  {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean hasPreferenceValues()
  {
    return false;
  }

  @Override
  public float getMaxPreference()
  {
    return (float)RecommenderPropertiesHandle.getInstance().getMaxPreferenceValue();
  }

  @Override
  public float getMinPreference()
  {
    return (float)RecommenderPropertiesHandle.getInstance().getMinPreferenceValue();
  }

  @Override
  public void refresh(Collection<Refreshable> alreadyRefreshed)
  {
    
  }

  private synchronized LongPrimitiveIterator getNodesByType(String type, final String identifier)
  {
    final FastIDSet result = new FastIDSet();
    learningDataset.getNodesByType(type,
                                   new IGraphCallable<INode>()
    {

      @Override
      public void call(INode item)
      {
        String idStr = item.getProperty(identifier);
        result.add(Long.parseLong(idStr));
      }
    });
    return result.iterator();
  }
}
