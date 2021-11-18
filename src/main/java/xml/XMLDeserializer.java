package xml;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLDeserializer {

    public static void load(CityMap cityMap) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("map")) {
            deserializeMap(cityMap, document);
        } else {
            throw new ExceptionXML("Bad document");
        }
    }

    public static void load(Tour tour, CityMap cityMap) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("planningRequest")) {
            deserializeRequests(tour, cityMap, document);
        } else {
            throw new ExceptionXML("Bad document");
        }
    }

    public static Document extractDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }


    public static void deserializeMap(CityMap cityMap, Document document) {
        if(document != null) {
            parseXMLIntersections(document, cityMap);
            parseXMLSegments(document, cityMap);
        }
    }


    public static void parseXMLIntersections(Document document, CityMap cityMap) {
        NodeList intersectionNodes = document.getElementsByTagName("intersection");
        for (int x = 0, size = intersectionNodes.getLength(); x < size; x++) {
            long id = Long.parseLong(intersectionNodes.item(x).getAttributes().getNamedItem("id").getNodeValue());
            double latitude = Double.parseDouble(intersectionNodes.item(x).getAttributes().getNamedItem("latitude").getNodeValue());
            double longitude = Double.parseDouble(intersectionNodes.item(x).getAttributes().getNamedItem("longitude").getNodeValue());
            // create the intersection object and add it to the city map
            cityMap.addIntersection(new Intersection(id, latitude, longitude));
        }
    }


    public static void parseXMLSegments(Document document, CityMap cityMap) {
        NodeList nodeSegment = document.getElementsByTagName("segment");
        for (int x = 0, size = nodeSegment.getLength(); x < size; x++) {
            long destinationId = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("destination").getNodeValue());
            double length = Double.parseDouble(nodeSegment.item(x).getAttributes().getNamedItem("length").getNodeValue());
            String name = nodeSegment.item(x).getAttributes().getNamedItem("name").getNodeValue();
            long originId = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("origin").getNodeValue());
            // find the origin and destination intersections
            Intersection origin = cityMap.getAdjacenceMap().keySet().stream().filter(i -> i.getId() == originId).findFirst().get();
            Intersection destination = cityMap.getAdjacenceMap().keySet().stream().filter(i -> i.getId() == destinationId).findFirst().get();
            // create the Segment object and add it to the city map
            cityMap.addSegment(new Segment(length, name, destination, origin), origin);
        }
    }


    public static void deserializeRequests(Tour tour, CityMap cityMap, Document document) throws ExceptionXML {
        if(document != null) {
            parseXMLRequests(tour, cityMap, document);
            // parse XMLDepot
            NodeList nodeDepot = document.getElementsByTagName("depot");
            long address = Long.parseLong(nodeDepot.item(0).getAttributes().getNamedItem("address").getNodeValue());
            String departureTime = nodeDepot.item(0).getAttributes().getNamedItem("departureTime").getNodeValue();
            Optional<Intersection> optionalDepotAddress = cityMap.getAdjacenceMap().keySet().stream().filter(i -> i.getId() == address).findFirst();
            if (optionalDepotAddress.isPresent()) {
                Intersection depotAddress = optionalDepotAddress.get();
                // set the Tour object
                tour.setDepotAddress(depotAddress);
                tour.setDepartureTime(departureTime);
            } else {
                throw new ExceptionXML("The depot address is not an intersection of the city map.");
            }
        }
    }

    public static void parseXMLRequests(Tour tour, CityMap cityMap, Document document) throws ExceptionXML {
        NodeList nodeRequest = document.getElementsByTagName("request");
        for (int x = 0, size = nodeRequest.getLength(); x < size; x++) {
            long pickupAddressId = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("pickupAddress").getNodeValue());
            long deliveryAddressId = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("deliveryAddress").getNodeValue());
            int pickupDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("pickupDuration").getNodeValue());
            int deliveryDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("deliveryDuration").getNodeValue());

            Optional<Intersection> pickupAddress = cityMap.getAdjacenceMap().keySet().stream().filter(i -> i.getId() == pickupAddressId).findFirst();
            Optional<Intersection> deliveryAddress = cityMap.getAdjacenceMap().keySet().stream().filter(i -> i.getId() == deliveryAddressId).findFirst();
            if (pickupAddress.isPresent() && deliveryAddress.isPresent()) {
                Request request = new Request(pickupDuration, deliveryDuration, pickupAddress.get(), deliveryAddress.get());
                tour.addRequest(request);
            } else {
                throw new ExceptionXML("One of the pickup or delivery address is not an intersection of the city map.");
            }
        }
    }

}
