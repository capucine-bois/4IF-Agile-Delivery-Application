package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

import java.util.Optional;

/**
 * Computed tour state. State of the application when a tour has been computed.
 */
public class TourComputedState implements State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        State.super.loadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
    }

    @Override
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {}

    @Override
    public void showRequestsPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        window.showRequestsPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.requestsComputedState);
    }

    @Override
    public void leftClickOnTourIntersection(int indexShortestPath, Tour tour, Controller controller) {
        ShortestPath shortestPath = tour.getListShortestPaths().get(indexShortestPath);
        Request requestClicked;
        boolean pickupWasSelected = false;
        boolean deliveryWasSelected = false;
        if (shortestPath.getEndNodeNumber() % 2 == 1) {
            requestClicked = tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2);
            pickupWasSelected = requestClicked.isPickupSelected();
        } else {
            requestClicked = tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2 - 1);
            deliveryWasSelected = requestClicked.isDeliverySelected();
        }
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        if (!pickupWasSelected && shortestPath.getEndNodeNumber() % 2 == 1)  {
            requestClicked.setPickupSelected(true);
        } else if (!deliveryWasSelected && shortestPath.getEndNodeNumber() % 2 == 0) {
            requestClicked.setDeliverySelected(true);
        }
        tour.notifyObservers();
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void leftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        ShortestPath shortestPath = tour.getListShortestPaths().get(indexShortestPath);
        shortestPath.setSelected(true);
        if (shortestPath.getStartNodeNumber() != 0) {
            if (shortestPath.getStartNodeNumber() % 2 == 1) {
                tour.getPlanningRequests().get(shortestPath.getStartNodeNumber() / 2).setPickupSelected(true);
            } else {
                tour.getPlanningRequests().get(shortestPath.getStartNodeNumber() / 2 - 1).setDeliverySelected(true);
            }
        }
        if (shortestPath.getEndNodeNumber() != 0) {
            if (shortestPath.getEndNodeNumber() % 2 == 1) {
                tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2).setPickupSelected(true);
            } else {
                tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2 - 1).setDeliverySelected(true);
            }
        }
        controller.setCurrentState(controller.pathDetailsComputedState);
        tour.notifyObservers();
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        ShortestPath shortestPath = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexIcon).findFirst().get();
        leftClickOnTourIntersection(tour.getListShortestPaths().indexOf(shortestPath), tour, controller);
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void enterMouseOnTourIntersection(int indexShortestPath, Window window) {
        window.colorTourIntersectionPanelOnMouseEntered(indexShortestPath);
    }

    @Override
    public void exitMouseOnTourIntersection(int indexShortestPath, Tour tour, Window window) {
        window.colorTourIntersectionPanelOnMouseExited(indexShortestPath);
    }
}