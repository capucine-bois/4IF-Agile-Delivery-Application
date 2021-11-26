package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

/**
 * Computed tour state. State of the application when a tour has been computed.
 */
public class ComputedTourState implements State {


    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadMap(cityMap);
            controller.setCurrentState(controller.mapLoadedState);
            tour.clearLists();
            window.showRequestsPanel();
            window.setEnabledRequests(false);
            window.setEnabledTour(false);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                cityMap.getIntersections().clear();
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.initialState);
                window.showRequestsPanel();
                window.setEnabledRequests(false);
                window.setEnabledTour(false);
            }
        } finally {
            tour.notifyObservers();
        }
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.loadRequests(tour, cityMap);
            controller.setCurrentState(controller.requestsLoadedState);
            window.showRequestsPanel();
            window.setEnabledTour(false);
        } catch (Exception e) {
            if(!e.getMessage().equals("Cancel opening file")) {
                tour.clearLists();
                window.displayErrorMessage(e.getMessage());
                controller.setCurrentState(controller.mapLoadedState);
                window.showRequestsPanel();
                window.setEnabledRequests(false);
                window.setEnabledTour(false);
            }
        } finally {
            tour.notifyObservers();
        }
    }

    @Override
    public void showRequestsPanel(Window window) {
        window.showRequestsPanel();
    }

    @Override
    public void showTourPanel(Window window) {
        window.showTourPanel();
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

    @Override
    public void leftClickOnShortestPath(int indexShortestPath, Tour tour) {
        ShortestPath shortestPath = tour.getListShortestPaths().get(indexShortestPath);
        shortestPath.setSelected(true);
        tour.notifyObservers();
    }

    @Override
    public void goBackToTour(Tour tour) {
        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            shortestPath.setSelected(false);
        }
        tour.notifyObservers();
    }
}