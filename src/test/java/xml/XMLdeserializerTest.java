package xml;

import model.CityMap;
import model.Intersection;
import model.Segment;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XMLdeserializerTest test case")
class XMLdeserializerTest {

    static Instant startedAt;

    @BeforeAll
    static public void initStartingTime() {
        startedAt = Instant.now();
    }

    @AfterAll
    static public void showTestDuration() {
        Instant endedAt = Instant.now();
        long duration = Duration.between(startedAt, endedAt).toMillis();
        System.out.println(MessageFormat.format("Test duration : {0} ms", duration));
    }



    /*
     * Method to test:
     * parseXMLSegments()
     *
     * What it does:
     * Read XML to convert in segment
     */
    @Test
    @DisplayName("Test on parseXMLSegments")
    void parseXMLSegmentsTest() throws ParserConfigurationException, IOException, SAXException {
        // Create cityMap to compare to
        CityMap cityMap = new CityMap();
        // Create input of parseXMLSegments
        CityMap cityMap2 = new CityMap();
        File file = new File("src/test/resources/testMap.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        ArrayList<Intersection> listIntersection =  new ArrayList<Intersection>();
        listIntersection.add(new Intersection(1,45.750404,4.8744674));
        listIntersection.add(new Intersection(2,45.75171,4.8718166));
        listIntersection.add(new Intersection(3,45.754265,4.886816));
        listIntersection.add(new Intersection(4,45.755234,4.8867016));
        listIntersection.add(new Intersection(5,45.754894,4.8855863));
        listIntersection.add(new Intersection(6,42.754894,4.1155863));
        listIntersection.add(new Intersection(7,45.354894,4.4855863));
        listIntersection.add(new Intersection(8,45.234244,4.5355862));
        for(int i = 0 ; i < listIntersection.size(); i++) {
            cityMap.addIntersection(listIntersection.get(i));
            cityMap2.addIntersection(listIntersection.get(i));
        }
        Segment s1 = new Segment(23.662397, "Rue Léon Blum", listIntersection.get(1), listIntersection.get(0));
        Segment s2 = new Segment(28.430933, "Avenue Édouard Herriot", listIntersection.get(3),listIntersection.get(2));
        Segment s3 = new Segment(233.383, "Avenue Edouard", listIntersection.get(5), listIntersection.get(4));
        Segment s4 = new Segment(202.5549, "Rue des coquelicots", listIntersection.get(7), listIntersection.get(6));
        cityMap.addSegment(s1,listIntersection.get(0));
        cityMap.addSegment(s2,listIntersection.get(2));
        cityMap.addSegment(s3,listIntersection.get(4));
        cityMap.addSegment(s4,listIntersection.get(6));


        // Function test
        XMLDeserializer.parseXMLSegments(document,cityMap2);

        for(int i = 0; i < cityMap.getAdjacenceMap().size(); i++) {
            ArrayList<Segment> listSegments = cityMap.getAdjacenceMap().get(i);
            ArrayList<Segment> listSegments2 = cityMap2.getAdjacenceMap().get(i);
            if(listSegments != null ) {
                for (int j = 0; j < listSegments.size(); j++) {
                    Segment segmentA = listSegments.get(j);
                    Segment segmentB = listSegments2.get(j);
                    assertEquals(segmentA.getLength(), segmentB.getLength(), "Length not equal");
                    assertEquals(segmentA.getName(), segmentB.getName(), "Name not equal");
                    assertEquals(segmentA.getDestination(), segmentB.getDestination(), "Destination not equal");
                    assertEquals(segmentA.getOrigin(), segmentA.getOrigin(), "Origin not equal");
                }
            }


        }


    }

    /*
     * Method to test:
     * parseXMLIntersections()
     *
     * What it does:
     * Read XML to convert in intersections
     */
    @Test
    @DisplayName("Test on parseXMLIntersections")
    void parseXMLIntersectionsTest() throws ParserConfigurationException, IOException, SAXException {
        File file = new File("src/test/resources/testMap.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        CityMap cityMap = new CityMap();
        ArrayList<Intersection> listIntersections = new ArrayList<>();
        listIntersections.add(new Intersection(1,45.750404,4.8744674));
        listIntersections.add(new Intersection(2,45.75171,4.8718166));
        listIntersections.add(new Intersection(3,45.754265,4.886816));
        listIntersections.add(new Intersection(4,45.755234,4.8867016));
        listIntersections.add(new Intersection(5,45.754894,4.8855863));
        listIntersections.add(new Intersection(6,42.754894,4.1155863));
        listIntersections.add(new Intersection(7,45.354894,4.4855863));
        listIntersections.add(new Intersection(8,45.234244,4.5355862));
        listIntersections.add(new Intersection(9,45.221244,4.9955862));

        // Function test
        XMLDeserializer.parseXMLIntersections(document,cityMap);
        // Same length
        assertEquals(cityMap.getAdjacenceMap().size(), listIntersections.size(), "Wrong number of segments");
        // Take all keys
        List<Intersection> listIntersections2 = new ArrayList<>(cityMap.getAdjacenceMap().keySet());
        // Check if keys are all good
        for(int i = 0; i < listIntersections.size(); i++) {
            listIntersections.contains(listIntersections2.get(i));
        }


    }





}