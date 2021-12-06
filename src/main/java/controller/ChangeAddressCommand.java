package controller;

import model.Intersection;
import model.Tour;

import java.util.List;

public class ChangeAddressCommand implements Command {

    private Tour tour;
    private int indexNode;
    private Intersection formerAddress;
    private Intersection newAddress;
    private List<Intersection> intersections;

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
