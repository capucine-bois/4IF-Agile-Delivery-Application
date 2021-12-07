package model;

import observer.Observable;

import java.util.ArrayList;
import java.util.HashMap;
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
    private final List<Intersection> intersections;
    private final HashMap<Long,Long> dictionaryId;

    /* CONSTRUCTORS */

    /**
     * Constructor initializing adjacenceMap attribute as an empty HashMap.
     */
    public CityMap() {
        this.intersections = new ArrayList<>();
        this.dictionaryId = new HashMap<>();
    }

    /* METHODS */

    /**
     * Add an entry to list of intersections.
     * @param i intersection to insert
     */
    public void addIntersection(Intersection i) {
        intersections.add(i);
    }

    /**
     * Add an entry to hashmap of id .
     * @param idXML id of the original XML
     * @param id id chosen by the application
     */
    public void addDictionaryId(Long idXML, Long id) {
        dictionaryId.put(idXML,id);
    }

    /**
     * Check is id is in the dictionary .
     * @param idXML id of the original XML
     */
    public boolean containsDictionaryIdKey(Long idXML) {
        return dictionaryId.containsKey(idXML);
    }

    /**
     * Return dictionary value .
     * @param idXML id of the original XML
     * @return id of the dictionary
     */
    public long getValueDictionary(Long idXML) {
        return dictionaryId.get(idXML);
    }

    /**
     * Clear intersections list and dictionary hashmap.
     */
    public void clearLists() {
        intersections.clear();
        dictionaryId.clear();
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

    /* GETTERS */

    public List<Intersection> getIntersections() {
        return intersections;
    }

}
