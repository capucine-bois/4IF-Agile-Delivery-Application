package controller;

import model.*;
import view.Window;
import xml.XMLDeserializer;

import javax.swing.*;

/**
 * Request loaded state. State when the application has successfully loaded requests.
 */
public class RequestsLoadedState implements State {


    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadMap(cityMap);
            controller.setCurrentState(controller.mapLoadedState);
            tour.clearLists();
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                cityMap.getIntersections().clear();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.initialState);
            }
        } finally {
            tour.notifyObservers();
            window.setEnabledRequests(false);
        }
    }


    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadRequests(tour, cityMap);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.mapLoadedState);
                window.setEnabledRequests(false);
            }
        } finally {
            tour.notifyObservers();
        }
    }


    @Override
    public void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {
        tour.computeTour(cityMap.getIntersections());
        controller.setCurrentState(controller.computedTourState);
        window.showTourPanel();
        window.setEnabledTour(true);
    }

    @Override
    public void leftClickOnRequest(int indexRequest, Tour tour) {
        for (int i = 0; i < tour.getPlanningRequests().size(); i++) {
            Request request = tour.getPlanningRequests().get(i);
            if (i != indexRequest || (request.isPickupSelected() && request.isDeliverySelected())) {
                request.setPickupSelected(false);
                request.setDeliverySelected(false);
            } else {
                request.setPickupSelected(true);
                request.setDeliverySelected(true);
            }
        }
        tour.notifyObservers();
    }
}