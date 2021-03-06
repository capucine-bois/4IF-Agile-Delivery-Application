package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

/**
 * The computing of TSP
 */
public abstract class TemplateTSP implements TSP {

    /**
     * A table containing the order of vertex of the best solution
     */
    private Integer[] bestSol;

    /**
     * The graph studied
     */
    protected Graph g;

    /**
     * The length of the solution
     */
    private double bestSolCost;

    /**
     * The start time of computing
     */
    private long startTime;

    public void searchSolution(int timeLimit, Graph g, Tour tour){
        if (timeLimit <= 0) return;
        startTime = System.currentTimeMillis();
        /*
          The time limit before which we want a solution
         */
        this.g = g;
        bestSol = new Integer[g.getNbVertices()];
        Collection<Integer> unvisited = new ArrayList<>(g.getNbVertices() - 1);
        for (int i=1; i<g.getNbVertices(); i++) unvisited.add(i);
        Collection<Integer> visited = new ArrayList<>(g.getNbVertices());
        visited.add(0); // The first visited vertex is 0 which is the depot
        bestSolCost = Double.MAX_VALUE;
        branchAndBound(0, unvisited, visited, 0, tour);
        tour.setTourComputed(true);
    }

    public double getSolutionCost(){
        if (g != null)
            return bestSolCost;
        return -1;
    }

    /**
     * Method that must be defined in TemplateTSP subclasses
     * @param currentVertex the vertex from which the evaluation begin
     * @param unvisited the unvisited vertices
     * @return a lower bound of the cost of paths in <code>g</code> starting from <code>currentVertex</code>, visiting
     * every vertex in <code>unvisited</code> exactly once, and returning to vertex <code>0</code>.
     */
    protected abstract double bound(Integer currentVertex, Collection<Integer> unvisited, Graph g);

    /**
     * Method that must be defined in TemplateTSP subclasses
     * @param currentVertex the current vertex in TSP
     * @param unvisited the unvisited vertices
     * @param g the graph with all the costs
     * @return an iterator for visiting all vertices in <code>unvisited</code> which are successors of <code>currentVertex</code>
     */
    protected abstract Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g);

    /**
     * Template method of a branch and bound algorithm for solving the TSP in <code>g</code>.
     * @param currentVertex the last visited vertex
     * @param unvisited the set of vertex that have not yet been visited
     * @param visited the sequence of vertices that have been already visited (including currentVertex)
     * @param currentCost the cost of the path corresponding to <code>visited</code>
     * @param tour the last computed tour
     */
    private void branchAndBound(int currentVertex, Collection<Integer> unvisited,
                                Collection<Integer> visited, double currentCost, Tour tour){
        if (tour.isTourComputed()) return;
        if (unvisited.size() == 0){
            if (g.isArc(currentVertex,0)){
                if (currentCost+g.getCost(currentVertex,0) < bestSolCost){
                    visited.toArray(bestSol);
                    bestSolCost = currentCost+g.getCost(currentVertex,0);
                    CompleteGraph completeGraph = (CompleteGraph) g;
                    tour.updateTourInformation(completeGraph.listNodesGraph, startTime, this);
                    tour.notifyObservers();
                }
            }
        } else if (currentCost+bound(currentVertex,unvisited, g) < bestSolCost){
            Iterator<Integer> it = iterator(currentVertex, unvisited, g);
            while (it.hasNext()){
                Integer nextVertex = it.next();
                visited.add(nextVertex);
                unvisited.remove(nextVertex);
                branchAndBound(nextVertex, unvisited, visited,
                        currentCost+g.getCost(currentVertex, nextVertex), tour);
                visited.remove(nextVertex);
                unvisited.add(nextVertex);
            }
        }
    }

    /**
     * Getter for bestSol attribute.
     * @return best solution
     */
    public Integer[] getBestSol() {
        return bestSol;
    }
}
