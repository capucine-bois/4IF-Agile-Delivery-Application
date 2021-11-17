package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CityMap {
    private Map<Intersection, ArrayList<Segment>> adjacenceMap = new HashMap<>();

    public CityMap(Map<Intersection, ArrayList<Segment>> adjacenceMap) {
        this.adjacenceMap = adjacenceMap;
    }

    public Map<Intersection, ArrayList<Segment>> getAdjacenceMap() {
        return adjacenceMap;
    }

    public void setAdjacenceMap(Map<Intersection, ArrayList<Segment>> adjacenceMap) {
        this.adjacenceMap = adjacenceMap;
    }
}
