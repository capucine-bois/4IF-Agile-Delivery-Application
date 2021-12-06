package model;

public interface Graph {

    int getNbVertices();


    double getCost(int i, int j);

    /**
     * Check if the intersections in parameter make a possible path
     * @param i origin intersection
     * @param j end intersection
     * @return boolean
     */
    boolean isArc(int i, int j);

}
