package controller;

import model.CityMap;
import model.Tour;
import view.Window;

public class SelectionIntersectionState extends State {

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {}

    @Override
    public void cancel(Tour tour, Window window, Controller controller) {
        window.exitSelectionMode();
        controller.setCurrentState(controller.addRequestState);
    }
}
