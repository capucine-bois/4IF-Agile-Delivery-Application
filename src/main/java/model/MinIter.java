package model;

import java.util.*;

/**
 * The iterator use for the TSP
 */
public class MinIter implements Iterator<Integer> {

    /* ATTRIBUTES */

    /**
     * The vertex a vertex can reach
     */
    private Integer[] candidates;

    /**
     * The number of vertex to visit remaining
     */
    private int nbCandidates;

    /**
     * Create an iterator to traverse the set of vertices in <code>unvisited</code>
     * which are successors of <code>currentVertex</code> in <code>g</code>
     * Vertices are traversed in the same order as in <code>unvisited</code>
     * @param unvisited the unvisited vertex
     * @param currentVertex the current vertex
     * @param g
     */
    public MinIter(Collection<Integer> unvisited, int currentVertex, Graph g){
        this.candidates = new Integer[unvisited.size()];
        for (Integer s : unvisited){
            // if we found a delivery Address and the pick-up linked is in the unvisited nodes,
            // we don't add the delivery address to the candidates
            if (g.isArc(currentVertex, s) && !(s%2==0 && unvisited.contains(s-1))) {
                candidates[nbCandidates++] = s;
            }
        }

        System.out.println("Candidates unsorted : \n" + Arrays.toString(candidates));
        //Arrays.sort(this.candidates, Comparator.comparingDouble(o -> -g.getCost(currentVertex, o)));

        Arrays.sort(candidates, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                if (o1 == null && o2 == null) {
                    return 0;
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                // Reverse order
                return (int)(g.getCost(currentVertex, o2) - g.getCost(currentVertex, o1));
            }
        });
        System.out.println("Candidates sorted : \n" + Arrays.toString(candidates));

    }

    @Override
    public boolean hasNext() {
        return nbCandidates > 0;
    }

    @Override
    public Integer next() {
        nbCandidates--;
        return candidates[nbCandidates];
    }

    @Override
    public void remove() {}




}
