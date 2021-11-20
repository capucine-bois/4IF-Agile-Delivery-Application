package controller;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 * Computed tour state. State of the application when a tour has been computed.
 */
public class ComputedTourState implements State {

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

}