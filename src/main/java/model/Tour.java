package model;

import observer.Observable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
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
     * Total duration of the tour
     */
    private String totalDuration;

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

    public String getTotalDuration() {
        return totalDuration;
    }

    public ArrayList<Request> getPlanningRequests() {
        return planningRequests;
    }

    public ArrayList<ShortestPath> getListShortestPaths() {
        return listShortestPaths;
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

    public void addShortestPaths(ShortestPath path) {this.listShortestPaths.add(path);}

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
        Calendar departureCalendar = Calendar.getInstance();
        try {
            departureCalendar.setTime(parser.parse(departureTime));
            this.calendar.setTime(parser.parse(departureTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (ShortestPath path: listShortestPaths) {

            calendar.add(Calendar.SECOND, (int) metersToSeconds(path.getPathLength()));
            String arrivalTime = parser.format(calendar.getTime());

            int processDuration;
            if (path.getEndNodeNumber() != 0) {
                boolean endAddressIsPickup = path.getEndNodeNumber()%2 == 1;
                int indexRequest = endAddressIsPickup ? path.getEndNodeNumber()/2 : path.getEndNodeNumber()/2 - 1;

                Request currentRequest = planningRequests.get(indexRequest);
                if (endAddressIsPickup) {
                    // pickup
                    processDuration = currentRequest.getPickupDuration();
                } else {
                    // delivery
                    processDuration = currentRequest.getDeliveryDuration();
                }

                calendar.add(Calendar.SECOND, processDuration);
                String departureTime = parser.format(calendar.getTime());

                if (endAddressIsPickup) {
                    // pickup
                    currentRequest.setPickupArrivalTime(arrivalTime);
                    currentRequest.setPickupDepartureTime(departureTime);
                } else {
                    // delivery
                    currentRequest.setDeliveryArrivalTime(arrivalTime);
                    currentRequest.setDeliveryDepartureTime(departureTime);
                }
            } else {
                this.arrivalTime = arrivalTime;
            }

        }
        long days = Duration.between(departureCalendar.toInstant(), calendar.toInstant()).toDays();
        long hours = Duration.between(departureCalendar.toInstant(), calendar.toInstant()).toHours() % 24;
        long minutes = Duration.between(departureCalendar.toInstant(), calendar.toInstant()).toMinutes() % 60;

        totalDuration = minutes + " min";
        if (hours > 0) totalDuration =  hours + " h " + totalDuration;
        if (days > 0) totalDuration = days + " d " + totalDuration;
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
    }

    /**
     * Fill the list with intersections which are not in the same strongly connected components than depot
     */
    public boolean checkIntersectionsUnreachable(List<Intersection> allIntersectionsList) {
        ArrayList<Intersection> intersectionsToTest = new ArrayList<>();
        for(Request req : planningRequests) {
            intersectionsToTest.add(req.getPickupAddress());
            intersectionsToTest.add(req.getDeliveryAddress());
        }
        return (StronglyConnectedComponents.getAllUnreachableIntersections((ArrayList<Intersection>) allIntersectionsList,depotAddress, intersectionsToTest).isEmpty());
    }

    /**
     * Method which calls dijkstra method, creates a graph according to the result of dijkstra and then calls the TSP method
     * @param allIntersectionsList the list with all intersections of the map
     */
    public void computeTour(List<Intersection> allIntersectionsList) {
        ArrayList<Node> listNodes = new ArrayList<>();

        processDijkstraToComputeTour(allIntersectionsList, listNodes);
        TSP tsp = new TSP3();
        Graph g = new CompleteGraph(listNodes);

        // Run Tour
        tsp.searchSolution(1000000, g, this);
    }

    /**
     * Identify all useful points and runs Dijkstra in order compute the tour.
     * @param allIntersectionsList All intersections of the tour
     * @param listNodes All nodes of the tour
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

            listNodes.add(new Node(pickupReq1, shortestPathsFromPickUp));
            listNodes.add(new Node(deliveryReq1, shortestPathsFromDelivery));
        }

        listNodes.add(0, new Node(depotAddress, Dijkstra.compute(allIntersectionsList, listUsefulEndPointsForDepot, depotAddress)));

    }


    /**
     * get all useful end points for the intersections of the current request (at index i)
     * @param i current index in main loop
     * @param pickupReq1
     * @param deliveryReq1
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
     * @param listNodes the list with all nodes in the tour
     * @param startTime the start time of the tour
     * @param tsp the TSP used to compute the tour
     */
    public void updateTourInformation(ArrayList<Node> listNodes, long startTime, TSP tsp) {
        this.setTourLength(tsp.getSolutionCost());
        Integer[] intersectionsOrder = tsp.getBestSol();
        listShortestPaths.clear();

        for(int i=0; i< intersectionsOrder.length-1; i++) {
            Intersection end  = listNodes.get(intersectionsOrder[i+1]).getIntersection();
            ShortestPath shortestPathToAdd = listNodes.get(intersectionsOrder[i]).getListArcs().stream().filter(x -> x.getEndAddress()==end).findFirst().get();
            shortestPathToAdd.setStartNodeNumber(intersectionsOrder[i]);
            shortestPathToAdd.setEndNodeNumber(intersectionsOrder[i+1]);
            listShortestPaths.add(shortestPathToAdd);
            if(i==intersectionsOrder.length-2) {
                shortestPathToAdd = listNodes.get(intersectionsOrder[i+1]).getListArcs().stream().filter(x -> x.getEndAddress()==depotAddress).findFirst().get();
                shortestPathToAdd.setStartNodeNumber(intersectionsOrder[i+1]);
                shortestPathToAdd.setEndNodeNumber(0);
                listShortestPaths.add(shortestPathToAdd);
            }
        }
        updateTimes();
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
     * @param indexShortestPathToPickup
     * @param indexShortestPathToDelivery
     * @param requestToInsert
     * @param allIntersections
     */
    public void insertRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, Request requestToInsert, List<Intersection> allIntersections) {
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

        processShortestPaths(indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery, allIntersections, pickupAddress, deliveryAddress, indexShortestPathToPickupToUpdate, indexShortestPathToDeliveryToUpdate);
        putShortestPathInPlace(indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery, allIntersections, pickupAddress, deliveryAddress, addressBeforePickup, nodeNumberBeforePickup, addressBeforeDelivery, nodeNumberBeforeDelivery);

        updateLength();
        updateTimes();
        notifyObservers();
    }

    private void processShortestPaths(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, List<Intersection> allIntersections, Intersection pickupAddress, Intersection deliveryAddress, int indexShortestPathToPickupToUpdate, int indexShortestPathToDeliveryToUpdate) {
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
    }

    private void putShortestPathInPlace(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, List<Intersection> allIntersections, Intersection pickupAddress, Intersection deliveryAddress, Intersection addressBeforePickup, int nodeNumberBeforePickup, Intersection addressBeforeDelivery, int nodeNumberBeforeDelivery) {
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
        currentShortestPath.setPathLength(newShortestPath.getPathLength());
    }

    /**
     * Remove a request in an already computed tour.
     * Some paths are deleted, some are created to accomplish every request without the deleted one.
     * @param indexRequest index of the request to remove in the planning
     * @param indexShortestPathToPickup
     * @param indexShortestPathToDelivery
     * @param allIntersections all the intersections of the map
     */
    public void removeRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, List<Intersection> allIntersections) {
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
                if (currentShortestPath.getStartNodeNumber() > indexRequest * 2) currentShortestPath.setStartNodeNumber(currentShortestPath.getStartNodeNumber() - 2);
                if (currentShortestPath.getEndNodeNumber() > indexRequest * 2) currentShortestPath.setEndNodeNumber(currentShortestPath.getEndNodeNumber() - 2);
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
        currentShortestPath.setPathLength(newShortestPath.getPathLength());
    }

    /**
     * Change order of the tour to visit an intersection earlier.
     * Some paths are deleted, some are created to accomplish every request with the new order.
     * @param indexShortestPath current position of the shortest path in the Tour
     * @param allIntersections all intersections of the map
     */
    public void moveIntersectionBefore(int indexShortestPath, List<Intersection> allIntersections) {
        ArrayList<ShortestPath> deletedPaths = new ArrayList<>();
        ArrayList<Intersection> intersections = new ArrayList<>();
        ArrayList<Integer> newOrder = new ArrayList<>();
        this.deliveryBeforePickup = false;

        // sanity check
        if (indexShortestPath > 0 && indexShortestPath < listShortestPaths.size()-1) {
            getIntersectionsAndOrderForFuturePaths(indexShortestPath, intersections, newOrder);

            // remove paths from tour
            deletedPaths.add(listShortestPaths.get(indexShortestPath-1));
            deletedPaths.add(listShortestPaths.get(indexShortestPath));
            deletedPaths.add(listShortestPaths.get(indexShortestPath+1));
            listShortestPaths.remove(indexShortestPath-1);
            listShortestPaths.remove(indexShortestPath-1);
            listShortestPaths.remove(indexShortestPath-1);

            recomputePathAfterMovingIntersection(indexShortestPath, allIntersections, intersections, newOrder);
            // check if a delivery is before a pickup
            if (newOrder.get(2) == newOrder.get(1) - 1 && newOrder.get(1) % 2 == 0) this.deliveryBeforePickup = true;

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
        // Sanity check
        if(indexNode > 0 && indexNode <= planningRequests.size()*2) {
            if (indexNode % 2 == 0) {
                planningRequests.get(indexNode / 2 - 1).setDeliveryAddress(newAddress);
            } else {
                planningRequests.get(indexNode / 2).setPickupAddress(newAddress);
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

    public void changeProcessTime(int indexNode, int newTime) {
        // Sanity check
        if(indexNode > 0 && indexNode <= planningRequests.size()*2 ) {
            if (indexNode % 2 == 0) {
                planningRequests.get(indexNode / 2 - 1).setDeliveryDuration(newTime);
            } else {
                planningRequests.get(indexNode / 2).setPickupDuration(newTime);
            }

            updateTimes();
            notifyObservers();
        }
    }
}