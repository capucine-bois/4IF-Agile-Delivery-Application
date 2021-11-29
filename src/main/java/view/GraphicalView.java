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
    private CityMap cityMap;
    private Tour tour;
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

    // listeners
    private MouseListener mouseListener;

    /**
     * Create the graphical view
     * @param cityMap the city map
     * @param tour the tour
     * @param w the window
     */
    public GraphicalView(CityMap cityMap, Tour tour, Window w, MouseListener mouseListener) {
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Constants.COLOR_1, firstBorder),BorderFactory.createLineBorder(Constants.COLOR_4, secondBorder)));
        w.getContentPane().add(this, BorderLayout.CENTER);
        cityMap.addObserver(this);
        tour.addObserver(this);
        this.cityMap = cityMap;
        this.tour = tour;
        mouseListener.setGraphicalView(this);
        this.mouseListener = mouseListener;
        addMouseWheelListener(mouseListener);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
        try {
            roadFont = Constants.getFont("DMSans-Medium.ttf", 12);
            roadShortestPathFont = Constants.getFont("DMSans-Bold.ttf", 12);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * Initialize the list of intersections on the GUI.
     * Parse the list of intersections to instantiate all the intersections for the GUI (IntersectionView) with coordinates X and Y.
     * @param intersections map containing an intersection as key and a list of segments where they are part of as value
     */
    private void displayIntersectionsAndSegments(List<Intersection> intersections) {
        latitudeLength = maxLatitude - minLatitude;
        longitudeLength = maxLongitude - minLongitude;

        double viewWidth = g.getClipBounds().width - allBorders * 2;
        width = viewWidth * scale;
        double viewHeight = g.getClipBounds().height - allBorders * 2;
        height = viewHeight * scale;

        if (originX > allBorders) originX = allBorders;
        if (originY > allBorders) originY = allBorders;
        if (originX + width - allBorders < viewWidth) originX = (int) (viewWidth - width + allBorders);
        if (originY + height - allBorders < viewHeight) originY = (int) (viewHeight - height + allBorders);

        displaySegmentsForEachOrigin(intersections, Constants.COLOR_6, (float) (scale + 2), false);
        displaySegmentsForEachOrigin(intersections, Constants.COLOR_7, (float) scale, true);

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
            int depotCoordinateX = getCoordinateX(tour.getDepotAddress());
            int depotCoordinateY = getCoordinateY(tour.getDepotAddress());
            drawIcon(null, depotCoordinateX, depotCoordinateY, "depot-icon");
        }
    }

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

    private int getCoordinateX(Intersection intersection) {
        double coordinateLongitude = intersection.getLongitude() - minLongitude;
        return (int) ((coordinateLongitude * width) / longitudeLength) + originX;
    }

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
     * @param arg notification data
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(cityMap)) {
            scale = 1;
        }
        repaint();
    }

    public void setCanZoom(boolean canZoom) {
        this.canZoom = canZoom;
    }

    public void zoom(int x, int y, double rotation) {

        if (canZoom) {
            double zoomCoefficient = 1.2;
            int zoomBorders = allBorders - fakeBorder;
            if (x > zoomBorders && x < g.getClipBounds().width - zoomBorders && y > zoomBorders && y < g.getClipBounds().height - zoomBorders) {
                if ((rotation < 0) && scale < 20) {
                    scale *= zoomCoefficient;
                    originX -= (x - originX) * (zoomCoefficient - 1);
                    originY -= (y - originY) * (zoomCoefficient - 1);
                    repaint();
                } else if (rotation > 0 && scale > 1) {
                    scale /= zoomCoefficient;
                    originX += (x - originX) - (x - originX) / zoomCoefficient;
                    originY += (y - originY) - (y - originY) / zoomCoefficient;
                    repaint();
                }
            }
        }

    }

    public void moveMap(int x, int y) {
        originX += x - previousMouseX;
        originY += y - previousMouseY;
        previousMouseX = x;
        previousMouseY = y;
        repaint();
    }

    public void updatePrevious(int x, int y) {
        previousMouseX = x;
        previousMouseY = y;
    }

    public int findIcon(int x, int y) {
        int indexIcon = -1;
        for (int i = 0; i < tour.getPlanningRequests().size(); i++) {
            Intersection pickupAddress = tour.getPlanningRequests().get(i).getPickupAddress();
            int pickupCoordinateX = getCoordinateX(pickupAddress);
            int pickupCoordinateY = getCoordinateY(pickupAddress);
            if (x >= pickupCoordinateX - 20  && x <= pickupCoordinateX + 20 && y >= pickupCoordinateY - 40 && y <= pickupCoordinateY) {
                if (checkCursorOnIcon(x - (pickupCoordinateX - 20), y - (pickupCoordinateY - 40), "depot-icon")) {
                    indexIcon = i * 2 + 1;
                }
            }
            Intersection deliveryAddress = tour.getPlanningRequests().get(i).getDeliveryAddress();
            int deliveryCoordinateX = getCoordinateX(deliveryAddress);
            int deliveryCoordinateY = getCoordinateY(deliveryAddress);
            if (x >= deliveryCoordinateX - 20  && x <= deliveryCoordinateX + 20 && y >= deliveryCoordinateY - 40 && y <= deliveryCoordinateY) {
                if (checkCursorOnIcon(x - (deliveryCoordinateX - 20), y - (deliveryCoordinateY - 40), "depot-icon")) {
                    indexIcon = i * 2 + 2;
                }
            }
        }
        return indexIcon;
    }

    private boolean checkCursorOnIcon(int x, int y, String iconName) {
        boolean cursorOnIcon = true;
        BufferedImage image = null;
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
}
