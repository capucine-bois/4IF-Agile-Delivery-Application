package controller;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;
import xml.XMLDeserializer;

public class SelectedIntersectionState extends State {

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
    public void moveMouseOnIcon(Window window) {
        window.setHandCursorOnIcon();
    }
}
