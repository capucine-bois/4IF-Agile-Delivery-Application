package model;

public interface Graph {

    public abstract int getNbVertices();


    public abstract double getCost(int i, int j);

    /**
     * Check if the intersections in parameter make a possible path
     * @param i
     * @param j
     * @return boolean
     */
    public abstract boolean isArc(int i, int j);

}
