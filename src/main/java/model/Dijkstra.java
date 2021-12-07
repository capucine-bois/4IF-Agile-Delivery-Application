package model;

import java.util.*;

/**
 * Implementation of the dijkstra algorithm
 */
public class Dijkstra {


    static class undeterminedIntersection implements Comparator<undeterminedIntersection> {
        /* ATTRIBUTES */

        /**
         * The calculated distance to access the intersection from the origin(most of the time temporary)
         */
        double distance;

        /**
         * The intersection to release arcs
         */
        Intersection intersection;

        /**
         * Empty constructor
         */
        public undeterminedIntersection(){}

        /**
         * Complete constructor
         * @param i         intersection
         * @param distance  distance to the intersection
         */
        public undeterminedIntersection(Intersection i, double distance){
            this.distance = distance;
            this.intersection = i;
        }

        /**
         * Compare method used by PriorityQueue
         * @param o1 undeterminedIntersection to compare
         * @param o2 undeterminedIntersection to compare
         * @return 0,1 or -1 to sort the PriorityQueue
         **/
        @Override
        public int compare(undeterminedIntersection o1, undeterminedIntersection o2) {
            return Double.compare(o1.distance,o2.distance);
        }
    }

    /**
     * Algorithm dijkstra : compute all shortest paths between a point and the points in the list "listUsefulEndPoints"
     * @param listIntersections the list with all intersections
     * @param listUsefulEndPoints the intersections which can be reached by the origin Intersection (for example a delivery address cannot reach his pick-up address)
     * @param origin the intersection from which we search the shortest paths
     * @return listShortestPathFromOrigin, the list of the shortest paths from the origin to the intersection in the list of useful end points
     */
    public static ArrayList<ShortestPath> compute(List<Intersection> listIntersections, ArrayList<Intersection> listUsefulEndPoints, Intersection origin) {
        ArrayList<ShortestPath> listShortestPathFromOrigin = new ArrayList<>();
        int intersectionsSize = listIntersections.size();
        // Initiate PriorityQueue ( for undetermined Intersection ) O(logN) worst case insertion Keep the minimum distance on top of the queue
        PriorityQueue<undeterminedIntersection> undeterminedIntersection = new PriorityQueue<>(intersectionsSize, new undeterminedIntersection());
        // We take advantage of having intersections that start with index 0 to be able to directly access the distance, the parent and whether the path of the intersection is determined or not. O(1) access
        boolean[] settledNodes = new boolean[intersectionsSize];
        double [] distance = new double[intersectionsSize];
        int [] parent = new int[intersectionsSize];

        //initialize arrays
        for(int i = 0; i< intersectionsSize ; i++) {
            distance[i] = Double.MAX_VALUE;
            parent[i] = -1;
        }

        // First node
        distance[(int) origin.getId()] = 0;
        undeterminedIntersection.add(new undeterminedIntersection(origin,0));

        while (!undeterminedIntersection.isEmpty()) {
            Intersection currentNode = undeterminedIntersection.remove().intersection;
            // Do not care of duplicate
            if(settledNodes[(int)currentNode.getId()]){
                continue;
            }
            fillQueueVerticesToProcess(undeterminedIntersection, settledNodes, distance, parent, currentNode);
            if(listUsefulEndPoints.contains(currentNode)) {
                searchPath(listIntersections, origin, listShortestPathFromOrigin, distance, parent, currentNode);
                if(listShortestPathFromOrigin.size()==listUsefulEndPoints.size())
                    break;
            }
        }
        return listShortestPathFromOrigin;
    }

    private static void searchPath(List<Intersection> listIntersections, Intersection origin, ArrayList<ShortestPath> listShortestPathFromOrigin, double[] distance, int[] parent, Intersection currentNode) {
        Intersection tempoIntersection = currentNode;
        ArrayList<Segment> listSegments = new ArrayList<>();
        ShortestPath shortestPath;
        if(origin.equals(currentNode)) {
            Segment segmentZero = new Segment(0.0, "segment", currentNode, origin);
            listSegments.add(segmentZero);
            shortestPath = new ShortestPath(0.0,listSegments, origin, currentNode);
        } else {
            while(parent[(int) tempoIntersection.getId()]!= -1) {
                Intersection finalTempoIntersection1 = tempoIntersection;
                Intersection tmpParent = listIntersections.get(parent[(int) finalTempoIntersection1.getId()]);
                listSegments.add(0,tmpParent.getAdjacentSegments().stream().filter(x -> x.getDestination() == finalTempoIntersection1).findFirst().get());
                tempoIntersection = tmpParent;
            }
            shortestPath = new ShortestPath(distance[(int) currentNode.getId()],listSegments, origin, currentNode);
        }

        listShortestPathFromOrigin.add((shortestPath));
    }

    private static void fillQueueVerticesToProcess(PriorityQueue<undeterminedIntersection> undeterminedIntersection, boolean[] settledNodes, double[] distance, int[] parent, Intersection currentNode) {
        List<Segment> adjacentSegment = currentNode.getAdjacentSegments();
        for (Segment seg: adjacentSegment) {
            int start = (int) seg.getOrigin().getId();
            int end = (int) seg.getDestination().getId();
            // Not fixed distance intersection
            if(!settledNodes[end]) {
                Intersection destination = seg.getDestination();
                double cost = seg.getLength();
                relaxArc(start,end,cost, distance, parent);
                undeterminedIntersection.add(new undeterminedIntersection(destination, distance[end]));
            }
        }
        settledNodes[(int) currentNode.getId()] = true;
    }


    /**
     * Check if the path is shorter
     * @param noeudInit index of the intersection which starts the segment
     * @param noeudDest index of the intersection which ends the segment
     * @param cost the cost of the segment
     * @param distance array with all intersection distance
     * @param parent array with all parent intersection index
     */
    private static void relaxArc(int noeudInit, int noeudDest, double cost, double[] distance, int[] parent) {
        if(distance[noeudDest] > distance[noeudInit] + cost) {
            distance[noeudDest] = distance[noeudInit] + cost;
            parent[noeudDest] = noeudInit;
        }
    }


}
