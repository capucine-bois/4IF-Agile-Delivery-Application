package controller;

import model.*;
import view.Window;

import java.util.List;

/**
 * State when a request is selected.
 */
public class SelectedRequestState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadRequests(cityMap, tour, window, controller);
    }

    @Override
    public void showTourPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            shortestPath.setSelected(false);
        }
        window.showTourPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.tourComputedState);
    }

    @Override
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {
        defaultLeftClickOnRequest(indexRequest, tour);
        if (!tour.getPlanningRequests().get(indexRequest).isDeliverySelected() ||
                !tour.getPlanningRequests().get(indexRequest).isPickupSelected()) {
            controller.setCurrentState(controller.requestsComputedState);
        }

    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        int indexRequest = indexIcon%2 == 0 ? indexIcon/2 - 1 : indexIcon/2;
        leftClickOnRequest(indexRequest, tour, controller);
    }

    @Override
    public void deleteRequest(Tour tour, int indexRequest, List<Intersection> allIntersections, Window window, ListOfCommands l, Controller controller) {
        Request requestToDelete = tour.getPlanningRequests().get(indexRequest);
        int indexShortestPathToPickup = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 1).findFirst().get());
        int indexShortestPathToDelivery = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 2).findFirst().get());
        l.add(new ReverseCommand(new AddCommand(tour, requestToDelete, allIntersections,indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery)));
        window.setUndoButtonState(true);
        if (requestToDelete.isPickupSelected() && requestToDelete.isDeliverySelected()) {
            controller.setCurrentState(controller.requestsComputedState);
        }
    }

    @Override
    public void enterMouseOnRequest(int indexRequest, Window window) {
        window.colorRequestPanelOnMouseEntered(indexRequest);
    }

    @Override
    public void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {
        Request request = tour.getPlanningRequests().get(indexRequest);
        if (!request.isDeliverySelected() || !request.isPickupSelected()) {
            window.colorRequestPanelOnMouseExited(indexRequest);
        }
    }
}
