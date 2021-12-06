package controller;

import model.Tour;

public class ChangeProcessTimeCommand implements Command {

    private Tour tour;
    private int indexNode;
    private int oldTime;
    private int newTime;

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
