package controller;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 * Request loaded state. State when the application has successfully loaded requests.
 */
public class RequestsLoadedState implements State {

    /**
     * Loading a map (intersections and segments) from XML file.
     * Deserialize XML map file selected by user and returned by GUI window.
     * @param cityMap the map structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    @Override
    public void loadMap(CityMap cityMap, Window window, Controller controller) {

    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    @Override
    public void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {

    }

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     */
    @Override
    public void computeTour() {
        // TODO implement here
    }

}