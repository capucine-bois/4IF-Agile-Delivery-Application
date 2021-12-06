package model;

/**
 * This class groups an intersection and its distance to the origin, its color and its parent (which is a dijkstra object, so we also get the parent's distance and color and its parent itself).
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


}