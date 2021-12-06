package controller;

import model.*;
import view.Window;
import xml.ExceptionXML;

import java.util.ArrayList;

public class ChangeAddressState extends State {

    @Override
    public void cancel(Tour tour, Window window, Controller controller) {
        window.exitSelectionMode();
        window.setDefaultButtonStates(new boolean[]{true, true, false});
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void leftClickOnIntersection(int indexIntersection, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {

        ArrayList<Intersection> intersectionsToTest = new ArrayList<>();
        intersectionsToTest.add(cityMap.getIntersections().stream().filter(x -> x.getId()==indexIntersection).findFirst().get());
        if (!StronglyConnectedComponents.getAllUnreachableIntersections((ArrayList<Intersection>) cityMap.getIntersections(), tour.getDepotAddress(), intersectionsToTest).isEmpty()) {
            window.displayErrorMessage("The selected address is unreachable.");
        } else {
            window.exitSelectionMode();
            Request requestToUpdate = tour.getPlanningRequests().stream().filter(x -> x.isPickupSelected() || x.isDeliverySelected()).findFirst().get();
            int indexRequest = tour.getPlanningRequests().indexOf(requestToUpdate);
            Intersection newAddress = cityMap.getIntersections().get(indexIntersection);
            if (requestToUpdate.isPickupSelected()) {
                listOfCommands.add(new ChangeAddressCommand(tour, indexRequest * 2 + 1, requestToUpdate.getPickupAddress(), newAddress, cityMap.getIntersections()));
            } else {
                listOfCommands.add(new ChangeAddressCommand(tour, indexRequest * 2 + 2, requestToUpdate.getDeliveryAddress(), newAddress, cityMap.getIntersections()));
            }
            window.setUndoButtonState(true);
            window.setDefaultButtonStates(new boolean[]{true, true, false});
            controller.setCurrentState(controller.selectedIntersectionState);
        }


    }
}
