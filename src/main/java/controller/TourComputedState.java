package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

import java.util.Optional;

/**
 * Computed tour state. State of the application when a tour has been computed.
 */
public class TourComputedState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        super.loadMap(cityMap, tour, window, controller);
        tour.notifyObservers();
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
        defaultLeftClickOnTourIntersection(indexShortestPath, tour);
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void leftClickOnShortestPath(int indexShortestPath, Tour tour, Controller controller) {
        defaultLeftClickOnShortestPath(indexShortestPath, tour, controller);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        ShortestPath shortestPath = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexIcon).findFirst().get();
        leftClickOnTourIntersection(tour.getListShortestPaths().indexOf(shortestPath), tour, controller);
        controller.setCurrentState(controller.selectedIntersectionState);
    }

    @Override
    public void enterMouseOnTourIntersection(int indexShortestPath, Window window) {
        window.colorTourIntersectionPanelOnMouseEntered(indexShortestPath);
    }

    @Override
    public void exitMouseOnTourIntersection(int indexShortestPath, Tour tour, Window window) {
        window.colorTourIntersectionPanelOnMouseExited(indexShortestPath);
    }

    @Override
    public void moveMouseOnIcon(Window window) {
        window.setHandCursorOnIcon();
    }
}