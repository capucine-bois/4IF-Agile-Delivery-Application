package controller;

import model.*;
import view.Window;

/**
 * Request loaded state. State when the application has successfully loaded requests.
 */
public class RequestsLoadedState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        super.loadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        defaultLoadRequests(cityMap, tour, window, controller);
    }

    @Override
    public void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        controller.setCurrentState(controller.tourComputingState);
        window.showTourPanel();
        window.setEnabledTour(true);
        window.showComputingPanel();
        Thread TSPThread = new Thread(() -> {
            // TODO: first call compute scc and dijkstra and add conditions for tourComputed to stop scc or dijkstra when stop button clicked
            tour.computeTour(cityMap.getIntersections());
            window.hideComputingPanel();
            if (!tour.getListShortestPaths().isEmpty()) {
                window.showTourPanel();
                controller.setCurrentState(controller.tourComputedState);
            } else {
                window.showRequestsPanel();
                window.setEnabledTour(false);
                controller.setCurrentState(controller.requestsLoadedState);
            }
            tour.notifyObservers();
        });
        TSPThread.start();
    }

    @Override
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {
        defaultLeftClickOnRequest(indexRequest, tour);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        int indexRequest = indexIcon%2 == 0 ? indexIcon/2 - 1 : indexIcon/2;
        leftClickOnRequest(indexRequest, tour, controller);
    }

    @Override
    public void enterMouseOnRequest(int indexRequest, Window window) {
        window.colorRequestPanelOnMouseEntered(indexRequest);
    }

    @Override
    public void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {
        Request request = tour.getPlanningRequests().get(indexRequest);
        if (!request.isDeliverySelected() || !request.isPickupSelected()) {
            window.colorRequestPanelOnMouseExited(indexRequest);
        }
    }

    @Override
    public void moveMouseOnIcon(Window window) {
        window.setHandCursorOnIcon();
    }
}