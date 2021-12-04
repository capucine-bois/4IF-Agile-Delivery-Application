package model;

import java.util.Collection;

public class TSP2 extends TSP1 {
    /*
    Custom bound method.
    Evalute the lower bound by summing the minimal costs from current vertex and all unvisited vertices
    to vertex 0 or any other unvisited vertices.
     */
    @Override
    protected double bound(Integer currentVertex, Collection<Integer> unvisited, Collection<Integer> visited, Graph g) {

        double evaluation = 0;

        //Min cost for current vertex
        double minCostFromCurrent = Integer.MAX_VALUE;
        for(int i : unvisited) {
            if(g.getCost(currentVertex, i) < minCostFromCurrent /*&& g.getCost(currentVertex, i)!=-1*/) {
                minCostFromCurrent = g.getCost(currentVertex, i);
            }

            //Min cost for unvisited vertex
            double minCost = g.getCost(i, 0);
            for(int j : unvisited) {
                if( g.getCost(i,j) < minCost && (i!=j) /*&& g.getCost(i,j)!=-1 */) {
                    minCost = g.getCost(i,j);
                }
            }
            evaluation = evaluation + minCost;
        }
        evaluation = evaluation + minCostFromCurrent;

        return evaluation;
    }


}
