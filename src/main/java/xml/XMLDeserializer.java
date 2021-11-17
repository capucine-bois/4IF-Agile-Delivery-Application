package xml;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import model.CityMap;
import model.Intersection;
import model.Segment;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class XMLDeserializer {

    //TODO : serializing for requests
    public static void main(String[] args){

        XMLDeserializer xmlDeserializer = new XMLDeserializer();
        xmlDeserializer.deserializeMap();
    }


    public Document extractDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(file);
    }


    public CityMap deserializeMap() {
        CityMap cityMap = new CityMap();
        ArrayList<Intersection> listXMLIntersections;
        Map<Long, Intersection> intersectionMap = new HashMap<>();
        ArrayList<Segment> listXMLSegments;

        File file = new File("src/main/resources/fichiersXML2020/smallMap.xml");
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

            Segment s = new Segment(length, name, destination, origin);
            listSegments.add(s);
        }
        return listSegments;
    }

}
