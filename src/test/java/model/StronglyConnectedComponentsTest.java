package model;

import org.junit.jupiter.api.*;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class StronglyConnectedComponentsTest {

    static Instant startedAt;
    ArrayList<Intersection> listIntersection;
    ArrayList<Request> listRequest;
    Intersection i0;

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
        listIntersection = new ArrayList<>();
        listRequest = new ArrayList<>();
        // Setup intersection
        i0 = new Intersection(0,45.75406,4.857418);
        Intersection i1 = new Intersection(1,45.750404,4.8744674);
        Intersection i2 = new Intersection(2,45.75871,4.8704023);
        Intersection i3 = new Intersection(3,45.75171,4.871819);
        Intersection i4 = new Intersection(4,45.750896,4.859119);
        Intersection i5 = new Intersection(5,45.75159,4.8700043);
        listIntersection.add(i0);
        listIntersection.add(i1);
        listIntersection.add(i2);
        listIntersection.add(i3);
        listIntersection.add(i4);
        listIntersection.add(i5);

        // Setup segment
        Segment s1 = new Segment(75,"Rue du Dauphiné", i0,i5);
        Segment s2 = new Segment(75,"Rue du Pardo",i5, i0);
        Segment s3 = new Segment(77,"Rue du Brager",i5,i1);
        Segment s4 = new Segment(77,"Rue de l'avenir",i1,i5);
        Segment s5 = new Segment(68,"Rue d'Esteban",i3,i5);
        Segment s6 = new Segment(68,"Rue de Fabien",i5,i3);
        Segment s7 = new Segment(9,"Rue de la Republique",i4,i5);
        Segment s8 = new Segment(9,"Rue de Domrémy",i5,i4);
        Segment s9 = new Segment(16,"Cours Albert Thomas",i3,i1);
        Segment s10 = new Segment(16,"Cours Thomas",i1,i3);
        Segment s11 = new Segment(118,"Boulevard Vivier-Merle",i4,i2);
        Segment s12 = new Segment(118,"oulevard Vivier-Pat",i2,i4);

        // Add segments to intersections
        i0.addAdjacentSegment(s2);
        i1.addAdjacentSegment(s3);
        i1.addAdjacentSegment(s9);
        i2.addAdjacentSegment(s11);
        i3.addAdjacentSegment(s6);
        i3.addAdjacentSegment(s10);
        i4.addAdjacentSegment(s8);
        i4.addAdjacentSegment(s12);
        i5.addAdjacentSegment(s1);
        i5.addAdjacentSegment(s4);
        i5.addAdjacentSegment(s5);
        i5.addAdjacentSegment(s7);

        // Setup request
        Request r1 = new Request(180,240,i1,i2);
        Request r2 = new Request(90,185,i3,i4);
        listRequest.add(r1);
        listRequest.add(r2);

    }

    /**
     * Method to test:
     * getAllUnreachableIntersections()
     *
     * What it does:
     * Check connex graph with the depot and if all request can be reachable
     */
    @Test
    @DisplayName("Test on getAllUnreachableIntersections - 3 cases")
    void getAllUnreachableIntersections() {
        // Create unreachable/one way intersections (non connex graph)
        Intersection i6 = new Intersection(6,30.75159,2.8700043);
        Intersection i7 = new Intersection(7,34.75159,1.8700043);
        Intersection i8 = new Intersection(8,33.75159,1.2700043);

        // i6 one way (return)
        Segment s13 = new Segment(54,"Rue des bonbons", i0,i6);
        // i7 one way
        Segment s14 = new Segment(123,"Rue de la joie",i7, i0);

        // Add requests with new intersections
        Request r3 = new Request(180,220,listIntersection.get(2),i7);
        Request r4 = new Request(120,210,i6,listIntersection.get(2));
        Request r5 = new Request(120,210,i8,listIntersection.get(2));

        i6.addAdjacentSegment(s13);
        i0.addAdjacentSegment(s14);

        listIntersection.add(i6);
        listIntersection.add(i7);
        listIntersection.add(i8);
        listRequest.add(r3);
        listRequest.add(r4);
        listRequest.add(r5);
        ArrayList<Intersection> intersectionsToTest = new ArrayList<>();
        for(Request req : listRequest ) {
            intersectionsToTest.add(req.getPickupAddress());
            intersectionsToTest.add(req.getDeliveryAddress());
        }

        ArrayList<Intersection> listUnreachable = StronglyConnectedComponents.getAllUnreachableIntersections(listIntersection, i0,intersectionsToTest);
        assertTrue(listUnreachable.contains(i6),"One way intersection (return) need to be is the unreachable list");
        assertTrue(listUnreachable.contains(i7),"One way intersection (first way) need to be is the unreachable list");
        assertTrue(listUnreachable.contains(i8),"Isolated point need to be is the unreachable list");
    }

    @Test
    void foretDFSnum() {
        // Create unreachable/one way intersections (non connex graph)
        Intersection i6 = new Intersection(6,30.75159,2.8700043);
        Intersection i7 = new Intersection(7,34.75159,1.8700043);
        Intersection i8 = new Intersection(8,33.75159,1.2700043);
        Intersection i9 = new Intersection(9,32.75159,1.2700043);

        // i6 one way (return)
        Segment s13 = new Segment(54,"Rue des bonbons", i0,i6);
        // i7 one way
        Segment s14 = new Segment(123,"Rue de la joie",i7, i0);

        i6.addAdjacentSegment(s13);
        i0.addAdjacentSegment(s14);

        listIntersection.add(i6);
        listIntersection.add(i7);
        listIntersection.add(i8);
        listIntersection.add(i9);

        ArrayList[] listVertex = new ArrayList[listIntersection.size()];
        Integer [] colorDFSnum = new Integer[listIntersection.size()];
        Integer[] numForet = StronglyConnectedComponents.foretDFSnum(listIntersection, listVertex, colorDFSnum);

        //System.out.println(Arrays.toString(numForet));
        assertEquals(3,numForet[0],"Wrong forest num order");
        assertEquals(1,numForet[1],"Wrong forest num order");
        assertEquals(2,numForet[2],"Wrong forest num order");
        assertEquals(4,numForet[3],"Wrong forest num order");
        assertEquals(5,numForet[4],"Wrong forest num order");
        assertEquals(7,numForet[5],"Wrong forest num order");
        assertEquals(0,numForet[6],"Wrong forest num order");
        assertEquals(6,numForet[7],"Wrong forest num order");
        assertEquals(8,numForet[8],"Wrong forest num order");
        assertEquals(9,numForet[9],"Wrong forest num order");
    }

    @Test
    void getTranspose() {
        // Create unreachable/one way intersections (non connex graph)
        Intersection i6 = new Intersection(6,30.75159,2.8700043);
        Intersection i7 = new Intersection(7,34.75159,1.8700043);
        Intersection i8 = new Intersection(8,33.75159,1.2700043);

        // i6 one way (return)
        Segment s13 = new Segment(54,"Rue des bonbons", i0,i6);
        // i7 one way
        Segment s14 = new Segment(123,"Rue de la joie",i7, i0);

        i6.addAdjacentSegment(s13);
        i0.addAdjacentSegment(s14);

        listIntersection.add(i6);
        listIntersection.add(i7);
        listIntersection.add(i8);

        List<Integer>[] transposedGraph = StronglyConnectedComponents.getTranspose(listIntersection);

        /*for (int i = 0; i < transposedGraph.length; i++){
            System.out.println(i+" : "+transposedGraph[i].toString());
        }*/
        assertTrue(transposedGraph[0].contains(5),"Two way intersection was not transposed correctly");
        assertTrue(transposedGraph[0].contains(6),"One way intersection was not transposed correctly");
        assertTrue(transposedGraph[8].isEmpty(),"Isolated intersection was not transposed correctly");
        assertTrue(transposedGraph[7].contains(0),"One way intersection was not transposed correctly 2");
        assertTrue(transposedGraph[6].isEmpty(),"One way intersection was not transposed correctly 3");
    }

    @Test
    void DFSrec() {
        // Create unreachable/one way intersections (non connex graph)
        Intersection i6 = new Intersection(6,30.75159,2.8700043);
        Intersection i7 = new Intersection(7,34.75159,1.8700043);
        Intersection i8 = new Intersection(8,33.75159,1.2700043);

        // i6 one way (return)
        Segment s13 = new Segment(54,"Rue des bonbons", i0,i6);
        // i7 one way
        Segment s14 = new Segment(123,"Rue de la joie",i7, i0);

        i6.addAdjacentSegment(s13);
        i0.addAdjacentSegment(s14);

        listIntersection.add(i6);
        listIntersection.add(i7);
        listIntersection.add(i8);

        List<Integer>[] transposedGraph = new List[listIntersection.size()];
        for (int i = 0; i < listIntersection.size(); i++) {
            transposedGraph[i] = new ArrayList<>();
        }
        transposedGraph[0].add(5);
        transposedGraph[0].add(6);
        transposedGraph[1].add(3);
        transposedGraph[1].add(5);
        transposedGraph[2].add(4);
        transposedGraph[3].add(1);
        transposedGraph[3].add(5);
        transposedGraph[4].add(2);
        transposedGraph[4].add(5);
        transposedGraph[5].add(0);
        transposedGraph[5].add(1);
        transposedGraph[5].add(3);
        transposedGraph[5].add(4);
        transposedGraph[7].add(0);

        Integer [] color = new Integer[listIntersection.size()];
        Arrays.fill(color, 0);
        Map<Integer, Integer> colorMap = new HashMap<>();
        for(int k=0; k<color.length; k++) {
                colorMap.put(k,0);
        }
        StronglyConnectedComponents.DFSrec(transposedGraph, 0, color, colorMap);

        assertEquals(2, colorMap.get(0), "Origin not black");
        assertEquals(2, colorMap.get(5), "5 not black");
        assertEquals(2, colorMap.get(4), "4 not black");
        assertEquals(2, colorMap.get(2), "2 not black");
        assertEquals(2, colorMap.get(1), "1 not black");
        assertEquals(2, colorMap.get(3), "3 not black");
        assertEquals(2, colorMap.get(6), "6 not black");
        assertEquals(0, colorMap.get(7), "7 black");
        assertEquals(0, colorMap.get(8), "8 black");
    }
}