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
import java.util.*;
import java.util.List;

/**
 * Graphical element on the GUI.
 * Used to display map and tour (with their segments).
 */
public class GraphicalView extends JPanel implements Observer, MouseWheelListener {

    private Map<Long, IntersectionView> intersectionViewMap;
    private List<SegmentView> segmentViewList;
    private List<Long> requestsIntersections;
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
        segmentViewList = new ArrayList<>();
        intersectionViewMap = new HashMap<>();
        requestsIntersections = new ArrayList<>();
        addMouseWheelListener(this);
    }

    /**
     * Creates intersections and segments of a given map with coordinates X and Y
     * @param adjacenceMap the map to display
     */
    public void initCityMapView(Map<Intersection, ArrayList<Segment>> adjacenceMap) {
        intersectionViewMap.clear();
        segmentViewList.clear();

        Set<Intersection> intersections = adjacenceMap.keySet();
        Optional<Intersection> optionalIntersection = intersections.stream().findFirst();
        if (optionalIntersection.isPresent()) {
            Intersection firstIntersection = optionalIntersection.get();
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

            initIntersectionViewList(adjacenceMap.keySet(), minLatitude, maxLatitude, minLongitude, maxLongitude);
            initSegmentViewList(adjacenceMap.values());
        }
    }

    /**
     * Initialize the list of intersections on the GUI.
     * Parse the list of intersections to instantiate all the intersections for the GUI (IntersectionView) with coordinates X and Y.
     * @param listIntersections map containing an intersection as key and a list of segments where they are part of as value
     * @param minLatitude minimal geographical latitude of all intersections
     * @param maxLatitude maximal geographical latitude of all intersections
     * @param minLongitude minimal geographical longitude of all intersections
     * @param maxLongitude maximal geographical longitude of all intersections
     */
    private void initIntersectionViewList(Set<Intersection> listIntersections, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
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
        intersectionViewMap = new HashMap<>();
        for (Intersection intersection : listIntersections) {
            double coordinateLongitude = intersection.getLongitude() - minLongitude;
            double coordinateLatitude = intersection.getLatitude() - minLatitude;
            int coordinateX = (int) ((coordinateLongitude * width) / longitudeLength) + originX;
            int coordinateY = (int) (height) - (int) ((coordinateLatitude * height) / latitudeLength) + originY;
            IntersectionView intersectionView = new IntersectionView(coordinateX, coordinateY);
            intersectionViewMap.put(intersection.getId(), intersectionView);
        }
    }

    /**
     * Initialize the list of segments on the GUI.
     * Parse the list of segments to instantiate all the segments for the GUI (SegmentView).
     * @param segmentsCollection all the segments
     */
    private void initSegmentViewList(Collection<ArrayList<Segment>> segmentsCollection) {
        for (List<Segment> segments : segmentsCollection) {
            for (Segment segment : segments) {
                IntersectionView origin = intersectionViewMap.get(segment.getOrigin().getId());
                IntersectionView destination = intersectionViewMap.get(segment.getDestination().getId());
                SegmentView segmentView = new SegmentView(origin, destination);
                segmentViewList.add(segmentView);
            }
        }
    }

    /**
     * Associates depot, pickups and deliveries addresses with intersections having coordinates X and Y
     * @param depotAddress depot address
     * @param planningRequests list of all requests with for each a pickup address and a delivery address
     */
    private void initTourIntersectionsView(Intersection depotAddress, ArrayList<Request> planningRequests) {
        requestsIntersections.clear();
        requestsIntersections.add(depotAddress.getId());

        for (Request request : planningRequests) {
            requestsIntersections.add(request.getPickupAddress().getId());
            requestsIntersections.add(request.getDeliveryAddress().getId());
        }
    }

    /**
     * Draw the component on GUI.
     * @param g parent component where the graphical view must be drawn.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;
        for (SegmentView segmentView : segmentViewList) {
            segmentView.paintSegmentBorder(g, scale);
        }
        for (SegmentView segmentView : segmentViewList) {
            segmentView.paintSegment(g, scale);
        }
        g.setColor(Constants.COLOR_7);
        for (IntersectionView intersectionView : intersectionViewMap.values()) {
            g.fillOval(intersectionView.getCoordinateX() - scale/2, intersectionView.getCoordinateY() - scale/2, scale, scale);
        }
        if (!requestsIntersections.isEmpty()) {
            Iterator<Long> iterator = requestsIntersections.iterator();
            IntersectionView depotAddress = intersectionViewMap.get(iterator.next());
            float[] hsv = new float[3];
            Color initialColor = Color.red;
            Color.RGBtoHSB(initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue(), hsv);
            double goldenRatioConjugate = 0.618033988749895;
            while (iterator.hasNext()) {
                IntersectionView pickupAddress = intersectionViewMap.get(iterator.next());
                IntersectionView deliveryAddress = intersectionViewMap.get(iterator.next());
                Color requestColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
                drawIcon(requestColor, pickupAddress, "pickup-icon.png");
                drawIcon(requestColor, deliveryAddress, "delivery-icon.png");
                hsv[0] += goldenRatioConjugate;
                hsv[0] %= 1;
            }
            drawIcon(null, depotAddress, "depot-icon.png");
        }
    }

    /**
     * Draw the icon for a depot, pickup or delivery address
     * @param color color of the icon
     * @param address address with coordinates X and Y
     * @param iconFileName name of the icon file
     */
    private void drawIcon(Color color, IntersectionView address, String iconFileName) {
        try {
            BufferedImage image = Constants.getImage(iconFileName);
            if (color != null) fillColorInImage(image, color);
            g.drawImage(image, address.getCoordinateX() - 20, address.getCoordinateY() - 40, 40, 40, this);
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
            requestsIntersections.clear();
            initCityMapView(cityMap.getAdjacenceMap());
        } else if (o.equals(tour)) {
            initTourIntersectionsView(tour.getDepotAddress(), tour.getPlanningRequests());
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
                initCityMapView(cityMap.getAdjacenceMap());
                repaint();
            } else if (e.getWheelRotation() > 0 && scale > 1) {
                scale /= zoomCoefficient;
                originX += (e.getX() - originX) - (e.getX() - originX) / zoomCoefficient;
                originY += (e.getY() - originY) - (e.getY() - originY) / zoomCoefficient;
                initCityMapView(cityMap.getAdjacenceMap());
                repaint();
            }
        }
    }
}
