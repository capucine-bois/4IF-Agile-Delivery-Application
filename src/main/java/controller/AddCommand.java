package controller;

import model.Intersection;
import model.Request;
import model.ShortestPath;
import model.Tour;

import java.util.ArrayList;
import java.util.List;

public class AddCommand implements Command {

    private Tour tour;
    private Request request;
    private List<Intersection> intersections;
    private List<ShortestPath> paths;

    /**
     * Create the command which delete a request
     * @param tour the tour to modify
     * @param request the request to delete
     */
    public AddCommand(Tour tour, Request request, List<Intersection> intersections) {
        this.tour = tour;
        this.request = request;
        this.intersections = intersections;
    }

    @Override
    public void doCommand() {
        paths = tour.getListShortestPaths();
        tour.insertRequest(request,paths,intersections);

    }

    @Override
    public void undoCommand() {
        tour.removeRequest(request,tour.getPlanningRequests().size()-1,intersections);
    }
}
