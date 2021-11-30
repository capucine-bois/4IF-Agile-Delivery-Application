package controller;

import model.CityMap;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

/**
 * Interface for state design pattern. Define every method corresponding to main features.
 */
public interface State {

    /**
     * Loading a map (intersections and segments) from XML file.
     * Deserialize XML map file selected by user and returned by GUI window.
     * @param cityMap the map structure to fill
     * @param tour the tour to clear
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    default void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadMap(cityMap);
            controller.setCurrentState(controller.mapLoadedState);
            tour.clearLists();
            window.showRequestsPanel();
            window.setEnabledRequests(false);
            window.setEnabledTour(false);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                cityMap.clearLists();
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.initialState);
                window.showRequestsPanel();
                window.setEnabledRequests(false);
                window.setEnabledTour(false);
            }
        }
    };

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param cityMap the map with the intersections
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    default void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {};

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     * @param cityMap the city map which contains the list of intersections of the map file
     * @param tour the tour which contains all the requests
     * @param controller the controller of our application
     */
    default void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {};

    default void showRequestsPanel(Tour tour, Window window, Controller controller) {};

    default void showTourPanel(Tour tour, Window window, Controller controller) {};

    default void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {};

    default void leftClickOnTourIntersection(int indexShortestPath, Tour tour, Controller controller) {};

    default void leftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {};

    default void goBackToTour(Tour tour, Controller controller) {};

    default void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {};

    default void enterMouseOnRequest(int indexRequest, Window window) {};

    default void enterMouseOnTourIntersection(int indexShortestPath, Window window) {};

    default void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {};

    default void exitMouseOnTourIntersection(int indexShortestPath, Tour tour, Window window) {};
}