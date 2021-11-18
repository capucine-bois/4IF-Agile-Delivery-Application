package model;

public class Segment {
    private double length;
    private String name;
    private Intersection destination;
    private Intersection origin;

    /* CONSTRUCTORS */

    /**
     * Complete constructor.
     * @param length length of the segment in meters
     * @param name name of the segment (as a road, a street...)
     * @param destination geographical destination
     * @param origin geographical origin
     */
    public Segment(double length, String name, Intersection destination, Intersection origin) {
        this.length = length;
        this.name = name;
        this.destination = destination;
        this.origin = origin;
    }

    /* GETTERS */

    /**
     * Getter for length attribute
     * @return length
     */
    public double getLength() {
        return length;
    }

    /**
     * Getter for name attribute
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for destination attribute
     * @return destination
     */
    public Intersection getDestination() {
        return destination;
    }

    /**
     * Getter for origin attribute
     * @return origin
     */
    public Intersection getOrigin() {
        return origin;
    }

    /* SETTERS */

    /**
     * Setter for length attribute
     * @param length new value wanted to length attribute
     */
    public void setLength(double length) {
        this.length = length;
    }

    /**
     * Setter for name attribute
     * @param name new value wanted for name attribute
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for destination attribute
     * @param destination new value wanted for destination attribute
     */
    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    /**
     * Setter for origin attribute
     * @param origin new value wanted for origin attribute
     */
    public void setOrigin(Intersection origin) {
        this.origin = origin;
    }
}
