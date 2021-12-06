package controller;

import model.CityMap;
import model.Tour;
import view.Window;

/**
 * Initial state. State of the application when its launches, and when map is not loaded.
 */
public class InitialState extends State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller, ListOfCommands listOfCommands) {
        defaultLoadMap(cityMap, tour, window, controller, listOfCommands);
        cityMap.notifyObservers();
    }
}