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

    public void loadMap(CityMap cityMap, Window window, Controller controller) {
        try {
            XMLDeserializer.load(cityMap);
        } catch (Exception e) {
            // TODO afficher un message sur la fenetre
        }
        controller.setCurrentState(controller.mapLoadedState);
    }

}