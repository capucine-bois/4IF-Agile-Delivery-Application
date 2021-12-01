package controller;

import model.Intersection;
import model.Request;
import model.ShortestPath;
import model.Tour;

import java.util.ArrayList;
import java.util.List;

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
        System.out.println("MoveRequestAfterCommand.doCommand");
        tour.moveIntersectionBefore(indexIntersection+1, intersections);
    }

    @Override
    public void undoCommand() {
        tour.moveIntersectionBefore(indexIntersection+1, intersections);
    }
}