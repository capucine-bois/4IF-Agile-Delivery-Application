package model;

import org.junit.jupiter.api.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import xml.ExceptionXML;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
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
    private Tour tour ;
    private CityMap cityMap ;
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
        listIntersection = new ArrayList<>();
        listRequest = new ArrayList<>();
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


        // ********** Intersection 1
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

        //Intersection of each list of segment (each shortest path)
        //first shortest path
        assertEquals(1,sp1.get(0).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection1/SP1/EL1/ORIGIN");
        assertEquals(6,sp1.get(0).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection1/SP1/EL1/DESTINATION");
        assertEquals(6,sp1.get(0).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection1/SP1/EL2/ORIGIN");
        assertEquals(4,sp1.get(0).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection1/SP1/EL2/DESTINATION");
        //second shortest path
        assertEquals(1,sp1.get(1).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection1/SP2/EL1/ORIGIN");
        assertEquals(6,sp1.get(1).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection1/SP2/EL1/DESTINATION");
        assertEquals(6,sp1.get(1).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection1/SP2/EL2/ORIGIN");
        assertEquals(2,sp1.get(1).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection1/SP2/EL2/DESTINATION");


        // ********** Intersection 2
        Intersection origin2 = cityMap.getIntersections().get(1);
        ArrayList<Intersection> listUsefulEndPoints2 = new ArrayList<>();
        for (Intersection endPoint : listUsefulPoints) {
            if (tour.isPossiblePath(origin2, endPoint)) {
                listUsefulEndPoints2.add(endPoint);
            }
        }

        //original method to test
        ArrayList<ShortestPath> sp2 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints2, origin2);
        //number of shortest paths
        assertEquals(3,sp2.size(), "Wrong number of shortest paths for Intersection 2");
        //number of segments in each shortest paths
        assertEquals(1,sp2.get(0).getListSegments().size(), "Wrong number of segments in first SP for Intersection 2");
        assertEquals(2,sp2.get(1).getListSegments().size(), "Wrong number of segments in second SP for Intersection 2");
        assertEquals(3,sp2.get(2).getListSegments().size(), "Wrong number of segments in third SP for Intersection 2");

        //Intersection of each list of segment (each shortest path)
        //first shortest path
        assertEquals(2,sp2.get(0).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection2/SP1/EL1/ORIGIN");
        assertEquals(4,sp2.get(0).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection2/SP1/EL1/DESTINATION");
        //second shortest path
        assertEquals(2,sp2.get(1).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection2/SP2/EL1/ORIGIN");
        assertEquals(6,sp2.get(1).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection2/SP2/EL1/DESTINATION");
        assertEquals(6,sp2.get(1).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection2/SP2/EL2/ORIGIN");
        assertEquals(5,sp2.get(1).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection2/SP2/EL2/DESTINATION");
        //third shortest path
        assertEquals(2,sp2.get(2).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection2/SP3/EL1/ORIGIN");
        assertEquals(6,sp2.get(2).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection2/SP3/EL1/DESTINATION");
        assertEquals(6,sp2.get(2).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection2/SP3/EL2/ORIGIN");
        assertEquals(5,sp2.get(2).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection2/SP3/EL2/DESTINATION");
        assertEquals(5,sp2.get(2).getListSegments().get(2).getOrigin().getId(), "Wrong id for Intersection2/SP3/EL3/ORIGIN");
        assertEquals(3,sp2.get(2).getListSegments().get(2).getDestination().getId(), "Wrong id for Intersection2/SP3/EL3/DESTINATION");


        //Intersection 3
        Intersection origin3 = cityMap.getIntersections().get(2);
        ArrayList<Intersection> listUsefulEndPoints3 = new ArrayList<>();
        for (Intersection endPoint : listUsefulPoints) {
            if (tour.isPossiblePath(origin3, endPoint)) {
                listUsefulEndPoints3.add(endPoint);
            }
        }

        //original method to test
        ArrayList<ShortestPath> sp3 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints3, origin3);
        //number of shortest paths
        assertEquals(3,sp3.size(), "Wrong number of shortest paths for Intersection 3");
        //number of segments in each shortest paths
        assertEquals(1,sp3.get(0).getListSegments().size(), "Wrong number of segments in first SP for Intersection 3");
        assertEquals(3,sp3.get(1).getListSegments().size(), "Wrong number of segments in second SP for Intersection 3");
        assertEquals(3,sp3.get(2).getListSegments().size(), "Wrong number of segments in third SP for Intersection 3");

        //Intersection of each list of segment (each shortest path)
        //first shortest path
        assertEquals(3,sp3.get(0).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection3/SP1/EL1/ORIGIN");
        assertEquals(5,sp3.get(0).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection3/SP1/EL1/DESTINATION");
        //second shortest path
        assertEquals(3,sp3.get(1).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection3/SP2/EL1/ORIGIN");
        assertEquals(5,sp3.get(1).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection3/SP2/EL1/DESTINATION");
        assertEquals(5,sp3.get(1).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection3/SP2/EL2/ORIGIN");
        assertEquals(6,sp3.get(1).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection3/SP2/EL2/DESTINATION");
        assertEquals(6,sp3.get(1).getListSegments().get(2).getOrigin().getId(), "Wrong id for Intersection3/SP2/EL3/ORIGIN");
        assertEquals(4,sp3.get(1).getListSegments().get(2).getDestination().getId(), "Wrong id for Intersection3/SP2/EL3/DESTINATION");
        //third shortest path
        assertEquals(3,sp3.get(2).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection3/SP3/EL1/ORIGIN");
        assertEquals(5,sp3.get(2).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection3/SP3/EL1/DESTINATION");
        assertEquals(5,sp3.get(2).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection3/SP3/EL2/ORIGIN");
        assertEquals(6,sp3.get(2).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection3/SP3/EL2/DESTINATION");
        assertEquals(6,sp3.get(2).getListSegments().get(2).getOrigin().getId(), "Wrong id for Intersection3/SP3/EL3/ORIGIN");
        assertEquals(1,sp3.get(2).getListSegments().get(2).getDestination().getId(), "Wrong id for Intersection3/SP3/EL3/DESTINATION");


        //Intersection 4
        Intersection origin4 = cityMap.getIntersections().get(3);
        ArrayList<Intersection> listUsefulEndPoints4 = new ArrayList<>();
        for (Intersection endPoint : listUsefulPoints) {
            if (tour.isPossiblePath(origin4, endPoint)) {
                listUsefulEndPoints4.add(endPoint);
            }
        }

        //original method to test
        ArrayList<ShortestPath> sp4 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints4, origin4);
        //number of shortest paths
        assertEquals(3,sp4.size(), "Wrong number of shortest paths for Intersection 4");
        //number of segments in each shortest paths
        assertEquals(1,sp4.get(0).getListSegments().size(), "Wrong number of segments in first SP for Intersection 4");
        assertEquals(2,sp4.get(1).getListSegments().size(), "Wrong number of segments in second SP for Intersection 4");
        assertEquals(3,sp4.get(2).getListSegments().size(), "Wrong number of segments in third SP for Intersection 4");

        //Intersection of each list of segment (each shortest path)
        //first shortest path
        assertEquals(4,sp4.get(0).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection4/SP1/EL1/ORIGIN");
        assertEquals(2,sp4.get(0).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection4/SP1/EL1/DESTINATION");
        //second shortest path
        assertEquals(4,sp4.get(1).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection4/SP2/EL1/ORIGIN");
        assertEquals(6,sp4.get(1).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection4/SP2/EL1/DESTINATION");
        assertEquals(6,sp4.get(1).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection4/SP2/EL2/ORIGIN");
        assertEquals(5,sp4.get(1).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection4/SP2/EL2/DESTINATION");
        //third shortest path
        assertEquals(4,sp4.get(2).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection4/SP3/EL1/ORIGIN");
        assertEquals(6,sp4.get(2).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection4/SP3/EL1/DESTINATION");
        assertEquals(6,sp4.get(2).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection4/SP3/EL2/ORIGIN");
        assertEquals(5,sp4.get(2).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection4/SP3/EL2/DESTINATION");
        assertEquals(5,sp4.get(2).getListSegments().get(2).getOrigin().getId(), "Wrong id for Intersection4/SP3/EL3/ORIGIN");
        assertEquals(3,sp4.get(2).getListSegments().get(2).getDestination().getId(), "Wrong id for Intersection4/SP3/EL3/DESTINATION");


        //Intersection 5
        Intersection origin5 = cityMap.getIntersections().get(4);
        ArrayList<Intersection> listUsefulEndPoints5 = new ArrayList<>();
        for (Intersection endPoint : listUsefulPoints) {
            if (tour.isPossiblePath(origin5, endPoint)) {
                listUsefulEndPoints5.add(endPoint);
            }
        }

        //original method to test
        ArrayList<ShortestPath> sp5 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints5, origin5);
        //number of shortest paths
        assertEquals(3,sp5.size(), "Wrong number of shortest paths for Intersection 5");
        //number of segments in each shortest paths
        assertEquals(2,sp5.get(0).getListSegments().size(), "Wrong number of segments in first SP for Intersection 5");
        assertEquals(2,sp5.get(1).getListSegments().size(), "Wrong number of segments in second SP for Intersection 5");
        assertEquals(1,sp5.get(2).getListSegments().size(), "Wrong number of segments in third SP for Intersection 5");

        //Intersection of each list of segment (each shortest path)
        //first shortest path
        assertEquals(5,sp5.get(0).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection5/SP1/EL1/ORIGIN");
        assertEquals(6,sp5.get(0).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection5/SP1/EL1/DESTINATION");
        assertEquals(6,sp5.get(0).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection5/SP1/EL2/ORIGIN");
        assertEquals(1,sp5.get(0).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection5/SP1/EL2/DESTINATION");
        //second shortest path
        assertEquals(5,sp5.get(1).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection5/SP2/EL1/ORIGIN");
        assertEquals(6,sp5.get(1).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection5/SP2/EL1/DESTINATION");
        assertEquals(6,sp5.get(1).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection5/SP2/EL2/ORIGIN");
        assertEquals(2,sp5.get(1).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection5/SP2/EL2/DESTINATION");
        //third shortest path
        assertEquals(5,sp5.get(2).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection5/SP3/EL1/ORIGIN");
        assertEquals(3,sp5.get(2).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection5/SP3/EL1/DESTINATION");


        /*
        5,6,1
        5,6,2
        5,3
         */
    }

    /**
     * Method to test:
     * dijkstra()
     *
     * What it does:
     * Expect dijkstra to handle impossible graph
     */
    @Test
    @DisplayName("Test on dijkstra - Unreachable intersection")
    void impossiblePathDijkstraTest(){
        // Add request alone impossible to access by any segment
        Intersection aloneIntersec = new Intersection(10,45.3112,33.2245);
        Intersection aloneIntersec2 = new Intersection(11,45.5112,33.2245);
        Request request =  new Request(100,120, aloneIntersec,aloneIntersec2,new Color(65, 65, 65));
        listRequest = tour.getPlanningRequests();
        listRequest.add(request);


        List<Intersection> listIntersectionsDijkstra = cityMap.getIntersections();
        listIntersectionsDijkstra.add(aloneIntersec);
        listIntersectionsDijkstra.add(aloneIntersec2);

        ArrayList<Intersection> listUsefulPoints = new ArrayList<>();
        for(Request req : listRequest) {
            listUsefulPoints.add(req.getPickupAddress());
            listUsefulPoints.add(req.getDeliveryAddress());
        }
        listUsefulPoints.add(cityMap.getIntersections().get(0));
        Intersection origin1 = cityMap.getIntersections().get(0);


        ArrayList<Intersection> listUsefulEndPoints1 = new ArrayList<>();
        for (Intersection endPoint : listUsefulPoints) {
            if (tour.isPossiblePath(origin1, endPoint)) {
                listUsefulEndPoints1.add(endPoint);
            }
        }

        ArrayList<ShortestPath> sp1 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints1, origin1);
    }

}
