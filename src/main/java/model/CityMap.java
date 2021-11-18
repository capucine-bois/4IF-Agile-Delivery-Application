package model;

import observer.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CityMap extends Observable {
    private Map<Intersection, ArrayList<Segment>> adjacenceMap;

    public CityMap() {
        this.adjacenceMap = new HashMap<>();
    }

    public Map<Intersection, ArrayList<Segment>> getAdjacenceMap() {
        return adjacenceMap;
    }

    public void addIntersection(Intersection i) {
        adjacenceMap.put(i, new ArrayList<>());
    }

    public void addSegment(Segment s, Intersection i) {
        adjacenceMap.get(i).add(s);
    }


}
