package controller;

import model.Intersection;
import model.Tour;
import view.Window;

import java.util.List;

/**
 * Move an intersection to later.
 */
public class MoveIntersectionBeforeCommand implements Command {
    private final Tour tour;
    private final int indexShortestPath;
    private final List<Intersection> intersections;
    private final Window window;

    public MoveIntersectionBeforeCommand(Tour tour, int indexShortestPath, List<Intersection> intersections, Window window) {
        this.tour = tour;
        this.indexShortestPath = indexShortestPath;
        this.intersections = intersections;
        this.window = window;
    }

    @Override
    public void doCommand() {
        tour.moveIntersectionBefore(indexShortestPath, intersections);
        if (tour.isDeliveryBeforePickup())
            window.displayErrorMessage("WARNING: A delivery address is visited before its pickup address!");
    }

    @Override
    public void undoCommand() {
        tour.moveIntersectionBefore(indexShortestPath, intersections);
        if (tour.isDeliveryBeforePickup())
            window.displayErrorMessage("WARNING: A delivery address is visited before its pickup address!");
    }
}
