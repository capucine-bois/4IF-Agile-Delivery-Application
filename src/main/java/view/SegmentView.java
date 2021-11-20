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
}
