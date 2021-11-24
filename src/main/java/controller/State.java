package controller;

import model.CityMap;
import model.Tour;
import view.Window;

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
    public default void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {};

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param cityMap the map with the intersections
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public default void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {};

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     * @param cityMap the city map which contains the list of intersections of the map file
     * @param tour the tour which contains all the requests
     * @param controller the controller of our application
     */
    public default void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {};

    public default void showRequestsPanel(Window window) {};

    public default void showTourPanel(Window window) {};

    public default void leftClickOnRequest(int indexRequest, Tour tour) {};

    public default void leftClickOnShortestPath(int indexShortestPath, Tour tour) {};

    public default void goBackToTour(Tour tour) {};
}