package model;

import java.util.ArrayList;

/**
 * A kind of launcher of TSP
 */
public class RunTSP {
    public RunTSP(){}

    /**
     * Create a graphe and launch the TSP
     * @param listNodes
     * @param tour
     * @return
     */
    public TSP computeTour(ArrayList<Node> listNodes, Tour tour) {
        TSP tsp = new TSP1();
        Graph g = new CompleteGraph(listNodes, tour);
        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, g);
        System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
                +(System.currentTimeMillis() - startTime)+"ms : ");
        for (int i=0; i<g.getNbVertices(); i++) System.out.print(tsp.getSolution(i)+" ");
        System.out.println("0");
        return tsp;
    }

}


