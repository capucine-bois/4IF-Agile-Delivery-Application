package controller;

import model.*;
import view.Window;

import java.util.List;

/**
 * State when an intersection is selected.
 */
public class SelectedIntersectionState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadMap(cityMap, tour, window, controller,listOfCommands);
        tour.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadRequests(cityMap, tour, window, controller, listOfCommands);
    }

    @Override
    public void showRequestsPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        window.showRequestsPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.requestsComputedState);
    }

    @Override
    public void leftClickOnTourIntersection(int indexShortestPath, Tour tour, Controller controller) {
        boolean pickupOrDeliveryWasSelected = defaultLeftClickOnTourIntersection(indexShortestPath, tour);
        if (pickupOrDeliveryWasSelected) {
            controller.setCurrentState(controller.tourComputedState);
        }
    }

    @Override
    public void leftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {
        defaultLeftClickOnShortestPath(indexShortestPath, tour, controller);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        ShortestPath shortestPath = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexIcon).findFirst().get();
        leftClickOnTourIntersection(tour.getListShortestPaths().indexOf(shortestPath), tour, controller);
    }

    @Override
    public void enterMouseOnTourIntersection(int indexShortestPath, Window window) {
        window.colorTourIntersectionPanelOnMouseEntered(indexShortestPath);
    }

    @Override
    public void exitMouseOnTourIntersection(int indexShortestPath, Tour tour, Window window) {
        ShortestPath shortestPath = tour.getListShortestPaths().get(indexShortestPath);
        boolean tourIntersectionSelected;
        if (shortestPath.getEndNodeNumber() % 2 == 1) {
            tourIntersectionSelected = tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2).isPickupSelected();
        } else {
            tourIntersectionSelected = tour.getPlanningRequests().get(shortestPath.getEndNodeNumber() / 2 - 1).isDeliverySelected();
        }
        if (!tourIntersectionSelected) {
            window.colorTourIntersectionPanelOnMouseExited(indexShortestPath);
        }
    }

    @Override
    public void moveIntersectionBefore(ListOfCommands l, Tour tour, int indexRequest,
                                       List<Intersection> allIntersections, Window window) {
        l.add(new MoveRequestBeforeCommand(tour, indexRequest, allIntersections));
        window.setUndoButtonState(true);
        if (tour.isDeliveryBeforePickup())
            window.displayErrorMessage("WARNING: A delivery address is visited before its pickup address!");
    }

    @Override
    public void moveIntersectionAfter(ListOfCommands l, Tour tour, int indexRequest,
                                      List<Intersection> allIntersections, Window window) {
        l.add(new ReverseCommand(new MoveRequestBeforeCommand(tour, indexRequest+1, allIntersections)));
        window.setUndoButtonState(true);
        if (tour.isDeliveryBeforePickup())
            window.displayErrorMessage("WARNING: A delivery address is visited before its pickup address!");
    }

    @Override
    public void changeAddress(Window window, Controller controller) {
        window.enterSelectionMode();
        window.setDefaultButtonStates(new boolean[]{false, false, false});
        controller.setCurrentState(controller.changeAddressState);
    }

    @Override
    public void changeTime(Window window, Controller controller) {
        window.enterChangeTimeMode();
        window.setDefaultButtonStates(new boolean[]{false, false, false});
        controller.setCurrentState(controller.changeProcessTimeState);
    }

    @Override
    public void arrowKeyUp(Tour tour, ListOfCommands listOfCommands, List<Intersection> allIntersections, Window window){
        Request requestToUpdate = tour.getPlanningRequests().stream().filter(x -> x.isPickupSelected() || x.isDeliverySelected()).findFirst().get();
        int index = tour.getPlanningRequests().indexOf(requestToUpdate);
        if (requestToUpdate.isPickupSelected()) {
            index = index * 2 + 1;
        } else {
            index = index * 2 + 2;
        }
        moveIntersectionBefore(listOfCommands, tour, index, allIntersections, window);
    }

    @Override
    public void arrowKeyDown(Tour tour, ListOfCommands listOfCommands, List<Intersection> allIntersections, Window window){
        Request requestToUpdate = tour.getPlanningRequests().stream().filter(x -> x.isPickupSelected() || x.isDeliverySelected()).findFirst().get();
        int index = tour.getPlanningRequests().indexOf(requestToUpdate);
        if (requestToUpdate.isPickupSelected()) {
            index = index * 2 + 1;
        } else {
            index = index * 2 + 2;
        }
        moveIntersectionAfter(listOfCommands, tour, index, allIntersections, window);
    }
}
