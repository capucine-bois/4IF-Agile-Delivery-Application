package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;

/**
 * State when tour has been computed and path details are shown on the textual view.
 */
public class PathDetailsComputedState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadMap(cityMap, tour, window, controller, listOfCommands);
        tour.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadRequests(cityMap, tour, window, controller, listOfCommands);
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
