/*
 * IGraphUpdateOperationConstants.java
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
package org.reco4j.graph;

/**
 *
 * @author Alessandro Negro <alessandro.negro at reco4j.org>
 */
public interface IGraphUpdateOperationConstants
{
  public static final int GRAPH_OPERATION_ADD_NODE = 1;
  public static final int GRAPH_OPERATION_ADD_EDGE = 2;
  public static final int GRAPH_OPERATION_ADD_NODE_PROPERTY = 3;
  public static final int GRAPH_OPERATION_ADD_EDGE_PROPERTY = 4;
  public static final int GRAPH_OPERATION_REMOVE_EDGE = 5;
  public static final int GRAPH_OPERATION_REMOVE_NODE = 6;
  public static final int GRAPH_OPERATION_REMOVE_NODE_PROPERTY = 7;
  public static final int GRAPH_OPERATION_REMOVE_EDGE_PROPERTY = 8;  
}
