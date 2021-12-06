package model;

import java.util.*;

/**
 * The iterator use for the TSP
 */
public class MinIter implements Iterator<Integer> {

    /* ATTRIBUTES */

    /**
     * The vertices a vertex can reach, ordered by ascending cost
     */
    private final PriorityQueue<Integer> candidates;

    /**
     * Create an iterator to traverse the set of vertices in <code>unvisited</code>
     * which are successors of <code>currentVertex</code> in <code>g</code>
     * Vertices are traversed in the same order as in <code>unvisited</code>
     * @param unvisited the unvisited vertex
     * @param currentVertex the current vertex
     * @param g the graph containing all the costs
     */
    public MinIter(Collection<Integer> unvisited, int currentVertex, Graph g){
        this.candidates = new PriorityQueue<>(Comparator.comparingDouble(vertex -> g.getCost(currentVertex, vertex)));
        for (Integer s : unvisited){
            // if we found a delivery Address and the pick-up linked is in the unvisited nodes,
            // we don't add the delivery address to the candidates
            if (g.isArc(currentVertex, s) && !(s % 2 == 0 && unvisited.contains(s - 1))) {
                candidates.add(s);
            }
        }

    }

    @Override
    public boolean hasNext() {
        return candidates.size() > 0;
    }

    @Override
    public Integer next() {
        return candidates.poll();
    }

    @Override
    public void remove() {}




}
