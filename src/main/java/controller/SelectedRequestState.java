package controller;

import model.*;
import view.Window;

/**
 * State when a request is selected.
 */
public class SelectedRequestState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadMap(cityMap, tour, window, controller, listOfCommands);
        tour.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadRequests(cityMap, tour, window, controller, listOfCommands);
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
    public void deleteRequest(int indexRequest, Tour tour, CityMap cityMap, Window window, ListOfCommands listOfCommands, Controller controller) {
        Request requestToDelete = defaultDeleteRequest(indexRequest, tour, cityMap, window, listOfCommands);
        if (requestToDelete.isPickupSelected() && requestToDelete.isDeliverySelected()) {
            for (Request request : tour.getPlanningRequests()) {
                request.setPickupSelected(false);
                request.setDeliverySelected(false);
            }
            for (ShortestPath shortestPath : tour.getListShortestPaths()) {
                shortestPath.setSelected(false);
            }
            controller.setCurrentState(controller.requestsComputedState);
            tour.notifyObservers();
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

    @Override
    public void addRequest(Tour tour, Window window, Controller controller) {
        defaultAddRequest(tour, window, controller);
    }
}
