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
 * 
 */
public class MapLoadedState implements State {

    /**
     * Default constructor
     */
    public MapLoadedState() {
    }

    /**
     * 
     */
    public void MapLoadedState() {
        // TODO implement here
    }


    @Override
    public void loadMap(CityMap cityMap, Window window, Controller controller) {

    }

    @Override
    public void loadRequests(Tour tour, Window window, Controller controller) {
        try {
            XMLDeserializer.load(tour);
        } catch (ParserConfigurationException
                | SAXException | IOException
                | ExceptionXML | NumberFormatException e) {
            // TODO afficher un message sur la fenetre
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     *
     */
    public void loadRequests(Tour tour, Window window) {
        try {
            XMLDeserializer.load(tour);
        } catch (ParserConfigurationException
                | SAXException | IOException
                | ExceptionXML | NumberFormatException e) {
            // TODO afficher un message sur la fenetre
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}