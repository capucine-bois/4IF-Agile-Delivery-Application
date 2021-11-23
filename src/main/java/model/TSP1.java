package model;

import java.util.Collection;
import java.util.Iterator;

/**
 * The redefinition of two parameters of the tsp : the bound and the iterator
 */
public class TSP1 extends TemplateTSP {
    @Override
    protected int bound(Integer currentVertex, Collection<Integer> unvisited) {
        return 0;
    }

    @Override
    protected Iterator<Integer> iterator(Integer currentVertex, Collection<Integer> unvisited, Graph g) {
        return new SeqIter(unvisited, currentVertex, g);
    }


}
