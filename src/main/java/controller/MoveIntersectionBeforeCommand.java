package controller;

import model.Intersection;
import model.Tour;

import java.util.List;

/**
 * Move an intersection to later.
 */
public class MoveIntersectionBeforeCommand implements Command {
    private final Tour tour;
    private final int indexShortestPath;
    private final List<Intersection> intersections;

    public MoveIntersectionBeforeCommand(Tour tour, int indexShortestPath, List<Intersection> intersections) {
        this.tour = tour;
        this.indexShortestPath = indexShortestPath;
        this.intersections = intersections;
    }

    @Override
    public void doCommand() {
        tour.moveIntersectionBefore(indexShortestPath, intersections);
    }

    @Override
    public void undoCommand() {
        tour.moveIntersectionBefore(indexShortestPath, intersections);
    }
}
