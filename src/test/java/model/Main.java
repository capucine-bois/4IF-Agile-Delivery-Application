package model;

import controller.Controller;
import model.CityMap;
import model.Tour;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import view.Window;
import xml.ExceptionXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static xml.XMLDeserializer.deserializeMap;
import static xml.XMLDeserializer.deserializeRequests;

/**
 * Main class.
 * Initialize city map and tour structures, and instantiate controller.
 */
public class Main {

    /**
     * Main method.
     * @param args the arguments
     */
    public static void main(String[] args) throws IOException, FontFormatException, InterruptedException, ParserConfigurationException, SAXException, ExceptionXML {
        File fileMap = new File("./src/test/resources/mapTestDijkstra.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docParsedMap = db.parse(fileMap);
        CityMap cityMap = new CityMap();
        deserializeMap(cityMap, docParsedMap);

        Tour tour = new Tour();
        File fileReq = new File("./src/test/resources/requestsTestDijkstra.xml");
        Document docParsedReq = db.parse(fileReq);
        deserializeRequests(tour, cityMap, docParsedReq);

        long origin1 = cityMap.getIntersections().get(3).getId();
        System.out.println("affichage : "+ origin1);


        List<Intersection> listIntersectionsDijkstra = cityMap.getIntersections();

    }
}
