package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * The graphe used in TSP
 */
public class CompleteGraph implements Graph {

    /* ATTRIBUTES */

    /**
     * A list of nodes
     */
    ArrayList<Node> listNodesGraph;

    /**
     * The number of address in the planning request
     */
    int nbVertices;

    /**
     * The cost of a travel between two nodes in each direction. -1 if the travel isn't possible
     */
    double[][] cost;

    /**
     * The tour object to verify if the path are possible
     */
    private Tour tour;

    /**
     * Create a complete directed graph such that each edge has a weight according to the cost of each arc
     * @param tour the tour
     * @param listNodes the list of nodes
     */
    public CompleteGraph(ArrayList<Node> listNodes, Tour tour){
        this.listNodesGraph=listNodes;
        this.tour = tour;
        this.nbVertices = listNodesGraph.size();
        cost = new double[nbVertices][nbVertices];
        for (int i=0; i<nbVertices; i++){
            for (int j=0; j<nbVertices; j++){
                if (i == j || (i%2==0 && i==j+1) || (i==0 && j%2==0) || (j==0 && i%2==1)) cost[i][j] = -1;
                else {
                    cost[i][j] = getCostArc(i,j);
                }
            }
        }
    }

    /**
     * Get the distance between two Intersection
     * @param i one intersection
     * @param j the other intersection
     */
    private double getCostArc(int i, int j) {
        Node startNode = listNodesGraph.get(i);
        Node endNode = listNodesGraph.get(j);
        double retour = startNode.getListArcs().get(0).getPathLength();
        for(ShortestPath shortPath : startNode.getListArcs()) {
            if(shortPath.getEndAddress()==endNode.getIntersection()) {
                retour = shortPath.getPathLength();
                break;
            }
        }
        return retour;
    }

    @Override
    public Integer[] getDestinationsInOrder(int currentVertex) {
        Node currentNode = listNodesGraph.get(currentVertex);
        Integer[] destinations = null;
        int i = 0;
        for (ShortestPath arc : currentNode.getListArcs()){
            destinations[i] = arc.getEndNodeNumber();
            i++;
        }
        return destinations;
    }

    @Override
    public int getNbVertices() {
        return nbVertices;
    }

    @Override
    public double getCost(int i, int j) {
        if (i<0 || i>=nbVertices || j<0 || j>=nbVertices)
            return -1;
        return cost[i][j];
    }

    @Override
    public boolean isArc(int i, int j) {
        return !(i<0 || i>=nbVertices || j<0 || j>=nbVertices || cost[i][j]==-1);
    }


}
