package controller;

import model.*;
import view.Window;
import xml.XMLDeserializer;

import java.util.List;

/**
 * Abstract class for state design pattern.
 * Define every method corresponding to main features.
 */
public abstract class State {

    /**
     * Loading a map (intersections and segments) from XML file.
     * Deserialize XML map file selected by user and returned by GUI window.
     * @param cityMap the map structure to fill
     * @param tour the tour to clear
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadMap(cityMap);
            controller.setCurrentState(controller.mapLoadedState);
            tour.clearLists();
            window.showRequestsPanel();
            window.setEnabledRequests(false);
            window.setEnabledTour(false);
            tour.setTourComputed(false);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                cityMap.clearLists();
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.initialState);
                window.showRequestsPanel();
                window.setEnabledRequests(false);
                window.setEnabledTour(false);
                tour.setTourComputed(false);
            }
        }
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param cityMap the map with the intersections
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadRequests(tour, cityMap);
            controller.setCurrentState(controller.requestsLoadedState);
            window.showRequestsPanel();
            window.setEnabledTour(false);
            tour.setTourComputed(false);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
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
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     * @param cityMap the city map which contains the list of intersections of the map file
     * @param tour the tour which contains all the requests
     * @param controller the controller of our application
     */
    public void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {}

    /**
     * Display requests on the textual view.
     * @param tour tour with the requests to show
     * @param window the GUI
     * @param controller application controller
     */
    public void showRequestsPanel(Tour tour, Window window, Controller controller) {}

    /**
     * Display tour intersections and paths on the textual view.
     * @param tour tour with the intersections and the paths to show
     * @param window the GUI
     * @param controller application controller
     */
    public void showTourPanel(Tour tour, Window window, Controller controller) {}

    /**
     * Called when user clicks on a request on the textual view.
     * @param indexRequest index of the clicked request
     * @param tour tour with the clicked request
     * @param controller application controller
     */
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {}

    /**
     * Default behaviour when users click on a request on the textual view.
     * Set request to selected or not (if it was already), and update GUI to change its background.
     * @param indexRequest index of the clicked request
     * @param tour the tour with the clicked request
     */
    protected void defaultLeftClickOnRequest(int indexRequest, Tour tour) {
        for (int i = 0; i < tour.getPlanningRequests().size(); i++) {
            Request request = tour.getPlanningRequests().get(i);
            if (i != indexRequest || (request.isPickupSelected() && request.isDeliverySelected())) {
                request.setPickupSelected(false);
                request.setDeliverySelected(false);
            } else {
                request.setPickupSelected(true);
                request.setDeliverySelected(true);
            }
        }
        tour.notifyObservers();
    }

    /**
     * Called when user clicks on an intersection on the textual view.
     * @param indexShortestPath index of the clicked
     * @param tour tour with the intersections and the paths to show
     * @param controller application controller
     */
    public void leftClickOnTourIntersection(int indexShortestPath, Tour tour, Controller controller) {}

    /**
     * Default behaviour when users click on an intersection on the textual view.
     * Set intersection to selected or not (if it was already), and update GUI to change its background and its icon
     * on the graphical view.
     * @param indexShortestPath index of shortest path
     * @param tour tour
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
        if (!pickupWasSelected && shortestPath.getEndNodeNumber() % 2 == 1)  {
            requestClicked.setPickupSelected(true);
        } else if (!deliveryWasSelected && shortestPath.getEndNodeNumber() % 2 == 0) {
            requestClicked.setDeliverySelected(true);
        }
        tour.notifyObservers();
        return pickupWasSelected || deliveryWasSelected;
    }

    /**
     *
     * @param indexShortestPath
     * @param tour
     * @param controller
     */
    public void leftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {}

    /**
     *
     * @param indexShortestPath
     * @param tour
     * @param controller
     */
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

    public void goBackToTour(Tour tour, Controller controller) {}

    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {}

    public void enterMouseOnRequest(int indexRequest, Window window) {}

    public void exitMouseOnTourIntersection(int indexShortestPath, Tour tour, Window window) {}

    public void undo(ListOfCommands l, Window window) {
        l.undo();
        if (l.size() == 0) {
            window.setUndoButtonState(false);
        }
    }


    public void enterMouseOnTourIntersection(int indexShortestPath, Window window) {}

    public void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {}

    public void moveMouseOnIcon(Window window) {}

    public void moveIntersectionBefore(ListOfCommands l, Tour tour, int indexIntersection, List<Intersection> allIntersections, Window window) {}

    public void moveIntersectionAfter(ListOfCommands l, Tour tour, int indexIntersection, List<Intersection> allIntersections, Window window) {}

    public void stopTourComputation(Tour tour) {}

    public void deleteRequest(Tour tour, Request requestToDelete, int indexRequest, List<Intersection> allIntersections, Window window, ListOfCommands l) {}

    public void insertRequest(Tour tour, Request requestToAdd, List<Intersection> allIntersections, Window window, ListOfCommands l) {}

}