/*
 * RecommenderSessionManager.java
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
package org.reco4j.session;

import org.reco4j.graph.IGraph;

/**
 *
 ** @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public class RecommenderSessionManager
{
  private IGraph learningDataSet;
  private IGraph testingDataSet;
  
  private String rankValueProprertyName;
  
  private static RecommenderSessionManager instance = new RecommenderSessionManager();
  
  private RecommenderSessionManager()
  {
  }

  public static RecommenderSessionManager getInstance()
  {
    return instance;
  }
  
  public IGraph getLearningDataSet()
  {
    return learningDataSet;
  }

  public void setLearningDataSet(IGraph learningDataSet)
  {
    this.learningDataSet = learningDataSet;
  }

  public IGraph getTestingDataSet()
  {
    return testingDataSet;
  }

  public void setTestingDataSet(IGraph testingDataSet)
  {
    this.testingDataSet = testingDataSet;
  }  

  public String getRankValueProprertyName()
  {
    return rankValueProprertyName;
  }

  public void setRankValueProprertyName(String rankValueProprertyName)
  {
    this.rankValueProprertyName = rankValueProprertyName;
  }
  
}
