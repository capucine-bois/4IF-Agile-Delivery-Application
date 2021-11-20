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

    /**
     * Loading a map (intersections and segments) from XML file.
     * Deserialize XML map file selected by user and returned by GUI window.
     * @param cityMap the map structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    @Override
    public void loadMap(CityMap cityMap, Window window, Controller controller) {
        try {
            XMLDeserializer.load(cityMap);
        } catch (Exception e) {
            // TODO afficher un message sur la fenetre
        }
        controller.setCurrentState(controller.mapLoadedState);
    }

    /**
     * Loading a planning requests (pickup and deliveries) from XML file.
     * @param tour the tour structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    @Override
    public void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {
        try {
            XMLDeserializer.load(tour, cityMap);
            tour.computeTour(cityMap.getAdjacenceMap());
        } catch (Exception e) {
            // TODO afficher un message sur la fenetre
            e.printStackTrace();
        }
    }
}