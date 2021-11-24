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
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static xml.XMLDeserializer.deserializeMap;
import static xml.XMLDeserializer.deserializeRequests;

class TourTest {
    ArrayList<Intersection> listIntersection = new ArrayList<>();
    Tour tour = new Tour();
    CityMap cityMap = new CityMap();
    ArrayList<Request> listRequest;
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
        listIntersection = new ArrayList<>();
        cityMap = new CityMap();
        File fileMap = new File("./src/test/resources/mapTestDijkstra.xml");
        File fileReq = new File("./src/test/resources/requestsTestDijkstra.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docParsedMap = db.parse(fileMap);
        Document docParsedReq = db.parse(fileReq);
        deserializeMap(cityMap, docParsedMap);
        deserializeRequests(tour, cityMap, docParsedReq);
    }


    /**
     * Method to test:
     * computeTour()
     *
     * What it does:
     * Compute shortest path for the whole Tour
     */
    @Test
    @DisplayName("Test on computeTour")
    void computeTour() {
        listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();

        // Method to test
        tour.computeTour(listIntersection);

        // Result
        ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
        double tourLength = tour.getTourLength();


        // Check answer
        assertEquals(tourLength,565,"Length of the best path isn't find ");
        assertEquals(listShortestPaths.get(0).getStartAddress().getId(), 1,"Start point isn't right");
        assertEquals(listShortestPaths.get(1).getStartAddress().getId(), 4,"Not the best order of request");
        assertEquals(listShortestPaths.get(2).getStartAddress().getId(), 2,"Not the best order of request");
        assertEquals(listShortestPaths.get(3).getStartAddress().getId(), 5,"Not the best order of request");
        assertEquals(listShortestPaths.get(4).getStartAddress().getId(), 3,"Not the best order of request");
        assertEquals(listShortestPaths.get(4).getEndAddress().getId(), 1,"End of the tour isn't equal to start ");
    }

    /**
     * Method to test:
     * computeTour()
     *
     * What it does:
     * Compute shortest path for a wrong tour (one way road, no other road)
     */
    @Test
    @DisplayName("Test on computeTour - Impossible tour (one way road)")
    void computeTourImpossible() {
        listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();

        // Add request alone accessable only in one way
        Intersection aloneIntersec = new Intersection(10,45.3112,33.2245);
        Intersection aloneIntersec2 = new Intersection(11,45.5112,33.2245);
        Request request =  new Request(100,120, aloneIntersec,aloneIntersec2,new Color(65, 65, 65));
        listRequest = tour.getPlanningRequests();
        listRequest.add(request);

        // Add segment to access new points (one way)
        Segment segmentOneWay = new Segment(140,"Rue sens unique",aloneIntersec, listIntersection.get(1));
        Segment segmentOneWay2 = new Segment(10,"Rue sens unique 2",aloneIntersec2,aloneIntersec);
        listIntersection.get(1).addAdjacentSegment(segmentOneWay);
        aloneIntersec.addAdjacentSegment(segmentOneWay2);


        // Method to test
        tour.computeTour(listIntersection);

        // Result
        ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
        double tourLength = tour.getTourLength();


        // Check answer

    }

    /**
     * Method to test:
     * computeTour()
     *
     * What it does:
     * Compute shortest path for a wrong tour
     */
    @Test
    @DisplayName("Test on computeTour - Empty tour")
    void computeEmptyTour() {

        // Add one request only
        Intersection aloneIntersec = new Intersection(1,45.3112,33.2245);
        listIntersection.add(aloneIntersec);


        // Method to test
        tour.computeTour(listIntersection);

        // Result
        ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
        double tourLength = tour.getTourLength();


        // Check answer
    }

    /**
     * Method to test:
     * computeTour()
     *
     * What it does:
     * Compute shortest path for a wrong tour (delivery unreachable)
     */
    @Test
    @DisplayName("Test on computeTour - Impossible tour (no segment to access intersection)")
    void computeTourImpossibleNoRoad() {
        listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();

        // Add request
        Intersection aloneIntersec = new Intersection(10,45.3112,33.2245);
        Intersection aloneIntersec2 = new Intersection(11,45.5112,33.2245);
        Request request =  new Request(100,120, aloneIntersec,aloneIntersec2,new Color(65, 65, 65));
        listRequest = tour.getPlanningRequests();
        listRequest.add(request);

        // Add segment to access pick-up point (but not the delivery point)
        Segment segment = new Segment(140,"Rue Richard",aloneIntersec, listIntersection.get(1));
        Segment segment2 = new Segment(140,"Rue Richard",listIntersection.get(1), aloneIntersec);
        listIntersection.get(1).addAdjacentSegment(segment);
        aloneIntersec.addAdjacentSegment(segment2);


        // Method to test
        tour.computeTour(listIntersection);

        // Result
        ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
        double tourLength = tour.getTourLength();


        // Check answer

    }
}