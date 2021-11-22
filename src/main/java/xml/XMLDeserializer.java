package xml;

import java.io.File;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Deserializer for XML files. Used to load city and requests.
 */
public class XMLDeserializer {

    /**
     * Load XML file, deserialize it, and fill cityMap parameter with parsed data.
     * Notify view through Observable design pattern.
     * @param cityMap structure to fill
     * @throws Exception raised if file can't be loaded
     */
    public static void load(CityMap cityMap) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("map")) {
            cityMap.getIntersections().clear();
            deserializeMap(cityMap, document);
            cityMap.notifyObservers();
        } else {
            throw new ExceptionXML("Bad document");
        }
    }

    /**
     * Load XML file, deserialize it, and fill tour parameter with parsed data.
     * Check if requests use intersections of cityMap parameter.
     * Notify view through Observable design pattern.
     * @param tour structure to fill
     * @param cityMap city map where the tour takes place
     * @throws Exception raised if file can't be loaded or if incoherence is caught
     */
    public static void load(Tour tour, CityMap cityMap) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("planningRequest")) {
            tour.getPlanningRequests().clear();
            deserializeRequests(tour, cityMap, document);
            tour.notifyObservers();
        } else {
            throw new ExceptionXML("Bad document");
        }
    }

    /**
     * Create a Document instance for file parameter.
     * @param file the file
     * @return the document
     * @throws Exception raised if the file can't be parsed and converted into a XML Document instance.
     */
    public static Document extractDocument(File file) throws Exception {
        Document docParsed;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            docParsed = db.parse(file);
        }catch(Exception e) {
            throw new ExceptionXML("Bad extension");
        }
        return docParsed;
    }

    /**
     * Read a Document instance and fill list of intersections and list of segments with parsed data.
     * @param cityMap city map to fill
     * @param document document to parse
     */
    public static void deserializeMap(CityMap cityMap, Document document) {
        if(document != null) {
            parseXMLIntersections(document, cityMap);
            parseXMLSegments(document, cityMap);
        }
    }

    /**
     * Get line in opened XML document, and parse it to instantiate an intersection.
     * Store intersections in the list of intersections of cityMap parameter
     * @param document document to parse
     * @param cityMap structure to fill
     */
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

    /**
     * Get line in opened XML document, and parse it to instantiate a segment.
     * Store segments in the list of segments of cityMap parameter.
     * @param document document to parse
     * @param cityMap structure to fill
     */
    public static void parseXMLSegments(Document document, CityMap cityMap) {
        NodeList nodeSegment = document.getElementsByTagName("segment");
        for (int x = 0, size = nodeSegment.getLength(); x < size; x++) {
            long destinationId = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("destination").getNodeValue());
            double length = Double.parseDouble(nodeSegment.item(x).getAttributes().getNamedItem("length").getNodeValue());
            String name = nodeSegment.item(x).getAttributes().getNamedItem("name").getNodeValue();
            long originId = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("origin").getNodeValue());
            // find the origin and destination intersections
            Intersection origin = cityMap.getIntersections().stream().filter(i->i.getId()==originId).findFirst().get();
            Intersection destination = cityMap.getIntersections().stream().filter(i->i.getId()==destinationId).findFirst().get();
            // create the Segment object and add it to the city map
            origin.addAdjacentSegment(new Segment(length, name, destination, origin));
        }
    }

    /**
     * Read a Document instance and fill the list of requests of tour parameter with parsed data.
     * Check if the depot exists, otherwise raise an ExceptionXML.
     * @param tour structure to fill
     * @param cityMap city map where the tour takes place
     * @param document document to read
     * @throws ExceptionXML raised if the depot address found in XML file do not correspond to an existing intersection
     * in city map.
     */
    public static void deserializeRequests(Tour tour, CityMap cityMap, Document document) throws ExceptionXML {
        if(document != null) {
            parseXMLRequests(tour, cityMap, document);
            // parse XMLDepot
            NodeList nodeDepot = document.getElementsByTagName("depot");
            long address = Long.parseLong(nodeDepot.item(0).getAttributes().getNamedItem("address").getNodeValue());
            String departureTime = nodeDepot.item(0).getAttributes().getNamedItem("departureTime").getNodeValue();
            Optional<Intersection> optionalDepotAddress = cityMap.getIntersections().stream().filter(i->i.getId() == address).findFirst();

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

    /**
     * Get line in opened XML document, and parse it to instantiate a request.
     * Check if request intersections exist in cityMap parameter.
     * Store request in the list of intersections of tour parameter.
     * @param tour structure to fill
     * @param cityMap city map where the tour takes place
     * @param document opened XML file
     * @throws ExceptionXML raised if requests intersections can't be found in cityMap parameter.
     */
    public static void parseXMLRequests(Tour tour, CityMap cityMap, Document document) throws ExceptionXML {
        NodeList nodeRequest = document.getElementsByTagName("request");
        for (int x = 0, size = nodeRequest.getLength(); x < size; x++) {
            long pickupAddressId = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("pickupAddress").getNodeValue());
            long deliveryAddressId = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("deliveryAddress").getNodeValue());
            int pickupDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("pickupDuration").getNodeValue());
            int deliveryDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("deliveryDuration").getNodeValue());

            Optional<Intersection> pickupAddress = cityMap.getIntersections().stream().filter(i->i.getId() == pickupAddressId).findFirst();
            Optional<Intersection> deliveryAddress = cityMap.getIntersections().stream().filter(i->i.getId() == deliveryAddressId).findFirst();
            if (pickupAddress.isPresent() && deliveryAddress.isPresent()) {
                Request request = new Request(pickupDuration, deliveryDuration, pickupAddress.get(), deliveryAddress.get());
                tour.addRequest(request);
            } else {
                throw new ExceptionXML("One of the pickup or delivery address is not an intersection of the city map.");
            }
        }
    }

}
