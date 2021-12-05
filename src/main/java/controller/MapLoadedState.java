package controller;

import model.*;
import view.Window;
import xml.ExceptionXML;
import xml.XMLDeserializer;

import java.awt.*;
import java.util.ArrayList;

/**
 * Map loaded state. State of the application when map has been loaded.
 */
public class MapLoadedState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        defaultLoadMap(cityMap, tour, window, controller);
        cityMap.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            Request.lastColor = Color.red;
            XMLDeserializer.loadRequests(tour, cityMap);
            tour.checkIntersectionsUnreachable(cityMap.getIntersections());
            if (!tour.getIntersectionsUnreachableFromDepot().isEmpty()) {
                throw new ExceptionXML("An address in the planning is unreachable.");
            }
            window.setDefaultButtonStates(new boolean[]{true, true, true});
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