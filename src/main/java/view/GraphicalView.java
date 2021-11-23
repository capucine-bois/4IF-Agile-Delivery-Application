package view;

import model.*;
import observer.Observable;
import observer.Observer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

/**
 * Graphical element on the GUI.
 * Used to display map and tour (with their segments).
 */
public class GraphicalView extends JPanel implements Observer, MouseWheelListener {

    private Graphics g;
    private final int firstBorder = 10;
    private final int secondBorder = 2;
    private final int fakeBorder = 10;
    private final int allBorders = firstBorder + secondBorder + fakeBorder;
    private int scale = 1;
    private int originX = allBorders;
    private int originY = allBorders;
    private CityMap cityMap;
    private Tour tour;

    /**
     * Create the graphical view
     * @param cityMap the city map
     * @param tour the tour
     * @param w the window
     */
    public GraphicalView(CityMap cityMap, Tour tour, Window w) {
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createLineBorder(Constants.COLOR_1, firstBorder),BorderFactory.createLineBorder(Constants.COLOR_4, secondBorder)));
        w.getContentPane().add(this, BorderLayout.CENTER);
        cityMap.addObserver(this);
        tour.addObserver(this);
        this.cityMap = cityMap;
        this.tour = tour;
        addMouseWheelListener(this);
    }

    /**
     * Creates intersections and segments of a given map with coordinates X and Y
     * @param adjacenceMap the map to display
     */
    public void displayCityMap(List<Intersection> intersections) {
        if (!intersections.isEmpty()) {
            Intersection firstIntersection = intersections.get(0);
            double minLatitude = firstIntersection.getLatitude();
            double maxLatitude = firstIntersection.getLatitude();
            double minLongitude = firstIntersection.getLongitude();
            double maxLongitude = firstIntersection.getLongitude();

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

            displayIntersectionsAndSegments(intersections, minLatitude, maxLatitude, minLongitude, maxLongitude);
        }
    }

    /**
     * Initialize the list of intersections on the GUI.
     * Parse the list of intersections to instantiate all the intersections for the GUI (IntersectionView) with coordinates X and Y.
     * @param intersections map containing an intersection as key and a list of segments where they are part of as value
     * @param minLatitude minimal geographical latitude of all intersections
     * @param maxLatitude maximal geographical latitude of all intersections
     * @param minLongitude minimal geographical longitude of all intersections
     * @param maxLongitude maximal geographical longitude of all intersections
     */
    private void displayIntersectionsAndSegments(List<Intersection> intersections, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        double latitudeLength = maxLatitude - minLatitude;
        double longitudeLength = maxLongitude - minLongitude;

        double viewWidth = g.getClipBounds().width - allBorders * 2;
        double width = viewWidth * scale;
        double viewHeight = g.getClipBounds().height - allBorders * 2;
        double height = viewHeight * scale;

        if (originX > allBorders) originX = allBorders;
        if (originY > allBorders) originY = allBorders;
        if (originX + width - allBorders < viewWidth) originX = (int) (viewWidth - width + allBorders);
        if (originY + height - allBorders < viewHeight) originY = (int) (viewHeight - height + allBorders);

        for (Intersection origin : intersections) {
            int originCoordinateX = getCoordinateX(origin, minLongitude, width, longitudeLength);
            int originCoordinateY = getCoordinateY(origin, minLatitude, height, latitudeLength);
            for (Segment segment : origin.getAdjacentSegments()) {
                int destinationCoordinateX = getCoordinateX(segment.getDestination(), minLongitude, width, longitudeLength);
                int destinationCoordinateY = getCoordinateY(segment.getDestination(), minLatitude, height, latitudeLength);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Constants.COLOR_6);
                g2.setStroke(new BasicStroke(scale + 2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
            }
        }

        for (Intersection origin : intersections) {
            int originCoordinateX = getCoordinateX(origin, minLongitude, width, longitudeLength);
            int originCoordinateY = getCoordinateY(origin, minLatitude, height, latitudeLength);
            for (Segment segment : origin.getAdjacentSegments()) {
                int destinationCoordinateX = getCoordinateX(segment.getDestination(), minLongitude, width, longitudeLength);
                int destinationCoordinateY = getCoordinateY(segment.getDestination(), minLatitude, height, latitudeLength);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Constants.COLOR_7);
                g2.setStroke(new BasicStroke(scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
            }
        }

        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            for (Segment segment : shortestPath.getListSegments()) {
                int originCoordinateX = getCoordinateX(segment.getOrigin(), minLongitude, width, longitudeLength);
                int originCoordinateY = getCoordinateY(segment.getOrigin(), minLatitude, height, latitudeLength);
                int destinationCoordinateX = getCoordinateX(segment.getDestination(), minLongitude, width, longitudeLength);
                int destinationCoordinateY = getCoordinateY(segment.getDestination(), minLatitude, height, latitudeLength);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Constants.COLOR_8);
                g2.setStroke(new BasicStroke(scale + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
            }
        }

        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            for (Segment segment : shortestPath.getListSegments()) {
                int originCoordinateX = getCoordinateX(segment.getOrigin(), minLongitude, width, longitudeLength);
                int originCoordinateY = getCoordinateY(segment.getOrigin(), minLatitude, height, latitudeLength);
                int destinationCoordinateX = getCoordinateX(segment.getDestination(), minLongitude, width, longitudeLength);
                int destinationCoordinateY = getCoordinateY(segment.getDestination(), minLatitude, height, latitudeLength);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(Constants.COLOR_9);
                g2.setStroke(new BasicStroke(scale, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(originCoordinateX, originCoordinateY, destinationCoordinateX, destinationCoordinateY);
            }
        }

        if (!tour.getPlanningRequests().isEmpty()) {
            boolean oneRequestSelected = tour.getPlanningRequests().stream().anyMatch(Request::isSelected);
            for (Request request : tour.getPlanningRequests()) {
                if (!oneRequestSelected || request.isSelected()) {
                    Intersection pickupAddress = request.getPickupAddress();
                    int pickupCoordinateX = getCoordinateX(pickupAddress, minLongitude, width, longitudeLength);
                    int pickupCoordinateY = getCoordinateY(pickupAddress, minLatitude, height, latitudeLength);
                    Intersection deliveryAddress = request.getDeliveryAddress();
                    int deliveryCoordinateX = getCoordinateX(deliveryAddress, minLongitude, width, longitudeLength);
                    int deliveryCoordinateY = getCoordinateY(deliveryAddress, minLatitude, height, latitudeLength);
                    drawIcon(request.getColor(), pickupCoordinateX, pickupCoordinateY, "pickup-icon.png");
                    drawIcon(request.getColor(), deliveryCoordinateX, deliveryCoordinateY, "delivery-icon.png");
                }
            }
            int depotCoordinateX = getCoordinateX(tour.getDepotAddress(), minLongitude, width, longitudeLength);
            int depotCoordinateY = getCoordinateY(tour.getDepotAddress(), minLatitude, height, latitudeLength);
            drawIcon(null, depotCoordinateX, depotCoordinateY, "depot-icon.png");
        }
    }
    
    private int getCoordinateX(Intersection intersection, double minLongitude, double width, double longitudeLength) {
        double coordinateLongitude = intersection.getLongitude() - minLongitude;
        return (int) ((coordinateLongitude * width) / longitudeLength) + originX;
    }

    private int getCoordinateY(Intersection intersection, double minLatitude, double height, double latitudeLength) {
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
     * @param address address with coordinates X and Y
     * @param iconFileName name of the icon file
     */
    private void drawIcon(Color color, int coordinateX, int coordinateY, String iconFileName) {
        try {
            BufferedImage image = Constants.getImage(iconFileName);
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

    /**
     * Method called each time the mouse wheel is moved
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int zoomCoefficient = 2;
        int zoomBorders = allBorders - fakeBorder;
        if (e.getX() > zoomBorders && e.getX() < g.getClipBounds().width - zoomBorders && e.getY() > zoomBorders && e.getY() < g.getClipBounds().height - zoomBorders) {
            if ((e.getWheelRotation() < 0) && scale < 20) {
                scale *= zoomCoefficient;
                originX -= (e.getX() - originX) * (zoomCoefficient - 1);
                originY -= (e.getY() - originY) * (zoomCoefficient - 1);
                repaint();
            } else if (e.getWheelRotation() > 0 && scale > 1) {
                scale /= zoomCoefficient;
                originX += (e.getX() - originX) - (e.getX() - originX) / zoomCoefficient;
                originY += (e.getY() - originY) - (e.getY() - originY) / zoomCoefficient;
                repaint();
            }
        }
    }
}
