package model;

import observer.Observable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A tour is composed of many (shortest) paths, all connected between them, to accomplish every request that must
 * be made during the same trip. It has access to these requests.
 * A tour also has a depot address where the delivery man starts and ends its travel, and a tour length in meters,
 * which is the sum of the length of its paths.
 * As it extends Observable, an instance can notify observer when their attributes change.
 */
public class Tour extends Observable {

    /* ATTRIBUTES */

    private double tourLength;
    private Intersection depotAddress;
    private String departureTime;
    private ArrayList<Request> planningRequests = new ArrayList<>();
    private ArrayList<ShortestPath> listShortestPaths;

    /* CONSTRUCTORS */

    /**
     * Constructor initializing the planning requests and the list of the shortest paths
     */
    public Tour() {
        planningRequests = new ArrayList<>();
        listShortestPaths = new ArrayList<>();
    }

    /* GETTERS */

    /**
     * Getter for tour tourLength attribute
     *
     * @return tour length
     */
    public double getTourLength() {
        return tourLength;
    }

    /**
     * Getter for depotAddress attribute
     *
     * @return depot address
     */
    public Intersection getDepotAddress() {
        return depotAddress;
    }

    /**
     * Getter for departureTime attribute
     *
     * @return departure time
     */
    public String getDepartureTime() {
        return departureTime;
    }

    /**
     * Getter for planningRequests attribute
     *
     * @return planning of requests
     */
    public ArrayList<Request> getPlanningRequests() {
        return planningRequests;
    }

    /**
     * Getter for listShortestPaths attribute
     *
     * @return list of shortest paths
     */
    public ArrayList<ShortestPath> getListShortestPaths() {
        return listShortestPaths;
    }

    /* SETTERS */

    /**
     * Setter for tourLength attribute
     *
     * @param tourLength wanted value for tourLength attribute
     */
    public void setTourLength(double tourLength) {
        this.tourLength = tourLength;
    }

    /**
     * Setter for depotAddress attribute
     *
     * @param depotAddress wanted value for depotAddress attribute
     */
    public void setDepotAddress(Intersection depotAddress) {
        this.depotAddress = depotAddress;
    }

    /**
     * Setter for departureTime attribute
     *
     * @param departureTime wanted value for departureTime attribute
     */
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    /**
     * Setter for planningRequests attribute
     *
     * @param planningRequests wanted value for planningRequests attribute
     */
    public void setPlanningRequests(ArrayList<Request> planningRequests) {
        this.planningRequests = planningRequests;
    }

    /**
     * Setter for listShortestPaths attribute
     *
     * @param listShortestPaths wanted value for listShortestPaths attribute
     */
    public void setListShortestPaths(ArrayList<ShortestPath> listShortestPaths) {
        this.listShortestPaths = listShortestPaths;
    }

    /* METHODS */

    /**
     * Adding a request in the planning requests.
     * @param request the request to add
     */
    public void addRequest(Request request) {
        planningRequests.add(request);
    }

    /**
     * Method which calls dijkstra and then the TSP method
     * @param adjacenceMap the map with all intersections and the segments starting from this intersections
     */
    public void  computeTour(List<Intersection> adjacenceMap) {

        ArrayList<Node> listNodes = new ArrayList<>();
        // get the points useful for the computing : pick-up address, delivery address, depot
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        for(Request req : planningRequests) {
            listUsefulPoints.add(req.getPickupAddress());
            listUsefulPoints.add(req.getDeliveryAddress());
        }
        listUsefulPoints.add(depotAddress);

        int iteratorNumber = 1;
        for(Intersection startPoint : listUsefulPoints) {
            // gets the ends points useful for the computing according to the start point
            ArrayList<Intersection> listUsefulEndPoints = new ArrayList<>();
            for(Intersection endPoint : listUsefulPoints) {
                //if(endPoint!=startPoint && isPossiblePath(startPoint,endPoint) && !(isPickUp(startPoint) && endPoint==depotAddress) && !(startPoint==depotAddress && isDelivery(endPoint))) {
                if(isPossiblePath(startPoint,endPoint)) {
                    listUsefulEndPoints.add(endPoint);
                }
            }
            ArrayList<ShortestPath> shortestPathsFromStartPoint = dijkstra(adjacenceMap,listUsefulEndPoints, startPoint);
            Node nodeToAdd = null;
            if(startPoint==depotAddress) {
                nodeToAdd = new Node(startPoint,shortestPathsFromStartPoint,0);
            } else {
                nodeToAdd = new Node(startPoint,shortestPathsFromStartPoint,iteratorNumber);
                iteratorNumber++;
            }
            listNodes.add(nodeToAdd);
        }



        // Run Tour
        TSP tsp = new TSP1();
        Graph g = new CompleteGraph(listNodes, this);
        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, g);
        System.out.print("Solution of cost "+tsp.getSolutionCost()+" found in "
                +(System.currentTimeMillis() - startTime)+"ms : ");

        Integer[] intersectionsOrder = tsp.getBestSol();
        for (Integer i: intersectionsOrder) System.out.print(intersectionsOrder[i] + " ");
        System.out.println("0");

        Intersection previous = null;
        for (Integer index: intersectionsOrder) {
            Intersection currentIntersection;
            if (index == 0)
                currentIntersection = listUsefulPoints.get(listUsefulPoints.size()-1);
            else
                currentIntersection = listUsefulPoints.get(index-1);
            if (previous != null) {
                Intersection finalPrevious = previous;
                for (ShortestPath p: listNodes.stream().filter(x -> x.getIntersection() == finalPrevious).findFirst().get().getListArcs()) {
                    if (p.getEndAddress().equals(currentIntersection))
                        listShortestPaths.add(p);
                }
            }
            previous = currentIntersection;
        }

        Intersection finalPrevious1 = previous;
        for (ShortestPath p: listNodes.stream().filter(x -> x.getIntersection() == finalPrevious1).findFirst().get().getListArcs()) {
            if (p.getEndAddress().equals(depotAddress))
                listShortestPaths.add(p);
        }

        // print order
        for (ShortestPath p: listShortestPaths) {
            System.out.println(p.getStartAddress().getLatitude() + " " + p.getStartAddress().getLongitude() + " -> " +
                    p.getEndAddress().getLatitude() + " " + p.getEndAddress().getLongitude());
        }

        notifyObservers();
    }


    /**
     * Algorithm dijkstra : compute all shortest paths between a point and the points in the list "listUsefulEndPoints"
     * @param listIntersections the map with all intersections and the segments starting from this intersections
     * @param listUsefulEndPoints the intersections which can be reached by the origin Intersection
     * @param origin the intersection from we search the shortest paths
     * @return listShortestPathFromOrigin, the list of shortest paths from the origin
     */
    private ArrayList<ShortestPath> dijkstra(List<Intersection> listIntersections, ArrayList<Intersection> listUsefulEndPoints, Intersection origin) {
        ArrayList<ObjectDijkstra> listDijkstra = new ArrayList<>();
        ArrayList<ShortestPath> listShortestPathFromOrigin = new ArrayList<>();
        for (Intersection noeud : listIntersections) {
            ObjectDijkstra object = new ObjectDijkstra(noeud,null,Double.MAX_VALUE,0);
            listDijkstra.add(object);
        }

        listDijkstra.stream().filter(x -> x.getIntersection()==origin).findFirst().get().setDist(0.0);
        listDijkstra.stream().filter(x -> x.getIntersection()==origin).findFirst().get().setColor(1);

        while (listDijkstra.contains(listDijkstra.stream().filter(x -> x.getColor() == 1).findFirst().get())) {
            List<Intersection> noeudsGris = new ArrayList<>();
            Double min  = Double.MAX_VALUE;
            for(ObjectDijkstra obj : listDijkstra) {
                if(obj.getColor()==1) {
                    noeudsGris.add(obj.getIntersection());
                    if(obj.getDist()<min) {
                        min = obj.getDist();
                    }
                }
            }
            Double finalMin = min;
            ObjectDijkstra noeudGrisAvecDistMin = listDijkstra.stream().filter(x -> x.getDist() == finalMin).findFirst().get();

            List<ObjectDijkstra> listDest = listDijkstra.stream()
                    .filter(x -> noeudGrisAvecDistMin.getIntersection().getAdjacentSegments()
                            .stream().map(Segment::getDestination).collect(Collectors.toList()).contains(x.getIntersection())).collect(Collectors.toList());

            for (ObjectDijkstra noeudAdj :listDest) {

                int colorNoeudAdj = noeudAdj.getColor();
                if (colorNoeudAdj == 0 || colorNoeudAdj == 1 ) {
                    double length = noeudGrisAvecDistMin.getIntersection().getAdjacentSegments().stream().filter(x -> x.getDestination() == noeudAdj.getIntersection()).findFirst().get().getLength();
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
                while(findParent(tempoIntersection,listDijkstra)!=null) {
                    ObjectDijkstra finalTempoIntersection1 = tempoIntersection;
                    ObjectDijkstra tmpParent = finalTempoIntersection1.getParent();
                    listSegments.add(0,tmpParent.getIntersection().getAdjacentSegments().stream().filter(x -> x.getDestination() == finalTempoIntersection1.getIntersection()).findFirst().get());
                    tempoIntersection = tmpParent;
                }
                ShortestPath shortestPath = new ShortestPath(noeudGrisAvecDistMin.getDist(),listSegments,origin,noeudGrisAvecDistMin.getIntersection());
                listShortestPathFromOrigin.add((shortestPath));
                if(listShortestPathFromOrigin.size()==listUsefulEndPoints.size())
                    break;
            }
        }
        return listShortestPathFromOrigin;
    }




    /**
     * Return true if the two intersections in parameters make a possible path
     * @param startPoint the beginning of the segment
     * @param endPoint the end of a segment begin by startPoint
     * @return true if the path is possible, false otherwise
     */
    public boolean isPossiblePath(Intersection startPoint, Intersection endPoint) {
        boolean retour = true;
        for(Request req: planningRequests) {
            // case where the path is not possible : the start and the end are the same, the start is a pick-up address and the end
            // is the depot, the start is the depot and the end a delivery address, the start is a delivery address and the end is the
            // pick-up links to this delivery address
            if((req.getDeliveryAddress() == startPoint && req.getPickupAddress()==endPoint)
                    || (startPoint==depotAddress && req.getDeliveryAddress()==endPoint)
                    || (req.getPickupAddress()==startPoint && endPoint==depotAddress)
                    || startPoint==endPoint) {
                retour = false;
                break;
            }
        }
        return retour;
    }

    /**
     * Check if the path is shorter
     * @param noeudInit the intersection where the segment begins
     * @param noeudDest the intersections where the segment ends
     * @param cout the cost of the segment
     * @param listDijkstra the list of Intersection with its infos (cost, parent and color)
     */
    private void relax(ObjectDijkstra noeudInit, ObjectDijkstra noeudDest, double cout, ArrayList<ObjectDijkstra> listDijkstra) {
        if(noeudDest.getDist() > noeudInit.getDist() + cout) {
            noeudDest.setDist(noeudInit.getDist() + cout);
            noeudDest.setParent(noeudInit);
        }
    }

    /**
     * Return the parent of the intersection of the dijkstra object in parameter
     * @param intersectionToFind
     * @param listDijkstra the list of all ObjectDijkstra
     * @return
     */
    public ObjectDijkstra findParent(ObjectDijkstra intersectionToFind, ArrayList<ObjectDijkstra> listDijkstra) {
        for(ObjectDijkstra obj : listDijkstra) {
            if(obj==intersectionToFind) {
                return obj.getParent();
            }
        }
        return null;
    }

    /**
     * This inner class groups an intersection its his parent (and his its parent distance and color and his parent itself), its distance to the origin and its color
     */
    class ObjectDijkstra {
        private Intersection intersection;
        private ObjectDijkstra parent;
        private Double dist;
        private Integer color;

        public Intersection getIntersection() {
            return intersection;
        }

        public void setIntersection(Intersection intersection) {
            this.intersection = intersection;
        }

        public ObjectDijkstra getParent() {
            return parent;
        }

        public void setParent(ObjectDijkstra parent) {
            this.parent = parent;
        }

        public Double getDist() {
            return dist;
        }

        public void setDist(Double dist) {
            this.dist = dist;
        }

        public Integer getColor() {
            return color;
        }

        public void setColor(Integer color) {
            this.color = color;
        }

        public ObjectDijkstra(Intersection intersection, ObjectDijkstra parent, Double dist, Integer color) {
            this.intersection = intersection;
            this.parent = parent;
            this.dist = dist;
            this.color = color;
        }


    }
}
