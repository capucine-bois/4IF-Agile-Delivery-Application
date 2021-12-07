package controller;

import model.Intersection;
import model.Tour;

import java.util.List;

public class ChangeAddressCommand implements Command {

    private final Tour tour;
    private final int indexNode;
    private final Intersection formerAddress;
    private final Intersection newAddress;
    private final List<Intersection> intersections;

    /**
     * Create the command which change the address of an intersection in a request
     * @param tour
     * @param indexNode
     * @param formerAddress
     * @param newAddress
     * @param intersections
     */
    public ChangeAddressCommand(Tour tour, int indexNode, Intersection formerAddress, Intersection newAddress, List<Intersection> intersections) {
        this.tour = tour;
        this.indexNode = indexNode;
        this.formerAddress = formerAddress;
        this.newAddress = newAddress;
        this.intersections = intersections;
    }

    @Override
    public void doCommand() {
        tour.changeAddress(indexNode, newAddress, intersections);
    }

    @Override
    public void undoCommand() {
        tour.changeAddress(indexNode, formerAddress, intersections);
    }
}
