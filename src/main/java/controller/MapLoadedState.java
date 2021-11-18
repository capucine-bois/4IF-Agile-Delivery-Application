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
 * Map loaded state. State of the application when map has been loaded.
 */
public class MapLoadedState implements State {

    @Override
    public void loadMap(CityMap cityMap, Window window, Controller controller) {
        try {
            XMLDeserializer.load(cityMap);
        } catch (Exception e) {
            // TODO afficher un message sur la fenetre
        }
        controller.setCurrentState(controller.mapLoadedState);
    }

    @Override
    public void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {
        try {
            XMLDeserializer.load(tour, cityMap);
        } catch (Exception e) {
            // TODO afficher un message sur la fenetre
            e.printStackTrace();
        }
    }
}