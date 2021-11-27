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
    private ArrayList<Intersection> listUsefulPoints;
    private ArrayList<Request> listRequest;
    public DijkstraTest(){
        resetCityMap();
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
    public void resetCityMap() {
        tour = new Tour();
        cityMap = new CityMap();
        listRequest = new ArrayList<>();
        listUsefulPoints = new ArrayList<>();
        // Setup intersection
        Intersection i1 = new Intersection(1,45.75406,4.857418);
        Intersection i2 = new Intersection(2,45.750404,4.8744674);
        Intersection i3 = new Intersection(3,45.75871,4.8704023);
        Intersection i4 = new Intersection(4,45.75171,4.871819);
        Intersection i5 = new Intersection(5,45.750896,4.859119);
        Intersection i6 = new Intersection(6,45.75159,4.8700043);
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
        Request r1 = new Request(180,240,i2,i3,Color.BLACK);
        Request r2 = new Request(90,185,i4,i5,Color.BLACK);
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
        tour.setPlanningRequests(listRequest);

        // Setup Dijkstra input
        listUsefulPoints.add(i2);
        listUsefulPoints.add(i3);
        listUsefulPoints.add(i4);
        listUsefulPoints.add(i5);

    }



    /**
     * Method to test:
     * dijkstra()
     *
     * What it does:
     * Compute shortests paths from one origin intersection
     */
    @Nested
    @DisplayName("Test on dijkstra")
    class dijkstraTest {
        List<Intersection> listIntersectionsDijkstra;
        ArrayList<Intersection> listUsefulEndPoints;

        @BeforeEach
        void beforeEach(){
            listIntersectionsDijkstra = new ArrayList<>();
            listUsefulEndPoints = new ArrayList<>();
            listIntersectionsDijkstra = cityMap.getIntersections();
            listUsefulEndPoints = (ArrayList<Intersection>) cityMap.getIntersections();
        }



        @Test
        @DisplayName("Origin - 1 ")
        void origin1() {
            //origin
            Intersection origin1 = cityMap.getIntersections().get(0);
            //original method to test
            ArrayList<ShortestPath> sp1 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints, origin1);
            //number of shortest paths
            assertEquals(6,sp1.size(), "Wrong number of shortest paths for Intersection 1");
            //number of segments in each shortest paths
            assertEquals(1,sp1.get(1).getListSegments().size(), "Wrong number of segments in first SP for Intersection 1");
            assertEquals(2,sp1.get(2).getListSegments().size(), "Wrong number of segments in second SP for Intersection 1");

            //Intersection of each list of segment (each shortest path)
            //first shortest path
            assertEquals(1,sp1.get(2).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection1/SP1/EL1/ORIGIN");
            assertEquals(6,sp1.get(2).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection1/SP1/EL1/DESTINATION");
            assertEquals(6,sp1.get(2).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection1/SP1/EL2/ORIGIN");
            assertEquals(4,sp1.get(2).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection1/SP1/EL2/DESTINATION");
            //second shortest path
            assertEquals(1,sp1.get(1).getListSegments().get(0).getOrigin().getId(), "Wrong id for Intersection1/SP2/EL1/ORIGIN");
            assertEquals(6,sp1.get(1).getListSegments().get(0).getDestination().getId(), "Wrong id for Intersection1/SP2/EL1/DESTINATION");
            assertEquals(6,sp1.get(1).getListSegments().get(1).getOrigin().getId(), "Wrong id for Intersection1/SP2/EL2/ORIGIN");
            assertEquals(2,sp1.get(1).getListSegments().get(1).getDestination().getId(), "Wrong id for Intersection1/SP2/EL2/DESTINATION");
        }


        @Test
        @DisplayName("Origin - 2 ")
        void origin2() {
            Intersection origin2 = cityMap.getIntersections().get(1);
            //original method to test
            ArrayList<ShortestPath> sp2 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints, origin2);
            //number of shortest paths
            assertEquals(6,sp2.size(), "Wrong number of shortest paths for Intersection 2");
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
        }

        @Test
        @DisplayName("Origin - 3 ")
        void origin3() {
            Intersection origin3 = cityMap.getIntersections().get(2);
            //original method to test
            ArrayList<ShortestPath> sp3 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints, origin3);
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

        }

        @Test
        @DisplayName("Origin - 4 ")
        void origin4() {
            Intersection origin4 = cityMap.getIntersections().get(3);
            //original method to test
            ArrayList<ShortestPath> sp4 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints, origin4);
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
        }


        @Test
        @DisplayName("Origin - 5 ")
        void origin5() {
            Intersection origin5 = cityMap.getIntersections().get(4);
            //original method to test
            ArrayList<ShortestPath> sp5 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints, origin5);
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

        }
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
        listRequest.add(request);

        //
        List<Intersection> listIntersectionsDijkstra = cityMap.getIntersections();
        ArrayList<Intersection> listUsefulEndPoints = (ArrayList<Intersection>) cityMap.getIntersections();


        Intersection origin1 = cityMap.getIntersections().get(0);



        ArrayList<ShortestPath> sp1 = tour.dijkstra(listIntersectionsDijkstra, listUsefulEndPoints, origin1);


        // Verify answer
        // TODO: Add answer and check that dijkstra hasn't added cost to the unreachable intersection
        // TODO: Notify user that some intersections can't be access
        fail();
    }

}
