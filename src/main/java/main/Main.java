package main;

import controller.Controller;
import model.CityMap;
import model.Tour;
import view.Window;

import java.awt.*;
import java.io.IOException;

public class Main {

    /**
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException, FontFormatException, InterruptedException {
        CityMap cityMap = new CityMap();
        Tour tour = new Tour();
        Controller controller = new Controller(cityMap, tour);
        Thread.sleep(100);
        controller.getWindow().displayMap();
    }
}
