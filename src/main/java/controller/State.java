package controller;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 * 
 */
public interface State {

    /**
     *
     * @param cityMap
     * @param window
     */
    public void loadMap(CityMap cityMap, Window window, Controller controller);

    /**
     * 
     */
    public void loadRequests(Tour tour, Window window, Controller controller);

    /**
     * 
     */
    public void closeError();

    /**
     * 
     */
    public void computeTour();

}