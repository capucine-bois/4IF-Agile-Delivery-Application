package main;

import controller.Controller;
import model.CityMap;
import model.Tour;
import view.Window;

import java.awt.*;
import java.io.IOException;

/**
 * Main class.
 * Initialize city map and tour structures, and instantiate controller.
 */
public class Main {

    /**
     * Main method.
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException, FontFormatException, InterruptedException {
        CityMap cityMap = new CityMap();
        Tour tour = new Tour();
        new Controller(cityMap, tour) ;
    }
}
