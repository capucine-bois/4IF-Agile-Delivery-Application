package view;

/**
 * An intersection view is a representation of an intersection with coordinates X and Y
 */
public class IntersectionView {
    private int coordinateX;
    private int coordinateY;

    /**
     * Constructor
     * @param coordinateX coordinate X of the intersection
     * @param coordinateY coordinate Y of the intersection
     */
    public IntersectionView(int coordinateX, int coordinateY) {
        this.coordinateX = coordinateX;
        this.coordinateY = coordinateY;
    }

    /**
     * Getter for coordinate X
     * @return coordinate X
     */
    public int getCoordinateX() {
        return coordinateX;
    }

    /**
     * Setter for coordinate X
     * @param coordinateX new coordinate X wanted
     */
    public void setCoordinateX(int coordinateX) {
        this.coordinateX = coordinateX;
    }

    /**
     * Getter for coordinate Y
     * @return coordinate Y
     */
    public int getCoordinateY() {
        return coordinateY;
    }

    /**
     * Setter for coordinate Y
     * @param coordinateY new coordinate Y wanted
     */
    public void setCoordinateY(int coordinateY) {
        this.coordinateY = coordinateY;
    }
}
