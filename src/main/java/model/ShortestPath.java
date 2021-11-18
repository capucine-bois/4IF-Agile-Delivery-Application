package model;

import java.util.ArrayList;

public class ShortestPath {

    /* ATTRIBUTES */

    private double pathLength;
    private ArrayList<Segment> listSegments;
    private Intersection startAddress;
    private Intersection endAddress;

    /* CONSTRUCTORS */

    /**
     * Complete constructor
     * @param pathLength length (in meters) of the path, equivalent to the sum of its segments
     * @param listSegments segments composing the path
     * @param startAddress start address
     * @param endAddress end address
     */
    public ShortestPath(double pathLength, ArrayList<Segment> listSegments, Intersection startAddress, Intersection endAddress) {
        this.pathLength = pathLength;
        this.listSegments = listSegments;
        this.startAddress = startAddress;
        this.endAddress = endAddress;
    }

    /* GETTERS */

    /**
     * Getter for pathLength attribute
     * @return path length
     */
    public double getPathLength() {
        return pathLength;
    }

    /**
     * Getter for listSegments attribute
     * @return list of segments
     */
    public ArrayList<Segment> getListSegments() {
        return listSegments;
    }

    /**
     * Getter for startAddress attribute
     * @return start address
     */
    public Intersection getStartAddress() {
        return startAddress;
    }

    /**
     * Getter for endAddress attribute
     * @return end address
     */
    public Intersection getEndAddress() {
        return endAddress;
    }

    /* SETTERS */

    /**
     * Setter for pathLength attribute
     * @param pathLength wanted value for pathLength attribute
     */
    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    /**
     * Setter for listSegments attribute
     * @param listSegments wanted value for listSegments attribute
     */
    public void setListSegments(ArrayList<Segment> listSegments) {
        this.listSegments = listSegments;
    }

    /**
     * Setter for startAddress attribute
     * @param startAddress wanted value for startAddress attribute
     */
    public void setStartAddress(Intersection startAddress) {
        this.startAddress = startAddress;
    }

    /**
     * Setter for endAddress attribute
     * @param endAddress wanted value for endAddress attribute
     */
    public void setEndAddress(Intersection endAddress) {
        this.endAddress = endAddress;
    }
}
