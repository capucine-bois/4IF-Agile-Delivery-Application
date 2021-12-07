package controller;

import model.Tour;

public class ChangeProcessTimeCommand implements Command {

    private final Tour tour;
    private final int indexNode;
    private final int oldTime;
    private final int newTime;

    /**
     * Create the command which add change the process time of a
     * delivery address or a pickup address
     * @param tour
     * @param indexNode
     * @param oldTime
     * @param newTime
     */
    public ChangeProcessTimeCommand(Tour tour, int indexNode, int oldTime, int newTime) {
        this.tour = tour;
        this.indexNode = indexNode;
        this.oldTime = oldTime;
        this.newTime = newTime;
    }

    @Override
    public void doCommand() {
        tour.changeProcessTime(indexNode, newTime);
    }

    @Override
    public void undoCommand() {
        tour.changeProcessTime(indexNode, oldTime);
    }
}
