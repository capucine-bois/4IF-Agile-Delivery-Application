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
                window.setEnabledTour(false);
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
        Request requestClicked = tour.getPlanningRequests().get(indexRequest);
        if (requestClicked.isSelected()) {
            requestClicked.setSelected(false);
        } else {
            requestClicked.setSelected(true);
            for (int i = 0; i < tour.getPlanningRequests().size(); i++) {
                if (i != indexRequest) tour.getPlanningRequests().get(i).setSelected(false);
            }
        }
        tour.notifyObservers();
    }
}