package model;

import java.util.ArrayList;

public class ShortestPath {

    /* ATTRIBUTES */

    private double pathLength;
    private ArrayList<Segment> listSegments;
    private Intersection startAddress;
    private Intersection endAddress;

    /* CONSTRUCTORS */

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
}
