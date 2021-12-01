package model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DijkstraNode {

    /* ATTRIBUTES */
    /**
     * The intersection
     */
    private Intersection intersection;

    private Long number;

    private List<DijkstraNode> shortestPath = new LinkedList<>();

    /**
     * The distance between the origin and the intersection
     */
    private Double distance = Double.MAX_VALUE;

    public Intersection getIntersection() {
        return intersection;
    }

    public Long getNumber() {
        return number;
    }

    public Double getDistance() {
        return distance;
    }

    public Map<DijkstraNode, Double> getAdjacentNodes() {
        return adjacentNodes;
    }

    public List<DijkstraNode> getShortestPath() {
        return shortestPath;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setNumber(Long number) {
        this.number = number;
    }


    public void setShortestPath(List<DijkstraNode> shortestPath) {
        this.shortestPath = shortestPath;
    }

    Map<DijkstraNode, Double> adjacentNodes = new HashMap<>();

    public void addDestination(DijkstraNode destination, double distance) {
        adjacentNodes.put(destination, distance);
    }

    /**
     * Constructor initialize all parameter of the dijkstra object
     * @param intersection
     * @param dist
     */
    public DijkstraNode(Intersection intersection, double dist) {
        this.distance = dist;
        this.intersection = intersection;
        this.number = intersection.getId();
        List<Segment> listSegment =  intersection.getAdjacentSegments();
        for( Segment seg : listSegment) {
            DijkstraNode neighbour = new DijkstraNode(seg.getDestination());
            adjacentNodes.put(neighbour, seg.getLength());
        }
    }

    /**
     * Constructor initialize node with intersection
     * @param intersection
     */
    public DijkstraNode(Intersection intersection) {
        this.intersection = intersection;
        this.number = intersection.getId();
        List<Segment> listSegment =  intersection.getAdjacentSegments();
        for( Segment seg : listSegment) {
            DijkstraNode neighbour = new DijkstraNode(seg.getDestination());
            adjacentNodes.put(neighbour, seg.getLength());
        }
    }

    // getters and setters
}