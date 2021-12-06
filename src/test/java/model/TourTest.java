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
    ArrayList<ShortestPath> paths = new ArrayList<>();
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
        Request r2 = new Request(95,185,i4,i5);
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
    class TestComputeTour {
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


        @Test
        @DisplayName("Empty tour")
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


    /**
     * Method to test:
     * updateTimes()
     *
     * What it does:
     * Update all durations
     */
    @Nested
    @DisplayName("Test on updateTimes")
    class updateTimes {
        @Test
        @DisplayName("Normal scenario")
        void updateTimesNormal() {
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();
            ArrayList<Segment> emptySegmentList = new ArrayList<>();
            // create the previous list of shortest path (before adding a request)
            ShortestPath sh1 = new ShortestPath(143,emptySegmentList, listIntersection.get(0), listIntersection.get(3));
            sh1.setStartNodeNumber(0);
            sh1.setEndNodeNumber(3);
            tour.addShortestPaths(sh1);
            ShortestPath sh2 = new ShortestPath(16,emptySegmentList, listIntersection.get(3), listIntersection.get(1));
            sh2.setStartNodeNumber(3);
            sh2.setEndNodeNumber(1);
            tour.addShortestPaths(sh2);
            ShortestPath sh3 = new ShortestPath(86,emptySegmentList, listIntersection.get(1), listIntersection.get(4));
            sh3.setStartNodeNumber(1);
            sh3.setEndNodeNumber(4);
            tour.addShortestPaths(sh3);
            ShortestPath sh4 = new ShortestPath(118,emptySegmentList, listIntersection.get(4), listIntersection.get(2));
            sh4.setStartNodeNumber(4);
            sh4.setEndNodeNumber(2);
            tour.addShortestPaths(sh4);
            ShortestPath sh5 = new ShortestPath(202,emptySegmentList, listIntersection.get(2), listIntersection.get(0));
            sh5.setStartNodeNumber(2);
            sh5.setEndNodeNumber(0);
            tour.addShortestPaths(sh5);
            tour.updateTimes();
            assertEquals("08:13",tour.getArrivalTime(),"Arrival time isn't right.");

        }
    }


    /**
     * Method to test:
     * insertRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, Request requestToInsert, List<Intersection> allIntersections)
     *
     * What it does:
     * Insert the request in parameter at the tour
     */
    @Nested
    @DisplayName("Test on insertRequest")
    class TestInsertRequest {

        @BeforeEach
        public void setList() {
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();
            ArrayList<Segment> emptySegmentList = new ArrayList<>();
            // create the previous list of shortest path (before adding a request)
            tour.addShortestPaths(new ShortestPath(143,emptySegmentList, listIntersection.get(0), listIntersection.get(3)));
            tour.addShortestPaths(new ShortestPath(16,emptySegmentList, listIntersection.get(3), listIntersection.get(1)));
            tour.addShortestPaths(new ShortestPath(86,emptySegmentList, listIntersection.get(1), listIntersection.get(4)));
            tour.addShortestPaths(new ShortestPath(118,emptySegmentList, listIntersection.get(4), listIntersection.get(2)));
            tour.addShortestPaths(new ShortestPath(202,emptySegmentList, listIntersection.get(2), listIntersection.get(0)));
            listRequest = tour.getPlanningRequests();
        }

        @Test
        @DisplayName("Normal scenario")
        void insertRequestNormal() {
            // Add an intersection in the same scc of depot
            Intersection i7 = new Intersection(6,45.759,4.8703);
            listIntersection.add(i7);
            Segment s13 = new Segment(75,"Rue du Dauphiné",listIntersection.get(0),i7);
            Segment s14 = new Segment(5,"Rue du Dauphin",listIntersection.get(5),i7);
            Segment s15 = new Segment(75,"Rue de l'Orque",i7,listIntersection.get(0));
            Segment s16 = new Segment(5,"Rue du Bélouga",i7,listIntersection.get(5));
            i7.addAdjacentSegment(s13);
            i7.addAdjacentSegment(s14);
            listIntersection.get(0).addAdjacentSegment(s15);
            listIntersection.get(5).addAdjacentSegment(s16);
            Request r3 = new Request(180,240,i7,listIntersection.get(5));
            //tour.getPlanningRequests().add(r3);

            long lastPointBeforeDepot = tour.getListShortestPaths().get(tour.getListShortestPaths().size()-1).getStartAddress().getId();

            // Method to test
            tour.insertRequest(tour.getPlanningRequests().size(),tour.getListShortestPaths().size()-1, tour.getListShortestPaths().size(),r3, listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
            int size = listShortestPaths.size();

            for(ShortestPath sh : listShortestPaths) {
                System.out.println("start from : " + sh.getStartAddress().getId() + " to : " + sh.getEndAddress().getId() + ". The length of the path is : " + sh.getPathLength());
            }
            // Check answer
            assertEquals(7, size,  "The number of shortest paths isn't right");
            assertEquals(575, tour.getTourLength(),  "The total length isn't right.");
            assertEquals(lastPointBeforeDepot, listShortestPaths.get(size-3).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i7.getId(), listShortestPaths.get(size-3).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i7.getId(), listShortestPaths.get(size-2).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(5, listShortestPaths.get(size-2).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(5,listShortestPaths.get(size-1).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0,listShortestPaths.get(size-1).getEndAddress().getId(), "The order of shortest paths isn't right");

        }


        /**
         * Method to test:
         * insertRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, Request requestToInsert, List<Intersection> allIntersections)
         *
         * What it does:
         * Try to insert a request while one intersection of the request is the same as the depot
         */
        @Test
        @DisplayName("Delivery Address is the same as the depot")
        void insertRequestDeliveryEqualDepot() {

            Intersection i1 = listIntersection.get(0);
            Intersection i5 = listIntersection.get(5);
            Request r3 = new Request(180,240,i5,i1);

            long lastPointBeforeDepot = tour.getListShortestPaths().get(tour.getListShortestPaths().size()-1).getStartAddress().getId();

            // Method to test
            tour.insertRequest(tour.getPlanningRequests().size(),tour.getListShortestPaths().size()-1, tour.getListShortestPaths().size(),r3, listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
            int size = listShortestPaths.size();

            // Check answer
            assertEquals(7, size, "The number of shortest paths isn't right");
            assertEquals(565, tour.getTourLength(),  "The total length isn't right.");
            assertEquals(lastPointBeforeDepot, listShortestPaths.get(size-3).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i5.getId(),listShortestPaths.get(size-3).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i5.getId(), listShortestPaths.get(size-2).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(size-2).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(size-1).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(size-1).getEndAddress().getId(), "The order of shortest paths isn't right");
        }


        /**
         * Method to test:
         * insertRequest(int indexRequest, int indexShortestPathToPickup, int indexShortestPathToDelivery, Request requestToInsert, List<Intersection> allIntersections)
         *
         * What it does:
         * Try to insert the pickup and the delivery addresses are equals
         */
        @Test
        @DisplayName("Delivery Address is the same as the pickup address")
        void insertRequestDeliveryEqualPickup() {

            Intersection i5 = listIntersection.get(5);
            Request r3 = new Request(180,240,i5,i5);

            long lastPointBeforeDepot = tour.getListShortestPaths().get(tour.getListShortestPaths().size()-1).getStartAddress().getId();

            // Method to test
            tour.insertRequest(tour.getPlanningRequests().size(),tour.getListShortestPaths().size()-1, tour.getListShortestPaths().size(),r3, listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();
            int size = listShortestPaths.size();

            // Check answer
            assertEquals(7,size,  "The number of shortest paths isn't right");
            assertEquals(565, tour.getTourLength(),  "The total length isn't right.");
            assertEquals(lastPointBeforeDepot,listShortestPaths.get(size-3).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i5.getId(), listShortestPaths.get(size-3).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i5.getId(), listShortestPaths.get(size-2).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i5.getId(), listShortestPaths.get(size-2).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(i5.getId(), listShortestPaths.get(size-1).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(size-1).getEndAddress().getId(), "The order of shortest paths isn't right");
        }

        @Test
        @DisplayName("Put a request back, at the middle of the list, after undo a delete")
        void insertRequestBackMiddle() {

            Intersection i7 = new Intersection(6,45.759,4.8703);
            listIntersection.add(i7);
            Segment s13 = new Segment(75,"Rue du Dauphiné",listIntersection.get(0),i7);
            Segment s14 = new Segment(5,"Rue du Dauphin",listIntersection.get(5),i7);
            Segment s15 = new Segment(75,"Rue de l'Orque",i7,listIntersection.get(0));
            Segment s16 = new Segment(5,"Rue du Bélouga",i7,listIntersection.get(5));
            i7.addAdjacentSegment(s13);
            i7.addAdjacentSegment(s14);
            listIntersection.get(0).addAdjacentSegment(s15);
            listIntersection.get(5).addAdjacentSegment(s16);
            Request r3 = new Request(180,240,i7,listIntersection.get(5));


            // Method to test
            tour.insertRequest(1, 1, 3,r3, listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();

            for(ShortestPath sh : listShortestPaths) {
                System.out.println("start from : " + sh.getStartAddress().getId() + " to : " + sh.getEndAddress().getId() + ". The length of the path is : " + sh.getPathLength());
            }

            int size = listShortestPaths.size();
            // Check answer
            assertEquals(7,size,  "The number of shortest paths isn't right");
            assertEquals(704, tour.getTourLength(),  "The total length isn't right.");
            assertEquals(0,listShortestPaths.get(0).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(3, listShortestPaths.get(0).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(3, listShortestPaths.get(1).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(6, listShortestPaths.get(1).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(6, listShortestPaths.get(2).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(1, listShortestPaths.get(2).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(1, listShortestPaths.get(3).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(5, listShortestPaths.get(3).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(5, listShortestPaths.get(4).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(4, listShortestPaths.get(4).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(4, listShortestPaths.get(5).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(2, listShortestPaths.get(5).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(2, listShortestPaths.get(6).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(6).getEndAddress().getId(), "The order of shortest paths isn't right");
        }


        @Test
        @DisplayName("Put a request back, at the start of the list, after undo a delete")
        void insertRequestBackStart() {

            Intersection i7 = new Intersection(6,45.759,4.8703);
            listIntersection.add(i7);
            Segment s13 = new Segment(75,"Rue du Dauphiné",listIntersection.get(0),i7);
            Segment s14 = new Segment(5,"Rue du Dauphin",listIntersection.get(5),i7);
            Segment s15 = new Segment(75,"Rue de l'Orque",i7,listIntersection.get(0));
            Segment s16 = new Segment(5,"Rue du Bélouga",i7,listIntersection.get(5));
            i7.addAdjacentSegment(s13);
            i7.addAdjacentSegment(s14);
            listIntersection.get(0).addAdjacentSegment(s15);
            listIntersection.get(5).addAdjacentSegment(s16);
            Request r3 = new Request(180,240,i7,listIntersection.get(5));


            // Method to test
            tour.insertRequest(0, 0, 1,r3, listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();

            for(ShortestPath sh : listShortestPaths) {
                System.out.println("start from : " + sh.getStartAddress().getId() + " to : " + sh.getEndAddress().getId() + ". The length of the path is : " + sh.getPathLength());
            }

            int size = listShortestPaths.size();
            // Check answer
            assertEquals(7,size,  "The number of shortest paths isn't right");
            assertEquals(570, tour.getTourLength(),  "The total length isn't right.");
            assertEquals(0,listShortestPaths.get(0).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(6, listShortestPaths.get(0).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(6, listShortestPaths.get(1).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(5, listShortestPaths.get(1).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(5, listShortestPaths.get(2).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(3, listShortestPaths.get(2).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(3, listShortestPaths.get(3).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(1, listShortestPaths.get(3).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(1, listShortestPaths.get(4).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(4, listShortestPaths.get(4).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(4, listShortestPaths.get(5).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(2, listShortestPaths.get(5).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(2, listShortestPaths.get(6).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(6).getEndAddress().getId(), "The order of shortest paths isn't right");
        }




    }


    /**
     * Method to test:
     * moveIntersectionBefore()
     *
     * What it does:
     * Move up an intersection of the tour
     */
    @Nested
    @DisplayName("Test on moveIntersectionBefore")
    class TestMoveIntersectionBefore {

        @BeforeEach
        public void setList() {
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();
            ArrayList<Segment> emptyListSegment = new ArrayList<>();
            // create the previous list of shortest path (before adding a request)
            tour.addShortestPaths(new ShortestPath(143,emptyListSegment, listIntersection.get(0), listIntersection.get(3)));
            tour.addShortestPaths(new ShortestPath(16,emptyListSegment, listIntersection.get(3), listIntersection.get(1)));
            tour.addShortestPaths(new ShortestPath(86,emptyListSegment, listIntersection.get(1), listIntersection.get(4)));
            tour.addShortestPaths(new ShortestPath(118,emptyListSegment, listIntersection.get(4), listIntersection.get(2)));
            tour.addShortestPaths(new ShortestPath(202,emptyListSegment, listIntersection.get(2), listIntersection.get(0)));
            listRequest = tour.getPlanningRequests();
        }

        @Test
        @DisplayName("Normal scenario")
        void moveBefore(){



            // Method to test
            tour.moveIntersectionBefore(1,listIntersection);

            // Check answer (swap 1 and 3)
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();
            assertEquals(0,newListShortestPath.get(0).getStartAddress().getId(),"Start must be intersection 0");
            assertEquals(1,newListShortestPath.get(0).getEndAddress().getId());
            // Check new length (i1 -> i6 -> i4)
            assertEquals(152.0,newListShortestPath.get(0).getPathLength(),"Wrong path found");

            assertEquals(1,newListShortestPath.get(1).getStartAddress().getId());
            assertEquals(3,newListShortestPath.get(1).getEndAddress().getId());
            // Check new length (i2 -> i4)
            assertEquals(16.0,newListShortestPath.get(1).getPathLength(),"Wrong path found");

            assertEquals(3,newListShortestPath.get(2).getStartAddress().getId());
            assertEquals(4,newListShortestPath.get(2).getEndAddress().getId());
            assertEquals(4,newListShortestPath.get(3).getStartAddress().getId());
            assertEquals(2,newListShortestPath.get(3).getEndAddress().getId());
            assertEquals(2,newListShortestPath.get(4).getStartAddress().getId());
            assertEquals(0,newListShortestPath.get(4).getEndAddress().getId(),"End must be intersection 0");

        }
        @Test
        @DisplayName("Move before on the first intersection")
        void moveBeforeFirst(){
            // Method to test
            tour.moveIntersectionBefore(0,listIntersection);

            // Check answer (must remain identical)
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();
            assertEquals(0,newListShortestPath.get(0).getStartAddress().getId(),"Start must be intersection 0");
            assertEquals(3,newListShortestPath.get(0).getEndAddress().getId());
            assertEquals(3,newListShortestPath.get(1).getStartAddress().getId());
            assertEquals(1,newListShortestPath.get(1).getEndAddress().getId());
            assertEquals(1,newListShortestPath.get(2).getStartAddress().getId());
            assertEquals(4,newListShortestPath.get(2).getEndAddress().getId());
            assertEquals(4,newListShortestPath.get(3).getStartAddress().getId());
            assertEquals(2,newListShortestPath.get(3).getEndAddress().getId());
            assertEquals(2,newListShortestPath.get(4).getStartAddress().getId());
            assertEquals(0,newListShortestPath.get(4).getEndAddress().getId(),"End must be intersection 0");
        }

        @Test
        @DisplayName("Move before last intersection")
        void moveBeforeDelivery(){
            // Method to test
            tour.moveIntersectionBefore(5,listIntersection);

            // Check answer (must remain identical)
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();
            assertEquals(0,newListShortestPath.get(0).getStartAddress().getId(),"Start must be intersection 0");
            assertEquals(3,newListShortestPath.get(0).getEndAddress().getId());
            assertEquals(3,newListShortestPath.get(1).getStartAddress().getId());
            assertEquals(1,newListShortestPath.get(1).getEndAddress().getId());
            assertEquals(1,newListShortestPath.get(2).getStartAddress().getId());
            assertEquals(4,newListShortestPath.get(2).getEndAddress().getId());
            assertEquals(4,newListShortestPath.get(3).getStartAddress().getId());
            assertEquals(2,newListShortestPath.get(3).getEndAddress().getId());
            assertEquals(2,newListShortestPath.get(4).getStartAddress().getId());
            assertEquals(0,newListShortestPath.get(4).getEndAddress().getId(),"End must be intersection 0");
        }

    }


    /**
     * Method to test:
     * removeRequest()
     *
     * What it does:
     * Remove the request from the tour
     */
    @Nested
    @DisplayName("Test on removeRequest")
    class TestRemoveRequest {

        @BeforeEach
        public void setList() {
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();
            ArrayList<Segment> emptyListSegment = new ArrayList<>();
            // create the previous list of shortest path (before adding a request)
            tour.addShortestPaths(new ShortestPath(143, emptyListSegment, listIntersection.get(0), listIntersection.get(3)));
            tour.addShortestPaths(new ShortestPath(16, emptyListSegment, listIntersection.get(3), listIntersection.get(1)));
            tour.addShortestPaths(new ShortestPath(86, emptyListSegment, listIntersection.get(1), listIntersection.get(4)));
            tour.addShortestPaths(new ShortestPath(118, emptyListSegment, listIntersection.get(4), listIntersection.get(2)));
            tour.addShortestPaths(new ShortestPath(202, emptyListSegment, listIntersection.get(2), listIntersection.get(0)));
            listRequest = tour.getPlanningRequests();
        }

        @Test
        @DisplayName("Normal scenario")
        void removeRequestNormal() {


            // Method to test
            tour.removeRequest(0,1,3,listIntersection);

            // Result
            ArrayList<ShortestPath> listShortestPaths = tour.getListShortestPaths();

            for(ShortestPath sh : listShortestPaths) {
                System.out.println("start from : " + sh.getStartAddress().getId() + " to : " + sh.getEndAddress().getId() + ". The length of the path is : " + sh.getPathLength());
            }

            int size = listShortestPaths.size();
            // Check answer
            assertEquals(3,size,  "The number of shortest paths isn't right");
            assertEquals(304, tour.getTourLength(),  "The total length isn't right.");
            assertEquals(0,listShortestPaths.get(0).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(3, listShortestPaths.get(0).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(3, listShortestPaths.get(1).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(4, listShortestPaths.get(1).getEndAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(4, listShortestPaths.get(2).getStartAddress().getId(), "The order of shortest paths isn't right");
            assertEquals(0, listShortestPaths.get(2).getEndAddress().getId(), "The order of shortest paths isn't right");

        }
    }

    /**
     * Method to test:
     * changeAddress()
     *
     * What it does:
     * Change address of an intersection
     */
    @Nested
    @DisplayName("Test on moveIntersectionBefore")
    class TestChangeAddress {

        @BeforeEach
        public void setList() {
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();
            ArrayList<Segment> emptyListSegment = new ArrayList<>();
            // create the previous list of shortest path (before adding a request)
            tour.addShortestPaths(new ShortestPath(143,emptyListSegment, listIntersection.get(0), listIntersection.get(3)));
            tour.addShortestPaths(new ShortestPath(16,emptyListSegment, listIntersection.get(3), listIntersection.get(1)));
            tour.addShortestPaths(new ShortestPath(86,emptyListSegment, listIntersection.get(1), listIntersection.get(4)));
            tour.addShortestPaths(new ShortestPath(118,emptyListSegment, listIntersection.get(4), listIntersection.get(2)));
            tour.addShortestPaths(new ShortestPath(202,emptyListSegment, listIntersection.get(2), listIntersection.get(0)));
            listRequest = tour.getPlanningRequests();
            tour.getListShortestPaths().get(0).setStartNodeNumber(0);
            tour.getListShortestPaths().get(0).setEndNodeNumber(3);
            tour.getListShortestPaths().get(1).setStartNodeNumber(3);
            tour.getListShortestPaths().get(1).setEndNodeNumber(1);
            tour.getListShortestPaths().get(2).setStartNodeNumber(1);
            tour.getListShortestPaths().get(2).setEndNodeNumber(4);
            tour.getListShortestPaths().get(3).setStartNodeNumber(4);
            tour.getListShortestPaths().get(3).setEndNodeNumber(2);
            tour.getListShortestPaths().get(4).setStartNodeNumber(2);
            tour.getListShortestPaths().get(4).setEndNodeNumber(0);

            // Add intersection and segment
            Intersection i7 = new Intersection(6,45.759,4.8703);
            listIntersection.add(i7);
            Segment s13 = new Segment(75,"Rue du Dauphiné",listIntersection.get(0),i7);
            Segment s14 = new Segment(5,"Rue du Dauphin",listIntersection.get(5),i7);
            Segment s15 = new Segment(75,"Rue de l'Orque",i7,listIntersection.get(0));
            Segment s16 = new Segment(5,"Rue du Bélouga",i7,listIntersection.get(5));
            i7.addAdjacentSegment(s13);
            i7.addAdjacentSegment(s14);
            listIntersection.get(0).addAdjacentSegment(s15);
            listIntersection.get(5).addAdjacentSegment(s16);

            for(ShortestPath s : tour.getListShortestPaths()){
                System.out.println(s.getStartAddress().getId()+"----"+s.getEndAddress().getId());
            }


        }

        @Test
        @DisplayName("Normal scenario")
        void changeAddressNormal(){


            // Method to test
            tour.changeAddress(3,listIntersection.get(6),listIntersection);


            // Check answer
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();

            System.out.println("New shortest path:");
            for(ShortestPath s : newListShortestPath){
                System.out.println(s.getStartAddress().getId()+"----"+s.getEndAddress().getId());
            }
            assertEquals(0,newListShortestPath.get(0).getStartAddress().getId(),"Start must be intersection 0");
            assertEquals(6,newListShortestPath.get(0).getEndAddress().getId(),"New address isn't right");
            assertEquals(6,newListShortestPath.get(1).getStartAddress().getId(),"New address isn't right");
            assertEquals(1,newListShortestPath.get(1).getEndAddress().getId());
        }

        @Test
        @DisplayName("Index out of range")
        void changeAddressIndexOut(){

            // Method to test
            tour.changeAddress(11,listIntersection.get(6),listIntersection);

            // Check answer
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();

            System.out.println("New shortest path:");
            for(ShortestPath s : newListShortestPath){
                System.out.println(s.getStartAddress().getId()+"----"+s.getEndAddress().getId());
            }
        }

        @Test
        @DisplayName("First intersection (depot)")
        void changeAddressFirst(){
            Intersection i = new Intersection(6,45.2314,31.1234);
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();

            // Method to test
            tour.changeAddress(0,i,listIntersection);

            // Check answer
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();

            System.out.println("New shortest path:");
            for(ShortestPath s : newListShortestPath){
                System.out.println(s.getStartAddress().getId()+"----"+s.getEndAddress().getId());
            }
            assertEquals(0,newListShortestPath.get(0).getStartAddress().getId(),"Start must be intersection 0");
            assertEquals(3,newListShortestPath.get(0).getEndAddress().getId());
            assertEquals(3,newListShortestPath.get(1).getStartAddress().getId());
            assertEquals(1,newListShortestPath.get(1).getEndAddress().getId());
        }

        @Test
        @DisplayName("Last intersection (depot)")
        void changeAddressLast(){
            Intersection i = new Intersection(6,45.2314,31.1234);
            listIntersection = (ArrayList<Intersection>) cityMap.getIntersections();

            // Method to test
            tour.changeAddress(9,listIntersection.get(6),listIntersection);

            // Check answer
            ArrayList<ShortestPath> newListShortestPath = tour.getListShortestPaths();

            System.out.println("New shortest path:");
            for(ShortestPath s : newListShortestPath){
                System.out.println(s.getStartAddress().getId()+"----"+s.getEndAddress().getId());
            }

            assertEquals(0,newListShortestPath.get(0).getStartAddress().getId(),"Start must be intersection 0");
            assertEquals(3,newListShortestPath.get(0).getEndAddress().getId());
            assertEquals(3,newListShortestPath.get(1).getStartAddress().getId());
            assertEquals(1,newListShortestPath.get(1).getEndAddress().getId());

        }

    }



    /**
     * Method to test:
     * changeProcessTime()
     *
     * What it does:
     * Change address of an intersection
     */
    @Nested
    @DisplayName("Test on changeProcessTime")
    class TestChangeProcessTime {

        @Test
        @DisplayName("Normal scenario")
        void changeProcessTimeNormal(){
            tour.changeProcessTime(3,10000);
            assertEquals(180,tour.getPlanningRequests().get(0).getPickupDuration());
            assertEquals(240,tour.getPlanningRequests().get(0).getDeliveryDuration());
            assertEquals(10000,tour.getPlanningRequests().get(1).getPickupDuration());
            assertEquals(185,tour.getPlanningRequests().get(1).getDeliveryDuration());
        }

        @Test
        @DisplayName("Out index")
        void changeProcessOutIndex(){
            tour.changeProcessTime(33,10000);
            assertEquals(180,tour.getPlanningRequests().get(0).getPickupDuration());
            assertEquals(240,tour.getPlanningRequests().get(0).getDeliveryDuration());
            assertEquals(90,tour.getPlanningRequests().get(1).getPickupDuration());
            assertEquals(185,tour.getPlanningRequests().get(1).getDeliveryDuration());
        }

        @Test
        @DisplayName("First index")
        void changeProcessFirstIndex(){
            tour.changeProcessTime(0,10000);
            assertEquals(180,tour.getPlanningRequests().get(0).getPickupDuration());
            assertEquals(240,tour.getPlanningRequests().get(0).getDeliveryDuration());
            assertEquals(90,tour.getPlanningRequests().get(1).getPickupDuration());
            assertEquals(185,tour.getPlanningRequests().get(1).getDeliveryDuration());

        }

        @Test
        @DisplayName("Last index")
        void changeProcessLastIndex(){
            tour.changeProcessTime(9,10000);
            assertEquals(180,tour.getPlanningRequests().get(0).getPickupDuration());
            assertEquals(240,tour.getPlanningRequests().get(0).getDeliveryDuration());
            assertEquals(90,tour.getPlanningRequests().get(1).getPickupDuration());
            assertEquals(185,tour.getPlanningRequests().get(1).getDeliveryDuration());

        }

    }



}