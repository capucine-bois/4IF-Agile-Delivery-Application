package controller;

import model.CityMap;
import model.Intersection;
import model.StronglyConnectedComponents;
import model.Tour;
import view.Window;
import xml.ExceptionXML;
import xml.XMLDeserializer;

import java.util.ArrayList;

/**
 * Map loaded state. State of the application when map has been loaded.
 */
public class MapLoadedState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        super.loadMap(cityMap, tour, window, controller);
        cityMap.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadRequests(tour, cityMap);
            tour.checkIntersectionsUnreachable(cityMap.getIntersections());
            if (!tour.getIntersectionsUnreachableFromDepot().isEmpty()) {
                throw new ExceptionXML("An address in the planning is unreachable.");
            }
            controller.setCurrentState(controller.requestsLoadedState);
            tour.notifyObservers();
            window.setEnabledRequests(true);
            window.showRequestsPanel();
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
            }
        }
    }
}