package controller;

import model.CityMap;
import model.Request;
import model.Tour;
import view.Window;

public class RequestsComputingState extends State {

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {}

    @Override
    public void showTourPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        window.showTourPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.tourComputingState);
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
        window.colorRequestPanelOnMouseExited(indexRequest);
    }

    @Override
    public void moveMouseOnIcon(Window window) {
        window.setHandCursorOnIcon();
    }
}
