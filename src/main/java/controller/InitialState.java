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
     * Default constructor
     */
    public InitialState() {
    }

    /**
     * 
     */
    public void InitialState() {
        // TODO implement here
    }

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
        } catch (ParserConfigurationException
                | SAXException | IOException
                | ExceptionXML | NumberFormatException e) {
            // TODO afficher un message sur la fenetre
        } catch (Exception e) {
            e.printStackTrace();
        }
        controller.setCurrentState(controller.mapLoadedState);

    }

    /**
     * Loading requests when the application is in initial state is forbidden and raises an error.
     * @param tour the tour structure to fill
     * @param window the window where to show map and popup messages
     * @param controller application controller
     */
    @Override
    public void loadRequests(Tour tour, Window window, Controller controller) {

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