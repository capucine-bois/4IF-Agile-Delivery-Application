package view;

/**
 * Intersection on GraphicalView.
 */
public class IntersectionView {

    /* ATTRIBUTES */

    private int coordinateX;
    private int coordinateY;

    /* CONSTRUCTORS */

    /**
     * Complete constructor.
     * @param coordinateX x position on GraphicalView
     * @param coordinateY y position on GraphicalView
     */
    public IntersectionView(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    /* GETTERS */

    /**
     * Getter for coordinateX attribute.
     * @return x coordinate
     */
    public int getCoordinateX() {
        return coordinateX;
    }

    /**
     * Getter for coordinateY attribute.
     * @return y coordinate
     */
    public int getCoordinateY() {
        return coordinateY;
    }

    /* SETTERS */

    /**
     * Setter for coordinateX attribute.
     * @param coordinateX wanted value for coordinateX attribute
     */
    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    /**
     * Setter for coordinateY attribute.
     * @param coordinateY wanted value for coordinateY attribute
     */
    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }
}
