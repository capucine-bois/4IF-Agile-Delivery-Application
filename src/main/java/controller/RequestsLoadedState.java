package controller;

import model.*;
import view.Window;
import xml.XMLDeserializer;

/**
 * Request loaded state. State when the application has successfully loaded requests.
 */
public class RequestsLoadedState implements State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        State.super.loadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
    }

    @Override
    public void computeTour(CityMap cityMap, Tour tour, Window window, Controller controller) {
        tour.computeTour(cityMap.getIntersections());
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        controller.setCurrentState(controller.tourComputedState);
        window.showTourPanel();
        window.setEnabledTour(true);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        int indexRequest = indexIcon%2 == 0 ? indexIcon/2 - 1 : indexIcon/2;
        leftClickOnRequest(indexRequest, tour, controller);
        controller.setCurrentState(controller.selectedIntersectionState);
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
}