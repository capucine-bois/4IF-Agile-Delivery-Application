package controller;

import model.Intersection;
import model.Request;
import model.ShortestPath;
import model.Tour;

import java.util.List;

public class AddCommand implements Command {

    private Tour tour;
    private Request request;
    private List<Intersection> intersections;
    private int indexRequest;
    private int indexShortestPathToPickup;
    private int indexShortestPathToDelivery;

    /**
     * Create the command which delete a request
     * @param tour the tour to modify
     * @param request the request to delete
     */
    public AddCommand(Tour tour, Request request, List<Intersection> intersections) {
        this.tour = tour;
        this.request = request;
        this.intersections = intersections;
        this.indexRequest = tour.getPlanningRequests().size();
        this.indexShortestPathToPickup = tour.getListShortestPaths().size() - 1;
        this.indexShortestPathToDelivery = tour.getListShortestPaths().size();

    }

    @Override
    public void doCommand() {
        tour.insertRequest(indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery, request, intersections);
    }

    @Override
    public void undoCommand() {
        tour.removeRequest(indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery, intersections);
    }
}
