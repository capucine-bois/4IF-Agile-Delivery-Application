package controller;

import model.*;
import view.Window;

import java.util.ArrayList;

/**
 * State of the application when the user wants to add a request a
 * is selecting the delivery address on the map.
 */
public class DeliveryAddressSelectionState extends State {

    @Override
    public void cancel(Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {
        window.exitSelectionMode();
        controller.setCurrentState(controller.addRequestState);
    }

    @Override
    public void leftClickOnIntersection(int indexIntersection, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {

        ArrayList<Intersection> intersectionsToTest = new ArrayList<>();
        intersectionsToTest.add(cityMap.getIntersections().get(indexIntersection));
        if (!StronglyConnectedComponents.getAllUnreachableIntersections((ArrayList<Intersection>) cityMap.getIntersections(), tour.getDepotAddress(), intersectionsToTest).isEmpty()) {
            window.displayErrorMessage("The selected address is unreachable.");
        } else {
            window.exitSelectionMode();
            tour.getNewRequest().setDeliveryAddress(cityMap.getIntersections().get(indexIntersection));
            controller.setCurrentState(controller.addRequestState);
            tour.notifyObservers();
        }

    }
}
