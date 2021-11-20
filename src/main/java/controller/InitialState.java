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

    /**
     * Loading a map (intersections and segments) from XML file.
     * Deserialize XML map file selected by user and returned by GUI window.
     * @param cityMap the map structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    public void loadMap(CityMap cityMap, Window window, Controller controller) {
        try {
            XMLDeserializer.load(cityMap);
            controller.setCurrentState(controller.mapLoadedState);
        } catch (Exception e) {
            window.displayErrorMessage(e.getMessage());
        }
    }

}