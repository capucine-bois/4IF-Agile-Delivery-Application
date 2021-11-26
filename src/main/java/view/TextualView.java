package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import model.*;
import observer.Observable;
import observer.Observer;

/**
 * Textual element on the GUI.
 * Used to display requests.
 */
public class TextualView extends JPanel implements Observer {

    // Titles of textual view buttons
    protected final static String REQUESTS_HEADER = "Requests";
    protected static final String TOUR_HEADER = "Tour";
    protected static final String GO_BACK_TO_TOUR = "Back";
    private Tour tour;
    private final int gap = 20;
    private final int colorWidth = 10;
    private final int border = 10;
    private List<JPanel> requestPanels;
    private List<JPanel> shortestPathsPanels;
    private CardLayout cardLayout;
    private Window window;
    private JButton requestsHeader;
    private JButton tourHeader;
    private JButton backToTour;
    private JPanel cardLayoutPanel;
    private JPanel requestsMainPanel;
    private JPanel tourMainPanel;

    // Listeners
    private MouseListener mouseListener;
    private ButtonListener buttonListener;

    /**
     * Create a textual view in window
     * @param w the GUI
     */
    public TextualView(Tour tour, Window w, MouseListener mouseListener, ButtonListener buttonListener) throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_4);
        setBorder(BorderFactory.createMatteBorder(border,border,border,0,Constants.COLOR_1));
        w.getContentPane().add(this, BorderLayout.LINE_START);
        tour.addObserver(this);
        this.tour = tour;
        requestPanels = new ArrayList<>();
        shortestPathsPanels = new ArrayList<>();
        mouseListener.setTextualView(this);
        this.mouseListener = mouseListener;
        this.buttonListener = buttonListener;
        this.window = w;
        createHeader();
        createCardLayout();
        createRequestsPanel();
        createTourPanel();
    }

    private void createHeader() throws IOException, FontFormatException {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1, 2));

        requestsHeader = new JButton(REQUESTS_HEADER);
        window.setStyle(requestsHeader);
        requestsHeader.setEnabled(false);
        requestsHeader.addActionListener(buttonListener);
        header.add(requestsHeader);

        tourHeader = new JButton(TOUR_HEADER);
        window.setStyle(tourHeader);
        tourHeader.setEnabled(false);
        tourHeader.addActionListener(buttonListener);
        header.add(tourHeader);

        add(header, BorderLayout.PAGE_START);
    }

    private void createCardLayout() {
        cardLayout = new CardLayout();
        cardLayoutPanel = new JPanel();
        cardLayoutPanel.setBackground(Constants.COLOR_4);
        cardLayoutPanel.setLayout(cardLayout);
        add(cardLayoutPanel);
    }

    private void displayTextualView() {
        requestsMainPanel.removeAll();
        tourMainPanel.removeAll();
        if (!tour.getPlanningRequests().isEmpty()) {
            addRequests();
            if (!tour.getListShortestPaths().isEmpty()) {
                // addShortestPaths();
                addTourIntersections();
            }
        }
        revalidate();
        repaint();
    }

    private void createTourPanel() {
        tourMainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(tourMainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        cardLayoutPanel.add("tour", scrollPane);
    }

    private void addTourIntersections() {
        tourMainPanel.setBackground(Constants.COLOR_4);
        shortestPathsPanels.clear();
        Optional<ShortestPath> optionalShortestPath = tour.getListShortestPaths().stream().filter(ShortestPath::isSelected).findFirst();
        if (optionalShortestPath.isPresent()) {
            tourMainPanel.setLayout(new BorderLayout());
            tourMainPanel.setBorder(BorderFactory.createMatteBorder(0,0,gap,0,Constants.COLOR_4));
            displaySegmentsHeader(tourMainPanel);
            displaySegments(tourMainPanel, optionalShortestPath.get().getListSegments());
        } else {
            tourMainPanel.setLayout(new BoxLayout(tourMainPanel, BoxLayout.Y_AXIS));
            tourMainPanel.setBorder(BorderFactory.createMatteBorder(gap,0,gap,0,Constants.COLOR_4));
            displayTourGlobalInformation();
            for (int i=0; i<tour.getListShortestPaths().size(); i++) {
                displayPoint(tourMainPanel, tour.getListShortestPaths().get(i));
            }
        }
    }

    private void displaySegmentsHeader(JPanel parentPanel) {
        backToTour = new JButton(GO_BACK_TO_TOUR);
        try {
            window.setStyle(backToTour);
            backToTour.setBackground(Constants.COLOR_12);
        } catch (Exception e) {
            e.printStackTrace();
        }
        backToTour.addActionListener(buttonListener);
        parentPanel.add(backToTour, BorderLayout.PAGE_START);
    }

    private void displayTourGlobalInformation() {
        JPanel tourFirstPanel = new JPanel();
        tourFirstPanel.setLayout(new BoxLayout(tourFirstPanel, BoxLayout.Y_AXIS));
        addLine(tourFirstPanel, "Total length", String.format("%.1f", tour.getTourLength() / (double) 1000) + " km", false, 14);
        addLine(tourFirstPanel, "Speed", String.format("%.1f", tour.getSpeed()) + " km/h", false, 14);
        addLine(tourFirstPanel, "Starting at", tour.getDepartureTime(), false, 14);
        addLine(tourFirstPanel, "Ending at", tour.getArrivalTime(), false, 14);
        tourFirstPanel.setBorder(BorderFactory.createMatteBorder(0, 0, gap, 0, Constants.COLOR_4));
        tourFirstPanel.setMaximumSize(new Dimension(getPreferredSize().width, 116 + gap));
        tourMainPanel.add(tourFirstPanel);
    }

    private void displayPoint(JPanel parentPanel, ShortestPath shortestPath) {
        Map<String, String> pointsInformation = new HashMap<>();
        if (shortestPath.getEndNodeNumber() != 0) {
            boolean endAddressIsPickup = shortestPath.getEndNodeNumber()%2 == 1;
            pointsInformation.put("Type", endAddressIsPickup ? "Pickup" : "Delivery");

            int requestIndex = endAddressIsPickup ? shortestPath.getEndNodeNumber()/2 : shortestPath.getEndNodeNumber()/2 - 1;
            Request request = tour.getPlanningRequests().get(requestIndex);
            if (endAddressIsPickup) {
                pointsInformation.put("Arrival time", request.getPickupArrivalTime());
                pointsInformation.put("Process time", request.getPickupDuration()/60 + " min");
                pointsInformation.put("Departure time", request.getPickupDepartureTime());
            } else {
                pointsInformation.put("Arrival time", request.getDeliveryArrivalTime());
                pointsInformation.put("Process time", request.getDeliveryDuration()/60 + " min");
                pointsInformation.put("Departure time", request.getDeliveryDepartureTime());
            }
            displayInformation(parentPanel, pointsInformation, request.getColor(), shortestPathsPanels, false);
        }
    }

    private void displaySegments(JPanel parentPanel, ArrayList<Segment> segments) {
        JPanel segmentsPanel = new JPanel();
        segmentsPanel.setLayout(new BoxLayout(segmentsPanel, BoxLayout.Y_AXIS));
        segmentsPanel.setBackground(Constants.COLOR_4);
        for (int i = 0; i < segments.size() - 1; i++) {
            Segment segment = segments.get(i);
            String name = segment.getName();
            double length = segment.getLength();
            while (i < segments.size() - 1 && segments.get(i+1).getName().equals(name)) {
                length += segments.get(i+1).getLength();
                i++;
            }
            JPanel segmentPanel = new JPanel();
            segmentPanel.setLayout(new BoxLayout(segmentPanel, BoxLayout.Y_AXIS));
            segmentPanel.setBorder(new CompoundBorder(BorderFactory.createMatteBorder(0,20,0,20, Constants.COLOR_4), BorderFactory.createMatteBorder(1,0,0,0, Constants.COLOR_2)));
            addLine(segmentPanel, "", name, false, 12);
            addLine(segmentPanel, "", (int) length + " m", false, 12);
            segmentPanel.setMaximumSize(new Dimension(getPreferredSize().width, 53));
            segmentsPanel.add(segmentPanel);
        }
        parentPanel.add(segmentsPanel);
    }

    private void createRequestsPanel() {
        requestsMainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(requestsMainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        cardLayoutPanel.add("requests", scrollPane);
    }

    private void addRequests() {
        requestsMainPanel.setLayout(new BoxLayout(requestsMainPanel, BoxLayout.Y_AXIS));
        requestsMainPanel.setBorder(BorderFactory.createMatteBorder(gap,0,0,0,Constants.COLOR_4));
        requestsMainPanel.setBackground(Constants.COLOR_4);
        displayDepotInformation(requestsMainPanel);
        displayRequestsInformation(requestsMainPanel);
    }

    private void displayDepotInformation(JPanel mainPanel) {
        Map<String, String> depotInformation = new HashMap<>();
        String depotCoordinates = tour.getDepotAddress().getLatitude() + ", " + tour.getDepotAddress().getLongitude();
        depotInformation.put("Depot address", depotCoordinates);
        depotInformation.put("Departure time", tour.getDepartureTime());
        displayInformation(mainPanel, depotInformation, Color.black, requestPanels, false);
    }

    private void displayRequestsInformation(JPanel mainPanel) {
        requestPanels.clear();
        for (Request request : tour.getPlanningRequests()) {
            Map<String, String> requestInformation = new HashMap<>();
            String pickupCoordinates = request.getPickupAddress().getLatitude() + ", " + request.getPickupAddress().getLongitude();
            String deliveryCoordinates = request.getDeliveryAddress().getLatitude() + ", " + request.getDeliveryAddress().getLongitude();
            String pickupDuration = request.getPickupDuration() / 60 + " min " + request.getPickupDuration() % 60 + " s";
            if (request.getPickupDuration() % 60 == 0) pickupDuration = pickupDuration.substring(0, pickupDuration.lastIndexOf("0"));
            String deliveryDuration = request.getDeliveryDuration() / 60 + " min " + request.getDeliveryDuration() % 60 + " s";
            if (request.getDeliveryDuration() % 60 == 0) deliveryDuration = deliveryDuration.substring(0, deliveryDuration.lastIndexOf("0"));
            requestInformation.put("Pickup address", pickupCoordinates);
            requestInformation.put("Pickup duration", pickupDuration);
            requestInformation.put("Delivery address", deliveryCoordinates);
            requestInformation.put("Delivery duration", deliveryDuration);
            displayInformation(mainPanel, requestInformation, request.getColor(), requestPanels, request.isSelected());
        }
    }

    private void displayInformation(JPanel mainPanel, Map<String, String> informations, Color color, List<JPanel> panelsForMouseEvent, boolean selected) {
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new BorderLayout());

        JLabel colorInformation = new JLabel();
        colorInformation.setBackground(color);
        colorInformation.setOpaque(true);
        colorInformation.setPreferredSize(new Dimension(colorWidth, informationPanel.getPreferredSize().height));
        informationPanel.add(colorInformation, BorderLayout.LINE_START);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        for(Map.Entry<String, String> information : informations.entrySet()) {
            addLine(contentPanel, information.getKey(), information.getValue(), selected, 12);
        }
        if (panelsForMouseEvent != null) {
            contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            panelsForMouseEvent.add(contentPanel);
            contentPanel.addMouseListener(mouseListener);
        }
        informationPanel.add(contentPanel);

        informationPanel.setBorder(BorderFactory.createMatteBorder(0,0,gap,0,Constants.COLOR_4));
        int maxLineHeight = 26;
        informationPanel.setMaximumSize(new Dimension(getPreferredSize().width - colorWidth, informations.size()*maxLineHeight + gap));
        mainPanel.add(informationPanel);
    }

    private void addLine(JPanel information, String fieldName, String fieldContent, boolean selected, int fontSize) {
        JPanel line = new JPanel();
        line.setLayout(new FlowLayout(FlowLayout.LEFT));
        String finalFieldName = fieldName.equals("") ? "" : fieldName + " : ";
        JLabel fieldNameLabel = new JLabel(finalFieldName);
        setLabelStyle(fieldNameLabel, "DMSans-Bold.ttf", fontSize);
        line.add(fieldNameLabel);
        JLabel fieldContentLabel = new JLabel(fieldContent);
        setLabelStyle(fieldContentLabel, "DMSans-Regular.ttf", fontSize);
        line.add(fieldContentLabel);
        if (!selected) {
            line.setBackground(Constants.COLOR_4);
            line.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_4));
        } else {
            line.setBackground(Constants.COLOR_2);
            line.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_2));
        }
        information.add(line);
    }

    private void setLabelStyle(JLabel label, String fontName, int fontSize) {
        try {
            label.setFont(Constants.getFont(fontName, fontSize));
        } catch (Exception e) {
            e.printStackTrace();
        }
        label.setForeground(Constants.COLOR_3);
    }

    public List<JPanel> getRequestPanels() {
        return requestPanels;
    }

    public List<JPanel> getShortestPathsPanels() {
        return shortestPathsPanels;
    }

    /**
     * Method called by observable instances when the textual view must be updated.
     * @param o observable instance which send the notification
     * @param arg notification data
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(tour)) {
            displayTextualView();
        }
    }

    private void changeButton(JButton previousButton, JButton nextButton) {
        nextButton.setBorder(BorderFactory.createMatteBorder(0,0,5,0,Constants.COLOR_3));
        nextButton.setBackground(Constants.COLOR_4);
        previousButton.setBorder(BorderFactory.createEmptyBorder());
        previousButton.setBackground(Constants.COLOR_2);
    }

    public void showRequestsPanel() {
        changeButton(tourHeader, requestsHeader);
        cardLayout.show(cardLayoutPanel, "requests");
    }

    public void showTourPanel() {
        changeButton(requestsHeader, tourHeader);
        cardLayout.show(cardLayoutPanel, "tour");
    }

    public void setEnabledRequests(boolean enabled) {
        requestsHeader.setEnabled(enabled);
        if(!enabled) {
            try {
                window.setStyle(requestsHeader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setEnabledTour(boolean enabled) {
        tourHeader.setEnabled(enabled);
        if(!enabled) {
            try {
                window.setStyle(tourHeader);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
