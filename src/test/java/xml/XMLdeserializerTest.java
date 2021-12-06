package xml;

import model.*;
import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("XMLdeserializerTest test case")
public class XMLdeserializerTest {

    private CityMap cityMap = new CityMap();
    private Tour tour = new Tour();
    private ArrayList<Intersection> listIntersection;
    private ArrayList<Request> listRequest;
    public XMLdeserializerTest(){
        initializeMap();
    }
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

    @BeforeEach
    public void resetCityMap(){
        initializeMap();
    }

    private void initializeMap(){
        this.listIntersection =  new ArrayList<>();
        this.cityMap = new CityMap();
        this.tour = new Tour();
        cityMap.addDictionaryId(1L,0L);
        cityMap.addDictionaryId(2L,1L);
        cityMap.addDictionaryId(3L,2L);
        cityMap.addDictionaryId(4L,3L);
        cityMap.addDictionaryId(5L,4L);
        cityMap.addDictionaryId(6L,5L);
        cityMap.addDictionaryId(7L,6L);
        cityMap.addDictionaryId(8L,7L);

        listIntersection.add(new Intersection(0,45.750404,4.8744674));
        listIntersection.add(new Intersection(1,45.75171,4.8718166));
        listIntersection.add(new Intersection(2,45.754265,4.886816));
        listIntersection.add(new Intersection(3,45.755234,4.8867016));
        listIntersection.add(new Intersection(4,45.754894,4.8855863));
        listIntersection.add(new Intersection(5,42.754894,4.1155863));
        listIntersection.add(new Intersection(6,45.354894,4.4855863));
        listIntersection.add(new Intersection(7,45.234244,4.5355862));
        listIntersection.add(new Intersection(8,45.221244,4.9955862));

        Segment s1 = new Segment(23.662397, "Rue Léon Blum", listIntersection.get(1), listIntersection.get(0));
        Segment s2 = new Segment(28.430933, "Avenue Édouard Herriot", listIntersection.get(3),listIntersection.get(2));
        Segment s3 = new Segment(233.383, "Avenue Edouard", listIntersection.get(5), listIntersection.get(4));
        Segment s4 = new Segment(202.5549, "Rue des coquelicots", listIntersection.get(7), listIntersection.get(6));
        listIntersection.get(0).addAdjacentSegment(s1);
        listIntersection.get(2).addAdjacentSegment(s2);
        listIntersection.get(4).addAdjacentSegment(s3);
        listIntersection.get(6).addAdjacentSegment(s4);
        for (Intersection intersection : listIntersection) {
            this.cityMap.addIntersection(intersection);
        }
        this.listRequest =  new ArrayList<>();
        this.listRequest.add(new Request(0,600,listIntersection.get(0),listIntersection.get(1)));
        this.listRequest.add(new Request(600,120,listIntersection.get(3),listIntersection.get(6)));
    }



    /**
     * Method to test:
     * parseXMLSegments()
     *
     * What it does:
     * Read XML to convert in segment
     */
    @Nested
    @DisplayName("Test on parseXMLSegments")
    class parseSegment {


        @Test
        @DisplayName("Normal scenario")
        void parseXMLSegmentsTest() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
            // Create input of parseXMLSegments
            CityMap cityMap2 = new CityMap();
            File file = new File("src/test/resources/testMap.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);

            for (Intersection intersection : listIntersection) {
                cityMap2.addIntersection(intersection);
            }

            // Dictionnary
            cityMap2.addDictionaryId(1L,0L);
            cityMap2.addDictionaryId(2L,1L);
            cityMap2.addDictionaryId(3L,2L);
            cityMap2.addDictionaryId(4L,3L);
            cityMap2.addDictionaryId(5L,4L);
            cityMap2.addDictionaryId(6L,5L);
            cityMap2.addDictionaryId(7L,6L);
            cityMap2.addDictionaryId(8L,7L);
            // Function test
            XMLDeserializer.parseXMLSegments(document,cityMap2);
            List<Intersection> listIntersection1 = cityMap.getIntersections();
            List<Intersection> listIntersection2 = cityMap2.getIntersections();
            assertEquals(listIntersection1.size(),listIntersection2.size(), "Not the same intersections");
            for(int i = 0; i < listIntersection1.size(); i++) {
                List<Segment> listSegments = listIntersection1.get(i).getAdjacentSegments();
                List<Segment> listSegments2 = listIntersection2.get(i).getAdjacentSegments();
                if(listSegments != null ) {
                    for (int j = 0; j < listSegments.size(); j++) {
                        Segment segmentA = listSegments.get(j);
                        Segment segmentB = listSegments2.get(j);
                        assertEquals(segmentA, segmentB, "Segment aren't the same");
                    }
                }
            }
        }

        @Test
        @DisplayName("Duplicate segment")
        void parseXMLSegmentsDuplicateTest() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/testMapDuplicateSegment.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            // Function to test
            try{
                XMLDeserializer.parseXMLSegments(document,cityMap);
                // Need to fail
                fail();
            } catch(Exception exception) {
                assertEquals(exception.getMessage(),"The selected map contains a duplicate road which origin identifier is : 7 and destination identifier is : 8");
            }
        }

        @Test
        @DisplayName("Impossible segment")
        void parseXMLSegmentsImpossibleTest() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/testMapImpossibleSegment.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            // Function to test
            try{
                XMLDeserializer.parseXMLSegments(document,cityMap);
                // Need to fail
                fail();
            } catch(Exception exception) {
                assertEquals(exception.getMessage(),"The selected map contains an impossible road which origin identifier is : 12 and destination identifier is : 11");
            }
        }
    }

    /**
     * Method to test:
     * parseXMLIntersections()
     *
     * What it does:
     * Read XML to convert in intersections
     */
    @Nested
    @DisplayName("Test on parseXMLIntersection")
    class parseIntersections {
        @Test
        @DisplayName("Normal scenario")
        void parseXMLIntersectionsTest() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
            File file = new File("src/test/resources/testMap.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            CityMap cityMap = new CityMap();
            // Function to test
            XMLDeserializer.parseXMLIntersections(document,cityMap);
            // Same length
            assertEquals(cityMap.getIntersections().size(), listIntersection.size(), "Wrong number of segments");
            // Take all keys
            List<Intersection> listIntersections2 = new ArrayList<>(cityMap.getIntersections());
            // Check if keys are all good
            for(int i = 0; i < listIntersection.size(); i++) {
                assertEquals(listIntersection.get(i), listIntersections2.get(i));
            }
        }

        @Test
        @DisplayName("Duplicate intersection")
        void parseXMLIntersectionsDuplicateTest() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/testMapDuplicateIntersection.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            CityMap cityMap = new CityMap();

            // Function to test
            try{
                XMLDeserializer.parseXMLIntersections(document,cityMap);
                // Need to fail
                fail();
            } catch(Exception exception) {
                assertEquals(exception.getMessage(),"Duplicate index found for the intersection, index = 8");
            }
        }

        @Test
        @DisplayName("Duplicate intersection coordinate")
        void parseXMLIntersectionsDuplicateCoordinateTest() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/testMapDuplicateIntersectionCoordinate.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            CityMap cityMap = new CityMap();
            // Function to test
            try{
                XMLDeserializer.parseXMLIntersections(document,cityMap);
                // Need to fail
                fail();
            } catch(Exception exception) {
                assertEquals(exception.getMessage(),"Duplicate intersection found, latitude = 45.354894 longitude = 4.1155863");
            }
        }
    }


    /**
     * Method to test:
     * deserializeRequests()
     *
     * What it does:
     * Check wether a request can be added or not
     *
     */
    @Nested
    @DisplayName("Test on deserializeRequests")
    class deserializeRequest {

        @Test
        @DisplayName("Normal scenario")
        void deserializeRequests() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/requestTestValid.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);

            try{
                XMLDeserializer.deserializeRequests(tour,cityMap, document);
                assertEquals(tour.getDepotAddress(), listIntersection.get(5), "Bad depot address");
                assertEquals("08:00",tour.getDepartureTime(),"Bad departure address");
            }catch(Exception exception) {
                fail("Exception find!");
            }
        }

        @Test
        @DisplayName("Request out of the map")
        void deserializeRequestsFail() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/requestTest.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            Tour tour = new Tour();

            try{
                XMLDeserializer.deserializeRequests(tour,cityMap, document);
            }catch(Exception exception) {
                assertTrue(exception.getMessage().contains("One of the pickup or delivery address is not an intersection of the city map."), "Request is out of the map");
            }
        }
    }

    /**
     * Method to test:
     * parseXMLRequests()
     *
     * What it does:
     * Parse XML requests
     *
     */
    @Nested
    @DisplayName("Test on parseXMLRequests")
    class parseRequest {
        @Test
        @DisplayName("Normal scenario")
        void parseXMLRequests() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/requestTestValid.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            try{
                XMLDeserializer.parseXMLRequests(tour,cityMap, document);
                ArrayList<Request> listRequestOutput = tour.getPlanningRequests();
                assertEquals(listRequestOutput.size(),listRequest.size(),"Wrong number of requests");
                for (int j = 0; j < listRequestOutput.size(); j++) {
                    Request requestA = listRequestOutput.get(j);
                    Request requestB = listRequest.get(j);
                    assertEquals(requestA, requestB, "Request aren't the same");
                }
            }catch(Exception exception) {
                fail("Exception find!");
            }
        }

        @Test
        @DisplayName("Impossible request")
        void parseXMLRequestsImpossible() throws ParserConfigurationException, IOException, SAXException {
            File file = new File("src/test/resources/requestTest.xml");
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(file);
            try{
                XMLDeserializer.parseXMLRequests(tour,cityMap, document);
                // Should fail
                fail();
            }catch(Exception exception) {
                assertTrue(exception.getMessage().contains("One of the pickup or delivery address is not an intersection of the city map."), "Request is out of the map");
            }
        }

    }





}