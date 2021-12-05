package controller;

import model.CityMap;
import model.Intersection;
import model.Request;
import model.Tour;
import view.Window;

public class ChangeAddressState extends State {

    @Override
    public void cancel(Tour tour, Window window, Controller controller) {
        window.exitSelectionMode();
        window.setDefaultButtonStates(new boolean[]{true, true, false});
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void leftClickOnIntersection(int indexIntersection, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {
        window.exitSelectionMode();
        // TODO: check if address chosen is in the same scc as the depot address
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
