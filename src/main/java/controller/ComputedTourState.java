package controller;

import model.CityMap;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

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
            controller.setCurrentState(controller.requestsLoadedState);
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

}