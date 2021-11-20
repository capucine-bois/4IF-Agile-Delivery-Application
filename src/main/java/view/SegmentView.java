package view;

import java.awt.*;

/**
 * A segment view is a segment which has two intersection views (origin and destination) that are represented with coordinates X and Y
 */
public class SegmentView {
    private IntersectionView origin;
    private IntersectionView destination;

    /**
     * Constructor
     * @param origin origin intersection view
     * @param destination destination intersection view
     */
    public SegmentView(IntersectionView origin, IntersectionView destination) {
        this.origin = origin;
        this.destination = destination;
    }

    /**
     * Getter for origin
     * @return origin
     */
    public IntersectionView getOrigin() {
        return origin;
    }

    /**
     * Setter for origin
     * @param origin new origin wanted
     */
    public void setOrigin(IntersectionView origin) {
        this.origin = origin;
    }

    /**
     * Getter for destination
     * @return destination
     */
    public IntersectionView getDestination() {
        return destination;
    }

    /**
     * Getter for destination
     * @param destination new destination wanted
     */
    public void setDestination(IntersectionView destination) {
        this.destination = destination;
    }

    /**
     * Paints a segment view with two strokes : one for background and one for border
     * @param g graphics of the graphical view
     * @param scale zoom scale of the city map
     */
    public void paintSegment(Graphics g, int scale) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Constants.COLOR_6);
        g2.setStroke(new BasicStroke(scale + 2));
        g2.drawLine(origin.getCoordinateX(), origin.getCoordinateY(), destination.getCoordinateX(), destination.getCoordinateY());
        g2.setColor(Constants.COLOR_7);
        g2.setStroke(new BasicStroke(scale));
        g2.drawLine(origin.getCoordinateX(), origin.getCoordinateY(), destination.getCoordinateX(), destination.getCoordinateY());

    }
}
