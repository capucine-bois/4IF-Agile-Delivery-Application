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
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public default void loadMap(CityMap cityMap, Window window, Controller controller) {};

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public default void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {};

    /**
     * Close error popup.
     */
    public default void closeError() {};

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     */
    public default void computeTour() {};

}