package org.reco4j.similarity;

///*
// * To change this template, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package org.recommender.similarity;
//
//import java.util.BitSet;
//import org.codemotor.recommender.model.Item;
//import org.codemotor.recommender.model.Rating;
//
///**
// *
// ** @author Alessandro Negro <alessandro.negro at reco4j.org>
// */
//public class BinaryJaccardSimilarity implements ISimilarity
//{
//
//  public double getSimilarity(Item x, Item y)
//  {
//    if (x.getBinaryRating() == null || y.getBinaryRating() == null)
//      return -1;
//
//    BitSet tmp = (BitSet) x.getBinaryRating().clone();
//    tmp.and(y.getBinaryRating());
//    int commonUsers = tmp.cardinality();
//
//    tmp = (BitSet) x.getBinaryRating().clone();
//    tmp.or(y.getBinaryRating());
//    int totalUsers = tmp.cardinality();
//    
//    double sim = 0.0;
//    if (commonUsers > 0)
//      sim = (double) commonUsers / (double) totalUsers;
//
//    return sim;
//  }
//}
