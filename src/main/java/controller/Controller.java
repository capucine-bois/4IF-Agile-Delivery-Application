package controller;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
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

    public void leftClickonIcon(int indexIcon) {
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

    public void deleteRequest(int indexRequest) {
        System.out.println("Controller.deleteRequest");
        System.out.println("indexRequest = " + indexRequest);
        ArrayList<Request> planning = tour.getPlanningRequests();
        ArrayList<ShortestPath> shortestPaths = tour.getListShortestPaths();
        Request requestToDelete = planning.get(indexRequest);

        // remove request
        planning.remove(indexRequest);

        // remove paths with this
        int i = 0;
        while (i<shortestPaths.size()) {
            //System.out.println("StartNode: " + shortestPaths.get(i).getStartNodeNumber());
            //System.out.println("EndNode: " + shortestPaths.get(i).getEndNodeNumber());
            ShortestPath path = shortestPaths.get(i);
            if (path.getStartAddress().equals(requestToDelete.getPickupAddress()) ||
                    path.getStartAddress().equals(requestToDelete.getDeliveryAddress()) ||
                    path.getEndAddress().equals(requestToDelete.getPickupAddress()) ||
                    path.getEndAddress().equals(requestToDelete.getDeliveryAddress())) {

                /*
                if (i-1 >= 0) {
                    shortestPaths.get(i - 1).setEndNodeNumber(path.getEndNodeNumber());
                }

                 */

                int removedNode = path.getStartNodeNumber();
                shortestPaths.remove(i);

                /*
                for (int j=0; j<shortestPaths.size();j++) {
                    ShortestPath path2 = shortestPaths.get(j);
                    int start = path2.getStartNodeNumber();
                    int end = path2.getEndNodeNumber();
                    if (start > removedNode)
                        path2.setStartNodeNumber(start-1);
                    if (end > removedNode)
                        path2.setEndNodeNumber(end-1);
                }

                 */

            } else {
                i++;
            }
        }

        for (ShortestPath p: shortestPaths) {
            System.out.println("Start: " + p.getStartNodeNumber());
            System.out.println("End: " + p.getEndNodeNumber());
        }

        tour.notifyObservers();
    }
}