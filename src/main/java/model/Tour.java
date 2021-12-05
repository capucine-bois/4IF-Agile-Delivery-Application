package model;

import observer.Observable;

import java.text.ParseException;
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
    private ArrayList<Intersection> intersectionsUnreachableFromDepot;

    /**
     * Parser used to convert a Calendar object into a string (to show date and time).
     */
    private final SimpleDateFormat parser = new SimpleDateFormat("HH:mm");

    /**
     * Calendar to generate arrival and departure time.
     */
    private Calendar calendar;

    /**
     * All requests the tour need to cover
     */
    private final ArrayList<Request> planningRequests;

    /**
     * A list of shortest path which is used to print the best path. Sorted ascending for optimization.
     */
    private final ArrayList<ShortestPath> listShortestPaths;

    /**
     * Boolean indicating if the tour has already been computed.
     */
    private boolean tourComputed;

    /**
     * Whether a delivery address is visited before a pickup address.
     */
    private boolean deliveryBeforePickup = false;

    private Request newRequest;

    /* CONSTRUCTORS */

    /**
     * Constructor initializing the planning requests and the list of the shortest paths
     */
    public Tour() {
        planningRequests = new ArrayList<>();
        listShortestPaths = new ArrayList<>();
        intersectionsUnreachableFromDepot = new ArrayList<>();
        tourComputed = false;
        newRequest = null;
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

    public ArrayList<Intersection> getIntersectionsUnreachableFromDepot() {
        return intersectionsUnreachableFromDepot;
    }

    public SimpleDateFormat getParser() {
        return parser;
    }

    public boolean isDeliveryBeforePickup() {
        return deliveryBeforePickup;
    }

    public double getSpeed() {
        return speed;
    }

    public boolean isTourComputed() {
        return tourComputed;
    }

    public Request getNewRequest() {
        return newRequest;
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

    public void setTourComputed(boolean tourComputed) {
        this.tourComputed = tourComputed;
    }

    public void setNewRequest(Request newRequest) {
        this.newRequest = newRequest;
    }
    /* METHODS */

    /**
     * Update arrival and departure times for every intersection visited.
     * Update ending time of tour.
     */
    public void updateTimes() {
        // reset calendar
        try {
            this.calendar.setTime(parser.parse(departureTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ShortestPath path: listShortestPaths) {

            calendar.add(Calendar.SECOND, (int) metersToSeconds(path.getPathLength()));
            String arrivalTime = parser.format(calendar.getTime());

            System.out.println("EndNode: " + path.getEndNodeNumber());

            int processDuration;
            if (path.getEndNodeNumber() != 0) {
                boolean endAddressIsPickup = path.getEndNodeNumber()%2 == 1;
                int indexRequest = endAddressIsPickup ? path.getEndNodeNumber()/2 : path.getEndNodeNumber()/2 - 1;

                Request currentRrequest = planningRequests.get(indexRequest);
                if (endAddressIsPickup) {
                    // pickup
                    processDuration = currentRrequest.getPickupDuration();
                } else {
                    // delivery
                    processDuration = currentRrequest.getDeliveryDuration();
                }

                calendar.add(Calendar.SECOND, processDuration);
                String departureTime = parser.format(calendar.getTime());

                if (endAddressIsPickup) {
                    // pickup
                    currentRrequest.setPickupArrivalTime(arrivalTime);
                    currentRrequest.setPickupDepartureTime(departureTime);
                } else {
                    // delivery
                    currentRrequest.setDeliveryArrivalTime(arrivalTime);
                    currentRrequest.setDeliveryDepartureTime(departureTime);
                }
            } else {
                System.out.println("Depot!");
                this.arrivalTime = arrivalTime;
            }

        }

    }

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
        intersectionsUnreachableFromDepot.clear();
    }

    /**
     * Fill the list of intersections which are not in the same strongly connected components than depot
     */
    public void checkIntersectionsUnreachable(List<Intersection> allIntersectionsList) {
        intersectionsUnreachableFromDepot = StronglyConnectedComponents.getAllUnreachableIntersections((ArrayList<Intersection>) allIntersectionsList,depotAddress, planningRequests);
    }

    /**
     * Method which calls dijkstra method, creates a graph according to the result of dijkstra and then calls the TSP method
     * @param allIntersectionsList the list with all intersections of the map
     */
    public void computeTour(List<Intersection> allIntersectionsList) {
        ArrayList<Node> listNodes = new ArrayList<>();

        processDijkstraToComputeTour(allIntersectionsList, listNodes);
        TSP tsp = new TSP3();
        Graph g = new CompleteGraph(listNodes, this);

        // Run Tour
        tsp.searchSolution(1000000, g, this);

    }

    /**
     * Identify all useful points and runs Dijkstra in order compute the tour.
     * @param allIntersectionsList
     * @param listNodes
     */
    private void processDijkstraToComputeTour(List<Intersection> allIntersectionsList, ArrayList<Node> listNodes) {
        long startTimeDijkstra = System.currentTimeMillis();
        // get the points useful for the computing : pick-up address, delivery address, depot
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        ArrayList<Intersection> listUsefulEndPointsForDepot = new ArrayList<>();

        listUsefulPoints.add(depotAddress);

        for (int i = 0; i < planningRequests.size(); i++) {
            Intersection pickupReq1 = planningRequests.get(i).getPickupAddress();
            listUsefulPoints.add(pickupReq1);
            Intersection deliveryReq1 = planningRequests.get(i).getDeliveryAddress();
            listUsefulPoints.add(deliveryReq1);
            // gets the ends points useful for the computing according to the start point
            ArrayList<Intersection> listUsefulEndPointsPickUp = new ArrayList<>();
            ArrayList<Intersection> listUsefulEndPointsDelivery = new ArrayList<>();

            // get all useful end points
            identifyUsefulEndPoints(listUsefulEndPointsForDepot, i, pickupReq1, deliveryReq1, listUsefulEndPointsPickUp, listUsefulEndPointsDelivery);

            // execute Dijkstra
            ArrayList<ShortestPath> shortestPathsFromPickUp = Dijkstra.compute(allIntersectionsList, listUsefulEndPointsPickUp, pickupReq1);
            ArrayList<ShortestPath> shortestPathsFromDelivery = Dijkstra.compute(allIntersectionsList, listUsefulEndPointsDelivery, deliveryReq1);

            listNodes.add(new Node(pickupReq1, shortestPathsFromPickUp, i + 1));
            listNodes.add(new Node(deliveryReq1, shortestPathsFromDelivery, i + 2));
        }

        listNodes.add(0, new Node(depotAddress, Dijkstra.compute(allIntersectionsList, listUsefulEndPointsForDepot, depotAddress), 0));

        // print the time to compute Dijkstra
        System.out.println("Dijkstra finished in "
                + (System.currentTimeMillis() - startTimeDijkstra) + "ms\n");
    }


    /**
     * get all useful end points for the intersections of the current request (at index i)
     * @param listUsefulEndPointsForDepot
     * @param i current index in main loop
     * @param pickupReq1
     * @param deliveryReq1
     * @param listUsefulEndPointsPickUp
     * @param listUsefulEndPointsDelivery
     */
    private void identifyUsefulEndPoints(ArrayList<Intersection> listUsefulEndPointsForDepot, int i, Intersection pickupReq1, Intersection deliveryReq1, ArrayList<Intersection> listUsefulEndPointsPickUp, ArrayList<Intersection> listUsefulEndPointsDelivery) {
        listUsefulEndPointsPickUp.add(deliveryReq1);
        listUsefulEndPointsDelivery.add(depotAddress);
        listUsefulEndPointsForDepot.add(pickupReq1);

        for (int j = 0; j < planningRequests.size(); j++) {
            Intersection pickupReq2 = planningRequests.get(j).getPickupAddress();
            Intersection deliveryReq2 = planningRequests.get(j).getDeliveryAddress();
            if (i != j) {
                listUsefulEndPointsPickUp.add(pickupReq2);
                listUsefulEndPointsPickUp.add(deliveryReq2);
                listUsefulEndPointsDelivery.add(pickupReq2);
                listUsefulEndPointsDelivery.add(deliveryReq2);
            }
        }
    }

    /**
     *
     * @param listNodes
     * @param startTime
     * @param tsp
     */
    public void updateTourInformation(ArrayList<Node> listNodes, long startTime, TSP tsp) {
        this.setTourLength(tsp.getSolutionCost());
        // print the cost of the solution and the TSP time
        System.out.print("Solution of cost "+this.tourLength+" found in "
                +(System.currentTimeMillis() - startTime)+"ms : ");

        // print the solution with number which correspond to the order in the planning request
        Integer[] intersectionsOrder = tsp.getBestSol();
        for (Integer integer : intersectionsOrder) {
            System.out.print(integer + "  ");
        }
        System.out.println("0");

        listShortestPaths.clear();
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
     * Convert meters to seconds, according to speed attribute.
     * @param meters the distance
     * @return number of seconds to cover the distance
     */
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
                    t.getDepartureTime().equals(this.getDepartureTime()) &&
                    t.getPlanningRequests().equals(this.getPlanningRequests()) &&
                    t.getListShortestPaths().equals(this.getListShortestPaths());
        } else {
            check = false;
        }
        return check;
    }

    /**
     * Update tour length by doing the sum of its paths length.
     */
    public void updateLength() {
        this.tourLength = 0;
        for (ShortestPath p: this.listShortestPaths) {
            this.tourLength += p.getPathLength();
        }
    }

    /**
     * Insert a request to an already computed tour (after deleted a request).
     * Position of intersections are stored in Request.
     * Some paths are deleted, some are created to accomplish every request including the new one.
     * @param indexRequest
     * @param requestToInsert request to insert
     */
    public void insertRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, Request requestToInsert, List<Intersection> allIntersections) {
        System.out.println("Tour.putBackRequest");

        // add request
        planningRequests.add(indexRequest, requestToInsert);
        Intersection pickupAddress = requestToInsert.getPickupAddress();
        Intersection deliveryAddress = requestToInsert.getDeliveryAddress();

        int indexShortestPathToPickupToUpdate = indexShortestPathToPickup;
        int indexShortestPathToDeliveryToUpdate = indexShortestPathToDelivery;
        if (indexShortestPathToPickup < indexShortestPathToDelivery) {
            indexShortestPathToDeliveryToUpdate = indexShortestPathToDelivery - 1;
        } else {
            indexShortestPathToPickupToUpdate = indexShortestPathToPickup - 1;
        }

        Intersection addressBeforePickup = listShortestPaths.get(indexShortestPathToPickupToUpdate).getStartAddress();
        int nodeNumberBeforePickup = listShortestPaths.get(indexShortestPathToPickupToUpdate).getStartNodeNumber();
        nodeNumberBeforePickup = nodeNumberBeforePickup > indexRequest * 2 ? nodeNumberBeforePickup + 2 : nodeNumberBeforePickup;
        Intersection addressBeforeDelivery = listShortestPaths.get(indexShortestPathToDeliveryToUpdate).getStartAddress();
        int nodeNumberBeforeDelivery = listShortestPaths.get(indexShortestPathToDeliveryToUpdate).getStartNodeNumber();
        nodeNumberBeforeDelivery = nodeNumberBeforeDelivery > indexRequest * 2 ? nodeNumberBeforeDelivery + 2 : nodeNumberBeforeDelivery;

        for (int i = 0; i < listShortestPaths.size(); i++) {
            ShortestPath currentShortestPath = listShortestPaths.get(i);
            if (i == indexShortestPathToPickupToUpdate || i == indexShortestPathToDeliveryToUpdate) {
                if (indexShortestPathToDeliveryToUpdate == indexShortestPathToPickupToUpdate) {
                    setCurrentShortestPathAdd(allIntersections, currentShortestPath, indexShortestPathToPickup < indexShortestPathToDelivery ? deliveryAddress : pickupAddress, indexShortestPathToPickup < indexShortestPathToDelivery ? indexRequest * 2 + 2 : indexRequest * 2 + 1);
                } else {
                    setCurrentShortestPathAdd(allIntersections, currentShortestPath, i == indexShortestPathToPickupToUpdate ? pickupAddress : deliveryAddress, i == indexShortestPathToPickupToUpdate ? indexRequest * 2 + 1 : indexRequest * 2 + 2);
                }
            } else if (currentShortestPath.getStartNodeNumber() > indexRequest * 2) {
                currentShortestPath.setStartNodeNumber(currentShortestPath.getStartNodeNumber() + 2);
            }
            if (currentShortestPath.getEndNodeNumber() > indexRequest * 2) {
                currentShortestPath.setEndNodeNumber(currentShortestPath.getEndNodeNumber() + 2);
            }
        }

        if (Math.abs(indexShortestPathToDelivery - indexShortestPathToPickup) == 1) {
            if (indexShortestPathToPickup < indexShortestPathToDelivery) {
                addShortestPath(indexShortestPathToPickup, allIntersections, pickupAddress, deliveryAddress, indexRequest * 2 + 1, indexRequest * 2 + 2);
                addShortestPath(indexShortestPathToPickup, allIntersections, addressBeforePickup, pickupAddress, nodeNumberBeforePickup, indexRequest * 2 + 1);
            } else {
                addShortestPath(indexShortestPathToDelivery, allIntersections, deliveryAddress, pickupAddress, indexRequest * 2 + 2, indexRequest * 2 + 1);
                addShortestPath(indexShortestPathToDelivery, allIntersections, addressBeforeDelivery, deliveryAddress, nodeNumberBeforeDelivery, indexRequest * 2 + 2);
            }
        } else {
            if (indexShortestPathToPickup < indexShortestPathToDelivery) {
                addShortestPath(indexShortestPathToPickup, allIntersections, addressBeforePickup, pickupAddress, nodeNumberBeforePickup, indexRequest * 2 + 1);
                addShortestPath(indexShortestPathToDelivery, allIntersections, addressBeforeDelivery, deliveryAddress, nodeNumberBeforeDelivery, indexRequest * 2 + 2);
            } else {
                addShortestPath(indexShortestPathToDelivery, allIntersections, addressBeforeDelivery, deliveryAddress, nodeNumberBeforeDelivery, indexRequest * 2 + 2);
                addShortestPath(indexShortestPathToPickup, allIntersections, addressBeforePickup, pickupAddress, nodeNumberBeforePickup, indexRequest * 2 + 1);
            }
        }

        updateLength();
        updateTimes();
        notifyObservers();
    }

    private void addShortestPath(int indexToAdd, List<Intersection> allIntersections, Intersection startAddress, Intersection endAddress, int startNodeNumber, int endNodeNumber) {
        ShortestPath shortestPathToDelivery = Dijkstra.compute(allIntersections, new ArrayList<>(List.of(endAddress)), startAddress).get(0);
        shortestPathToDelivery.setStartNodeNumber(startNodeNumber);
        shortestPathToDelivery.setEndNodeNumber(endNodeNumber);
        listShortestPaths.add(indexToAdd, shortestPathToDelivery);
    }

    private void setCurrentShortestPathAdd(List<Intersection> allIntersections, ShortestPath currentShortestPath, Intersection endAddressBefore, int nodeNumberBefore) {
        ShortestPath newShortestPath = Dijkstra.compute(allIntersections, new ArrayList<>(List.of(currentShortestPath.getEndAddress())), endAddressBefore).get(0);
        currentShortestPath.setListSegments(newShortestPath.getListSegments());
        currentShortestPath.setStartNodeNumber(nodeNumberBefore);
        currentShortestPath.setStartAddress(newShortestPath.getStartAddress());
    }

    /**
     * Remove a request in an already computed tour.
     * Some paths are deleted, some are created to accomplish every request without the deleted one.
     * @param indexRequest index of the request to remove in the planning
     * @param indexShortestPathToPickup
     * @param indexShortestPathToDelivery
     * @param allIntersections all the intersections of the map
     * @return deleted paths
     */
    public void removeRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, List<Intersection> allIntersections) {
        System.out.println("Tour.removeRequest");

        // remove request
        planningRequests.remove(indexRequest);

        for (int i = 0; i < listShortestPaths.size() - 1; i++) {
            if (i != indexShortestPathToPickup + 1 && i != indexShortestPathToDelivery + 1) {
                ShortestPath currentShortestPath = listShortestPaths.get(i);
                if (i == indexShortestPathToPickup || i == indexShortestPathToDelivery) {
                    ShortestPath nextShortestPath;
                    if (Math.abs(indexShortestPathToDelivery - indexShortestPathToPickup) == 1) {
                        nextShortestPath = listShortestPaths.get(i + 2);
                    } else {
                        nextShortestPath = listShortestPaths.get(i + 1);
                    }
                    setCurrentShortestPathDelete(allIntersections, currentShortestPath, nextShortestPath);
                }
                if (currentShortestPath.getStartNodeNumber() > indexRequest * 2) {
                    currentShortestPath.setStartNodeNumber(currentShortestPath.getStartNodeNumber() - 2);
                }
                if (currentShortestPath.getEndNodeNumber() > indexRequest * 2) {
                    currentShortestPath.setEndNodeNumber(currentShortestPath.getEndNodeNumber() - 2);
                }
            }
        }

        listShortestPaths.remove(indexShortestPathToPickup + 1);
        if (indexShortestPathToPickup < indexShortestPathToDelivery) {
            listShortestPaths.remove(indexShortestPathToDelivery);
        } else {
            listShortestPaths.remove(indexShortestPathToDelivery + 1);
        }

        updateLength();
        updateTimes();
        notifyObservers();
    }

    private void setCurrentShortestPathDelete(List<Intersection> allIntersections, ShortestPath currentShortestPath, ShortestPath nextShortestPath) {
        ShortestPath newShortestPath = Dijkstra.compute(allIntersections, new ArrayList<>(List.of(nextShortestPath.getEndAddress())), currentShortestPath.getStartAddress()).get(0);
        currentShortestPath.setListSegments(newShortestPath.getListSegments());
        currentShortestPath.setEndNodeNumber(nextShortestPath.getEndNodeNumber());
        currentShortestPath.setEndAddress(nextShortestPath.getEndAddress());
    }

    /**
     * Change order of the tour to visit an intersection earlier.
     * Some paths are deleted, some are created to accomplish every request with the new order.
     * @param indexIntersection current position of the Intersection in the Tour
     * @param allIntersections all intersections of the map
     */
    public void moveIntersectionBefore(int indexIntersection, List<Intersection> allIntersections) {
        System.out.println("Tour.moveIntersectionBefore");
        System.out.println("indexIntersection = " + indexIntersection);

        ArrayList<ShortestPath> deletedPaths = new ArrayList<>();
        ArrayList<Intersection> intersections = new ArrayList<>();
        ArrayList<Integer> newOrder = new ArrayList<>();

        this.deliveryBeforePickup = false;

        // sanity check
        if (indexIntersection > 0 && indexIntersection < listShortestPaths.size()-1) {

            getIntersectionsAndOrderForFuturePaths(indexIntersection, intersections, newOrder);

            // remove paths from tour
            deletedPaths.add(listShortestPaths.get(indexIntersection-1));
            deletedPaths.add(listShortestPaths.get(indexIntersection));
            deletedPaths.add(listShortestPaths.get(indexIntersection+1));
            listShortestPaths.remove(indexIntersection-1);
            listShortestPaths.remove(indexIntersection-1);
            listShortestPaths.remove(indexIntersection-1);

            recomputePathAfterMovingIntersection(indexIntersection, allIntersections, intersections, newOrder);

            // check if a delivery is before a pickup
            for (int i=0; i<newOrder.size()-1; i++) {
                if (newOrder.get(i + 1) == newOrder.get(i) - 1 && newOrder.get(i) % 2 == 0) {
                    this.deliveryBeforePickup = true;
                    break;
                }
            }

            updateLength();
            updateTimes();
            notifyObservers();
        }
    }

    /**
     * Process the new path when moving an intersection in the textual view
     * If the moved intersection is in the middle of two intersections, Dijkstra
     * will be called three times.
     * @param indexIntersection current position of the Intersection in the Tour
     * @param allIntersections all intersections of the map
     * @param intersections new list of intersections created
     * @param newOrder  new order created
     */
    private void recomputePathAfterMovingIntersection(int indexIntersection, List<Intersection> allIntersections, ArrayList<Intersection> intersections, ArrayList<Integer> newOrder) {
        for (int i = 0; i< intersections.size()-1; i++) {
            // init data for dijkstra
            ArrayList<Intersection> endPoint = new ArrayList<>();
            endPoint.add(intersections.get(i+1));

            // compute path
            ShortestPath path = Dijkstra.compute(allIntersections, endPoint, intersections.get(i)).get(0);
            path.setStartNodeNumber(newOrder.get(i));
            path.setEndNodeNumber(newOrder.get(i+1));

            // add path
            listShortestPaths.add(indexIntersection -1+i, path);
        }
    }

    /**
     * Create the new list of intersections and the new order to recompute the path
     * after moving a request in the textual view
     * @param indexIntersection current position of the Intersection in the Tour
     * @param intersections new intersections for future paths
     * @param newOrder new Order for future paths
     */
    private void getIntersectionsAndOrderForFuturePaths(int indexIntersection, ArrayList<Intersection> intersections, ArrayList<Integer> newOrder) {
        intersections.add(listShortestPaths.get(indexIntersection -1).getStartAddress());
        intersections.add(listShortestPaths.get(indexIntersection +1).getStartAddress());
        intersections.add(listShortestPaths.get(indexIntersection).getStartAddress());
        intersections.add(listShortestPaths.get(indexIntersection +1).getEndAddress());

        newOrder.add(listShortestPaths.get(indexIntersection -1).getStartNodeNumber());
        newOrder.add(listShortestPaths.get(indexIntersection +1).getStartNodeNumber());
        newOrder.add(listShortestPaths.get(indexIntersection).getStartNodeNumber());
        newOrder.add(listShortestPaths.get(indexIntersection +1).getEndNodeNumber());
    }

    public void changeAddress(int indexNode, Intersection newAddress, List<Intersection> intersections) {
        if (indexNode % 2 == 0) {
            planningRequests.get(indexNode/2 - 1).setDeliveryAddress(newAddress);
        } else {
            planningRequests.get(indexNode/2).setPickupAddress(newAddress);
        }

        ShortestPath shortestPathToNode = listShortestPaths.stream().filter(x -> x.getEndNodeNumber() == indexNode).findFirst().get();
        ShortestPath newShortestPathToNode = Dijkstra.compute(intersections, new ArrayList<>(List.of(newAddress)), shortestPathToNode.getStartAddress()).get(0);
        shortestPathToNode.setEndAddress(newAddress);
        shortestPathToNode.setListSegments(newShortestPathToNode.getListSegments());
        shortestPathToNode.setPathLength(newShortestPathToNode.getPathLength());

        ShortestPath shortestPathFromNode = listShortestPaths.stream().filter(x -> x.getStartNodeNumber() == indexNode).findFirst().get();
        ShortestPath newShortestPathFromNode = Dijkstra.compute(intersections, new ArrayList<>(List.of(shortestPathFromNode.getEndAddress())), newAddress).get(0);
        shortestPathFromNode.setStartAddress(newAddress);
        shortestPathFromNode.setListSegments(newShortestPathFromNode.getListSegments());
        shortestPathFromNode.setPathLength(newShortestPathFromNode.getPathLength());

        updateLength();
        updateTimes();
        notifyObservers();
    }
}
