package controller;
import model.CityMap;
import model.Tour;
import view.Window;

public class RequestsErrorState implements State {

    /**
     * Default constructor
     */
    public RequestsErrorState() {
    }

    /**
     * 
     */
    public void RequestErrorState() {
        // TODO implement here
    }

    @Override
    public void loadMap(CityMap cityMap, Window window, Controller controller) {

    }

    @Override
    public void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {

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

}