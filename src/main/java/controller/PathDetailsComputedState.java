package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

import java.util.Optional;

public class PathDetailsComputedState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        super.loadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
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
