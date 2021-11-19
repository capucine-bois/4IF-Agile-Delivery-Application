package controller;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 * Request loaded state. State when the application has successfully loaded requests.
 */
public class RequestsLoadedState implements State {

    @Override
    public void loadMap(CityMap cityMap, Window window, Controller controller) {

    }

    @Override
    public void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {

    }

    @Override
    public void computeTour() {
        // TODO implement here
    }

}