package model;

import observer.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CityMap extends Observable {

    /* ATTRIBUTES */

    /**
     * Map linking intersections and segments they are part of.
     */
    private Map<Intersection, ArrayList<Segment>> adjacenceMap;

    /* CONSTRUCTORS */

    /**
     * Constructor initializing adjacenceMap attribute as an empty HashMap.
     */
    public CityMap() {
        this.adjacenceMap = new HashMap<>();
    }

    /* METHODS */

    /**
     * Add an entry to adjacence map. The value corresponding to the new entry is an empty ArrayList.
     * @param i intersection to insert
     */
    public void addIntersection(Intersection i) {
        adjacenceMap.put(i, new ArrayList<>());
    }

    /**
     * Add a segment in adjacence map for an existing intersection.
     * @param s the segment to insert
     * @param i an existing intersection
     */
    public void addSegment(Segment s, Intersection i) {
        adjacenceMap.get(i).add(s);
    }

    /* GETTERS */

    /**
     * Getter for adjacenceMap attribute
     * @return adjacence map
     */
    public Map<Intersection, ArrayList<Segment>> getAdjacenceMap() {
        return adjacenceMap;
    }

    /* SETTERS */


}
