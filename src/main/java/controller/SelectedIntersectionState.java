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
    public void moveIntersectionBefore(ListOfCommands l, Tour tour, int indexShortestPath,
                                       List<Intersection> allIntersections, Window window) {
        l.add(new MoveIntersectionBeforeCommand(tour, indexShortestPath, allIntersections, window));
        checkIfUndoOrRedoPossible(l, window);
    }

    @Override
    public void moveIntersectionAfter(ListOfCommands l, Tour tour, int indexShortestPath,
                                      List<Intersection> allIntersections, Window window) {
        l.add(new ReverseCommand(new MoveIntersectionBeforeCommand(tour, indexShortestPath+1, allIntersections, window)));
        checkIfUndoOrRedoPossible(l, window);
    }

    @Override
    public void changeAddress(Window window, Controller controller) {
        window.enterSelectionMode();
        window.setDefaultButtonStates(new boolean[]{false, false, false});
        controller.setCurrentState(controller.changeAddressState);
        window.setUndoButtonState(false);
        window.setRedoButtonState(false);
    }

    @Override
    public void changeTime(Window window, Controller controller) {
        window.enterChangeTimeMode();
        window.setDefaultButtonStates(new boolean[]{false, false, false});
        controller.setCurrentState(controller.changeProcessTimeState);
        window.setUndoButtonState(false);
        window.setRedoButtonState(false);
    }

    @Override
    public void arrowKeyPressed(boolean up, CityMap cityMap, Tour tour, ListOfCommands listOfCommands, Window window){
        Request requestToUpdate = tour.getPlanningRequests().stream().filter(x -> x.isPickupSelected() || x.isDeliverySelected()).findFirst().get();
        int indexRequest = tour.getPlanningRequests().indexOf(requestToUpdate);
        ShortestPath shortestPathSelected;
        if (requestToUpdate.isPickupSelected()) {
            shortestPathSelected = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 1).findFirst().get();
        } else {
            shortestPathSelected = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 2).findFirst().get();
        }
        if (up) {
            moveIntersectionBefore(listOfCommands, tour, tour.getListShortestPaths().indexOf(shortestPathSelected), cityMap.getIntersections(), window);
        } else {
            moveIntersectionAfter(listOfCommands, tour, tour.getListShortestPaths().indexOf(shortestPathSelected), cityMap.getIntersections(), window);
        }
    }
}
