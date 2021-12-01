package model;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the dijkstra algorithm
 */
public class Dijkstra {


    public Dijkstra() {
        
    }


    /**
     * Algorithm dijkstra : compute all shortest paths between a point and the points in the list "listUsefulEndPoints"
     * @param listIntersections the list with all intersections
     * @param listUsefulEndPoints the intersections which can be reached by the origin Intersection (for example a delivery address cannot reach his pick-up address)
     * @param origin the intersection from which we search the shortest paths
     * @return listShortestPathFromOrigin, the list of shortest paths from the origin to the intersection in the list of useful end points
     */
    public static ArrayList<ShortestPath> compute(List<Intersection> listIntersections, ArrayList<Intersection> listUsefulEndPoints, Intersection origin) {




        DijkstraNode originNode = new DijkstraNode(origin,0);
        // Initiate Dijkstra Binary Heap
        Set<DijkstraNode> settledNodes = new HashSet<>();
        Set<DijkstraNode> unsettledNodes = new HashSet<>();


        unsettledNodes.add(originNode);



        while (unsettledNodes.size() != 0) {
            DijkstraNode currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (Map.Entry < DijkstraNode, Double> adjacencyPair:
                    currentNode.getAdjacentNodes().entrySet()) {
                DijkstraNode adjacentNode = adjacencyPair.getKey();
                Double edgeWeight = adjacencyPair.getValue();
                if (!settledNodes.contains(adjacentNode)) {
                    calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                    unsettledNodes.add(adjacentNode);
                }
            }
            settledNodes.add(currentNode);
        }
            ObjectDijkstra noeudGrisAvecDistMin = listDijkstra.stream().filter(x-> x.getColor() == 1).min(Comparator.comparing(ObjectDijkstra::getDist)).get();
            List<ObjectDijkstra> listDest = listDijkstra.stream()
                    .filter(x -> noeudGrisAvecDistMin.getIntersection().getAdjacentSegments()
                            .stream().map(Segment::getDestination).collect(Collectors.toList()).contains(x.getIntersection())).collect(Collectors.toList());

            for (ObjectDijkstra noeudAdj :listDest) {

                int colorNoeudAdj = noeudAdj.getColor();
                if (colorNoeudAdj == 0 || colorNoeudAdj == 1 ) {
                    double length;
                    if(noeudGrisAvecDistMin.equals(noeudAdj)) {
                        length = 0;
                    } else {
                        length = noeudGrisAvecDistMin.getIntersection().getAdjacentSegments().stream().filter(x -> x.getDestination() == noeudAdj.getIntersection()).findFirst().get().getLength();
                    }

                    relax(noeudGrisAvecDistMin, noeudAdj, length,listDijkstra);
                    if (colorNoeudAdj == 0) {
                        noeudAdj.setColor(1);
                    }
                }
            }
            noeudGrisAvecDistMin.setColor(2);
            if(listUsefulEndPoints.contains(noeudGrisAvecDistMin.getIntersection())) {
                ObjectDijkstra tempoIntersection = noeudGrisAvecDistMin;
                ArrayList<Segment> listSegments = new ArrayList<>();
                ShortestPath shortestPath;
                if(origin.equals(noeudGrisAvecDistMin.getIntersection())) {
                    Segment segmentZero = new Segment(0.0, "segment", noeudGrisAvecDistMin.getIntersection(),origin);
                    listSegments.add(segmentZero);
                    shortestPath = new ShortestPath(0.0,listSegments,origin,noeudGrisAvecDistMin.getIntersection());
                } else {
                    while(findParent(tempoIntersection,listDijkstra)!=null) {
                        ObjectDijkstra finalTempoIntersection1 = tempoIntersection;
                        ObjectDijkstra tmpParent = finalTempoIntersection1.getParent();
                        listSegments.add(0,tmpParent.getIntersection().getAdjacentSegments().stream().filter(x -> x.getDestination() == finalTempoIntersection1.getIntersection()).findFirst().get());
                        tempoIntersection = tmpParent;
                    }
                    shortestPath = new ShortestPath(noeudGrisAvecDistMin.getDist(),listSegments,origin,noeudGrisAvecDistMin.getIntersection());
                }

                listShortestPathFromOrigin.add((shortestPath));
                if(listShortestPathFromOrigin.size()==listUsefulEndPoints.size())
                    break;
            }
        }
        return listShortestPathFromOrigin;
    }




    private static DijkstraNode getLowestDistanceNode(Set < DijkstraNode > unsettledNodes) {
        DijkstraNode lowestDistanceNode = null;
        double lowestDistance = Double.MAX_VALUE;
        for (DijkstraNode DijkstraNode: unsettledNodes) {
            double nodeDistance = DijkstraNode.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = DijkstraNode;
            }
        }
        return lowestDistanceNode;
    }

    private static void calculateMinimumDistance(DijkstraNode evaluationNode,
                                                 Double edgeWeigh, DijkstraNode sourceNode) {
        Double sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<DijkstraNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

    /**
     * Check if the path is shorter
     * @param noeudInit the DijkstraNode which contains the intersection where the segment begins
     * @param noeudDest the DijkstraNode which contains the intersection where the segment ends
     * @param cost the cost of the segment
     * @param listDijkstra the list of all dijkstra objects with their infos (the Intersection with its cost, parent and color)
     */
    private static void relax(ObjectDijkstra noeudInit, ObjectDijkstra noeudDest, double cost, ArrayList<ObjectDijkstra> listDijkstra) {
        if(noeudDest.getDist() > noeudInit.getDist() + cost) {
            listDijkstra.stream().filter(x -> x==noeudDest).findFirst().get().setDist(noeudInit.getDist() + cost);
            listDijkstra.stream().filter(x -> x==noeudDest).findFirst().get().setParent(noeudInit); }
    }

    /**
     * Return the parent of the intersection of the dijkstra object in parameter
     * @param intersectionToFind the dijkstra object which contains the intersection we want to get the parent of
     * @param listDijkstra the list of all ObjectDijkstra
     * @return the parent of the intersection
     */
    public static ObjectDijkstra findParent(ObjectDijkstra intersectionToFind, ArrayList<ObjectDijkstra> listDijkstra) {
        for(ObjectDijkstra obj : listDijkstra) {
            if(obj==intersectionToFind) {
                return obj.getParent();
            }
        }
        return null;
    }

}
