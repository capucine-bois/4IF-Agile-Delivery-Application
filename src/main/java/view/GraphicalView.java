package view;

import model.CityMap;
import model.Intersection;
import model.Segment;
import observer.Observable;
import observer.Observer;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Graphical element on the GUI.
 * Used to display map and tour (with their segments).
 */
public class GraphicalView extends JPanel implements Observer {

    private Map<Long, IntersectionView> intersectionViewMap;
    private List<SegmentView> segmentViewList;
    private Graphics g;
    private final int firstBorder = 10;
    private final int secondBorder = 2;
    private CityMap cityMap;

    /**
     * Create the graphical view
     * @param w the window
     */
    public GraphicalView(CityMap cityMap, Window w) {
        setLayout(null);
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(firstBorder,firstBorder,firstBorder,5,Constants.COLOR_1),BorderFactory.createLineBorder(Constants.COLOR_4, secondBorder)));
        w.getContentPane().add(this, BorderLayout.CENTER);
        cityMap.addObserver(this);
        this.cityMap = cityMap;
        segmentViewList = new ArrayList<>();
        intersectionViewMap = new HashMap<>();
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

    /**
     * Initialize the list of intersections on the GUI.
     * Parse the list of intersections to instantiate all the intersections for the GUI (IntersectionView).
     * @param adjacenceMap map containing an intersection as key and a list of segments where they are part of as value
     * @param minLatitude minimal geographical latitude of all intersections
     * @param maxLatitude maximal geographical latitude of all intersections
     * @param minLongitude minimal geographical longitude of all intersections
     * @param maxLongitude maximal geographical longitude of all intersections
     */
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
     * Draw the component on GUI.
     * @param g parent component where the graphical view must be drawn.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.g = g;
        displayCityMap(cityMap.getAdjacenceMap());
        g.setColor(Color.red);
        for (SegmentView segmentView : segmentViewList) {
            segmentView.paintSegment(g);
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
        repaint();
    }
}
