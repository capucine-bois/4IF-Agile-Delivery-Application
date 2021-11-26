package model;

import java.util.ArrayList;

/**
 * A path is an ordered list of segments to connect one intersection to another by existing roads (segments).
 * It is used to find the fastest way to accomplish a request, going from its pickup address to its delivery address.
 * A path has a length in meters and is the sum of the duration of its segments.
 */
public class ShortestPath {

    /* ATTRIBUTES */

    /**
     * The length of the shortes path
     */
    private double pathLength;

    /**
     * The list of all segments needed by this path
     */
    private ArrayList<Segment> listSegments;

    /**
     * The start intersection
     */
    private Intersection startAddress;

    /**
     * The end intersection
     */
    private Intersection endAddress;

    /**
     * The number of the start node used in TSP
     */
    private int startNodeNumber;

    /**
     * The number of the end node used in TSP
     */
    private int endNodeNumber;

    private boolean selected;

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

    public double getPathLength() {
        return pathLength;
    }

    public ArrayList<Segment> getListSegments() {
        return listSegments;
    }

    public Intersection getStartAddress() {
        return startAddress;
    }

    public Intersection getEndAddress() {
        return endAddress;
    }

    public int getStartNodeNumber() {
        return startNodeNumber;
    }

    public int getEndNodeNumber() {
        return endNodeNumber;
    }

    /**
     * Getter for selection attribute
     * @return selected state
     */
    public boolean isSelected() {
        return selected;
    }
    /* SETTERS */

    public void setPathLength(double pathLength) {
        this.pathLength = pathLength;
    }

    public void setListSegments(ArrayList<Segment> listSegments) {
        this.listSegments = listSegments;
    }

    public void setStartAddress(Intersection startAddress) {
        this.startAddress = startAddress;
    }

    public void setEndAddress(Intersection endAddress) {
        this.endAddress = endAddress;
    }

    public void setStartNodeNumber(int startNodeNumber) {
        this.startNodeNumber = startNodeNumber;
    }

    public void setEndNodeNumber(int endNodeNumber) {
        this.endNodeNumber = endNodeNumber;
    }

    /**
     * Setter for selection attribute
     * @param selected wanted value for selected state
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
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
