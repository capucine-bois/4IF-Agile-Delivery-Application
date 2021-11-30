package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

import java.util.Optional;

public class PathDetailsComputedState implements State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        State.super.loadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
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
    public void showRequestsPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }

        for (ShortestPath path : tour.getListShortestPaths()){
            path.setSelected(false);
        }

        window.showRequestsPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.requestsComputedState);
    }

    @Override
    public void showTourPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        window.showTourPanel();
        Optional<ShortestPath> optionalShortestPath = tour.getListShortestPaths().stream().filter(ShortestPath::isSelected).findFirst();
        if (optionalShortestPath.isPresent()) {
            leftClickOnShortestPath(tour.getListShortestPaths().indexOf(optionalShortestPath.get()), tour, controller);
        } else {
            tour.notifyObservers();
        }
        controller.setCurrentState(controller.tourComputedState);
    }

    @Override
    public void goBackToTour(Tour tour, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            shortestPath.setSelected(false);
        }
        tour.notifyObservers();
        controller.setCurrentState(controller.tourComputedState);
    }

}
