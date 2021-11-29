package model;

import observer.Observable;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A tour is composed of a list of shortest paths. This list corresponds to all path which need to accomplish every request that must
 * be made during the same trip. It has access to these requests.
 * A tour also has a depot address where the delivery man starts and ends its travel, a tour length in meters,
 * which is the sum of the length of its paths and a departure's time.
 * As it extends Observable, an instance can notify observer when their attributes change.
 */
public class Tour extends Observable {

    /* ATTRIBUTES */

    // km/h
    private final double speed = 15;

    /**
     * The total length of the tour
     */
    private double tourLength;

    /**
     * The intersection corresponding to the depot
     */
    private Intersection depotAddress;

    /**
     * The time of starting for the tour
     */
    private String departureTime;

    /**
     * The time of ending for the tour
     */
    private String arrivalTime;

    private SimpleDateFormat parser = new SimpleDateFormat("HH:mm:ss");

    private Calendar calendar;

    /**
     * All requests the tour need to cover
     */
    private ArrayList<Request> planningRequests;

    /**
     * A list of shortest path which is used to print the best path. Sorted ascending for optimization.
     */
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
    public double getTourLength() {
        return tourLength;
    }

    public Intersection getDepotAddress() {
        return depotAddress;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public ArrayList<Request> getPlanningRequests() {
        return planningRequests;
    }

    public ArrayList<ShortestPath> getListShortestPaths() {
        return listShortestPaths;
    }

    public SimpleDateFormat getParser() {
        return parser;
    }

    public double getSpeed() {
        return speed;
    }

    /* SETTERS */

    public void setTourLength(double tourLength) {
        this.tourLength = tourLength;
    }

    public void setDepotAddress(Intersection depotAddress) {
        this.depotAddress = depotAddress;
    }

    public void setDepartureTime(String departureTime) {
        this.calendar = Calendar.getInstance();
        try {
            this.calendar.setTime(parser.parse(departureTime));
            this.departureTime = parser.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * Clear lists in tour
     */
    public void clearLists() {
        planningRequests.clear();
        listShortestPaths.clear();
    }

    /**
     * Method which calls dijkstra method, creates a graphe according to the result of dijkstra and then calls the TSP method
     * @param allIntersectionsList the list with all intersections of the map
     */
    public void  computeTour(List<Intersection> allIntersectionsList) {

        ArrayList<Node> listNodes = new ArrayList<>();
        // get the points useful for the computing : pick-up address, delivery address, depot
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        ArrayList<Intersection> listUsefulEndPointsForDepot = new ArrayList<>();

        listUsefulPoints.add(depotAddress);

        for(int i=0;i<planningRequests.size();i++) {
            Intersection pickupReq1 = planningRequests.get(i).getPickupAddress();
            listUsefulPoints.add(pickupReq1);
            Intersection deliveryReq1 = planningRequests.get(i).getDeliveryAddress();
            listUsefulPoints.add(deliveryReq1);
            // gets the ends points useful for the computing according to the start point
            ArrayList<Intersection> listUsefulEndPointsPickUp = new ArrayList<>();
            ArrayList<Intersection> listUsefulEndPointsDelivery = new ArrayList<>();

            listUsefulEndPointsPickUp.add(deliveryReq1);
            listUsefulEndPointsDelivery.add(depotAddress);
            // addingpickUp to the endPoints of depot
            listUsefulEndPointsForDepot.add(pickupReq1);

            for(int j=0;j<planningRequests.size();j++) {
                Intersection pickupReq2 = planningRequests.get(j).getPickupAddress();
                Intersection deliveryReq2 = planningRequests.get(j).getDeliveryAddress();
                if(i!=j) {
                    listUsefulEndPointsPickUp.add(pickupReq2);
                    listUsefulEndPointsPickUp.add(deliveryReq2);
                    listUsefulEndPointsDelivery.add(pickupReq2);
                    listUsefulEndPointsDelivery.add(deliveryReq2);
                }
            }
            ArrayList<ShortestPath> shortestPathsFromPickUp = dijkstra(allIntersectionsList,listUsefulEndPointsPickUp, pickupReq1);
            ArrayList<ShortestPath> shortestPathsFromDelivery = dijkstra(allIntersectionsList,listUsefulEndPointsDelivery, deliveryReq1);

            //sorting the shortestPaths in ascending order for optimization
            Collections.sort(shortestPathsFromPickUp);
            Collections.sort(shortestPathsFromDelivery);

            listNodes.add(new Node(pickupReq1,shortestPathsFromPickUp,i+1));
            listNodes.add(new Node(deliveryReq1,shortestPathsFromDelivery,i+2));


        }

        listNodes.add(0,new Node(depotAddress,dijkstra(allIntersectionsList,listUsefulEndPointsForDepot,depotAddress),0));


        // Run Tour
        TSP tsp = new TSP1();
        Graph g = new CompleteGraph(listNodes, this);
        long startTime = System.currentTimeMillis();
        tsp.searchSolution(20000, g);
        this.setTourLength(tsp.getSolutionCost());

        // print the cost of the solution
        System.out.print("Solution of cost "+this.tourLength+" found in "
                +(System.currentTimeMillis() - startTime)+"ms : ");

        // print the solution with number which correspond to the order in the planning request
        Integer[] intersectionsOrder = tsp.getBestSol();
        for(int i = 0; i<intersectionsOrder.length; i++) {
            System.out.print(intersectionsOrder[i] + "  ");
        }
        System.out.println("0");

        for(int i=0; i< intersectionsOrder.length-1; i++) {
            Intersection end  = listNodes.get(intersectionsOrder[i+1]).getIntersection();
            ShortestPath shortestPathToAdd = listNodes.get(intersectionsOrder[i]).getListArcs().stream().filter(x -> x.getEndAddress()==end).findFirst().get();
            shortestPathToAdd.setStartNodeNumber(intersectionsOrder[i]);
            shortestPathToAdd.setEndNodeNumber(intersectionsOrder[i+1]);
            listShortestPaths.add(shortestPathToAdd);
            calendar.add(Calendar.SECOND, (int) metersToSeconds(shortestPathToAdd.getPathLength()));
            int indexRequest = intersectionsOrder[i+1]%2 == 0 ? intersectionsOrder[i+1]/2 - 1 : intersectionsOrder[i+1]/2;
            Request requestEndPath = planningRequests.get(indexRequest);
            if (intersectionsOrder[i+1]%2 == 1) {
                requestEndPath.setPickupArrivalTime(parser.format(calendar.getTime()));
                calendar.add(Calendar.SECOND, requestEndPath.getPickupDuration());
                requestEndPath.setPickupDepartureTime(parser.format(calendar.getTime()));
            } else {
                requestEndPath.setDeliveryArrivalTime(parser.format(calendar.getTime()));
                calendar.add(Calendar.SECOND, requestEndPath.getDeliveryDuration());
                requestEndPath.setDeliveryDepartureTime(parser.format(calendar.getTime()));
            }
            if(i==intersectionsOrder.length-2) {
                shortestPathToAdd = listNodes.get(intersectionsOrder[i+1]).getListArcs().stream().filter(x -> x.getEndAddress()==depotAddress).findFirst().get();
                shortestPathToAdd.setStartNodeNumber(intersectionsOrder[i+1]);
                shortestPathToAdd.setEndNodeNumber(0);
                listShortestPaths.add(shortestPathToAdd);
                calendar.add(Calendar.SECOND, (int) metersToSeconds(shortestPathToAdd.getPathLength()));
                arrivalTime = parser.format(calendar.getTime());
            }
        }

        notifyObservers();
    }


    /**
     * Algorithm dijkstra : compute all shortest paths between a point and the points in the list "listUsefulEndPoints"
     * @param listIntersections the list with all intersections
     * @param listUsefulEndPoints the intersections which can be reached by the origin Intersection (for example a delivery address cannot reach his pick-up address)
     * @param origin the intersection from which we search the shortest paths
     * @return listShortestPathFromOrigin, the list of shortest paths from the origin to the intersection in the list of useful end points
     */
    ArrayList<ShortestPath> dijkstra(List<Intersection> listIntersections, ArrayList<Intersection> listUsefulEndPoints, Intersection origin) {
        ArrayList<ObjectDijkstra> listDijkstra = new ArrayList<>();
        ArrayList<ShortestPath> listShortestPathFromOrigin = new ArrayList<>();
        for (Intersection noeud : listIntersections) {
            ObjectDijkstra object = new ObjectDijkstra(noeud,null,Double.MAX_VALUE,0);
            listDijkstra.add(object);
        }

        listDijkstra.stream().filter(x -> x.getIntersection()==origin).findFirst().get().setDist(0.0);
        listDijkstra.stream().filter(x -> x.getIntersection()==origin).findFirst().get().setColor(1);

        while (listDijkstra.stream().anyMatch(x -> x.getColor() == 1)) {
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

    /**
     * Check if the path is shorter
     * @param noeudInit the node which contains the intersection where the segment begins
     * @param noeudDest the node which contains the intersection where the segment ends
     * @param cost the cost of the segment
     * @param listDijkstra the list of all dijkstra objects with their infos (the Intersection with its cost, parent and color)
     */
    private void relax(ObjectDijkstra noeudInit, ObjectDijkstra noeudDest, double cost, ArrayList<ObjectDijkstra> listDijkstra) {
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
    public ObjectDijkstra findParent(ObjectDijkstra intersectionToFind, ArrayList<ObjectDijkstra> listDijkstra) {
        for(ObjectDijkstra obj : listDijkstra) {
            if(obj==intersectionToFind) {
                return obj.getParent();
            }
        }
        return null;
    }

    public double metersToSeconds(double meters) {
        return (meters/(speed*1000))*60*60;
    }

    /**
     * This inner class groups an intersection and its distance to the origin, its color and its parent (which is a dijkstra object so we also get the parent's distance and color and its parent itself)
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


        /**
         * Constructor initialize all parameter of the dijkstra object
         * @param intersection
         * @param parent
         * @param dist
         * @param color
         */
        public ObjectDijkstra(Intersection intersection, ObjectDijkstra parent, Double dist, Integer color) {
            this.intersection = intersection;
            this.parent = parent;
            this.dist = dist;
            this.color = color;
        }


    }

    /**
     * Check if two tours have the same attributes
     * @param o the object to compare
     * @return whether they have the same attributes or not
     */
    public boolean equals(Object o) {
        boolean check;
        if (o instanceof Tour) {
            Tour t = (Tour) o;
            check = t.getDepotAddress().equals(this.getDepotAddress()) &&
                    t.getTourLength() == this.getTourLength() &&
                    t.getDepartureTime() == this.getDepartureTime() &&
                    t.getPlanningRequests().equals(this.getPlanningRequests()) &&
                    t.getListShortestPaths().equals(this.getListShortestPaths());
        } else {
            check = false;
        }
        return check;
    }
}
