package model;

/**
 * A segment is a road (or a part of a road) between two intersections: an origin and a destination.
 * It has a length in meters, and a name (for example, “Rue de l’Arc-en-Ciel").
 */
public class Segment {

    /* ATTRIBUTES */

    /**
     * The length of the segment
     */
    private double length;

    /**
     * The segment's name
     */
    private String name;

    /**
     * The intersection where the segment ends
     */
    private Intersection destination;

    /**
     * The intersection where the segment starts
     */
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

    public double getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public Intersection getDestination() {
        return destination;
    }

    public Intersection getOrigin() {
        return origin;
    }

    /* SETTERS */

    /**
     * Check if two segments have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof Segment) {
            Segment s = (Segment) o;
            check = s.getDestination().equals(this.getDestination()) &&
                    s.getOrigin().equals(this.getOrigin()) &&
                    s.getLength() == this.getLength() &&
                    s.getName().equals(this.getName());
        } else {
            check = false;
        }
        return check;
    }
}
