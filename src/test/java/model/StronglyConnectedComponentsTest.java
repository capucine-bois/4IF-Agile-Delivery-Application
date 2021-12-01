package model;

import org.junit.jupiter.api.*;

import java.awt.*;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class StronglyConnectedComponentsTest {

    static Instant startedAt;
    ArrayList<Intersection> listIntersection;
    ArrayList<Request> listRequest;
    Intersection i1;

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
        i1 = new Intersection(0,45.75406,4.857418);
        Intersection i2 = new Intersection(1,45.750404,4.8744674);
        Intersection i3 = new Intersection(2,45.75871,4.8704023);
        Intersection i4 = new Intersection(3,45.75171,4.871819);
        Intersection i5 = new Intersection(4,45.750896,4.859119);
        Intersection i6 = new Intersection(5,45.75159,4.8700043);
        listIntersection.add(i1);
        listIntersection.add(i2);
        listIntersection.add(i3);
        listIntersection.add(i4);
        listIntersection.add(i5);
        listIntersection.add(i6);

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
        Request r1 = new Request(180,240,i2,i3, Color.BLACK);
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
        // Create unreachable intersections (none connex graph)
        Intersection i7 = new Intersection(6,30.75159,2.8700043);
        Intersection i8 = new Intersection(7,34.75159,1.8700043);
        Intersection i9 = new Intersection(8,33.75159,1.2700043);
        // i7 one way (return)
        // i8 one way
        Segment s13 = new Segment(54,"Rue des bonbons",i1,i7);
        Segment s14 = new Segment(123,"Rue de la joie",i8,i1);
        Request r3 = new Request(180,220,listIntersection.get(2),i8, Color.BLACK);
        Request r4 = new Request(120,210,i7,listIntersection.get(2), Color.BLACK);
        Request r5 = new Request(120,210,i9,listIntersection.get(2), Color.BLACK);

        i7.addAdjacentSegment(s13);
        i1.addAdjacentSegment(s14);

        listIntersection.add(i7);
        listIntersection.add(i8);
        listIntersection.add(i9);
        listRequest.add(r3);
        listRequest.add(r4);
        listRequest.add(r5);
        ArrayList<Intersection> listUnreachable = StronglyConnectedComponents.getAllUnreachableIntersections(listIntersection,i1,listRequest);
        assertTrue(listUnreachable.contains(i7),"One way intersection (return) need to be is the unreachable list");
        assertTrue(listUnreachable.contains(i8),"One way intersection (first way) need to be is the unreachable list");
        assertTrue(listUnreachable.contains(i9),"Isolated point need to be is the unreachable list");
    }

    @Test
    void foretDFSnum() {
    }

    @Test
    void DFSrecNUM() {
    }

    @Test
    void getTranspose() {
    }

    @Test
    void DFSrec() {
    }
}