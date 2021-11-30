package model;

/**
 * This class groups an intersection and its distance to the origin, its color and its parent (which is a dijkstra object so we also get the parent's distance and color and its parent itself).
 * It is used for the algorithm dijkstra
 */
class ObjectDijkstra {
    /* ATTRIBUTES */
    /**
     * The intersection
     */
    private Intersection intersection;

    /**
     * The parent of the intersection
     */
    private ObjectDijkstra parent;

    /**
     * The distance between the origin and the intersection
     */
    private Double dist;

    /**
     * The color of the intersection during the computing of dijkstra
     */
    private Integer color;

    public Intersection getIntersection() {
        return intersection;
    }

    public void setIntersection(Intersection intersection) {
        this.intersection = intersection;
    }

    public ObjectDijkstra getParent() {
        return parent;
    }

    public void setParent(ObjectDijkstra parent) {
        this.parent = parent;
    }

    public Double getDist() {
        return dist;
    }

    public void setDist(Double dist) {
        this.dist = dist;
    }

    public Integer getColor() {
        return color;
    }

    public void setColor(Integer color) {
        this.color = color;
    }


    /**
     * Constructor initialize all parameter of the dijkstra object
     * @param intersection
     * @param parent
     * @param dist
     * @param color
     */
    public ObjectDijkstra(Intersection intersection, ObjectDijkstra parent, Double dist, Integer color) {
        this.intersection = intersection;
        this.parent = parent;
        this.dist = dist;
        this.color = color;
    }


}