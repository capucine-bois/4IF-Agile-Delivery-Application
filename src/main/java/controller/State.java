package controller;

import model.*;
import view.Window;
import xml.XMLDeserializer;

import java.awt.*;
import java.util.List;

/**
 * Abstract class for state design pattern.
 * Define every method corresponding to main features.
 */
public abstract class State {

    public void defaultLoadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        try {
            XMLDeserializer.loadMap(cityMap);
            window.setDefaultButtonStates(new boolean[]{true, true, false});
            controller.setCurrentState(controller.mapLoadedState);
            tour.clearLists();
            listOfCommands.reset();
            window.showRequestsPanel();
            window.setEnabledRequests(false);
            window.setEnabledTour(false);
            window.setRedoButtonState(false);
            window.setUndoButtonState(false);
            tour.setTourComputed(false);
        } catch (Exception e) {
            if (!e.getMessage().equals("Cancel opening file")) {
                cityMap.clearLists();
                tour.clearLists();
                listOfCommands.reset();
                window.setRedoButtonState(false);
                window.setUndoButtonState(false);
                window.displayErrorMessage(e.getMessage());
                window.setDefaultButtonStates(new boolean[]{true, false, false});
                controller.setCurrentState(controller.initialState);
                window.showRequestsPanel();
                window.setEnabledRequests(false);
                window.setEnabledTour(false);
                tour.setTourComputed(false);
            }
        }
    }

    protected void defaultLoadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        try {
            Request.lastColor = Color.red;
            XMLDeserializer.loadRequests(tour, cityMap);
            if (!tour.checkIntersectionsUnreachable(cityMap.getIntersections())) {
                window.displayErrorMessage("An address in the planning is unreachable.");
                Request.lastColor = Color.red;
                tour.clearLists();
                window.setDefaultButtonStates(new boolean[]{true, true, false});
                controller.setCurrentState(controller.mapLoadedState);
                window.setEnabledRequests(false);
            } else {
                window.setDefaultButtonStates(new boolean[]{true, true, true});
                controller.setCurrentState(controller.requestsLoadedState);
            }
            listOfCommands.reset();
            window.showRequestsPanel();
            window.setEnabledTour(false);
            window.setRedoButtonState(false);
            window.setUndoButtonState(false);
            tour.setTourComputed(false);
            controller.getListOfCommands().reset();
        } catch (Exception e) {
            if (!e.getMessage().equals("Cancel opening file")) {
                Request.lastColor = Color.red;
                tour.clearLists();
                listOfCommands.reset();
                window.setRedoButtonState(false);
                window.setUndoButtonState(false);
                window.displayErrorMessage(e.getMessage());
                window.setDefaultButtonStates(new boolean[]{true, true, false});
                controller.setCurrentState(controller.mapLoadedState);
                window.showRequestsPanel();
                window.setEnabledRequests(false);
                window.setEnabledTour(false);
                tour.setTourComputed(false);
            }
        } finally {
            tour.notifyObservers();
        }
    }

    /**
     * Default behaviour when users click on a request on the textual view.
     * Set request to selected or not (if it was already), and update GUI to change its background.
     *
     * @param indexRequest index of the clicked request
     * @param tour         the tour with the clicked request
     */
    protected void defaultLeftClickOnRequest(int indexRequest, Tour tour) {
        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            shortestPath.setSelected(false);
        }
        for (int i = 0; i < tour.getPlanningRequests().size(); i++) {
            Request request = tour.getPlanningRequests().get(i);
            if (i != indexRequest || (request.isPickupSelected() && request.isDeliverySelected())) {
                request.setPickupSelected(false);
                request.setDeliverySelected(false);
            } else {
                request.setPickupSelected(true);
                request.setDeliverySelected(true);
                if (!tour.getListShortestPaths().isEmpty() && tour.isTourComputed()) {
                    int finalI = i;
                    int indexShortestPathFromPickup = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getStartNodeNumber() == finalI * 2 + 1).findFirst().get());
                    int indexShortestPathToDelivery = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == finalI * 2 + 2).findFirst().get());
                    for (int j = indexShortestPathFromPickup; j <= indexShortestPathToDelivery; j++) {
                        tour.getListShortestPaths().get(j).setSelected(true);
                    }
                }
            }
        }
        tour.notifyObservers();
    }

    /**
     * Default behaviour when users click on an intersection on the textual view.
     * Set intersection to selected or not (if it was already), and update GUI to change its background and its icon
     * on the graphical view.
     *
     * @param indexShortestPath index of shortest path
     * @param tour              tour
     * @return whether an intersection is currently selected or not
     */
    protected boolean defaultLeftClickOnTourIntersection(int indexShortestPath, Tour tour) {
        ShortestPath shortestPath = tour.getListShortestPaths().get(indexShortestPath);
        Request requestClicked;
        boolean pickupWasSelected = false;
        boolean deliveryWasSelected = false;
        if (shortestPath.getEndNodeNumber() % 2 == 1) {
            requestClicked = tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2);
            pickupWasSelected = requestClicked.isPickupSelected();
        } else {
            requestClicked = tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2 - 1);
            deliveryWasSelected = requestClicked.isDeliverySelected();
        }
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        if (!pickupWasSelected && shortestPath.getEndNodeNumber() % 2 == 1) {
            requestClicked.setPickupSelected(true);
        } else if (!deliveryWasSelected && shortestPath.getEndNodeNumber() % 2 == 0) {
            requestClicked.setDeliverySelected(true);
        }
        tour.notifyObservers();
        return pickupWasSelected || deliveryWasSelected;
    }

    protected void defaultLeftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        ShortestPath shortestPath = tour.getListShortestPaths().get(indexShortestPath);
        shortestPath.setSelected(true);
        if (shortestPath.getStartNodeNumber() != 0) {
            if (shortestPath.getStartNodeNumber() % 2 == 1) {
                tour.getPlanningRequests().get(shortestPath.getStartNodeNumber() / 2).setPickupSelected(true);
            } else {
                tour.getPlanningRequests().get(shortestPath.getStartNodeNumber() / 2 - 1).setDeliverySelected(true);
            }
        }
        if (shortestPath.getEndNodeNumber() != 0) {
            if (shortestPath.getEndNodeNumber() % 2 == 1) {
                tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2).setPickupSelected(true);
            } else {
                tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2 - 1).setDeliverySelected(true);
            }
        }
        controller.setCurrentState(controller.pathDetailsComputedState);
        tour.notifyObservers();
    }

    protected void defaultUndo(Tour tour, ListOfCommands l, Window window) {
        l.undo();
        checkIfUndoOrRedoPossible(l, window);
        resetSelectionRequestAndShortestPath(tour);
    }


    public void defaultRedo(Tour tour, ListOfCommands l, Window window) {
        l.redo();
        checkIfUndoOrRedoPossible(l, window);
        resetSelectionRequestAndShortestPath(tour);
    }

    protected void resetSelectionRequestAndShortestPath(Tour tour) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            shortestPath.setSelected(false);
        }
        tour.notifyObservers();
    }

    protected void checkIfUndoOrRedoPossible(ListOfCommands listOfCommands, Window window) {
        window.setUndoButtonState(listOfCommands.undoPossible());
        window.setRedoButtonState(listOfCommands.redoPossible());
    }

    protected Request defaultDeleteRequest(int indexRequest, Tour tour, CityMap cityMap, Window window, ListOfCommands listOfCommands) {
        Request requestToDelete = tour.getPlanningRequests().get(indexRequest);
        int indexShortestPathToPickup = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 1).findFirst().get());
        int indexShortestPathToDelivery = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 2).findFirst().get());
        listOfCommands.add(new ReverseCommand(new AddCommand(tour, requestToDelete, cityMap.getIntersections(), indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery)));
        checkIfUndoOrRedoPossible(listOfCommands, window);
        return requestToDelete;
    }

    protected void defaultAddRequest(Tour tour, Window window, Controller controller) {
        resetSelectionRequestAndShortestPath(tour);
        tour.setNewRequest(new Request());
        window.showAddRequestPanel();
        window.setEnabledTour(false);
        window.setDefaultButtonStates(new boolean[]{false, false, false});
        controller.setCurrentState(controller.addRequestState);
        window.setUndoButtonState(false);
        window.setRedoButtonState(false);
        tour.notifyObservers();
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     *
     * @param cityMap        the map with the intersections
     * @param tour           the tour structure to fill
     * @param window         the window where to show map and popup messages
     * @param controller     application controller
     * @param listOfCommands
     */
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {}

    /**
     * Loading a map (intersections and segments) from XML file.
     * Deserialize XML map file selected by user and returned by GUI window.
     *
     * @param cityMap        the map structure to fill
     * @param tour           the tour to clear
     * @param window         the window where to show map and popup messages
     * @param controller     application controller
     * @param listOfCommands
     */
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {}

    /**
     * Called when user clicks on an intersection on the textual view.
     *
     * @param indexShortestPath index of the clicked
     * @param tour              tour with the intersections and the paths to show
     * @param controller        application controller
     */
    public void leftClickOnTourIntersection(int indexShortestPath, Tour tour, Controller controller) {}

    public void chooseAddress(int indexButton, Window window, Controller controller) {}

    public void cancel(Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {}

    public void leftClickOnIntersection(int indexIntersection, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {}

    public void insertRequest(String pickupTime, String deliveryTime, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {}

    public void changeAddress(Window window, Controller controller) {}

    public void changeTime(Window window, Controller controller) {}

    public void saveTime(String time, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {}

    public void arrowKeyPressed(boolean up, CityMap map, Tour tour, ListOfCommands listOfCommands, Window window) {}

    public void enterMouseOnTourIntersection(int indexShortestPath, Window window) {}

    public void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {}

    public void moveIntersectionBefore(ListOfCommands l, Tour tour, int indexIntersection, List<Intersection> allIntersections, Window window) {}

    public void moveIntersectionAfter(ListOfCommands l, Tour tour, int indexIntersection, List<Intersection> allIntersections, Window window) {}

    public void stopTourComputation(Tour tour) {}

    public void addRequest(Tour tour, Window window, Controller controller) {}

    public void deleteRequest(int indexRequest, Tour tour, CityMap cityMap, Window window, ListOfCommands listOfCommands, Controller controller) {}

    public void redo(Tour tour, ListOfCommands l, Window window, Controller controller) {}

    public void undo(Tour tour, ListOfCommands l, Window window, Controller controller) {}

    public void goBackToTour(Tour tour, Controller controller) {}

    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {}

    public void enterMouseOnRequest(int indexRequest, Window window) {}

    public void exitMouseOnTourIntersection(int indexShortestPath, Tour tour, Window window) {}

    public void leftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {}

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     *
     * @param cityMap    the city map which contains the list of intersections of the map file
     * @param tour       the tour which contains all the requests
     * @param controller the controller of our application
     */
    public void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {}

    /**
     * Display requests on the textual view.
     *
     * @param tour       tour with the requests to show
     * @param window     the GUI
     * @param controller application controller
     */
    public void showRequestsPanel(Tour tour, Window window, Controller controller) {}

    /**
     * Display tour intersections and paths on the textual view.
     *
     * @param tour       tour with the intersections and the paths to show
     * @param window     the GUI
     * @param controller application controller
     */
    public void showTourPanel(Tour tour, Window window, Controller controller) {}

    /**
     * Called when user clicks on a request on the textual view.
     *
     * @param indexRequest index of the clicked request
     * @param tour         tour with the clicked request
     * @param controller   application controller
     */
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {}

}