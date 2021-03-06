package controller;

import model.Intersection;
import model.Request;
import model.Tour;

import java.util.List;

public class AddCommand implements Command {

    private final Tour tour;
    private final Request request;
    private final List<Intersection> intersections;
    private final int indexRequest;
    private final int indexShortestPathToPickup;
    private final int indexShortestPathToDelivery;

    /**
     * Create the command which add a request
     * @param tour current Tour
     * @param request request to add
     * @param intersections list of all intersections
     * @param indexRequest
     * @param indexShortestPathToPickup
     * @param indexShortestPathToDelivery
     */
    public AddCommand(Tour tour, Request request, List<Intersection> intersections, int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery) {
        this.tour = tour;
        this.request = request;
        this.intersections = intersections;
        this.indexRequest = indexRequest;
        this.indexShortestPathToPickup = indexShortestPathToPickup;
        this.indexShortestPathToDelivery = indexShortestPathToDelivery;

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
