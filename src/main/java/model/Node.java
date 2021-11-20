package model;

import java.util.ArrayList;

/**
 * A node is an Intersection in a planning with his list of shortest path to the other points of the planning
 * It also has a number, useful to make the graphe of TSP
 */
public class Node {
    private int number;
    private ArrayList<ShortestPath> listArcs;
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

    public int getNumber() {
        return number;
    }

    public ArrayList<ShortestPath> getListArcs() {
        return listArcs;
    }

    public Intersection getIntersection() {
        return intersection;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setListArcs(ArrayList<ShortestPath> listArcs) {
        this.listArcs = listArcs;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }


}
