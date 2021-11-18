package view;

import java.awt.*;

public class SegmentView {
    private IntersectionView origin;
    private IntersectionView destination;

    public SegmentView(IntersectionView origin, IntersectionView destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public void paintComponent(Graphics g) {
        g.drawLine(origin.getCoordinateX(), origin.getCoordinateY(), destination.getCoordinateX(), destination.getCoordinateY());
    }
}
