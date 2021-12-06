package view;

import model.*;
import observer.Observable;
import observer.Observer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

/**
 * Graphical element on the GUI.
 * Used to display map and tour (with their segments).
 */
public class GraphicalView extends JPanel implements Observer {

    private Graphics g;
    private final int firstBorder = 10;
    private final int secondBorder = 2;
    private final int fakeBorder = 10;
    private final int allBorders = firstBorder + secondBorder + fakeBorder;
    private double scale = 1;
    private int originX = allBorders;
    private int originY = allBorders;
    private final CityMap cityMap;
    private final Tour tour;
    private boolean canZoom = true;
    private int previousMouseX;
    private int previousMouseY;
    private Font roadFont;
    private Font roadShortestPathFont;
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;
    private double latitudeLength;
    private double longitudeLength;
    private double width;
    private double height;
    private double proportion;
    private boolean selectionMode;
    private int radiusForSelection;

    /**
     * Create the graphical view
     * @param cityMap the city map
     * @param tour the tour
     */
    public GraphicalView(CityMap cityMap, Tour tour, MouseListener mouseListener) {
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Constants.COLOR_1, firstBorder),BorderFactory.createLineBorder(Constants.COLOR_4, secondBorder)));
        cityMap.addObserver(this);
        tour.addObserver(this);
        this.cityMap = cityMap;
        this.tour = tour;
        mouseListener.setGraphicalView(this);
        // listeners
        addMouseWheelListener(mouseListener);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        try {
            roadFont = Constants.getFont("DMSans-Medium.ttf", 12);
            roadShortestPathFont = Constants.getFont("DMSans-Bold.ttf", 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
        selectionMode = false;
    }

    /**
     * Creates intersections and segments of a given map with coordinates X and Y
     */
    public void displayCityMap(List<Intersection> intersections) {
        if (!intersections.isEmpty()) {
            Intersection firstIntersection = intersections.get(0);
            minLatitude = firstIntersection.getLatitude();
            maxLatitude = firstIntersection.getLatitude();
            minLongitude = firstIntersection.getLongitude();
            maxLongitude = firstIntersection.getLongitude();

            for (Intersection intersection : intersections) {
                double latitude = intersection.getLatitude();
                double longitude = intersection.getLongitude();
                if (latitude < minLatitude) {
                    minLatitude = latitude;
                } else if (latitude > maxLatitude) {
                    maxLatitude = latitude;
                }
                if (longitude < minLongitude) {
                    minLongitude = longitude;
                } else if (longitude > maxLongitude) {
                    maxLongitude = longitude;
                }
            }

            displayIntersectionsAndSegments(intersections);
        }
    }

    /**
     * Compute a distance between two points
     * @param latitude1 latitude of first point
     * @param latitude2 latitude of second point
     * @param longitude1 longitude of first point
     * @param longitude2 longitude of second point
     * @return the distance in meters
     */
    private double computeDistanceFromCoordinates(double latitude1, double latitude2, double longitude1, double longitude2) {
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);
        longitude1 = Math.toRadians(longitude1);
        longitude2 = Math.toRadians(longitude2);

        // Haversine formula
        double distLongitude = longitude2 - longitude1;
        double distLatitude = latitude2 - latitude1;
        double a = Math.pow(Math.sin(distLatitude / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(distLongitude / 2),2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371; // Radius of earth in kilometers.
        return c * r;
    }

    /**
     * Initialize the list of intersections on the GUI.
     * Parse the list of intersections to instantiate all the intersections for the GUI (IntersectionView) with coordinates X and Y.
     * @param intersections map containing an intersection as key and a list of segments where they are part of as value
     */
    private void displayIntersectionsAndSegments(List<Intersection> intersections) {
        latitudeLength = maxLatitude - minLatitude;
        longitudeLength = maxLongitude - minLongitude;

        double widthDistance = computeDistanceFromCoordinates(minLatitude, minLatitude, minLongitude, maxLongitude);
        double heightDistance = computeDistanceFromCoordinates(minLatitude, maxLatitude, minLongitude, minLongitude);
        proportion = heightDistance/widthDistance;

        double viewWidth = g.getClipBounds().width - allBorders * 2;
        double viewHeight = g.getClipBounds().height - allBorders * 2;
        if (viewWidth >= viewHeight) {
            width = viewWidth * scale;
            height = width * proportion;
            while (scale * proportion < 1) {
                zoom((int) (viewWidth/2), (int) (viewHeight/2), -1);
                width = viewWidth * scale;
                height = width * proportion;
            }
        } else {
            height = viewHeight * scale;
            width = height / proportion;
            while (scale / proportion < 1) {
                zoom((int) (viewWidth/2), (int) (viewHeight/2), -1);
                height = viewHeight * scale;
                width = height / proportion;
            }
        }

        if (originX > allBorders) originX = allBorders;
        if (originY > allBorders) originY = allBorders;
        if (originX + width - allBorders < viewWidth) originX = (int) (viewWidth - width + allBorders);
        if (originY + height - allBorders < viewHeight) originY = (int) (viewHeight - height + allBorders);

        displaySegmentsForEachOrigin(intersections, Constants.COLOR_6, (float) (scale + 2), false);
        displaySegmentsForEachOrigin(intersections, Constants.COLOR_7, (float) scale, true);

        if (!selectionMode) {
            boolean oneShortestPathSelected = tour.getListShortestPaths().stream().anyMatch(ShortestPath::isSelected);
            for (ShortestPath shortestPath : tour.getListShortestPaths()) {
                displayShortestPaths(shortestPath, oneShortestPathSelected && !shortestPath.isSelected(), Constants.COLOR_11, (float) (scale + 4), false);
            }
            for (ShortestPath shortestPath : tour.getListShortestPaths()) {
                displayShortestPaths(shortestPath, oneShortestPathSelected && !shortestPath.isSelected(), Constants.COLOR_12, (float) scale, true);
            }
            for (ShortestPath shortestPath : tour.getListShortestPaths()) {
                displayShortestPaths(shortestPath, !oneShortestPathSelected || shortestPath.isSelected(), Constants.COLOR_8, (float) (scale + 4), false);
            }
            for (ShortestPath shortestPath : tour.getListShortestPaths()) {
                displayShortestPaths(shortestPath, !oneShortestPathSelected || shortestPath.isSelected(), Constants.COLOR_9, (float) (scale), true);
            }

            if (!tour.getPlanningRequests().isEmpty()) {
                boolean oneRequestPointSelected = tour.getPlanningRequests().stream().anyMatch(req -> req.isDeliverySelected() || req.isPickupSelected());
                for (Request request : tour.getPlanningRequests()) {

                    Intersection pickupAddress = request.getPickupAddress();
                    int pickupCoordinateX = getCoordinateX(pickupAddress);
                    int pickupCoordinateY = getCoordinateY(pickupAddress);
                    Intersection deliveryAddress = request.getDeliveryAddress();
                    int deliveryCoordinateX = getCoordinateX(deliveryAddress);
                    int deliveryCoordinateY = getCoordinateY(deliveryAddress);
                    if (!oneRequestPointSelected || request.isPickupSelected()) {
                        drawIcon(request.getColor(), pickupCoordinateX, pickupCoordinateY, "pickup-icon");
                    } else {
                        drawIcon(Constants.COLOR_4, pickupCoordinateX, pickupCoordinateY, "pickup-icon");
                    }
                    if (!oneRequestPointSelected || request.isDeliverySelected()) {
                        drawIcon(request.getColor(), deliveryCoordinateX, deliveryCoordinateY, "delivery-icon");
                    } else {
                        drawIcon(Constants.COLOR_4, deliveryCoordinateX, deliveryCoordinateY, "delivery-icon");
                    }
                }

                if (tour.getNewRequest() != null) {
                    if (tour.getNewRequest().getPickupAddress() != null) {
                        Intersection pickupAddress = tour.getNewRequest().getPickupAddress();
                        int pickupCoordinateX = getCoordinateX(pickupAddress);
                        int pickupCoordinateY = getCoordinateY(pickupAddress);
                        drawIcon(tour.getNewRequest().getColor(), pickupCoordinateX, pickupCoordinateY, "pickup-icon");
                    }
                    if (tour.getNewRequest().getDeliveryAddress() != null) {
                        Intersection deliveryAddress = tour.getNewRequest().getDeliveryAddress();
                        int deliveryCoordinateX = getCoordinateX(deliveryAddress);
                        int deliveryCoordinateY = getCoordinateY(deliveryAddress);
                        drawIcon(tour.getNewRequest().getColor(), deliveryCoordinateX, deliveryCoordinateY, "delivery-icon");
                    }
                }

                int depotCoordinateX = getCoordinateX(tour.getDepotAddress());
                int depotCoordinateY = getCoordinateY(tour.getDepotAddress());
                drawIcon(null, depotCoordinateX, depotCoordinateY, "depot-icon");
            }
        } else {
            displayAllIntersections(intersections);
        }
    }

    private void displayAllIntersections(List<Intersection> intersections) {
        for (Intersection origin : intersections) {
            int originCoordinateX = getCoordinateX(origin);
            int originCoordinateY = getCoordinateY(origin);
            g.setColor(Color.red);
            radiusForSelection = (int) ((scale + 2)/2);
            g.fillOval(originCoordinateX -  radiusForSelection, originCoordinateY - radiusForSelection, radiusForSelection * 2, radiusForSelection * 2);
        }
    }

    /**
     * Display segments that match a specific origin
     * @param intersections intersections to consider
     * @param color color of segments
     * @param strokeSize size of segments
     * @param displayRoadNames whether road names must be displayed or not
     */
    private void displaySegmentsForEachOrigin(List<Intersection> intersections, Color color, float strokeSize, boolean displayRoadNames) {
        for (Intersection origin : intersections) {
            int originCoordinateX = getCoordinateX(origin);
            int originCoordinateY = getCoordinateY(origin);
            for (Segment segment : origin.getAdjacentSegments()) {
                int destinationCoordinateX = getCoordinateX(segment.getDestination());
                int destinationCoordinateY = getCoordinateY(segment.getDestination());
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(color);
                g2.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
                if (displayRoadNames) {
                    displayRoadName(g2, segment.getName(), originCoordinateX, destinationCoordinateX, originCoordinateY, destinationCoordinateY, false);
                }
            }
        }
    }

    /**
     * Display a shortest path between two intersections (two icons)
     * @param shortestPath the path to display
     * @param conditionToDisplay boolean to control display
     * @param color color of segments displayed
     * @param strokeSize size of segments
     * @param displayRoadNames boolean to control road name display
     */
    @SuppressWarnings("GrazieInspection")
    private void displayShortestPaths(ShortestPath shortestPath, boolean conditionToDisplay, Color color, float strokeSize, boolean displayRoadNames) {
        if (conditionToDisplay) {
            for (Segment segment : shortestPath.getListSegments()) {
                int originCoordinateX = getCoordinateX(segment.getOrigin());
                int originCoordinateY = getCoordinateY(segment.getOrigin());
                int destinationCoordinateX = getCoordinateX(segment.getDestination());
                int destinationCoordinateY = getCoordinateY(segment.getDestination());
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(color);
                g2.setStroke(new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
                displayArrow(g2, originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
                if (displayRoadNames) {
                    displayRoadName(g2, segment.getName(), originCoordinateX, destinationCoordinateX, originCoordinateY, destinationCoordinateY, true);
                }
            }
        }
    }

    /**
     * Display arrows on shortest paths for computed tour.
     * @param g2 graphical element
     * @param originX x position of origin
     * @param originY y position of origin
     * @param destinationX x position of destination
     * @param destinationY y position of destination
     */
    private void displayArrow(Graphics2D g2, int originX, int originY, int destinationX, int destinationY) {
        double oppositeSide = destinationY - originY;
        double adjacentSide = destinationX - originX;
        double hypotenuseSize = Math.sqrt(oppositeSide * oppositeSide + adjacentSide * adjacentSide);
        if (hypotenuseSize > 80) {
            double angle = Math.atan(oppositeSide / adjacentSide);
            int middleX;
            int middleY;
            if (originX > destinationX) {
                middleX = (int) (originX - Math.abs(adjacentSide) / 2);
                angle -= Math.PI;
            } else {
                middleX = (int) (originX + Math.abs(adjacentSide) / 2);
            }
            if (originY > destinationY) {
                middleY = (int) (originY - Math.abs(oppositeSide) / 2);
            } else {
                middleY = (int) (originY + Math.abs(oppositeSide) / 2);
            }
            g2.translate(middleX, middleY);
            BasicStroke stroke = (BasicStroke) g2.getStroke();
            g2.setStroke(new BasicStroke(stroke.getLineWidth()/2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.rotate(angle + 3 * Math.PI / 4);
            g2.drawLine(0, 0, (int) (2 * scale), 0);
            g2.drawLine(0, 0, 0, (int) (2 * scale));
            g2.rotate(-(angle + 3 * Math.PI / 4));
            g2.translate(-middleX, -middleY);
        }
    }

    /**
     * Display road name at a specific position on the graphical view.
     * @param g2 graphical element
     * @param name name of the road to display
     * @param originX x position of origin
     * @param destinationX x position of destination
     * @param originY y position of origin
     * @param destinationY y position of destination
     * @param shortestPath whether the segment is included in a shortest path (of the computed tour) or not
     */
    @SuppressWarnings("GrazieInspection")
    private void displayRoadName(Graphics2D g2, String name, int originX, int destinationX, int originY, int destinationY, boolean shortestPath) {
        double oppositeSide = destinationY - originY;
        double adjacentSide = destinationX - originX;
        double angle = Math.atan(oppositeSide/adjacentSide);
        int textCoordinateX;
        int textCoordinateY;
        textCoordinateX = Math.min(originX, destinationX);
        if (textCoordinateX == originX) textCoordinateY = originY;
        else textCoordinateY = destinationY;
        double hypotenuseSize = Math.sqrt(oppositeSide * oppositeSide + adjacentSide * adjacentSide);
        if (scale > 5 && name.length() * 10 < hypotenuseSize
                && !(((originX < 0 && destinationX < 0) || (originX > g.getClipBounds().width && destinationX > g.getClipBounds().width))
                || ((originY < 0 && destinationY < 0) || (originY > g.getClipBounds().height && destinationY > g.getClipBounds().height)))) {
            if (shortestPath) {
                g2.setColor(Constants.COLOR_7);
                g2.setFont(roadShortestPathFont);
            } else {
                g2.setColor(Constants.COLOR_10);
                g2.setFont(roadFont);
            }
            g2.translate(textCoordinateX, textCoordinateY);
            g2.rotate(angle);
            g2.drawString(name, (float) (hypotenuseSize / 2 - name.length() * 3), 4);
            g2.rotate(-angle);
            g2.translate(-textCoordinateX, -textCoordinateY);
        }
    }

    /**
     * Get x position of an intersection on the graphical view.
     * @param intersection intersection considered
     * @return x position on graphical view
     */
    private int getCoordinateX(Intersection intersection) {
        double coordinateLongitude = intersection.getLongitude() - minLongitude;
        return (int) ((coordinateLongitude * width) / longitudeLength) + originX;
    }

    /**
     * Get y position of an intersection on the graphical view.
     * @param intersection intersection considered
     * @return y position on graphical view
     */
    private int getCoordinateY(Intersection intersection) {
        double coordinateLatitude = intersection.getLatitude() - minLatitude;
        return (int) (height) - (int) ((coordinateLatitude * height) / latitudeLength) + originY;
    }

    /**
     * Draw the component on GUI.
     * @param g parent component where the graphical view must be drawn.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;
        displayCityMap(cityMap.getIntersections());
    }

    /**
     * Draw the icon for a depot, pickup or delivery address
     * @param color color of the icon
     * @param iconName name of the icon file
     */
    private void drawIcon(Color color, int coordinateX, int coordinateY, String iconName) {
        try {
            BufferedImage image = Constants.getImage(iconName);
            if (color != null) fillColorInImage(image, color);
            g.drawImage(image, coordinateX - 20, coordinateY - 40, 40, 40, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fill color in an icon
     * @param image image of the icon
     * @param finalColor wanted color for the icon
     */
    private void fillColorInImage(BufferedImage image, Color finalColor) {
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                Color color = new Color(image.getRGB(i, j), true);
                if (color.getAlpha() != 0) {
                    double scale = ((double)color.getRed()) / ((double)255);
                    int red = (int) (finalColor.getRed() * scale);
                    int green = (int) (finalColor.getGreen() * scale);
                    int blue = (int) (finalColor.getBlue() * scale);
                    image.setRGB(i, j, new Color(red, green, blue).getRGB());
                }
            }
        }
    }

    /**
     * Method called by observable instances when the graphical view must be updated.
     * Redraw every element (by calling paintComponent method).
     * @param o observable instance which send the notification
     *
     */
    @Override
    public void update(Observable o) {
        if (o.equals(cityMap)) {
            scale = 1;
        }
        repaint();
    }

    public void setCanZoom(boolean canZoom) {
        this.canZoom = canZoom;
    }

    /**
     * Zoom on map according to a specific scale.
     * Redraw every segment.
     * @param x x position of the center of the zoomed area
     * @param y y position of the center of the zoomed area
     * @param rotation rotation angle
     */
    public void zoom(int x, int y, double rotation) {
        if (canZoom) {
            double viewWidth = g.getClipBounds().width - allBorders * 2;
            double viewHeight = g.getClipBounds().height - allBorders * 2;
            double zoomCoefficient = 1.2;
            int zoomBorders = allBorders - fakeBorder;
            if (x > zoomBorders && x < g.getClipBounds().width - zoomBorders && y > zoomBorders && y < g.getClipBounds().height - zoomBorders) {
                if ((rotation < 0) && scale < 20) {
                    scale *= zoomCoefficient;
                    originX -= (x - originX) * (zoomCoefficient - 1);
                    originY -= (y - originY) * (zoomCoefficient - 1);
                    repaint();
                } else if (rotation > 0 && scale > 1 && !(viewWidth >= viewHeight && (scale/zoomCoefficient) * proportion < 1) && !(viewWidth <= viewHeight && (scale/zoomCoefficient) / proportion < 1)) {
                    scale /= zoomCoefficient;
                    originX += (x - originX) - (x - originX) / zoomCoefficient;
                    originY += (y - originY) - (y - originY) / zoomCoefficient;
                    repaint();
                }
            }
        }

    }

    /**
     * Move the map.
     * @param x new x position of the center of the zoomed area
     * @param y new y position of the center of the zoomed area
     */
    public void moveMap(int x, int y) {
        originX += x - previousMouseX;
        originY += y - previousMouseY;
        previousMouseX = x;
        previousMouseY = y;
        repaint();
    }

    /**
     * Keep trace of previous center coordinated (for zoomed area)
     * @param x x position of the center of the zoomed area
     * @param y y position of the center of the zoomed area
     */
    public void updatePrevious(int x, int y) {
        previousMouseX = x;
        previousMouseY = y;
    }

    /**
     * Find icon by looking at a given position on graphical view.
     * @param x x position
     * @param y y position
     * @return index of the found icon
     */
    public int findIcon(int x, int y) {
        int indexIcon = -1;
        for (int i = 0; i < tour.getPlanningRequests().size(); i++) {
            Intersection pickupAddress = tour.getPlanningRequests().get(i).getPickupAddress();
            int pickupCoordinateX = getCoordinateX(pickupAddress);
            int pickupCoordinateY = getCoordinateY(pickupAddress);
            if (x >= pickupCoordinateX - 20  && x <= pickupCoordinateX + 20 && y >= pickupCoordinateY - 40 && y <= pickupCoordinateY) {
                if (checkCursorOnIcon(x - (pickupCoordinateX - 20), y - (pickupCoordinateY - 40), "pickup-icon")) {
                    indexIcon = i * 2 + 1;
                }
            }
            Intersection deliveryAddress = tour.getPlanningRequests().get(i).getDeliveryAddress();
            int deliveryCoordinateX = getCoordinateX(deliveryAddress);
            int deliveryCoordinateY = getCoordinateY(deliveryAddress);
            if (x >= deliveryCoordinateX - 20  && x <= deliveryCoordinateX + 20 && y >= deliveryCoordinateY - 40 && y <= deliveryCoordinateY) {
                if (checkCursorOnIcon(x - (deliveryCoordinateX - 20), y - (deliveryCoordinateY - 40), "delivery-icon")) {
                    indexIcon = i * 2 + 2;
                }
            }
        }
        return indexIcon;
    }

    /**
     * Check if the cursor is on an icon.
     * @param x x position of cursor
     * @param y y position of cursor
     * @param iconName name of the icon (filename)
     * @return whether the cursor is on an icon or not
     */
    private boolean checkCursorOnIcon(int x, int y, String iconName) {
        boolean cursorOnIcon = true;
        BufferedImage image;
        try {
            image = Constants.getImage(iconName);
            x = (x * (image.getWidth())) / 40;
            y = (y * (image.getHeight())) / 40;
            if (x == image.getWidth()) x -= 1;
            if (y == image.getHeight()) y -= 1;
            Color color = new Color(image.getRGB(x, y), true);
            if (color.getAlpha() == 0) {
                cursorOnIcon = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cursorOnIcon;
    }

    public void enterSelectionMode() {
        selectionMode = true;
        repaint();
    }

    public void exitSelectionMode() {
        selectionMode = false;
        repaint();
    }

    public boolean isSelectionMode() {
        return !selectionMode;
    }

    public int findIntersection(int x, int y) {
        int indexIntersection = -1;
        for (int i = 0; i < cityMap.getIntersections().size(); i++) {
            int coordinateX = getCoordinateX(cityMap.getIntersections().get(i));
            int coordinateY = getCoordinateY(cityMap.getIntersections().get(i));
            if (Math.pow(coordinateX - x, 2) + Math.pow(coordinateY - y, 2) < Math.pow(radiusForSelection, 2)) {
                indexIntersection = i;
            }
        }
        return indexIntersection;
    }
}
