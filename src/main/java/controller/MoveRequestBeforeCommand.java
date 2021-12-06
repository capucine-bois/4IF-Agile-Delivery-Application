package controller;

import model.Intersection;
import model.Tour;

import java.util.List;

/**
 * Move an intersection to later.
 */
public class MoveRequestBeforeCommand implements Command {
    private final Tour tour;
    private final int indexIntersection;
    private final List<Intersection> intersections;

    public MoveRequestBeforeCommand(Tour tour, int indexIntersection, List<Intersection> intersections) {
        this.tour = tour;
        this.indexIntersection = indexIntersection;
        this.intersections = intersections;
    }

    @Override
    public void doCommand() {
        tour.moveIntersectionBefore(indexIntersection, intersections);
    }

    @Override
    public void undoCommand() {
        tour.moveIntersectionBefore(indexIntersection, intersections);
    }
}
