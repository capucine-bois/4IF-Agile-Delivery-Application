package xml;

import model.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class DijkstraTest {
    private Tour tour = new Tour();
    private ArrayList<Intersection> listIntersection;
    private ArrayList<Request> listRequest;
    public DijkstraTest(){
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
    public void resetCityMap(){
        initializeMap();
    }

    private void initializeMap(){
        this.listIntersection =  new ArrayList<>();
        this.tour = new Tour();
        listIntersection.add(new Intersection(1,45.75406,4.857418));
        listIntersection.add(new Intersection(2,45.750404,4.8744674));
        listIntersection.add(new Intersection(3,45.75871,4.8704023));
        listIntersection.add(new Intersection(4,45.75171,4.871819));
        listIntersection.add(new Intersection(5,45.750896,4.859119));
        listIntersection.add(new Intersection(6,45.75159,4.8700043));

        Segment s1 = new Segment(75, "Rue du Dauphiné", listIntersection.get(0), listIntersection.get(5));
        Segment s2 = new Segment(75, "Rue du Dauphiné", listIntersection.get(5), listIntersection.get(0));
        Segment s3 = new Segment(77, "Rue du Dauphiné", listIntersection.get(5), listIntersection.get(1));
        Segment s4 = new Segment(77, "Rue du Dauphiné", listIntersection.get(1), listIntersection.get(5));
        Segment s5 = new Segment(68, "Rue de Domrémy", listIntersection.get(3), listIntersection.get(5));
        Segment s6 = new Segment(68, "Rue de Domrémy", listIntersection.get(5), listIntersection.get(3));
        Segment s7 = new Segment(68, "Rue de Domrémy", listIntersection.get(4), listIntersection.get(5));
        Segment s8 = new Segment(68, "Rue de Domrémy", listIntersection.get(5), listIntersection.get(4));
        Segment s9 = new Segment(16, "Cours Albert Thomas", listIntersection.get(1),listIntersection.get(3));
        Segment s10 = new Segment(16, "Cours Albert Thomas", listIntersection.get(3),listIntersection.get(1));
        Segment s11 = new Segment(118, "Boulevard Vivier-Merle", listIntersection.get(4),listIntersection.get(2));
        Segment s12 = new Segment(118, "Boulevard Vivier-Merle", listIntersection.get(2),listIntersection.get(4));

        //TODO add the adjacent segments for each intersection
        listIntersection.get(0).addAdjacentSegment(s1);
        listIntersection.get(2).addAdjacentSegment(s2);
        listIntersection.get(4).addAdjacentSegment(s3);
        listIntersection.get(6).addAdjacentSegment(s4);

        //TODO add the request
        this.listRequest =  new ArrayList<>();
        this.listRequest.add(new Request(0,600,listIntersection.get(0),listIntersection.get(1)));
        this.listRequest.add(new Request(600,120,listIntersection.get(3),listIntersection.get(6)));
    }
}
