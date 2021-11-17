package xml;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLDeserializer {
    private final Map<Long, Intersection> intersectionMap = new HashMap<>();

    //TODO : make "opening file" generic
    public static void main(String[] args){
        XMLDeserializer xmlDeserializer = new XMLDeserializer();
        xmlDeserializer.deserializeMap();
        xmlDeserializer.deserializeRequests();
    }


    public Document extractDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }


    public CityMap deserializeMap() {
        CityMap cityMap = new CityMap();
        ArrayList<Intersection> listXMLIntersections;
        ArrayList<Segment> listXMLSegments;
        File file = new File("src/main/resources/fichiersXML2020/largeMap.xml");
        Document document = null;
        try {
            document = extractDocument(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        /*Iterator it = cityMap.getAdjacenceMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Intersection, ArrayList<Segment>> entry = (Map.Entry) it.next();
            System.out.println(entry.getKey().getId() + " = " + entry.getValue());
        }*/
        }
        return cityMap;
    }


    public ArrayList<Intersection> parseXMLIntersections(Document document) {
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


    public ArrayList<Segment> parseXMLSegments(Document document) {
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


    public Tour deserializeRequests(){
        ArrayList<Request> listXMLRequests;
        Tour tour = null;
        File file = new File("src/main/resources/fichiersXML2020/requestsLarge9.xml");
        Document document = null;
        try {
            document = extractDocument(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(document != null) {
            listXMLRequests = parseXMLRequests(document);
            // parse XMLDepot
            NodeList nodeDepot = document.getElementsByTagName("depot");
            long address = Long.parseLong(nodeDepot.item(0).getAttributes().getNamedItem("address").getNodeValue());
            String departureTime = nodeDepot.item(0).getAttributes().getNamedItem("departureTime").getNodeValue();
            Intersection depotAdress = intersectionMap.get(address);

            // create the Tour object
             tour = new Tour(depotAdress, departureTime, listXMLRequests);
             /*System.out.println(address + "   " + depotAdress);
             System.out.println(departureTime);
             for(Request r : listXMLRequests){
                 System.out.println(r.getDeliveryAddress().getId());
             }*/
        }
        return tour;
    }


    public ArrayList<Request> parseXMLRequests(Document document) {
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
