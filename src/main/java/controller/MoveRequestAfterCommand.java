package controller;

import model.Intersection;
import model.Request;
import model.ShortestPath;
import model.Tour;

import java.util.ArrayList;
import java.util.List;

/**
 * Move an intersection to earlier.
 */
public class MoveRequestAfterCommand implements Command {
    private Tour tour;
    private int indexIntersection;
    private List<Intersection> intersections;

    public MoveRequestAfterCommand(Tour tour, int indexIntersection, List<Intersection> intersections) {
        this.tour = tour;
        this.indexIntersection = indexIntersection;
        this.intersections = intersections;
    }

    @Override
    public void doCommand() {

        tour.moveIntersectionBefore(indexIntersection+1, intersections);
    }

    @Override
    public void undoCommand() {
        tour.moveIntersectionBefore(indexIntersection+1, intersections);
    }
}
