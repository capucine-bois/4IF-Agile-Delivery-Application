package model;

public class Segment {
    private double length;
    private String name;
    private Intersection destination;
    private Intersection origin;

    public Segment(double length, String name, Intersection destination, Intersection origin) {
        this.length = length;
        this.name = name;
        this.destination = destination;
        this.origin = origin;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Intersection getDestination() {
        return destination;
    }

    public void setDestination(Intersection destination) {
        this.destination = destination;
    }

    public Intersection getOrigin() {
        return origin;
    }

    public void setOrigin(Intersection origin) {
        this.origin = origin;
    }
}
