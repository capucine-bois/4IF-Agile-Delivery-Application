package controller;

import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;

/**
 * Computing tour state.
 * State of the application when a tour is currently computing and the textual view is displaying "Tour" tab.
 */
public class TourComputingState extends State {

    @Override
    public void showRequestsPanel(Tour tour, Window window, Controller controller) {
        for (Request request : tour.getPlanningRequests()) {
            request.setPickupSelected(false);
            request.setDeliverySelected(false);
        }
        window.showRequestsPanel();
        tour.notifyObservers();
        controller.setCurrentState(controller.requestsComputingState);
    }

    @Override
    public void leftClickOnTourIntersection(int indexShortestPath, Tour tour, Controller controller) {
        defaultLeftClickOnTourIntersection(indexShortestPath, tour);
    }

    @Override
    public void leftClickOnIcon(int indexIcon, Tour tour, Controller controller) {
        if (!tour.getListShortestPaths().isEmpty()) {
            ShortestPath shortestPath = tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexIcon).findFirst().get();
            leftClickOnTourIntersection(tour.getListShortestPaths().indexOf(shortestPath), tour, controller);
        }
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
    public void stopTourComputation(Tour tour) {
        tour.setTourComputed(true);
    }
}
