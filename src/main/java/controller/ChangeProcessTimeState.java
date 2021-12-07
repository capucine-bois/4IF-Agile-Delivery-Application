package controller;

import model.Request;
import model.Tour;
import view.Window;

import java.util.regex.Pattern;

public class ChangeProcessTimeState extends State {

    @Override
    public void cancel(Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {
        window.exitChangeTimeMode();
        window.setDefaultButtonStates(new boolean[]{true, true, false});
        controller.setCurrentState(controller.selectedIntersectionState);
        checkIfUndoOrRedoPossible(listOfCommands, window);
    }

    @Override
    public void saveTime(String time, Tour tour, Window window, ListOfCommands listOfCommands, Controller controller) {
        Pattern pattern = Pattern.compile("^[0-9]*$");
        boolean timeOK = pattern.matcher(time).find();
        if (timeOK) {
            Request requestToUpdate = tour.getPlanningRequests().stream().filter(x -> x.isPickupSelected() || x.isDeliverySelected()).findFirst().get();
            int indexRequest = tour.getPlanningRequests().indexOf(requestToUpdate);
            if (requestToUpdate.isPickupSelected()) {
                listOfCommands.add(new ChangeProcessTimeCommand(tour, indexRequest * 2 + 1, requestToUpdate.getPickupDuration(), Integer.parseInt(time) * 60));
            } else {
                listOfCommands.add(new ChangeProcessTimeCommand(tour, indexRequest * 2 + 2, requestToUpdate.getDeliveryDuration(), Integer.parseInt(time) * 60));
            }
            checkIfUndoOrRedoPossible(listOfCommands, window);
            window.exitChangeTimeMode();
            window.setDefaultButtonStates(new boolean[]{true, true, false});
            controller.setCurrentState(controller.selectedIntersectionState);
        } else {
            window.displayErrorMessage("Process time must be a positive integer.");
        }
    }
}
