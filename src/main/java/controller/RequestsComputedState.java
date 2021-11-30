package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

import java.util.Optional;

public class RequestsComputedState implements State{

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
    public void showTourPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        window.showTourPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.tourComputedState);
    }

    @Override
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {
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
        controller.setCurrentState(controller.selectedRequestState);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        ShortestPath shortestPath = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexIcon).findFirst().get();
        leftClickOnTourIntersection(tour.getListShortestPaths().indexOf(shortestPath), tour, controller);
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void enterMouseOnRequest(int indexRequest, Window window) {
        window.colorRequestPanelOnMouseEntered(indexRequest);
    }

    @Override
    public void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {
        window.colorRequestPanelOnMouseExited(indexRequest);
    }
}
