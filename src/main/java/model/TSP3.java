package model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class TSP3 extends TSP2 {
    @Override
    protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g) {
        return new MinIter(unvisited, currentVertex, g);
    }


}
