package controller;

import model.*;
import view.Window;
import xml.XMLDeserializer;

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
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadMap(cityMap);
            controller.setCurrentState(controller.mapLoadedState);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                cityMap.getIntersections().clear();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.initialState);
            }
        } finally {
            tour.clearLists();
            tour.notifyObservers();
        }
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadRequests(tour, cityMap);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.mapLoadedState);
            }
        } finally {
            tour.notifyObservers();
        }
    }

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     */
    @Override
    public void computeTour(CityMap cityMap, Tour tour, Controller controller) {
        tour.computeTour(cityMap.getIntersections());
        controller.setCurrentState(controller.computedTourState);
    }

}