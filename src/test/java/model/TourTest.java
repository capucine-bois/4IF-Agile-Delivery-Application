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
}