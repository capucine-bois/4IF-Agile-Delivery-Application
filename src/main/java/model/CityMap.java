package model;

import observer.Observable;

import java.util.ArrayList;
import java.util.List;

/**
 * A city map is a collection of intersections.
 * As it extends Observable, an instance can notify observer when their attributes change.
 */
public class CityMap extends Observable {

    /* ATTRIBUTES */

    /**
     * List of all intersections.
     */
    private List<Intersection> intersections;

    /* CONSTRUCTORS */

    /**
     * Constructor initializing adjacenceMap attribute as an empty HashMap.
     */
    public CityMap() {
        this.intersections = new ArrayList<>();
    }

    /* METHODS */

    /**
     * Add an entry to list of intersections.
     * @param i intersection to insert
     */
    public void addIntersection(Intersection i) {
        intersections.add(i);
    }

    /* GETTERS */

    public List<Intersection> getIntersections() {
        return intersections;
    }

    /**
     * Check if two citymaps have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof CityMap) {
            CityMap c = (CityMap) o;
            check = c.getIntersections().equals(this.getIntersections());
        } else {
            check = false;
        }
        return check;
    }


}
