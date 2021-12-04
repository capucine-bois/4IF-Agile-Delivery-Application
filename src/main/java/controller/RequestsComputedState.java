package controller;

import model.*;
import view.Window;
import xml.XMLDeserializer;

import java.util.List;
import java.util.Optional;

/**
 * Computed tour state.
 * State of the application when a tour has been computed and the textual view is displaying "Requests" tab.
 */
public class RequestsComputedState extends State{

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        defaultLoadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
    }

    @Override
    public void loadRequests(CityMap cityMap, Tour tour, Window window, Controller controller) {
        defaultLoadRequests(cityMap, tour, window, controller);
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
    public void moveMouseOnIcon(Window window) {
        window.setHandCursorOnIcon();
    }

    @Override
    public void deleteRequest(Tour tour, Request requestToDelete, int indexRequest, List<Intersection> allIntersections, Window window, ListOfCommands l) {
        l.add(new DeleteCommand(tour, requestToDelete, indexRequest, allIntersections));

        window.setUndoButtonState(true);
    }

    @Override
    public void addRequest(Tour tour, Window window, Controller controller) {
        tour.setNewRequest(new Request());
        window.showAddRequestPanel();
        window.setEnabledTour(false);
        controller.setCurrentState(controller.addRequestState);
        tour.notifyObservers();
    }
}
