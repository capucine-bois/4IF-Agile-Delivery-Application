package model;

import java.util.ArrayList;

/**
 * A node is an Intersection in a planning with his list of shortest path to the other points of the planning
 * It also has a number, useful to make the graphe of TSP
 */
public class Node {

    /* ATTRIBUTES */

    /**
     * A number used in the graph. We put the number in the same order as the planning request
     */
    private int number;

    /**
     * The list of shortest path from the intersection to the other intersections in the planning
     */
    private ArrayList<ShortestPath> listArcs;

    /**
     * The intersection
     */
    private Intersection intersection;

    /**
     * Complete constructor
     * @param intersection intersection itself
     * @param listArcs list of shortest paths
     * @param number number
     */
    public Node(Intersection intersection, ArrayList<ShortestPath> listArcs, int number) {
        this.number = number;
        this.listArcs = listArcs;
        this.intersection = intersection;
    }

    public ArrayList<ShortestPath> getListArcs() {
        return listArcs;
    }

    public Intersection getIntersection() {
        return intersection;
    }


}
