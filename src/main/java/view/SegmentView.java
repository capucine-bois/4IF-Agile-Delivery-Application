package view;

import java.awt.*;

/**
 * Segment on GraphicalView.
 */
public class SegmentView {

    /* ATTRIBUTES */

    private IntersectionView origin;
    private IntersectionView destination;

    /* CONSTRUCTORS */

    /**
     * Complete constructor.
     * @param origin origin intersection on GraphicalView
     * @param destination destination intersection on GraphicalView
     */
    public SegmentView(IntersectionView origin, IntersectionView destination) {
        this.origin = origin;
        this.destination = destination;
    }

    /* GETTERS */

    /**
     * Getter for origin
     * @return origin
     */
    public IntersectionView getOrigin() {
        return origin;
    }

    /**
     * Getter for destination
     * @return destination
     */
    public IntersectionView getDestination() {
        return destination;
    }

    /* SETTERS */

    /**
     * Setter for origin
     * @param origin new origin wanted
     */
    public void setOrigin(IntersectionView origin) {
        this.origin = origin;
    }

    /**
     * Getter for destination
     * @param destination new destination wanted
     */
    public void setDestination(IntersectionView destination) {
        this.destination = destination;
    }

    /* METHODS */

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
