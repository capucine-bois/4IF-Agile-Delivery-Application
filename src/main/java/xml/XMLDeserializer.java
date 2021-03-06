package xml;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

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
    public static void loadMap(CityMap cityMap) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("map")) {
            cityMap.clearLists();
            deserializeMap(cityMap, document);
        } else {
            throw new ExceptionXML("Bad document.");
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
    public static void loadRequests(Tour tour, CityMap cityMap) throws Exception {
        File file = XMLFileOpener.getInstance().open(true);
        Document document = extractDocument(file);
        Element racine = document.getDocumentElement();
        if (racine.getNodeName().equals("planningRequest")) {
            tour.clearLists();
            deserializeRequests(tour, cityMap, document);
        } else {
            throw new ExceptionXML("Bad document.");
        }
    }

    /**
     * Create a Document instance for file parameter.
     * @param file the file
     * @return the document
     * @throws Exception raised if the file can't be parsed and converted into an XML Document instance.
     */
    public static Document extractDocument(File file) throws Exception {
        Document docParsed;
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            docParsed = db.parse(file);
        }catch(Exception e) {
            throw new ExceptionXML("Malformed file");
        }
        return docParsed;
    }

    /**
     * Read a Document instance and fill list of intersections and list of segments with parsed data.
     * @param cityMap city map to fill
     * @param document document to parse
     */
    public static void deserializeMap(CityMap cityMap, Document document) throws ExceptionXML {
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
    public static void parseXMLIntersections(Document document, CityMap cityMap) throws ExceptionXML {
        NodeList intersectionNodes = document.getElementsByTagName("intersection");
        if (intersectionNodes.getLength() == 0)
            throw new ExceptionXML("No intersection found in the selected file.");
        Map<Double, ArrayList<Double>> coordinateDictionary = new HashMap<>();
        long index = 0;
        for (int x = 0, size = intersectionNodes.getLength(); x < size; x++) {
            if (!intersectionNodes.item(x).getParentNode().equals(document.getDocumentElement())) {
                throw new ExceptionXML("Malformed file.");
            }

            double latitude;
            double longitude;
            long idMap;

            try {
                latitude = Double.parseDouble(intersectionNodes.item(x).getAttributes().getNamedItem("latitude").getNodeValue());
                longitude = Double.parseDouble(intersectionNodes.item(x).getAttributes().getNamedItem("longitude").getNodeValue());
                idMap = Long.parseLong(intersectionNodes.item(x).getAttributes().getNamedItem("id").getNodeValue());
            } catch (Exception e) {
                throw new ExceptionXML("Bad document");
            }
            // check duplicate
            boolean isKeyPresent = coordinateDictionary.containsKey(latitude);
            if (isKeyPresent) {
                ArrayList<Double> list = coordinateDictionary.get(latitude);
                boolean acceptIntersection = true;
                for (double longitu : list) {
                    if (longitu == longitude) {
                        acceptIntersection = false;
                        break;
                    }
                }
                if (acceptIntersection) {
                    list.add(longitude);
                } else {
                    throw new ExceptionXML("Duplicate intersection found, latitude = " + latitude + " longitude = " + longitude);
                }
            } else {
                ArrayList<Double> list = new ArrayList<>();
                list.add(longitude);
                coordinateDictionary.put(latitude, list);
            }
            // create the intersection object and add it to the city map
            Intersection i1 = new Intersection(index, latitude, longitude);
            if (cityMap.containsDictionaryIdKey(idMap)) {
                throw new ExceptionXML("Duplicate index found for the intersection, index = " + idMap);
            } else {
                cityMap.addDictionaryId(idMap, index);
                cityMap.addIntersection(i1);
                index++;
            }
        }
    }

    /**
     * Get line in opened XML document, and parse it to instantiate a segment.
     * Store segments in the list of segments of cityMap parameter.
     * @param document document to parse
     * @param cityMap structure to fill
     */
    public static void parseXMLSegments(Document document, CityMap cityMap) throws ExceptionXML{
        NodeList nodeSegment = document.getElementsByTagName("segment");
        if (nodeSegment.getLength() == 0) throw new ExceptionXML("No segments found in the selected file.");
        HashMap<Long,ArrayList<Long>> idIntersectionsDictionary = new HashMap<>();
        for (int x = 0, size = nodeSegment.getLength(); x < size; x++) {
            if (!nodeSegment.item(x).getParentNode().equals(document.getDocumentElement())) {
                throw new ExceptionXML("Malformed file.");
            }

            long destinationId;
            double length;
            String name;
            long originId;
            try {
                destinationId = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("destination").getNodeValue());
                length = Double.parseDouble(nodeSegment.item(x).getAttributes().getNamedItem("length").getNodeValue());
                name = nodeSegment.item(x).getAttributes().getNamedItem("name").getNodeValue();
                originId = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("origin").getNodeValue());
            } catch (Exception e) {
                throw new ExceptionXML("Bad document");
            }
            long newOriginId;
            long newDestinationId;
            if(cityMap.containsDictionaryIdKey(originId) && cityMap.containsDictionaryIdKey(destinationId)){
                newOriginId = cityMap.getValueDictionary(originId);
                newDestinationId = cityMap.getValueDictionary(destinationId);

                // find the origin and destination intersections
                Intersection origin = cityMap.getIntersections().stream().filter(i->i.getId()==newOriginId).findFirst().get();
                Intersection destination = cityMap.getIntersections().stream().filter(i->i.getId()==newDestinationId).findFirst().get();
                // check duplicate
                boolean isKeyPresent = idIntersectionsDictionary.containsKey(newOriginId);
                if(isKeyPresent){
                    ArrayList<Long> listDestIds = idIntersectionsDictionary.get(newOriginId);
                    for(long dest: listDestIds){
                        if(dest == newDestinationId){
                            throw new ExceptionXML("The selected map contains a duplicate road which origin identifier is : "+ originId + " and destination identifier is : " + destinationId);
                        }
                    }
                }else{
                    ArrayList<Long> listDestinationsId = new ArrayList<>();
                    listDestinationsId.add(newDestinationId);
                    idIntersectionsDictionary.put(newOriginId,listDestinationsId);
                }

                // create the Segment object and add it to the city map
                origin.addAdjacentSegment(new Segment(length, name, destination, origin));
            } else {
                throw new ExceptionXML("The selected map contains an impossible road which origin identifier is : "+ originId + " and destination identifier is : " + destinationId);
            }

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
            if (nodeDepot.getLength() > 1) {
                throw new ExceptionXML("More than one depot have been found.");
            }
            if (!nodeDepot.item(0).getParentNode().equals(document.getDocumentElement())) {
                throw new ExceptionXML("Malformed file.");
            }
            long address = Long.parseLong(nodeDepot.item(0).getAttributes().getNamedItem("address").getNodeValue());
            long newAddress;
            if(cityMap.containsDictionaryIdKey(address)){
                newAddress = cityMap.getValueDictionary(address);
            } else {
                throw new ExceptionXML("The depot address is not an intersection of the city map.");
            }
            String departureTime = nodeDepot.item(0).getAttributes().getNamedItem("departureTime").getNodeValue();
            Pattern pattern = Pattern.compile("^[0-9]{1,2}:[0-9]{1,2}:[0-9]{1,2}$");
            boolean departureTimeOK = false;
            if (pattern.matcher(departureTime).find()) {
                int hour = Integer.parseInt(departureTime.substring(0, departureTime.indexOf(":")));
                int minutes = Integer.parseInt(departureTime.substring(departureTime.indexOf(":") + 1, departureTime.lastIndexOf(":")));
                int seconds = Integer.parseInt(departureTime.substring(departureTime.lastIndexOf(":") + 1));
                if (hour < 24 && minutes < 60 && seconds < 60) {
                    departureTimeOK = true;
                }
            }
            if (!departureTimeOK) throw new ExceptionXML("The departure time is not readable.");

            Optional<Intersection> optionalDepotAddress = cityMap.getIntersections().stream().filter(i->i.getId() == newAddress).findFirst();
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
        if (nodeRequest.getLength() == 0) {
            throw new ExceptionXML("No request found in the selected file.");
        }
        for (int x = 0, size = nodeRequest.getLength(); x < size; x++) {
            if (!nodeRequest.item(x).getParentNode().equals(document.getDocumentElement())) {
                throw new ExceptionXML("Malformed file.");
            }

            Pattern pattern = Pattern.compile("^[0-9]*$");
            boolean pickupDurationOK;
            boolean deliveryDurationOK;
            try {
                pickupDurationOK = pattern.matcher(nodeRequest.item(x).getAttributes().getNamedItem("pickupDuration").getNodeValue()).find();
                deliveryDurationOK = pattern.matcher(nodeRequest.item(x).getAttributes().getNamedItem("deliveryDuration").getNodeValue()).find();
            } catch (Exception e) {
                throw new ExceptionXML("Bad document");
            }
            if (!pickupDurationOK || !deliveryDurationOK) {
                throw new ExceptionXML("One of the duration is not a positive integer.");
            }

            long pickupAddressId;
            long deliveryAddressId;
            int pickupDuration;
            int deliveryDuration;
            try {
                pickupAddressId = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("pickupAddress").getNodeValue());
                deliveryAddressId = Long.parseLong(nodeRequest.item(x).getAttributes().getNamedItem("deliveryAddress").getNodeValue());
                pickupDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("pickupDuration").getNodeValue());
                deliveryDuration = Integer.parseInt(nodeRequest.item(x).getAttributes().getNamedItem("deliveryDuration").getNodeValue());
            } catch (Exception e) {
                throw new ExceptionXML("Bad document");
            }
            if(!(cityMap.containsDictionaryIdKey(pickupAddressId) && cityMap.containsDictionaryIdKey(deliveryAddressId))){
                throw new ExceptionXML("One of the pickup or delivery address is not an intersection of the city map.");
            } else {
                long newPickupAddressId = cityMap.getValueDictionary(pickupAddressId);
                long newDeliveryAddressId = cityMap.getValueDictionary(deliveryAddressId);

                Optional<Intersection> pickupAddress = cityMap.getIntersections().stream().filter(i -> i.getId() == newPickupAddressId).findFirst();
                Optional<Intersection> deliveryAddress = cityMap.getIntersections().stream().filter(i -> i.getId() == newDeliveryAddressId).findFirst();
                if (pickupAddress.isPresent() && deliveryAddress.isPresent()) {
                    Request request = new Request(pickupDuration, deliveryDuration, pickupAddress.get(), deliveryAddress.get());
                    tour.addRequest(request);
                } else {
                    throw new ExceptionXML("One of the pickup or delivery address is not an intersection of the city map.");
                }
            }
        }
    }

}
