package xml;

import java.io.File;
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

    //TODO : refactoring
    //TODO : serializing for requests
    public static void main(String[] args) throws Exception {

        XMLDeserializer xmlDeserializer = new XMLDeserializer();

        xmlDeserializer.deserializeMap();
    }

    public Document extractDocument(File file) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        return document;
    }

    public CityMap deserializeMap() {

        File file = new File("src/main/resources/smallMap.xml");

        Document document = null;
        try {
            document = extractDocument(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CityMap cityMap = new CityMap();

    /*
        listXMLIntersection
        listXMLSegments
     */
        return cityMap;
    }

    public void listXMLIntersection(CityMap citymap) {


        Map<Long,Intersection> intersectionMap = new HashMap<>();
        NodeList intersectionNodes = document.getElementsByTagName("intersection");
        NodeList segmentNodes = document.getElementsByTagName("segment");

        //************* INTERSECTION **************
        for (int x = 0, size = nodeIntersection.getLength(); x < size; x++) {
            long id = Long.parseLong(nodeIntersection.item(x).getAttributes().getNamedItem("id").getNodeValue());
            double latitude = Double.parseDouble(nodeIntersection.item(x).getAttributes().getNamedItem("latitude").getNodeValue());

            double longitude = Double.parseDouble(nodeIntersection.item(x).getAttributes().getNamedItem("longitude").getNodeValue());

            Intersection i = new Intersection(id, latitude, longitude);
            cityMap.addIntersection(i);
            intersectionMap.put(id,i);
        }


        //************* SEGMENT **************
        NodeList nodeSegment = document.getElementsByTagName("segment");
        for (int x = 0, size = nodeSegment.getLength(); x < size; x++) {
            long destination = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("destination").getNodeValue());
            double length = Double.parseDouble(nodeSegment.item(x).getAttributes().getNamedItem("length").getNodeValue());
            String name = nodeSegment.item(x).getAttributes().getNamedItem("name").getNodeValue();
            long origin = Long.parseLong(nodeSegment.item(x).getAttributes().getNamedItem("origin").getNodeValue());

            Segment s = new Segment(length, name, destination, origin);
            cityMap.addSegment(s,intersectionMap.get(origin));
        }

        /*Iterator it = cityMap.getAdjacenceMap().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Intersection, ArrayList<Segment>> entry = (Map.Entry)it.next();
            System.out.println(entry.getKey().getId() + " = " + entry.getValue());
        }*/
    }
}
