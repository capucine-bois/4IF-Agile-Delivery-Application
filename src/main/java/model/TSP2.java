package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TSP2 extends TSP1 {
    @Override
    protected int bound(Integer currentVertex, Collection<Integer> unvisited, Collection<Integer> visited, Graph g) {

        double sum = 0;


        double minCostFromCurrent = Integer.MAX_VALUE;
        for(Integer i : unvisited) {
            //For current vertex
            if(g.getCost(currentVertex, i) < minCostFromCurrent) {
                minCostFromCurrent = g.getCost(currentVertex, i);
            }
        }
        sum = sum + minCostFromCurrent;

        for(Integer i : unvisited) {
            //For unvisited vertex
            double minCost = g.getCost(i, 0);
            for(Integer j : unvisited) {
                if( (g.getCost(i, j) < minCost) && (i != j)  ) {
                    minCost = g.getCost(i, j);
                }
            }
            sum = sum + minCost;
        }

        return (int)sum;
    }


}
