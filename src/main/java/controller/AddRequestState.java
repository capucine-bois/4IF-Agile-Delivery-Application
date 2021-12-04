package controller;

import model.CityMap;
import model.Tour;
import view.Window;

public class AddRequestState extends State{

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {}

    @Override
    public void chooseAddress(Tour tour, Window window, Controller controller) {
        window.enterSelectionMode();
        controller.setCurrentState(controller.selectionIntersectionState);
    }

    @Override
    public void cancel(Tour tour, Window window, Controller controller) {
        window.setEnabledTour(true);
        window.showRequestsPanel();
        tour.setNewRequest(null);
        controller.setCurrentState(controller.requestsComputedState);
        tour.notifyObservers();
    }
}
