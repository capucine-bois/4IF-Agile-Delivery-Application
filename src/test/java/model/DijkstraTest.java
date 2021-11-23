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
import java.util.List;

import static xml.XMLDeserializer.deserializeMap;
import static xml.XMLDeserializer.deserializeRequests;

public class DijkstraTest {
    private Tour tour = new Tour();
    private CityMap cityMap = new CityMap();
    private ArrayList<Intersection> listIntersection;
    private ArrayList<Request> listRequest;
    public DijkstraTest() throws ParserConfigurationException, ExceptionXML, SAXException, IOException {
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
    public void resetCityMap() throws IOException, SAXException, ParserConfigurationException, ExceptionXML {
        initializeMap();
    }

    private void initializeMap() throws ParserConfigurationException, IOException, SAXException, ExceptionXML {
        File file = new File("./src/test/resources/mapTestDijkstra.xml");
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document docParsed = db.parse(file);
        deserializeMap(cityMap, docParsed);
        deserializeRequests(tour, cityMap, docParsed);
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
    private void dijkstraTest(){
        List<Intersection> listIntersectionsDijkstra = cityMap.getIntersections();

        // Intersection 1
        Intersection origin1 = cityMap.getIntersections().get(0);
        ArrayList<Intersection> listUsefulEndPointsDijkstra1 = new ArrayList<>();
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(1));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(3));



        //Intersection 2
        Intersection origin2 = cityMap.getIntersections().get(1);
        ArrayList<Intersection> listUsefulEndPointsDijkstra2 = new ArrayList<>();
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(2));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(3));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(4));

        //Intersection 3
        Intersection origin3 = cityMap.getIntersections().get(2);
        ArrayList<Intersection> listUsefulEndPointsDijkstra3 = new ArrayList<>();
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(3));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(4));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(0));

        //Intersection 4
        Intersection origin4 = cityMap.getIntersections().get(3);
        ArrayList<Intersection> listUsefulEndPointsDijkstra4 = new ArrayList<>();
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(1));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(4));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(2));

        //Intersection 5
        Intersection origin5 = cityMap.getIntersections().get(4);
        ArrayList<Intersection> listUsefulEndPointsDijkstra5 = new ArrayList<>();
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(2));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(1));
        listUsefulEndPointsDijkstra1.add(listIntersectionsDijkstra.get(0));

    }



}
