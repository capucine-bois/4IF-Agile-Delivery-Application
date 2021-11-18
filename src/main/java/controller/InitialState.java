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
     *
     * @param window
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

    @Override
    public void loadRequests(Tour tour, CityMap cityMap, Window window, Controller controller) {

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