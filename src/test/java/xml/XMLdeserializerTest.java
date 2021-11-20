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
        // Create input of parseXMLSegments
        File file = new File("../../resources/testMap.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
        ArrayList<Segment> listSegments = new ArrayList<>();
        listSegments.add(new Segment(23.662397, "Rue Léon Blum", 2, 1));
        listSegments.add(new Segment(28.430933, "Avenue Édouard Herriot", 4,3));
        listSegments.add(new Segment(233.383, "Avenue Edouard", 6, 5));
        listSegments.add(new Segment(202.5549, "Rue des coquelicots", 8, 7));


        // Function test
        ArrayList<Segment> outputFunction = XMLDeserializer.parseXMLSegments(document);
        // Check if same output
        assertTrue(listSegments.equals(outputFunction),"Segments aren't well parse");

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
        File file = new File("../../resources/testMap.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(file);
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
        ArrayList<Intersection> outputFunction = XMLDeserializer.parseXMLIntersections(document);
        // Check if same output
        assertTrue(listIntersections.equals(outputFunction), "Intersections aren't well parse");


    }





}