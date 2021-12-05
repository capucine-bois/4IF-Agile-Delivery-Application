package controller;

import model.CityMap;
import model.Tour;
import view.Window;

public class DeliveryAddressSelectionState extends State {

    @Override
    public void cancel(Tour tour, Window window, Controller controller) {
        window.exitSelectionMode();
        controller.setCurrentState(controller.addRequestState);
    }

    @Override
    public void leftClickOnIntersection(int indexIntersection, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {
        window.exitSelectionMode();
        // TODO: check if address chosen is in the same scc as the depot address
        tour.getNewRequest().setDeliveryAddress(cityMap.getIntersections().get(indexIntersection));
        controller.setCurrentState(controller.addRequestState);
        tour.notifyObservers();
    }
}
