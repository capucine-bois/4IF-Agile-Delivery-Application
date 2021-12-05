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
    public void resetCityMap() {
        initializeMapAndTour();
    }

    private void initializeMapAndTour() {
        tour = new Tour();
        cityMap = new CityMap();
        listIntersection = new ArrayList<>();
        listRequest = new ArrayList<>();
        // Setup intersection
        Intersection i1 = new Intersection(0,45.75406,4.857418);
        Intersection i2 = new Intersection(1,45.750404,4.8744674);
        Intersection i3 = new Intersection(2,45.75871,4.8704023);
        Intersection i4 = new Intersection(3,45.75171,4.871819);
        Intersection i5 = new Intersection(4,45.750896,4.859119);
        Intersection i6 = new Intersection(5,45.75159,4.8700043);
        // Setup segment
        Segment s1 = new Segment(75,"Rue du Dauphiné",i1,i6);
        Segment s2 = new Segment(75,"Rue du Pardo",i6,i1);
        Segment s3 = new Segment(77,"Rue du Brager",i6,i2);
        Segment s4 = new Segment(77,"Rue de l'avenir",i2,i6);
        Segment s5 = new Segment(68,"Rue d'Esteban",i4,i6);
        Segment s6 = new Segment(68,"Rue de Fabien",i6,i4);
        Segment s7 = new Segment(9,"Rue de la Republique",i5,i6);
        Segment s8 = new Segment(9,"Rue de Domrémy",i6,i5);
        Segment s9 = new Segment(16,"Cours Albert Thomas",i4,i2);
        Segment s10 = new Segment(16,"Cours Thomas",i2,i4);
        Segment s11 = new Segment(118,"Boulevard Vivier-Merle",i5,i3);
        Segment s12 = new Segment(118,"oulevard Vivier-Pat",i3,i5);
        // Setup request
        Request r1 = new Request(180,240,i2,i3);
        Request r2 = new Request(90,185,i4,i5);
        i1.addAdjacentSegment(s2);
        i2.addAdjacentSegment(s3);
        i2.addAdjacentSegment(s9);
        i3.addAdjacentSegment(s11);
        i4.addAdjacentSegment(s6);
        i4.addAdjacentSegment(s10);
        i5.addAdjacentSegment(s8);
        i5.addAdjacentSegment(s12);
        i6.addAdjacentSegment(s1);
        i6.addAdjacentSegment(s4);
        i6.addAdjacentSegment(s5);
        i6.addAdjacentSegment(s7);
        // Setup CityMap
        cityMap.addIntersection(i1);
        cityMap.addIntersection(i2);
        cityMap.addIntersection(i3);
        cityMap.addIntersection(i4);
        cityMap.addIntersection(i5);
        cityMap.addIntersection(i6);
        // Setup tour
        tour.setDepartureTime("8:0:0");
        tour.setDepotAddress(i1);
        listRequest.add(r1);
        listRequest.add(r2);
        tour.addRequest(r1);
        tour.addRequest(r2);
    }



    /**
     * Method to test:
     * computeTour()
     *
     * What it does:
     * Compute shortest path for the whole Tour
     */
    @Nested
    @DisplayName("Test on computeTour")
    class computeTour {
        @Test
        @DisplayName("Normal scenario")
        void computeTourNormal() {
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();

            // Method to test
            tour.computeTour(listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
            double tourLength = tour.getTourLength();


            // Check answer
            assertEquals(tourLength,565,"Length of the best path isn't find ");
            assertEquals(listShortestPaths.get(0).getStartAddress().getId(), 0,"Start point isn't right");
            assertEquals(listShortestPaths.get(1).getStartAddress().getId(), 3,"Not the best order of request");
            assertEquals(listShortestPaths.get(2).getStartAddress().getId(), 1,"Not the best order of request");
            assertEquals(listShortestPaths.get(3).getStartAddress().getId(), 4,"Not the best order of request");
            assertEquals(listShortestPaths.get(4).getStartAddress().getId(), 2,"Not the best order of request");
            assertEquals(listShortestPaths.get(4).getEndAddress().getId(), 0,"End of the tour isn't equal to start ");
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
            Intersection aloneIntersec = new Intersection(0,45.3112,33.2245);
            Tour tour = new Tour();
            tour.setDepotAddress(aloneIntersec);
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();


            // Method to test
            tour.computeTour(listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
            double tourLength = tour.getTourLength();

            // Check answer
            assertEquals(tourLength,0,"Path length must be 0");
            assertTrue(listShortestPaths.isEmpty(),"List of shortest must be empty");
        }
    }



}