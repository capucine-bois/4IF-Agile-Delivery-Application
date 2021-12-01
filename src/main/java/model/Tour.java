package model;

import observer.Observable;

import java.text.SimpleDateFormat;
import java.util.*;

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

    /**
     * A list which is empty if all request of the planning request are in the same scc of the depot. Otherwise it contains all intersections not in the same scc of the depot
     */
    private ArrayList<Intersection> intersectionsNotInSameSccOfDepot;

    private SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

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
        intersectionsNotInSameSccOfDepot = new ArrayList<>();
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

        long startTimeDijkstra = System.currentTimeMillis();

        ArrayList<Node> listNodes = new ArrayList<>();
        // get the points useful for the computing : pick-up address, delivery address, depot
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        ArrayList<Intersection> listUsefulEndPointsForDepot = new ArrayList<>();

        listUsefulPoints.add(depotAddress);

        StronglyConnectedComponents scc = new StronglyConnectedComponents();


        intersectionsNotInSameSccOfDepot = scc.getAllStronglyConnectedComponents((ArrayList<Intersection>) allIntersectionsList, depotAddress, planningRequests);

        System.out.println("temps pris par scc = " + (System.currentTimeMillis() - startTimeDijkstra));
        if(intersectionsNotInSameSccOfDepot.isEmpty()) {
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
                ArrayList<ShortestPath> shortestPathsFromPickUp = Dijkstra.compute(allIntersectionsList,listUsefulEndPointsPickUp, pickupReq1);
                ArrayList<ShortestPath> shortestPathsFromDelivery = Dijkstra.compute(allIntersectionsList,listUsefulEndPointsDelivery, deliveryReq1);

                listNodes.add(new Node(pickupReq1,shortestPathsFromPickUp,i+1));
                listNodes.add(new Node(deliveryReq1,shortestPathsFromDelivery,i+2));


            }

            listNodes.add(0,new Node(depotAddress,Dijkstra.compute(allIntersectionsList,listUsefulEndPointsForDepot,depotAddress),0));


            // check if the intersection in the planning request are in the same component of the graph



            // print the time to compute Dijkstra
            System.out.println("Dijkstra finished in "
                    +(System.currentTimeMillis() - startTimeDijkstra)+"ms\n");

            // Run Tour
            TSP tsp = new TSP3();
            Graph g = new CompleteGraph(listNodes, this);
            long startTime = System.currentTimeMillis();
            tsp.searchSolution(1000000, g);
            this.setTourLength(tsp.getSolutionCost());

            // print the cost of the solution and the TSP time
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
        } else {

        }




    }



    public double metersToSeconds(double meters) {
        return (meters/(speed*1000))*60*60;
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

    public void updateLength() {
        this.tourLength = 0;
        for (ShortestPath p: this.listShortestPaths) {
            this.tourLength += p.getPathLength();
        }
    }

    // TODO: implement
    public void insertRequest(Request requestToInsert, int indexRequest, List<ShortestPath> paths) {

        System.out.println("Tour.insertRequest");

        for (ShortestPath path: listShortestPaths) {
            System.out.println(path.getStartAddress().getId() + " -> " + path.getEndAddress().getId()); // debug
        }

        // get TSP values of deleted points
        ArrayList<Integer> intersectionsToAdd = new ArrayList<>();
        if (paths.size() == 3) {
            // deleted points are following
            intersectionsToAdd.add(paths.get(0).getEndNodeNumber());
            intersectionsToAdd.add(paths.get(1).getEndNodeNumber());
        } else {
            // deleted points are not following
            intersectionsToAdd.add(paths.get(0).getEndNodeNumber());
            intersectionsToAdd.add(paths.get(2).getEndNodeNumber());
        }


        // increase TSP node values
        for (ShortestPath path: listShortestPaths) {
            int currentStartNode = path.getStartNodeNumber();
            if (currentStartNode >= Collections.min(intersectionsToAdd)) {
                path.setStartNodeNumber(currentStartNode+intersectionsToAdd.size());
            }

            int currentEndNode = path.getEndNodeNumber();
            if (currentEndNode >= Collections.min(intersectionsToAdd)) {
                path.setEndNodeNumber(currentEndNode+intersectionsToAdd.size());
            }
        }

        int i = 0;
        while (i<listShortestPaths.size()) {
            ShortestPath path = listShortestPaths.get(i);
            if (!paths.isEmpty() && path.getStartAddress().equals(paths.get(0).getStartAddress())) {
                listShortestPaths.remove(i);
                if (paths.size() == 3) {
                    // add all paths
                    listShortestPaths.add(i, paths.get(0));
                    listShortestPaths.add(i+1, paths.get(1));
                    listShortestPaths.add(i+2, paths.get(2));
                    paths.clear();
                } else {
                    // add only two paths
                    listShortestPaths.add(i, paths.get(0));
                    listShortestPaths.add(i+1, paths.get(1));
                    paths.remove(0);
                    paths.remove(0);
                }
            } else {
                i++;
            }
        }


        // testing purpose
        for (ShortestPath p: listShortestPaths) {
            System.out.print(p.getEndNodeNumber() + " ");
        }
        System.out.println("");


        for (ShortestPath path: listShortestPaths) {
            System.out.println(path.getStartAddress().getId() + " -> " + path.getEndAddress().getId()); // debug
        }


        // insert request in planning
        planningRequests.add(requestToInsert);
        updateLength();
        notifyObservers();


    }

    public ArrayList<ShortestPath> removeRequest(Request requestToDelete, int indexRequest, List<Intersection> allIntersections) {

        for (ShortestPath path: listShortestPaths) {
            System.out.println(path.getStartAddress().getId() + " -> " + path.getEndAddress().getId()); // debug
        }

        ArrayList<Intersection> intersections = new ArrayList<Intersection>();
        ArrayList<ShortestPath> deleted = new ArrayList<ShortestPath>();

        // getting TSP order
        ArrayList<Integer> order = new ArrayList<Integer>();
        order.add(0);
        intersections.add(getDepotAddress());
        for (ShortestPath path: listShortestPaths) {
            order.add(path.getEndNodeNumber());
            intersections.add(path.getEndAddress());
        }

        // remove request
        planningRequests.remove(indexRequest);

        // remove paths starting or ending by a point of the request to delete
        int i = 0;
        ArrayList<Integer> intersectionToRemove = new ArrayList<Integer>();
        while (i<listShortestPaths.size()) {
            ShortestPath path = listShortestPaths.get(i);
            if (path.getEndAddress().equals(requestToDelete.getPickupAddress()) ||
                    path.getEndAddress().equals(requestToDelete.getDeliveryAddress())) {
                if (!intersectionToRemove.contains(path.getEndNodeNumber())) {
                    intersectionToRemove.add(path.getEndNodeNumber());
                }
                deleted.add(listShortestPaths.get(i));
                listShortestPaths.remove(i);
            } else if (path.getStartAddress().equals(requestToDelete.getPickupAddress()) ||
                    path.getStartAddress().equals(requestToDelete.getDeliveryAddress())) {
                if (!intersectionToRemove.contains(path.getStartNodeNumber())) {
                    intersectionToRemove.add(path.getStartNodeNumber());
                }
                deleted.add(listShortestPaths.get(i));
                listShortestPaths.remove(i);
            } else {
                i++;
            }
        }

        System.out.println("ToDelete: " + intersectionToRemove);

        boolean skipped = false;
        boolean alreadyAdded = false;
        i = 1;
        while (i<order.size()-1 && !skipped) {
            if (intersectionToRemove.contains(order.get(i))) {

                // check if points removed are following
                if (intersectionToRemove.contains(order.get(i+1))) {
                    skipped = true;
                }

                // find intersections of new path
                int startNode = order.get(i-1);
                int endNode;
                Intersection previousIntersection = intersections.get(i-1);
                Intersection nextIntersection;
                if (skipped) {
                    endNode = order.get(i+2);
                    nextIntersection = intersections.get(i+2);
                } else {
                    endNode = order.get(i+1);
                    nextIntersection = intersections.get(i+1);
                }

                // create path and insert
                ArrayList<Intersection> endPoints = new ArrayList<Intersection>();
                endPoints.add(nextIntersection);
                ShortestPath newPath = Dijkstra.compute(allIntersections,
                        endPoints, previousIntersection).get(0);
                //ShortestPath newPath = new ShortestPath(0,new ArrayList< Segment >(), previousIntersection, nextIntersection);
                newPath.setStartNodeNumber(startNode);
                newPath.setEndNodeNumber(endNode);
                if (alreadyAdded) {
                    listShortestPaths.add(i-2, newPath);
                } else {
                    listShortestPaths.add(i-1, newPath);
                }
                alreadyAdded = true;

            }
            i++;
        }

        // decrease node numbers
        for (ShortestPath path: listShortestPaths) {
            int currentStartNode = path.getStartNodeNumber();
            if (currentStartNode > Collections.min(intersectionToRemove)) {
                path.setStartNodeNumber(currentStartNode-intersectionToRemove.size());
            }

            int currentEndNode = path.getEndNodeNumber();
            if (currentEndNode > Collections.min(intersectionToRemove)) {
                path.setEndNodeNumber(currentEndNode-intersectionToRemove.size());
            }
        }

        updateLength();
        notifyObservers();

        return deleted;
    }
}
