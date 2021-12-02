package controller;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

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
    private ListOfCommands listOfCommands;

    // Instances associated with each possible state of the controller
    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final RequestsLoadedState requestsLoadedState = new RequestsLoadedState();
    protected final TourComputingState tourComputingState = new TourComputingState();
    protected final RequestsComputingState requestsComputingState = new RequestsComputingState();
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
        this.listOfCommands = new ListOfCommands();
    }

    /**
     * Change the current state of the controller
     * @param state the new current state
     */
    public void setCurrentState(State state) {
        System.out.println("state = " + state);
        this.currentState = state;
        if (state == initialState) {
            window.setDefaultButtonStates(new boolean[]{true, false, false,false});
        } else if (state == mapLoadedState || state == tourComputedState ||
                state == pathDetailsComputedState || state == requestsComputedState ||
                state == selectedIntersectionState || state == selectedRequestState){
            window.setDefaultButtonStates(new boolean[]{true, true, false,true});
        } else if (state == tourComputingState || state == requestsComputingState) {
            window.setDefaultButtonStates(new boolean[]{false, false, false,false});
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

    /**
     * Display requests on the textual view.
     */
    public void showRequestsPanel() { currentState.showRequestsPanel(tour, window, this); }

    /**
     * Display tour (order of visited intersections, details of paths, length...) on textual view.
     */
    public void showTourPanel() { currentState.showTourPanel(tour, window, this); }

    /**
     * Called when user clicks on a request on textual view.
     * @param indexRequest index of clicked request
     */
    public void leftClickOnRequest(int indexRequest) {
        currentState.leftClickOnRequest(indexRequest, tour, this);
    }

    /**
     * Called when user clicks on an intersection on textual view.
     * @param indexIntersection index of clicked intersection
     */
    public void leftClickOnTourIntersection(int indexIntersection) {
        currentState.leftClickOnTourIntersection(indexIntersection, tour, this);
    }

    /**
     * Called when user clicks on "Show details" buttons on textual view.
     * @param indexShortestPath index of the shortest path clicked
     */
    public void leftClickOnShortestPath(int indexShortestPath) {
        currentState.leftClickOnShortestPath(indexShortestPath, tour, this);
    }

    /**
     * Called when user clicks on "Go back" button.
     * Go back to previous display.
     */
    public void goBackToTour() {
        currentState.goBackToTour(tour, this);
    }

    /**
     * Called when user clicks on an intersection icon on graphical view.
     * @param indexIcon index of the clicked icon
     */
    public void leftClickOnIcon(int indexIcon) {
        currentState.leftClickOnIcon(indexIcon, tour, this);
    }

    /**
     * Called when user hover a request on the textual view.
     * @param indexRequest index of the hovered request
     */
    public void enterMouseOnRequest(int indexRequest) {
        currentState.enterMouseOnRequest(indexRequest, window);
    }

    /**
     * Called when user hover an intersection on the textual view.
     * @param indexShortestPath index of the hovered request
     */
    public void enterMouseOnTourIntersection(int indexShortestPath) {
        currentState.enterMouseOnTourIntersection(indexShortestPath, window);
    }

    /**
     * Called when mouse leave a request on the textual view.
     * @param indexRequest
     */
    public void exitMouseOnRequest(int indexRequest) {
        currentState.exitMouseOnRequest(indexRequest, tour, window);
    }

    /**
     * Called when mouse leave an intersection on the textual view.
     * @param indexShortestPath
     */
    public void exitMouseOnTourIntersection(int indexShortestPath) {
        currentState.exitMouseOnTourIntersection(indexShortestPath, tour, window);
    }

    /**
     * Set specific cursor when user's mouse hovers an icon on the graphical view.
     */
    public void moveMouseOnIcon() {
        currentState.moveMouseOnIcon(window);
    }

    /**
     * Delete a request in an already computed tour.
     * @param indexRequest index of the request to delete
     */
    public void deleteRequest(int indexRequest) {
        Request requestToDelete = tour.getPlanningRequests().get(indexRequest);
        currentState.deleteRequest(tour, requestToDelete, indexRequest, cityMap.getIntersections(), window, listOfCommands);
    }

    /**
     * Add a request in an already computed tour.
     *
     */
    public void insertRequest() {
        //TODO create the Request via UI
        Request requestToAdd = null;
        float[] hsv = new float[3];
        Color initialColor = Color.pink;
        Color.RGBtoHSB(initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue(), hsv);
        double goldenRatioConjugate = 0.618033988749895;
        hsv[0] += goldenRatioConjugate;
        hsv[0] %= 1;
        Optional<Intersection> pickupAddress = cityMap.getIntersections().stream().filter(i -> i.getId() == 3).findFirst();
        Optional<Intersection> deliveryAddress = cityMap.getIntersections().stream().filter(i -> i.getId() == 1).findFirst();
        if (pickupAddress.isPresent() && deliveryAddress.isPresent()) {
            Color requestColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
            requestToAdd = new Request(100, 200, pickupAddress.get(), deliveryAddress.get(), requestColor);
            window.setUndoButtonState(true);
        }
        currentState.insertRequest(tour, requestToAdd, cityMap.getIntersections(), window, listOfCommands);
    }

    /**
     * Undo last command.
     */
    public void undo() {
        currentState.undo(listOfCommands, window);
    }

    /**
     * For a computed tour, change order by visiting an intersection (pickup or delivery address) earlier.
     * @param indexIntersection the intersection to visit earlier
     */
    public void moveIntersectionBefore(int indexIntersection) {
        currentState.moveIntersectionBefore(listOfCommands, tour, indexIntersection, cityMap.getIntersections(), window);
    }

    /**
     * For a computed tour, change order by visiting an intersection (pickup or delivery address) later.
     * @param indexIntersection the intersection to visit later
     */
    public void moveIntersectionAfter(int indexIntersection) {
        currentState.moveIntersectionAfter(listOfCommands, tour, indexIntersection, cityMap.getIntersections(), window);
    }

    /**
     * Stop TSP solving computation.
     */
    public void stopTourComputation() {
        currentState.stopTourComputation(tour);
    }
}