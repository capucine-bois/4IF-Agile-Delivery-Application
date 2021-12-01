package controller;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import model.*;
import view.Window;

/**
 * Controller of our application, able to change the current state and call methods corresponding to main features.
 */
public class Controller {

    private State currentState;
    private Window window;
    private CityMap cityMap;
    private Tour tour;

    // Instances associated with each possible state of the controller
    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final RequestsLoadedState requestsLoadedState = new RequestsLoadedState();
    protected final TourComputedState tourComputedState = new TourComputedState();
    protected final RequestsComputedState requestsComputedState = new RequestsComputedState();
    protected final PathDetailsComputedState pathDetailsComputedState = new PathDetailsComputedState();
    protected final SelectedIntersectionState selectedIntersectionState = new SelectedIntersectionState();
    protected final SelectedRequestState selectedRequestState = new SelectedRequestState();


    /** Constructor taking already filled cityMap and tour structures
     * @param cityMap filled cityMap structure
     * @param tour filled tour structure
     */
    public Controller(CityMap cityMap, Tour tour) throws IOException, FontFormatException {
        this.window = new Window(cityMap, tour, this);
        this.currentState = initialState;
        this.cityMap = cityMap;
        this.tour = tour;
    }

    /**
     * Change the current state of the controller
     * @param state the new current state
     */
    public void setCurrentState(State state) {
        System.out.println("state = " + state);
        this.currentState = state;
        if (state == initialState) {
            window.setDefaultButtonStates(new boolean[]{true, false, false});
        } else if (state == mapLoadedState || state == tourComputedState ||
                state == pathDetailsComputedState || state == requestsComputedState ||
                state == selectedIntersectionState || state == selectedRequestState){
            window.setDefaultButtonStates(new boolean[]{true, true, false});
        } else {
            window.setDefaultButtonStates(new boolean[]{true, true, true});
        }
        window.resetComponentsState();
    }

    // Methods corresponding to user events

    /**
     * Loading a map (intersections and segments) from XML file.
     * The behavior depends on the current state.
     */
    public void loadMap() {
        currentState.loadMap(cityMap, tour, window, this);
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * The behavior depends on the current state.
     */
    public void loadRequests() {
        currentState.loadRequests(cityMap, tour, window, this);
    }

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     */
    public void computeTour() { currentState.computeTour(cityMap, tour, window, this); }

    public void showRequestsPanel() { currentState.showRequestsPanel(tour, window, this); }

    public void showTourPanel() { currentState.showTourPanel(tour, window, this); }

    public void leftClickOnRequest(int indexRequest) {
        currentState.leftClickOnRequest(indexRequest, tour, this);
    }

    public void leftClickOnTourIntersection(int indexIntersection) {
        currentState.leftClickOnTourIntersection(indexIntersection, tour, this);
    }


    public void leftClickOnShortestPath(int indexShortestPath) {
        currentState.leftClickOnShortestPath(indexShortestPath, tour, this);
    }

    public void goBackToTour() {
        currentState.goBackToTour(tour, this);
    }

    public void leftClickOnIcon(int indexIcon) {
        currentState.leftClickOnIcon(indexIcon, tour, this);
    }


    public void enterMouseOnRequest(int indexRequest) {
        currentState.enterMouseOnRequest(indexRequest, window);
    }

    public void enterMouseOnTourIntersection(int indexShortestPath) {
        currentState.enterMouseOnTourIntersection(indexShortestPath, window);
    }

    public void exitMouseOnRequest(int indexRequest) {
        currentState.exitMouseOnRequest(indexRequest, tour, window);
    }

    public void exitMouseOnTourIntersection(int indexShortestPath) {
        currentState.exitMouseOnTourIntersection(indexShortestPath, tour, window);
    }

    public void moveMouseOnIcon() {
        currentState.moveMouseOnIcon(window);
    }

    public void deleteRequest(int indexRequest) {
        //System.out.println("Controller.deleteRequest");
        //System.out.println("indexRequest = " + indexRequest);
        ArrayList<Request> planning = tour.getPlanningRequests();
        ArrayList<ShortestPath> shortestPaths = tour.getListShortestPaths();
        ArrayList<Intersection> intersections = new ArrayList<Intersection>();
        Request requestToDelete = planning.get(indexRequest);

        // getting TSP order
        ArrayList<Integer> order = new ArrayList<Integer>();
        order.add(0);
        intersections.add(tour.getDepotAddress());
        for (ShortestPath path: shortestPaths) {
            order.add(path.getEndNodeNumber());
            intersections.add(path.getEndAddress());
        }

        // remove request
        planning.remove(indexRequest);

        // remove paths starting or ending by a point of the request to delete
        int i = 0;
        ArrayList<Integer> intersectionToRemove = new ArrayList<Integer>();
        while (i<shortestPaths.size()) {
            ShortestPath path = shortestPaths.get(i);
            if (path.getEndAddress().equals(requestToDelete.getPickupAddress()) ||
                    path.getEndAddress().equals(requestToDelete.getDeliveryAddress())) {
                if (!intersectionToRemove.contains(path.getEndNodeNumber())) {
                    intersectionToRemove.add(path.getEndNodeNumber());
                }
                shortestPaths.remove(i);
            } else if (path.getStartAddress().equals(requestToDelete.getPickupAddress()) ||
                    path.getStartAddress().equals(requestToDelete.getDeliveryAddress())) {
                if (!intersectionToRemove.contains(path.getStartNodeNumber())) {
                    intersectionToRemove.add(path.getStartNodeNumber());
                }
                shortestPaths.remove(i);
            } else {
                i++;
            }
        }

        //System.out.println("order: " + order);
        //System.out.println("toDelete: " + intersectionToRemove);

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
                ShortestPath newPath = Dijkstra.compute(cityMap.getIntersections(),
                        endPoints, previousIntersection).get(0);
                //ShortestPath newPath = new ShortestPath(0,new ArrayList< Segment >(), previousIntersection, nextIntersection);
                newPath.setStartNodeNumber(startNode);
                newPath.setEndNodeNumber(endNode);
                if (alreadyAdded) {
                    shortestPaths.add(i-2, newPath);
                } else {
                    shortestPaths.add(i-1, newPath);
                }
                alreadyAdded = true;

            }
            i++;
        }

        // decrease node numbers
        for (ShortestPath path: shortestPaths) {
            int currentStartNode = path.getStartNodeNumber();
            if (currentStartNode > Collections.min(intersectionToRemove)) {
                path.setStartNodeNumber(currentStartNode-intersectionToRemove.size());
            }

            int currentEndNode = path.getEndNodeNumber();
            if (currentEndNode > Collections.min(intersectionToRemove)) {
                path.setEndNodeNumber(currentEndNode-intersectionToRemove.size());
            }
        }

        // debug
        /*
        for (ShortestPath path: shortestPaths) {
            System.out.println(path.getStartAddress().getId() + " -> " + path.getEndAddress().getId()); // debug
        }
        */

        tour.updateLength();
        tour.notifyObservers();
    }
}