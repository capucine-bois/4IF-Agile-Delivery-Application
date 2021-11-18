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
     * @param cityMap the map structure to fill
     * @param window the window where to show map and popup messages
     */
    public void loadMap(CityMap cityMap, Window window, Controller controller);

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public void loadRequests(Tour tour, Window window, Controller controller);

    /**
     * Close error message.
     */
    public void closeError();

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     */
    public void computeTour();

}