package model;

import java.util.ArrayList;

/**
 * A path is an ordered list of segments to connect one intersection to another by existing roads (segments).
 * It is used to find the fastest way to accomplish a request, going from its pickup address to its delivery address.
 * A path has a length in meters and is the sum of the duration of its segments.
 */
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

    /**
     * Check if two shortest paths have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof ShortestPath) {
            ShortestPath s = (ShortestPath) o;
            check = s.getEndAddress().equals(this.getEndAddress()) &&
                    s.getStartAddress().equals(this.getStartAddress()) &&
                    s.getPathLength() == this.getPathLength() &&
                    s.getListSegments().equals(this.getListSegments());
        } else {
            check = false;
        }
        return check;
    }
}
