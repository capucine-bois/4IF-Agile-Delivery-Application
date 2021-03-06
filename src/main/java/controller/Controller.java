package controller;

import java.awt.*;
import java.io.IOException;

import model.*;
import view.Window;

/**
 * Controller of our application, able to change the current state and call methods corresponding to main features.
 */
public class Controller {

    private State currentState;
    private final Window window;
    private final CityMap cityMap;
    private final Tour tour;
    private final ListOfCommands listOfCommands;

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
    protected final AddRequestState addRequestState = new AddRequestState();
    protected final PickupAddressSelectionState pickupAddressSelectionState = new PickupAddressSelectionState();
    protected final DeliveryAddressSelectionState deliveryAddressSelectionState = new DeliveryAddressSelectionState();
    protected final ChangeAddressState changeAddressState = new ChangeAddressState();
    protected final ChangeProcessTimeState changeProcessTimeState = new ChangeProcessTimeState();

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
        this.currentState = state;
    }

    // Methods corresponding to user events

    /**
     * Loading a map (intersections and segments) from XML file.
     * The behavior depends on the current state.
     */
    public void loadMap() {
        currentState.loadMap(cityMap, tour, window, this, listOfCommands);
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * The behavior depends on the current state.
     */
    public void loadRequests() {
        currentState.loadRequests(cityMap, tour, window, this, listOfCommands);
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
     * @param indexRequest the request the mouse left
     */
    public void exitMouseOnRequest(int indexRequest) {
        currentState.exitMouseOnRequest(indexRequest, tour, window);
    }

    /**
     * Called when mouse leave an intersection on the textual view.
     * @param indexShortestPath the shortest path the mouse left
     */
    public void exitMouseOnTourIntersection(int indexShortestPath) {
        currentState.exitMouseOnTourIntersection(indexShortestPath, tour, window);
    }

    /**
     * Delete a request in an already computed tour.
     * @param indexRequest index of the request to delete
     */
    public void deleteRequest(int indexRequest) {
        currentState.deleteRequest(indexRequest, tour, cityMap, window, listOfCommands, this);
    }

    /**
     * Start the process of adding a request.
     *
     */
    public void addRequest() {
        currentState.addRequest(tour, window, this);
    }

    /**
     * Undo last command.
     */
    public void undo() {
        currentState.undo(tour, listOfCommands, window, this);
    }

    /**
     * Redo last command.
     */
    public void redo() {
        currentState.redo(tour, listOfCommands, window, this);
    }

    /**
     * For a computed tour, change order by visiting an intersection (pickup or delivery address) earlier.
     * @param indexShortestPath the intersection to visit earlier
     */
    public void moveIntersectionBefore(int indexShortestPath) {
        currentState.moveIntersectionBefore(listOfCommands, tour, indexShortestPath, cityMap.getIntersections(), window);
    }

    /**
     * For a computed tour, change order by visiting an intersection (pickup or delivery address) later.
     * @param indexShortestPath the intersection to visit later
     */
    public void moveIntersectionAfter(int indexShortestPath) {
        currentState.moveIntersectionAfter(listOfCommands, tour, indexShortestPath, cityMap.getIntersections(), window);
    }

    /**
     * Stop TSP solving computation.
     */
    public void stopTourComputation() {
        currentState.stopTourComputation(tour);
    }

    public void chooseAddress(int indexButton) {
        currentState.chooseAddress(indexButton, window, this);
    }

    public void cancel() {
        currentState.cancel(tour, window, listOfCommands, this);
    }

    public void leftClickOnIntersection(int indexIntersection) {
        currentState.leftClickOnIntersection(indexIntersection, cityMap, tour, window, listOfCommands, this);
    }

    /**
     * Add a request to an already computed tour.
     * @param pickupTime
     * @param deliveryTime
     */
    public void insertRequest(String pickupTime, String deliveryTime) {
        currentState.insertRequest(pickupTime, deliveryTime, cityMap, tour, window, listOfCommands, this);
    }

    public ListOfCommands getListOfCommands() {
        return listOfCommands;
    }

    public void changeAddress() {
        currentState.changeAddress(window, this);
    }

    public void changeTime() {
        currentState.changeTime(window, this);
    }

    public void saveTime(String time) {
        currentState.saveTime(time, tour, window, listOfCommands, this);
    }

    public void arrowKeyPressed(boolean up){
        currentState.arrowKeyPressed(up, cityMap, tour, listOfCommands, window);
    }

}