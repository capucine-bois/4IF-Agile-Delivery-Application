package model;

public class Segment {
    private double length;
    private String name;
    private long destination;
    private long origin;

    public Segment(double length, String name, long destination, long origin) {
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

    public long getDestination() {
        return destination;
    }

    public void setDestination(long destination) {
        this.destination = destination;
    }

    public long getOrigin() {
        return origin;
    }

    public void setOrigin(long origin) {
        this.origin = origin;
    }
}
