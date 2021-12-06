package model;

import java.util.ArrayList;
import java.util.List;

/**
 * An intersection is a geographical point.
 * It has two coordinates (latitude and longitude, or x and y), and an identifier.
 * It also has a list of all segments which start from it.
 */
public class Intersection {

    /* ATTRIBUTES */

    /**
     * The id of the intersection
     */
    private final long id;

    /**
     * The intersection's latitude
     */
    private final double latitude;

    /**
     * The intersection's longitude
     */
    private final double longitude;

    /**
     * All segments which start from the intersection
     */
    private final List<Segment> adjacentSegments;

    /**
     * Complete constructor
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

    public long getId() {
        return id;
    }

    public double getLatitude() {
        return latitude;
    }


    public double getLongitude() {
        return longitude;
    }

    public List<Segment> getAdjacentSegments() {
        return adjacentSegments;
    }

    public void addAdjacentSegment(Segment segment) {
        adjacentSegments.add(segment);
    }

    /**
     * Check if two intersections have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof Intersection) {
            Intersection i = (Intersection) o;
            check = i.getLatitude() == this.getLatitude() &&
                    i.getLongitude() == this.getLongitude() &&
                    i.getId() == this.getId();
        } else {
            check = false;
        }
        return check;
    }
}
