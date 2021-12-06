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
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        listOfCommands.reset();
        defaultLoadMap(cityMap, tour, window, controller, listOfCommands);
        cityMap.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        try {
            listOfCommands.reset();
            Request.lastColor = Color.red;
            XMLDeserializer.loadRequests(tour, cityMap);
            if(tour.checkIntersectionsUnreachable(cityMap.getIntersections())) {
                window.setDefaultButtonStates(new boolean[]{true, true, true});
                controller.setCurrentState(controller.requestsLoadedState);
                tour.notifyObservers();
                window.setEnabledRequests(true);
                window.showRequestsPanel();
            } else {
                tour.clearLists();
                window.displayErrorMessage("An address in the planning is unreachable.");
            }
        } catch (Exception e) {
            listOfCommands.reset();
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
            }
        }
    }
}