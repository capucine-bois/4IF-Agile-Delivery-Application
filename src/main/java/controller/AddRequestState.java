package controller;

import model.CityMap;
import model.Tour;
import view.Window;

import java.util.regex.Pattern;

public class AddRequestState extends State{

    @Override
    public void chooseAddress(int indexButton, Window window, Controller controller) {
        window.enterSelectionMode();
        if (indexButton == 0) {
            controller.setCurrentState(controller.pickupAddressSelectionState);
        } else {
            controller.setCurrentState(controller.deliveryAddressSelectionState);
        }
    }

    @Override
    public void cancel(Tour tour, Window window, Controller controller) {
        window.setEnabledTour(true);
        window.showRequestsPanel();
        tour.setNewRequest(null);
        window.setDefaultButtonStates(new boolean[]{true, true, false});
        controller.setCurrentState(controller.requestsComputedState);
        tour.notifyObservers();
    }

    @Override
    public void insertRequest(String pickupTime, String deliveryTime, CityMap cityMap, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        boolean pickupTimeOK = pattern.matcher(pickupTime).find();
        boolean deliveryTimeOK = pattern.matcher(deliveryTime).find();
        if (pickupTimeOK && deliveryTimeOK && tour.getNewRequest().getPickupAddress() != null && tour.getNewRequest().getDeliveryAddress() != null) {
            tour.getNewRequest().setPickupDuration(Integer.parseInt(pickupTime) * 60);
            tour.getNewRequest().setDeliveryDuration(Integer.parseInt(deliveryTime) * 60);
            listOfCommands.add(new AddCommand(tour, tour.getNewRequest(), cityMap.getIntersections(),tour.getPlanningRequests().size(), tour.getListShortestPaths().size()-1, tour.getListShortestPaths().size()));
            window.showRequestsPanel();
            window.setEnabledTour(true);
            tour.setNewRequest(null);
            window.setDefaultButtonStates(new boolean[]{true, true, false});
            window.setUndoButtonState(true);
            controller.setCurrentState(controller.requestsComputedState);
            tour.notifyObservers();
        } else if (tour.getNewRequest().getPickupAddress() == null) {
            window.displayErrorMessage("No pickup address selected.");
        } else if (tour.getNewRequest().getDeliveryAddress() == null) {
            window.displayErrorMessage("No delivery address selected.");
        } else {
            window.displayErrorMessage("Process times must be positive integers.");
        }
        window.setRedoButtonState(false);
    }
}
