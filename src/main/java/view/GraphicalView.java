package view;

import model.*;
import observer.Observable;
import observer.Observer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Graphical element on the GUI.
 * Used to display map and tour (with their segments).
 */
public class GraphicalView extends JPanel implements Observer {

    private Map<Long, IntersectionView> intersectionViewMap;
    private List<SegmentView> segmentViewList;
    private List<IntersectionView> requests;
    private Graphics g;
    private final int firstBorder = 10;
    private final int secondBorder = 2;
    private CityMap cityMap;
    private Tour tour;

    /**
     * Create the graphical view
     * @param w the window
     */
    public GraphicalView(CityMap cityMap, Tour tour, Window w) {
        setLayout(null);
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(firstBorder,firstBorder,firstBorder,5,Constants.COLOR_1),BorderFactory.createLineBorder(Constants.COLOR_4, secondBorder)));
        w.getContentPane().add(this, BorderLayout.CENTER);
        cityMap.addObserver(this);
        tour.addObserver(this);
        this.cityMap = cityMap;
        this.tour = tour;
        segmentViewList = new ArrayList<>();
        intersectionViewMap = new HashMap<>();
        requests = new ArrayList<>();
    }

    /**
     * Display segments and intersections of a given map.
     * @param adjacenceMap the map to display
     */
    public void displayCityMap(Map<Intersection, ArrayList<Segment>> adjacenceMap) {
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

            initIntersectionViewList(adjacenceMap, minLatitude, maxLatitude, minLongitude, maxLongitude);
            initSegmentViewList(adjacenceMap.values());
        }
    }

    private void initIntersectionViewList(Map<Intersection, ArrayList<Segment>> adjacenceMap, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        double latitudeLength = maxLatitude - minLatitude;
        double longitudeLength = maxLongitude - minLongitude;

        double width = g.getClipBounds().width - (firstBorder + (double) (firstBorder / 2) + secondBorder * 2);
        double height = g.getClipBounds().height - (firstBorder * 2 + secondBorder * 2);

        intersectionViewMap = new HashMap<>();
        for (Intersection intersection : adjacenceMap.keySet()) {
            double coordinateLongitude = intersection.getLongitude() - minLongitude;
            double coordinateLatitude = intersection.getLatitude() - minLatitude;
            int coordinateX = (int) ((coordinateLongitude * width) / longitudeLength) + firstBorder + secondBorder;
            int coordinateY = (int) (height) - (int) ((coordinateLatitude * height) / latitudeLength) + firstBorder + secondBorder;
            IntersectionView intersectionView = new IntersectionView(coordinateX, coordinateY);
            intersectionViewMap.put(intersection.getId(), intersectionView);
        }
    }

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

    private void displayTourIntersections(Intersection depotAddress, ArrayList<Request> planningRequests) {
        requests.add(intersectionViewMap.get(depotAddress.getId()));

        for (Request request : planningRequests) {
            requests.add(intersectionViewMap.get(request.getPickupAddress().getId()));
            requests.add(intersectionViewMap.get(request.getDeliveryAddress().getId()));
        }
    }

    /**
     * Method called each time this must be redrawn
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;
        g.setColor(Color.gray);
        for (SegmentView segmentView : segmentViewList) {
            segmentView.paintSegment(g);
        }
        if (!requests.isEmpty()) {
            List<Color> usedColors = new ArrayList<>();
            Iterator<IntersectionView> iterator = requests.iterator();
            IntersectionView depotAddress = iterator.next();
            Color depotColor = Color.red;
            usedColors.add(depotColor);
            while (iterator.hasNext()) {
                IntersectionView pickupAddress = iterator.next();
                IntersectionView deliveryAddress = iterator.next();
                Color requestColor;
                do {
                    int red = (int) (Math.random() * 256);
                    int green = (int) (Math.random() * 256);
                    int blue = (int) (Math.random() * 256);
                    requestColor = new Color(red, green, blue);
                } while (usedColors.contains(requestColor));
                drawIcon(requestColor, pickupAddress, "pickup-icon.png");
                drawIcon(requestColor, deliveryAddress, "delivery-icon.png");
            }
            drawIcon(depotColor, depotAddress, "depot-icon.png");
        }
    }

    private void drawIcon(Color color, IntersectionView address, String iconFileName) {
        try {
            BufferedImage image = Constants.getImage(iconFileName);
            fillColorInImage(image, color);
            g.drawImage(image, address.getCoordinateX() - 20, address.getCoordinateY() - 40, 40, 40, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(cityMap)) {
            displayCityMap(cityMap.getAdjacenceMap());
        } else if (o.equals(tour)) {
            displayTourIntersections(tour.getDepotAddress(), tour.getPlanningRequests());
        }
        repaint();
    }
}
