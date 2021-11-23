package controller;

import model.CityMap;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

/**
 * Map loaded state. State of the application when map has been loaded.
 */
public class MapLoadedState implements State {


    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadMap(cityMap);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                cityMap.getIntersections().clear();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.initialState);
            }
        } finally {
            cityMap.notifyObservers();
        }
    }


    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadRequests(tour, cityMap);
            controller.setCurrentState(controller.requestsLoadedState);
            tour.notifyObservers();
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
            }
        }
    }
}