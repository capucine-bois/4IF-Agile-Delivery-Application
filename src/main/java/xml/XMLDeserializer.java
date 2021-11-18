package xml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLDeserializer {
    private static final Map<Long, Intersection> intersectionMap = new HashMap<>();

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

    public static void load(Tour tour) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("planningRequest")) {
            deserializeRequests(tour, document);
        } else {
            throw new ExceptionXML("Bad document");
        }
    }

    public static Document extractDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }


    public static CityMap deserializeMap(CityMap cityMap, Document document) {
        ArrayList<Intersection> listXMLIntersections;
        ArrayList<Segment> listXMLSegments;

        if(document != null) {
            listXMLIntersections = parseXMLIntersections(document);
            for (Intersection i : listXMLIntersections) {
                cityMap.addIntersection(i);
                intersectionMap.put(i.getId(), i);
            }
            listXMLSegments = parseXMLSegments(document);
            for (Segment s : listXMLSegments) {
                cityMap.addSegment(s, intersectionMap.get(s.getOrigin()));
            }
        }
        return cityMap;
    }


    public static ArrayList<Intersection> parseXMLIntersections(Document document) {
        NodeList intersectionNodes = document.getElementsByTagName("intersection");
        ArrayList<Intersection> listIntersections = new ArrayList<>();
        for (int x = 0, size = intersectionNodes.getLength(); x < size; x++) {
            long id = Long.parseLong(intersectionNodes.item(x).getAttributes().getNamedItem("id").getNodeValue());
            double latitude = Double.parseDouble(intersectionNodes.item(x).getAttributes().getNamedItem("latitude").getNodeValue());
            double longitude = Double.parseDouble(intersectionNodes.item(x).getAttributes().getNamedItem("longitude").getNodeValue());
            // create the intersection object and add it to the list
            listIntersections.add(new Intersection(id, latitude, longitude));
        }
        return listIntersections;
    }


    public static ArrayList<Segment> parseXMLSegments(Document document) {
        NodeList nodeSegment = document.getElementsByTagName("segment");
        ArrayList<Segment> listSegments = new ArrayList<>();
        for (int x = 0, size = nodeSegment.getLength(); x < size; x++) {
            long destination = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("destination").getNodeValue());
            double length = Double.parseDouble(nodeSegment.item(x).getAttributes().getNamedItem("length").getNodeValue());
            String name = nodeSegment.item(x).getAttributes().getNamedItem("name").getNodeValue();
            long origin = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("origin").getNodeValue());
            // create the Segment object and add it to the list
            listSegments.add(new Segment(length, name, destination, origin));
        }
        return listSegments;
    }


    public static void deserializeRequests(Tour tour, Document document){
        ArrayList<Request> listXMLRequests;
        if(document != null) {
            listXMLRequests = parseXMLRequests(document);
            // parse XMLDepot
            NodeList nodeDepot = document.getElementsByTagName("depot");
            long address = Long.parseLong(nodeDepot.item(0).getAttributes().getNamedItem("address").getNodeValue());
            String departureTime = nodeDepot.item(0).getAttributes().getNamedItem("departureTime").getNodeValue();
            Intersection depotAdress = intersectionMap.get(address);

            // set the Tour object
            tour.setDepotAddress(depotAdress);
            tour.setDepartureTime(departureTime);
            tour.setPlanningRequests(listXMLRequests);
        }
    }


    public static ArrayList<Request> parseXMLRequests(Document document) {
        NodeList nodeRequest = document.getElementsByTagName("request");
        ArrayList<Request> listRequests = new ArrayList<>();
        for (int x = 0, size = nodeRequest.getLength(); x < size; x++) {
            long pickupAddress = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("pickupAddress").getNodeValue());
            long deliveryAddress = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("deliveryAddress").getNodeValue());
            int pickupDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("pickupDuration").getNodeValue());
            int deliveryDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("deliveryDuration").getNodeValue());
            Request r = new Request(pickupDuration, deliveryDuration, intersectionMap.get(pickupAddress), intersectionMap.get(deliveryAddress));
            listRequests.add(r);
        }
        return listRequests;
    }

}
