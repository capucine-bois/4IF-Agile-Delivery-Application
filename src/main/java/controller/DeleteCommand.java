package controller;

import model.Intersection;
import model.Request;
import model.ShortestPath;
import model.Tour;

import java.util.ArrayList;
import java.util.List;

public class DeleteCommand implements Command {

    private Tour tour;
    private Request request;
    private int indexRequest;
    private List<Intersection> intersections;
    private List<ShortestPath> paths;

    /**
     * Create the command which delete a request
     * @param tour the tour to modify
     * @param request the request to delete
     */
    public DeleteCommand(Tour tour, Request request, int indexRequest, List<Intersection> intersections) {
        this.tour = tour;
        this.request = request;
        this.indexRequest = indexRequest;
        this.intersections = intersections;
    }

    @Override
    public void doCommand() {
        paths =
                tour.removeRequest(request, indexRequest, intersections);
    }

    @Override
    public void undoCommand() {
        tour.putBackRequest(request, paths);
    }
}
