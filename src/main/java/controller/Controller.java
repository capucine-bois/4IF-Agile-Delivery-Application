package controller;

import java.awt.*;
import java.io.IOException;

import model.CityMap;
import model.Request;
import model.ShortestPath;
import model.Tour;
import view.Window;

/**
 * Controller of our application, able to change the current state and call methods corresponding to main features.
 */
public class Controller {

    private State currentState;
    private Window window;
    private CityMap cityMap;
    private Tour tour;

    // Instances associated with each possible state of the controller
    protected final InitialState initialState = new InitialState();
    protected final MapLoadedState mapLoadedState = new MapLoadedState();
    protected final RequestsLoadedState requestsLoadedState = new RequestsLoadedState();
    protected final ComputedTourState computedTourState = new ComputedTourState();


    /** Constructor taking already filled cityMap and tour structures
     * @param cityMap filled cityMap structure
     * @param tour filled tour structure
     */
    public Controller(CityMap cityMap, Tour tour) throws IOException, FontFormatException {
        this.window = new Window(cityMap, tour, this);
        this.currentState = initialState;
        this.cityMap = cityMap;
        this.tour = tour;
    }

    /**
     * Change the current state of the controller
     * @param state the new current state
     */
    public void setCurrentState(State state) {
        this.currentState = state;
        if (state == initialState) {
            window.setDefaultButtonStates(new boolean[]{true, false, false});
        } else if (state == mapLoadedState || state == computedTourState){
            window.setDefaultButtonStates(new boolean[]{true, true, false});
        } else {
            window.setDefaultButtonStates(new boolean[]{true, true, true});
        }
        window.resetComponentsState();
    }

    // Methods corresponding to user events

    /**
     * Loading a map (intersections and segments) from XML file.
     * The behavior depends on the current state.
     */
    public void loadMap() {
        currentState.loadMap(cityMap, tour, window, this);
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * The behavior depends on the current state.
     */
    public void loadRequests() {
        currentState.loadRequests(cityMap, tour, window, this);
    }

    /**
     * Compute tour to accomplish all the requests as fast as possible (solving TSP problem).
     */
    public void computeTour() { currentState.computeTour(cityMap, tour, this); }

    public void leftClickOnRequest(int indexRequest) {
        currentState.leftClickOnRequest(indexRequest, tour);
    }

    public void leftClickOnShortestPath(int indexShortestPath) {
        currentState.leftClickOnShortestPath(indexShortestPath, tour);
    }

    public void goBackToTour() {
        currentState.goBackToTour(tour);
    }
}