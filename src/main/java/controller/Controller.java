package controller;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 *
 */
public class Controller {

    private State currentState;
    private Window window;
    private CityMap cityMap;
    private Tour tour;

    // Instances of each state
    protected final InitialState initState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();

    /**
     * Default constructor
     */
    public Controller() {
    }


    /**
     * @param cityMap
     */
    public Controller(CityMap cityMap, Tour tour) throws IOException, FontFormatException {
        this.window = new Window(cityMap, this);
        this.currentState = initState;
        this.cityMap = cityMap;
        this.tour = tour;
    }

    /**
     *
     */
    public void loadMap() {
        currentState.loadMap(cityMap, window, this);
    }

    /**
     *
     */
    public void loadRequests() {
        currentState.loadRequests(tour, window, this);
    }

    /**
     *
     */
    public void closeError() {
        // TODO implement here
    }

    /**
     *
     */
    public void computeTour() {
        // TODO implement here
    }

    public Window getWindow() {
        return window;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
}