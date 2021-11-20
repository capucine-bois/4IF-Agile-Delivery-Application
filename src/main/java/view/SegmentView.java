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

    /* METHODS */

    /**
     * Draw segment on the GUI.
     * @param g GUI element where segment will be drawn.
     */
    public void paintSegment(Graphics g) {
        g.drawLine(origin.getCoordinateX(), origin.getCoordinateY(), destination.getCoordinateX(), destination.getCoordinateY());
    }
}
