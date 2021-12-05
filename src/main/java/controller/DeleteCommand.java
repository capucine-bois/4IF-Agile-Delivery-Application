package controller;

import model.Intersection;
import model.Request;
import model.ShortestPath;
import model.Tour;

import java.util.List;

public class DeleteCommand implements Command {

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
    public DeleteCommand(Tour tour, Request request, int indexRequest, List<Intersection> intersections) {
        this.tour = tour;
        this.request = request;
        this.intersections = intersections;
        this.indexRequest = indexRequest;
        this.indexShortestPathToPickup = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 1).findFirst().get());
        this.indexShortestPathToDelivery = tour.getListShortestPaths().indexOf(tour.getListShortestPaths().stream().filter(x -> x.getEndNodeNumber() == indexRequest * 2 + 2).findFirst().get());
    }

    @Override
    public void doCommand() {
        tour.removeRequest(indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery, intersections);
    }

    @Override
    public void undoCommand() {
        tour.insertRequest(indexRequest, indexShortestPathToPickup, indexShortestPathToDelivery, request, intersections);
    }
}
