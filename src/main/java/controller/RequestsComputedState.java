package controller;

import model.*;
import view.Window;

/**
 * Computed tour state.
 * State of the application when a tour has been computed and the textual view is displaying "Requests" tab.
 */
public class RequestsComputedState extends State{

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
        window.showTourPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.tourComputedState);
    }

    @Override
    public void leftClickOnRequest(int indexRequest, Tour tour, Controller controller) {
        defaultLeftClickOnRequest(indexRequest, tour);
        controller.setCurrentState(controller.selectedRequestState);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        int indexRequest = indexIcon%2 == 0 ? indexIcon/2 - 1 : indexIcon/2;
        leftClickOnRequest(indexRequest, tour, controller);
    }

    @Override
    public void enterMouseOnRequest(int indexRequest, Window window) {
        window.colorRequestPanelOnMouseEntered(indexRequest);
    }

    @Override
    public void exitMouseOnRequest(int indexRequest, Tour tour, Window window) {
        window.colorRequestPanelOnMouseExited(indexRequest);
    }

    @Override
    public void deleteRequest(int indexRequest, Tour tour, CityMap cityMap, Window window, ListOfCommands listOfCommands, Controller controller) {
        defaultDeleteRequest(indexRequest, tour, cityMap, window, listOfCommands);
    }

    @Override
    public void addRequest(Tour tour, Window window, Controller controller) {
        defaultAddRequest(tour, window, controller);
    }
}
