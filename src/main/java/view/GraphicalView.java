package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphicalView extends JPanel {

    private Map<Long, IntersectionView> intersectionViewMap;
    private List<SegmentView> segmentViewList = new ArrayList<>();

    /**
     * Create the graphical view
     * @param w the window
     */
    public GraphicalView(Window w) {
        setLayout(null);
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(10,10,10,5,Constants.COLOR_1),BorderFactory.createLineBorder(Constants.COLOR_4, 2)));
        w.getContentPane().add(this, BorderLayout.CENTER);
    }

    public void initIntersectionViewList(List<double[]> intersectionsTest) {

        // MAYBE TRANSFORM ALL THAT WITH UPDATE OF NOTIFY OBSERVERS

        double minLatitude = intersectionsTest.get(0)[0]; // getLatitude() instead of [0]
        double maxLatitude = intersectionsTest.get(0)[0]; // getLatitude() instead of [0]
        double minLongitude = intersectionsTest.get(0)[1]; // getLongitude() instead of [1]
        double maxLongitude = intersectionsTest.get(0)[1]; // getLongitude() instead of [1]

        for (double[] intersection : intersectionsTest) {
            double latitude = intersection[0]; // getLatitude() instead of [0]
            double longitude = intersection[1]; // getLongitude() instead of [1]
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

        createAllIntersectionViews(intersectionsTest, minLatitude, maxLatitude, minLongitude, maxLongitude);
    }

    private void createAllIntersectionViews(List<double[]> intersectionsTest, double minLatitude, double maxLatitude, double minLongitude, double maxLongitude) {
        double latitudeLength = maxLatitude - minLatitude;
        double longitudeLength = maxLongitude - minLongitude;

        intersectionViewMap = new HashMap<>();
        for (double[] intersection : intersectionsTest) {
            double coordinateLongitude = intersection[1] - minLongitude; // getLongitude() instead of [1]
            double coordinateLatitude = intersection[0] - minLatitude; // getLatitude() instead of [0]
            double width = getPreferredSize().width;
            double height = getPreferredSize().height;
            int coordinateX = (int) ((coordinateLongitude * width) / longitudeLength);
            int coordinateY = (int) (height) - (int) ((coordinateLatitude * height) / latitudeLength);
            IntersectionView intersectionView = new IntersectionView(coordinateX, coordinateY);
            intersectionViewMap.put((long) intersection[2], intersectionView); // getId() instead of [2]
        }

        List<long[]> segmentsTest = new ArrayList<>();
        segmentsTest.add(new long[]{1, 3});
        segmentsTest.add(new long[]{2, 4});
        segmentsTest.add(new long[]{2, 3});
        segmentsTest.add(new long[]{3, 2});
        segmentsTest.add(new long[]{6, 5});
        segmentsTest.add(new long[]{5, 1});
        initSegmentViewList(segmentsTest);
    }

    private void initSegmentViewList(List<long[]> segmentsTest) {
        //segmentViewList = new ArrayList<>();
        for (long[] segment : segmentsTest) {
            IntersectionView origin = intersectionViewMap.get(segment[0]); // getOrigin() instead of [0]
            IntersectionView destination = intersectionViewMap.get(segment[1]); // getDestination() instead of [1]
            SegmentView segmentView = new SegmentView(origin, destination);
            segmentViewList.add(segmentView);
        }
    }

    /**
     * Method called each time this must be redrawn
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        for (SegmentView segmentView : segmentViewList) {
            segmentView.paintComponent(g);
        }
    }

}
