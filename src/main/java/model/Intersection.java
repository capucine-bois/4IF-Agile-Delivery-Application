package model;

import java.util.ArrayList;
import java.util.List;

/**
 * An intersection is a geographical point.
 * It has two coordinates (latitude and longitude, or x and y), and an identifier.
 */
public class Intersection {

    private long id;
    private double latitude;
    private double longitude;
    private List<Segment> adjacentSegments;

    /**
     * Complete constructor
     *
     * @param id        identifier
     * @param latitude  geographical latitude
     * @param longitude geographical longitude
     */
    public Intersection(long id, double latitude, double longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.adjacentSegments = new ArrayList<>();
    }

    /**
     * Getter for id attribute
     *
     * @return id attribute
     */
    public long getId() {
        return id;
    }

    /**
     * Setter for id attribute
     *
     * @param id new value wanted for id attribute
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Getter for latitude attribute
     *
     * @return latitude attribute
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Setter for latitude attribute
     *
     * @param latitude new value wanted for latitude attribute
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Getter for longitude attribute
     *
     * @return longitude attribute
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Setter for longitude attribute
     *
     * @param longitude new value wanted for longitude attribute
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Getter for adjacentSegments attribute
     *
     * @return adjacentSegments attribute
     */
    public List<Segment> getAdjacentSegments() {
        return adjacentSegments;
    }

    /**
     * Adder for adjacentSegments
     *
     * @param segment new segment that start from the intersection
     */
    public void addAdjacentSegment(Segment segment) {
        adjacentSegments.add(segment);
    }
}
