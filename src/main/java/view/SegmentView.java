package view;

import java.awt.*;

public class SegmentView {
    private IntersectionView origin;
    private IntersectionView destination;

    public SegmentView(IntersectionView origin, IntersectionView destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public IntersectionView getOrigin() {
        return origin;
    }

    public void setOrigin(IntersectionView origin) {
        this.origin = origin;
    }

    public IntersectionView getDestination() {
        return destination;
    }

    public void setDestination(IntersectionView destination) {
        this.destination = destination;
    }
    
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
