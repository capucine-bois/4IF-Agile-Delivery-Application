package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.*;
import java.util.List;

import controller.Controller;
import model.*;
import observer.Observable;
import observer.Observer;

/**
 * Textual element on the GUI.
 * Used to display requests.
 */
public class TextualView extends JPanel implements Observer, ActionListener {

    private Tour tour;
    private final int gap = 20;
    private final int colorWidth = 10;
    private final int border = 10;
    private MouseListener mouseListener;
    private List<JPanel> requestPanels;
    private CardLayout cardLayout;
    private Window window;
    private JButton requestsHeader;
    private JButton tourHeader;
    private JPanel cardLayoutPanel;

    /**
     * Create a textual view in window
     * @param w the GUI
     */
    public TextualView(Tour tour, Window w, Controller controller) throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_4);
        setBorder(BorderFactory.createMatteBorder(border,border,border,0,Constants.COLOR_1));
        w.getContentPane().add(this, BorderLayout.LINE_START);
        tour.addObserver(this);
        this.tour = tour;
        requestPanels = new ArrayList<>();
        mouseListener = new MouseListener(controller, this);
        this.window = w;
        createHeader();
        createCardLayout();
    }

    private void createHeader() throws IOException, FontFormatException {
        JPanel header = new JPanel();
        header.setLayout(new GridLayout(1, 2));

        requestsHeader = new JButton("Requests");
        window.setStyle(requestsHeader);
        requestsHeader.setEnabled(false);
        requestsHeader.addActionListener(this);
        header.add(requestsHeader);

        tourHeader = new JButton("Tour");
        window.setStyle(tourHeader);
        tourHeader.setEnabled(false);
        tourHeader.addActionListener(this);
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

    private void displayTextualRequests() {
        requestsHeader.setEnabled(false);
        tourHeader.setEnabled(false);
        cardLayoutPanel.removeAll();
        if (!tour.getPlanningRequests().isEmpty()) {
            createRequestsScrollPane();
            requestsHeader.setEnabled(true);
            if (!tour.getListShortestPaths().isEmpty()) {
                createTourScrollPane();
                tourHeader.setEnabled(true);
                cardLayout.next(cardLayoutPanel);
            }
        }
        revalidate();
    }

    private void createTourScrollPane() {
        JPanel mainTourPanel = createTourMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainTourPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        cardLayoutPanel.add(scrollPane);
    }

    private JPanel createTourMainPanel() {
        JPanel tourMainPanel = new JPanel();
        tourMainPanel.setLayout(new BoxLayout(tourMainPanel, BoxLayout.Y_AXIS));
        tourMainPanel.setBorder(BorderFactory.createMatteBorder(gap,0,0,0,Constants.COLOR_4));
        tourMainPanel.setBackground(Constants.COLOR_4);
        displayShortestPaths(tourMainPanel);
        return tourMainPanel;
    }

    private void displayShortestPaths(JPanel tourMainPanel) {
        for (ShortestPath shortestPath : tour.getListShortestPaths()) {
            Map<String, String> shortestPathInformation = new HashMap<>();
            String startPath = findTypeIntersection(shortestPath.getStartAddress());
            String endPath = findTypeIntersection(shortestPath.getEndAddress());
            shortestPathInformation.put("Length", String.format("%.1f", shortestPath.getPathLength() / (double) 1000) + " km");
            shortestPathInformation.put("From", startPath);
            shortestPathInformation.put("To", endPath);
            Color startColor = findColor(shortestPath.getStartAddress());
            Color endColor = findColor(shortestPath.getEndAddress());
            displayPathInformation(tourMainPanel, shortestPathInformation, startColor, endColor, shortestPath.getListSegments());
        }
    }

    private String findTypeIntersection(Intersection startAddress) {
        String type;
        if (startAddress == tour.getDepotAddress()) {
            type = "Depot";
        } else if (tour.getPlanningRequests().stream().anyMatch(x -> x.getPickupAddress() == startAddress)) {
            type = "Pickup";
        } else {
            type = "Delivery";
        }
        return type;
    }

    private Color findColor(Intersection intersection) {
        Color color;
        Optional<Request> optionalRequest = tour.getPlanningRequests().stream().filter(x -> x.getPickupAddress() == intersection || x.getDeliveryAddress() == intersection).findFirst();
        if (optionalRequest.isPresent()) {
            color = optionalRequest.get().getColor();
        } else {
            color = Color.black;
        }
        return color;
    }

    private void displayPathInformation(JPanel tourMainPanel, Map<String, String> shortestPathInformation, Color startColor, Color endColor, List<Segment> segments) {
        JPanel pathInformationPanel = new JPanel();
        pathInformationPanel.setLayout(new BoxLayout(pathInformationPanel, BoxLayout.Y_AXIS));

        for (Map.Entry<String, String> information : shortestPathInformation.entrySet()) {
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());

            JLabel colorInformation = new JLabel();
            if (information.getKey().equals("From")) {
                colorInformation.setBackground(startColor);
            } else if (information.getKey().equals("To")) {
                colorInformation.setBackground(endColor);
            } else {
                colorInformation.setBackground(Constants.COLOR_4);
            }
            colorInformation.setOpaque(true);
            colorInformation.setPreferredSize(new Dimension(colorWidth, pathInformationPanel.getPreferredSize().height));
            contentPanel.add(colorInformation, BorderLayout.LINE_START);

            addLine(contentPanel, information.getKey(), information.getValue(), null);
            pathInformationPanel.add(contentPanel);
        }

        pathInformationPanel.setBorder(BorderFactory.createMatteBorder(0,0,gap,0,Constants.COLOR_4));
        int maxLineHeight = 26;
        pathInformationPanel.setMaximumSize(new Dimension(getPreferredSize().width - colorWidth, shortestPathInformation.size()*maxLineHeight + gap));
        tourMainPanel.add(pathInformationPanel);
    }

    private void createRequestsScrollPane() {
        JPanel requestsMainPanel = createRequestsMainPanel();
        JScrollPane scrollPane = new JScrollPane(requestsMainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        cardLayoutPanel.add(scrollPane);
    }

    private JPanel createRequestsMainPanel() {
        JPanel requestsMainPanel = new JPanel();
        requestsMainPanel.setLayout(new BoxLayout(requestsMainPanel, BoxLayout.Y_AXIS));
        requestsMainPanel.setBorder(BorderFactory.createMatteBorder(gap,0,0,0,Constants.COLOR_4));
        requestsMainPanel.setBackground(Constants.COLOR_4);
        displayDepotInformation(requestsMainPanel);
        displayRequestsInformation(requestsMainPanel);
        return requestsMainPanel;
    }

    private void displayDepotInformation(JPanel mainPanel) {
        Map<String, String> depotInformation = new HashMap<>();
        String depotCoordinates = tour.getDepotAddress().getLatitude() + ", " + tour.getDepotAddress().getLongitude();
        depotInformation.put("Depot address", depotCoordinates);
        depotInformation.put("Departure time", tour.getDepartureTime());
        displayInformation(mainPanel, depotInformation, Color.black, null);
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
            displayInformation(mainPanel, requestInformation, request.getColor(), request);
        }
    }

    private void displayInformation(JPanel mainPanel, Map<String, String> informations, Color color, Request request) {
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
            addLine(contentPanel, information.getKey(), information.getValue(), request);
        }
        if (request != null) {
            requestPanels.add(contentPanel);
            contentPanel.addMouseListener(mouseListener);
        }
        informationPanel.add(contentPanel);

        informationPanel.setBorder(BorderFactory.createMatteBorder(0,0,gap,0,Constants.COLOR_4));
        int maxLineHeight = 26;
        informationPanel.setMaximumSize(new Dimension(getPreferredSize().width - colorWidth, informations.size()*maxLineHeight + gap));
        mainPanel.add(informationPanel);
    }

    private void addLine(JPanel information, String fieldName, String fieldContent, Request request) {
        JPanel line = new JPanel();
        line.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel fieldNameLabel = new JLabel(fieldName + " : ");
        setLabelStyle(fieldNameLabel, "DMSans-Bold.ttf");
        line.add(fieldNameLabel);
        JLabel fieldContentLabel = new JLabel(fieldContent);
        setLabelStyle(fieldContentLabel, "DMSans-Regular.ttf");
        line.add(fieldContentLabel);
        line.setPreferredSize(new Dimension(information.getPreferredSize().width, line.getPreferredSize().height));
        if (request == null || !request.isSelected()) {
            line.setBackground(Constants.COLOR_4);
            line.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_4));
        } else {
            line.setBackground(Constants.COLOR_2);
            line.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_2));
        }
        information.add(line);
    }

    private void setLabelStyle(JLabel label, String fontName) {
        try {
            label.setFont(Constants.getFont(fontName, 12));
        } catch (Exception e) {
            e.printStackTrace();
        }
        label.setForeground(Constants.COLOR_3);
    }

    public List<JPanel> getRequestPanels() {
        return requestPanels;
    }

    /**
     * Method called by observable instances when the textual view must be updated.
     * @param o observable instance which send the notification
     * @param arg notification data
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o.equals(tour)) {
            displayTextualRequests();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == requestsHeader) {
            cardLayout.previous(cardLayoutPanel);
        } else if (e.getSource() == tourHeader) {
            cardLayout.next(cardLayoutPanel);
        }
    }
}
