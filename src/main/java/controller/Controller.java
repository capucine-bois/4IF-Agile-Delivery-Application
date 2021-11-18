package controller;

import java.awt.*;
import java.io.IOException;
import java.util.*;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 * Controller of our application, able to change the current state and call methods corresponding to main features.
 */
public class Controller {

    /* ATTRIBUTES */

    private State currentState;
    private Window window;
    private CityMap cityMap;
    private Tour tour;

    // Instances of each state
    protected final InitialState initState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();

    /* CONSTRUCTORS */

    /**
     * Default constructor
     */
    public Controller() {
    }

    /** Constructor taking already filled cityMap and tour structures
     * @param cityMap filled cityMap structure
     * @param tour filled tour structure
     */
    public Controller(CityMap cityMap, Tour tour) throws IOException, FontFormatException {
        this.window = new Window(cityMap, this);
        this.currentState = initState;
        this.cityMap = cityMap;
        this.tour = tour;
    }

    /* METHODS */

    /**
     * Loading a map (intersections and segments) from XML file.
     * The behavior depends on the current state.
     */
    public void loadMap() {
        currentState.loadMap(cityMap, window, this);
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * The behavior depends on the current state.
     */
    public void loadRequests() {
        currentState.loadRequests(tour, cityMap, window, this);
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

    /* GETTERS */

    /**
     * Getter for window attribute
     * @return window
     */
    public Window getWindow() {
        return window;
    }

    /* SETTERS */

    /**
     * Setter for currentState attribute
     * @param currentState wanted state
     */
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
}