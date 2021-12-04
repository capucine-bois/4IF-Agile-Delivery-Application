package controller;

import model.CityMap;
import model.Tour;
import view.Window;

import javax.swing.plaf.IconUIResource;

public class AddRequestState extends State{

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {}

    @Override
    public void chooseAddress(Tour tour, Window window) {
        //window.showAllIntersections();
    }

    @Override
    public void cancelAddRequest(Window window, Controller controller) {
        window.setEnabledTour(true);
        window.showRequestsPanel();
        controller.setCurrentState(controller.requestsComputedState);
    }
}
