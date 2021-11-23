package model;

import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static xml.XMLDeserializer.deserializeMap;
import static xml.XMLDeserializer.deserializeRequests;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("DijkstraTest test case")
public class DijkstraTest {
    private Tour tour = new Tour();
    private CityMap cityMap = new CityMap();
    private ArrayList<Intersection> listIntersection;
    private ArrayList<Request> listRequest;
    public DijkstraTest() throws ParserConfigurationException, ExceptionXML, SAXException, IOException {
        initializeMapAndTour();
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
    public void resetCityMap() throws IOException, SAXException, ParserConfigurationException, ExceptionXML {
        initializeMapAndTour();
    }

    private void initializeMapAndTour() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        tour = new Tour();
        cityMap = new CityMap();
        ArrayList<Intersection> listIntersection = new ArrayList<>();
        ArrayList<Request> listRequest = new ArrayList<>();
        File fileMap = new File("./src/test/resources/mapTestDijkstra.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docParsedMap = db.parse(fileMap);
        deserializeMap(cityMap, docParsedMap);

        File fileReq = new File("./src/test/resources/requestsTestDijkstra.xml");
        Document docParsedReq = db.parse(fileReq);
        deserializeRequests(tour, cityMap, docParsedReq);
    }

    /**
     * Method to test:
     * dijkstra()
     *
     * What it does:
     * Compute shortests paths from one origin intersection
     */
    @Test
    @DisplayName("Test on dijkstra")
    void dijkstraTest(){
        List<Intersection> listIntersectionsDijkstra = cityMap.getIntersections();
        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        for(Request req : tour.getPlanningRequests()) {
            listUsefulPoints.add(req.getPickupAddress());
            listUsefulPoints.add(req.getDeliveryAddress());
        }
        listUsefulPoints.add(cityMap.getIntersections().get(0));


        // Intersection 1
        Intersection origin1 = cityMap.getIntersections().get(0);
        ArrayList<Intersection> listUsefulEndPoints1 = new ArrayList<>();
        for (Intersection endPoint : listUsefulPoints) {
            if (tour.isPossiblePath(origin1, endPoint)) {
                listUsefulEndPoints1.add(endPoint);
            }
        }

        //original method to test
        ArrayList<ShortestPath> sp1 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints1, origin1);

        //number of shortest paths
        assertEquals(2,sp1.size(), "Wrong number of shortest paths for Intersection 1");
        //number of segments in each shortest paths
        assertEquals(2,sp1.get(0).getListSegments().size(), "Wrong number of segments in first SP for Intersection 1");
        assertEquals(2,sp1.get(1).getListSegments().size(), "Wrong number of segments in second SP for Intersection 1");
        //Intersection of each list of segment for first shortest path
        assertEquals(1,sp1.get(0).getListSegments().get(0).getOrigin(), "Wrong origin for Intersection1/SP1/SL1/EL1/ORIGIN");
        assertEquals(6,sp1.get(0).getListSegments().get(0).getDestination(), "Wrong origin for Intersection1/SP1/SL1/EL1/DESTINATION");
        assertEquals(6,sp1.get(0).getListSegments().get(1).getOrigin(), "Wrong origin for Intersection1/SP1/SL1/EL2/ORIGIN");
        assertEquals(4,sp1.get(0).getListSegments().get(1).getDestination(), "Wrong origin for Intersection1/SP1/SL1/EL2/DESTINATION");


        //Intersection 2
        Intersection origin2 = cityMap.getIntersections().get(1);


        //Intersection 3
        Intersection origin3 = cityMap.getIntersections().get(2);


        //Intersection 4
        Intersection origin4 = cityMap.getIntersections().get(3);


        //Intersection 5
        Intersection origin5 = cityMap.getIntersections().get(4);

    }



}
