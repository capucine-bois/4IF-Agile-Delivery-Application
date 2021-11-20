package model;

import java.util.ArrayList;

/**
 * The graphe used in TSP
 */
public class CompleteGraph implements Graph {
    ArrayList<Node> listNodesGraph = new ArrayList<>();
    int nbVertices;
    double[][] cost;
    private Tour tour;

    /**
     * Create a complete directed graph such that each edge has a weight within [MIN_COST,MAX_COST]
     * @param listNodes
     */
    public CompleteGraph(ArrayList<Node> listNodes, Tour tour){
        this.listNodesGraph=listNodes;
        this.tour = tour;
        this.nbVertices = listNodesGraph.size();
        cost = new double[nbVertices][nbVertices];
        for (int i=0; i<nbVertices; i++){
            for (int j=0; j<nbVertices; j++){
                if (i == j || !tour.isPossiblePath(listNodesGraph.get(i).getIntersection(),listNodesGraph.get(j).getIntersection())) cost[i][j] = -1;
                else {
                    cost[i][j] = getCostArc(i,j);
                }
            }
        }
    }

    /**
     * Get the distance between two Intersection
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

    private boolean isPath(int i, int j) {
        boolean retour = false;
        Node node = listNodesGraph.get(i);
        Intersection intersection = listNodesGraph.get(j).getIntersection();
        for(ShortestPath shortPath : node.getListArcs()) {
            if(shortPath.getEndAddress()==intersection) {
                retour = true;
                break;
            }
        }
        return retour;
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
        if (i<0 || i>=nbVertices || j<0 || j>=nbVertices || !tour.isPossiblePath(listNodesGraph.get(i).getIntersection(),listNodesGraph.get(j).getIntersection()))
            return false;
        return i != j;
    }


}
