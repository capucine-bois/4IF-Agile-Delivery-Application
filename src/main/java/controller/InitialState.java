package controller;

import model.CityMap;
import model.Tour;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;
import xml.XMLDeserializer;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

/**
 * Initial state. State of the application when its launches, and when map is not loaded.
 */
public class InitialState implements State {

    @Override
    public void loadMap(CityMap cityMap, Tour tour, Window window, Controller controller) {
        State.super.loadMap(cityMap, tour, window, controller);
        cityMap.notifyObservers();
    }
}