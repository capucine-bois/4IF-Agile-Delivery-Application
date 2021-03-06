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
    protected static final String GO_BACK_TO_TOUR = "Back to tour";
    protected static final String PATH_DETAILS = "Show path";
    protected static final String DELETE_REQUEST = "Delete request";
    protected static final String ADD_REQUEST = "Add request";
    protected static final String CANCEL = "Cancel";
    protected static final String CONTINUE_ADD_REQUEST = "Continue";
    protected static final String CHOOSE_ADDRESS = "Choose address";
    protected static final String CHANGE_TIME = "Change time";
    protected static final String CHANGE_ADDRESS = "Change address";
    protected static final String SAVE_TIME = "Save";

    protected static List<JPanel> requestPanels;
    protected static List<JPanel> tourIntersectionsPanels;
    protected static List<JButton> pathDetailsButtons;
    protected static List<JButton> deleteRequestButtons;
    protected static List<JButton> goUpButtons;
    protected static List<JButton> goDownButtons;
    protected static List<JButton> chooseAddressButtons;
    protected static List<JSpinner> timeFields;
    protected static JSpinner changeTimeField;

    private final Tour tour;
    private final int gap = 20;
    private CardLayout cardLayout;
    private final Window window;
    private JButton requestsHeader;
    private JButton tourHeader;
    private JButton addRequest;
    private final List<JButton> addRequestButtons;
    private final List<JButton> backgroundTourPanelButtons;
    private JPanel cardLayoutPanel;
    private JPanel requestsPanelWithAddButton;
    private JPanel requestsMainPanel;
    private JPanel tourMainPanel;
    private JPanel addRequestPanel;
    private boolean changeTimeMode = false;

    // Listeners
    private final MouseListener mouseListener;
    private final ButtonListener buttonListener;

    /**
     * Create a textual view in window
     * @param w the GUI
     */
    public TextualView(Tour tour, Window w, MouseListener mouseListener, ButtonListener buttonListener) throws IOException, FontFormatException {
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_4);
        setBorder(BorderFactory.createMatteBorder(10,10,10,0,Constants.COLOR_1));
        w.getContentPane().add(this, BorderLayout.LINE_START);
        tour.addObserver(this);
        this.tour = tour;
        requestPanels = new ArrayList<>();
        tourIntersectionsPanels = new ArrayList<>();
        pathDetailsButtons = new ArrayList<>();
        deleteRequestButtons = new ArrayList<>();
        goUpButtons = new ArrayList<>();
        goDownButtons = new ArrayList<>();
        chooseAddressButtons = new ArrayList<>();
        timeFields = new ArrayList<>();
        addRequestButtons = new ArrayList<>();
        backgroundTourPanelButtons = new ArrayList<>();
        this.mouseListener = mouseListener;
        this.buttonListener = buttonListener;
        this.window = w;
        createHeader();
        createCardLayout();
        createRequestsPanel();
        createTourPanel();
        createAddRequestPanel();
    }

    /**
     * Create textual view header with buttons to switch tab.
     * @throws IOException
     * @throws FontFormatException
     */
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

    /**
     * Create layout to differentiate two panels (one for tour, one for requests)
     */
    private void createCardLayout() {
        cardLayout = new CardLayout();
        cardLayoutPanel = new JPanel();
        cardLayoutPanel.setBackground(Constants.COLOR_4);
        cardLayoutPanel.setLayout(cardLayout);
        add(cardLayoutPanel);
    }

    /**
     * Create panel for "Requests" tab.
     */
    private void createRequestsPanel() {
        requestsPanelWithAddButton = new JPanel();
        requestsPanelWithAddButton.setLayout(new BorderLayout());
        requestsMainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(requestsMainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        requestsPanelWithAddButton.add(scrollPane);
        addRequest = new JButton(ADD_REQUEST);
        try {
            window.setStyle(addRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        addRequest.setBackground(Constants.COLOR_12);
        addRequest.addActionListener(buttonListener);
        cardLayoutPanel.add("requests", requestsPanelWithAddButton);
    }
    /**
     * Create panel for "Tour" tab.
     */
    private void createTourPanel() {
        tourMainPanel = new JPanel();
        JScrollPane scrollPane = new JScrollPane(tourMainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        cardLayoutPanel.add("tour", scrollPane);
    }

    private void createAddRequestPanel() {
        addRequestPanel = new JPanel();
        addRequestPanel.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(addRequestPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        cardLayoutPanel.add("addRequest", scrollPane);
    }

    /**
     * Add all the elements on the textual view.
     */
    private void displayTextualView() {
        requestsMainPanel.removeAll();
        tourMainPanel.removeAll();
        requestsPanelWithAddButton.remove(addRequest);
        addRequestPanel.removeAll();
        if (!tour.getPlanningRequests().isEmpty()) {
            displayRequests();
            if (!tour.getListShortestPaths().isEmpty()) {
                displayTourIntersections();
            }
        }
        if (tour.isTourComputed()) {
            requestsPanelWithAddButton.add(addRequest, BorderLayout.PAGE_END);
            displayAddRequest();
        }
        revalidate();
        repaint();
    }

    /**
     * Add all the request on the textual view, on "Requests" tab
     */
    private void displayRequests() {
        deleteRequestButtons.clear();
        requestPanels.clear();
        requestsMainPanel.setLayout(new BoxLayout(requestsMainPanel, BoxLayout.Y_AXIS));
        requestsMainPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        requestsMainPanel.setBackground(Constants.COLOR_4);
        displayDepotInformation(requestsMainPanel);
        requestsMainPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        displayRequestsInformation(requestsMainPanel);
    }

    /**
     * Display details of depot intersection on textual view, on "Tour" tab.
     * @param parentPanel parent panel
     */
    private void displayDepotInformation(JPanel parentPanel) {
        Map<String, String> depotInformation = new HashMap<>();
        String depotCoordinates = tour.getDepotAddress().getLatitude() + ", " + tour.getDepotAddress().getLongitude();
        depotInformation.put("Depot address", depotCoordinates);
        depotInformation.put("Departure time", tour.getDepartureTime());
        displayDepotPanel(parentPanel, depotInformation);
    }

    /**
     * Display information of all the requests on the textual view, on "Requests" tab
     * @param parentPanel parent panel
     */
    private void displayRequestsInformation(JPanel parentPanel) {
        for (Request request : tour.getPlanningRequests()) {
            Map<String, String> requestInformation = new HashMap<>();
            String pickupCoordinates = request.getPickupAddress().getLatitude() + ", " + request.getPickupAddress().getLongitude();
            String deliveryCoordinates = request.getDeliveryAddress().getLatitude() + ", " + request.getDeliveryAddress().getLongitude();
            requestInformation.put("Pickup address", pickupCoordinates);
            requestInformation.put("Pickup duration", request.getPickupDuration() / 60 + " min ");
            requestInformation.put("Delivery address", deliveryCoordinates);
            requestInformation.put("Delivery duration", request.getDeliveryDuration() / 60 + " min ");
            displayRequestPanel(parentPanel, requestInformation, request.getColor(), request.isPickupSelected() && request.isDeliverySelected());
            parentPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        }
    }

    /**
     * Add all the intersections that must be visited to accomplish the tour.
     */
    private void displayTourIntersections() {
        tourMainPanel.setBackground(Constants.COLOR_4);
        pathDetailsButtons.clear();
        tourIntersectionsPanels.clear();
        goDownButtons.clear();
        goUpButtons.clear();
        Optional<ShortestPath> optionalShortestPath = tour.getListShortestPaths().stream().filter(ShortestPath::isSelected).findFirst();
        if (optionalShortestPath.isPresent()) {
            tourMainPanel.setLayout(new BorderLayout());
            displaySegmentsHeader(tourMainPanel, optionalShortestPath.get());
            displaySegments(tourMainPanel, optionalShortestPath.get().getListSegments());
        } else {
            tourMainPanel.setLayout(new BoxLayout(tourMainPanel, BoxLayout.Y_AXIS));
            tourMainPanel.add(Box.createRigidArea(new Dimension(0, gap)));
            displayTourGlobalInformation();
            displayDepotPoint(tourMainPanel, tour.getDepartureTime(), true);
            for (int i=0; i<tour.getListShortestPaths().size(); i++) {
                displayShortestPath(tourMainPanel, tour.getListShortestPaths().get(i), false);
                displayPoint(tourMainPanel, tour.getListShortestPaths().get(i), false, i);
            }
            displayDepotPoint(tourMainPanel, tour.getArrivalTime(), false);
            tourMainPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        }
    }

    /**
     * Display header for "Tour" tab when the tour is computed.
     * Show speed, length, starting and ending time.
     */
    private void displayTourGlobalInformation() {
        JPanel tourFirstPanel = new JPanel();
        tourFirstPanel.setLayout(new BoxLayout(tourFirstPanel, BoxLayout.Y_AXIS));
        addLine(tourFirstPanel, "Total length", String.format("%.1f", tour.getTourLength() / (double) 1000) + " km", false, 14);
        addLine(tourFirstPanel, "Speed", String.format("%.1f", tour.getSpeed()) + " km/h", false, 14);
        addLine(tourFirstPanel, "Starting at", tour.getDepartureTime(), false, 14);
        addLine(tourFirstPanel, "Ending at", tour.getArrivalTime(), false, 14);
        addLine(tourFirstPanel, "Total duration", tour.getTotalDuration(), false, 14);

        tourFirstPanel.setMaximumSize(new Dimension(getPreferredSize().width, 130 + gap));
        tourMainPanel.add(tourFirstPanel);
        tourMainPanel.add(Box.createRigidArea(new Dimension(0, gap)));
    }

    /**
     * Display header of shortest path details
     * @param parentPanel parent panel
     * @param shortestPath the shortest path concerned
     */
    private void displaySegmentsHeader(JPanel parentPanel, ShortestPath shortestPath) {
        JPanel segmentsHeader = new JPanel();
        segmentsHeader.setLayout(new BorderLayout());
        JButton backToTour = new JButton(GO_BACK_TO_TOUR);
        try {
            window.setStyle(backToTour);
            backToTour.setBackground(Constants.COLOR_12);
        } catch (Exception e) {
            e.printStackTrace();
        }
        backToTour.addActionListener(buttonListener);
        segmentsHeader.add(backToTour, BorderLayout.PAGE_START);

        JPanel pathInformation = new JPanel();
        pathInformation.add(Box.createRigidArea(new Dimension(0, gap)));
        pathInformation.setLayout(new BoxLayout(pathInformation, BoxLayout.Y_AXIS));
        pathInformation.setBackground(Constants.COLOR_4);
        int indexShortestPath = tour.getListShortestPaths().indexOf(shortestPath);
        if (indexShortestPath == 0) {
            displayDepotPoint(pathInformation, tour.getDepartureTime(), true);
        } else {
            displayPoint(pathInformation, tour.getListShortestPaths().get(indexShortestPath - 1), true, -1);
        }
        displayShortestPath(pathInformation, shortestPath, true);
        displayPoint(pathInformation, shortestPath, true, -1);
        if (indexShortestPath == tour.getListShortestPaths().size() - 1) {
            displayDepotPoint(pathInformation, tour.getArrivalTime(), false);
        }
        pathInformation.add(Box.createRigidArea(new Dimension(0, gap)));
        segmentsHeader.add(pathInformation);

        parentPanel.add(segmentsHeader, BorderLayout.PAGE_START);
    }

    /**
     * Display depot intersection differently (comparing to others intersections) on the textual view, on "Tour" tab when
     * the tour is already computed.
     * @param parentPanel parent panel
     * @param time starting or ending time
     * @param firstPoint whether it is the first intersection of the tour
     */
    private void displayDepotPoint(JPanel parentPanel, String time, boolean firstPoint) {
        Map<String, String> depotInformation = new HashMap<>();
        depotInformation.put("Type", "Depot");
        if (firstPoint) {
            depotInformation.put("Departure time", time);
        } else {
            depotInformation.put("Arrival time", time);
        }
        displayDepotPanel(parentPanel, depotInformation);
    }

    /**
     * Display intersection on the textual view, on "Tour" tab when the tour is already computed.
     * @param parentPanel parent panel
     * @param shortestPath shortest path that leads to the concerned intersection
     * @param segmentDetails whether details must be displayed or not
     * @param index index of the intersection
     */
    private void displayPoint(JPanel parentPanel, ShortestPath shortestPath, boolean segmentDetails, int index) {
        Map<String, String> pointsInformation = new HashMap<>();
        if (shortestPath.getEndNodeNumber() != 0) {
            boolean endAddressIsPickup = shortestPath.getEndNodeNumber() % 2 == 1;
            pointsInformation.put("Type", endAddressIsPickup ? "Pickup" : "Delivery");

            int requestIndex = endAddressIsPickup ? shortestPath.getEndNodeNumber() / 2 : shortestPath.getEndNodeNumber() / 2 - 1;
            Request request = tour.getPlanningRequests().get(requestIndex);
            boolean pointSelected;
            if (endAddressIsPickup) {
                pointsInformation.put("Arrival time", request.getPickupArrivalTime());
                pointsInformation.put("Process time", request.getPickupDuration() / 60 + " min");
                pointsInformation.put("Departure time", request.getPickupDepartureTime());
                pointSelected = request.isPickupSelected();
            } else {
                pointsInformation.put("Arrival time", request.getDeliveryArrivalTime());
                pointsInformation.put("Process time", request.getDeliveryDuration() / 60 + " min");
                pointsInformation.put("Departure time", request.getDeliveryDepartureTime());
                pointSelected = request.isDeliverySelected();
            }
            displayTourIntersectionPanel(parentPanel, pointsInformation, request.getColor(), pointSelected && !segmentDetails, segmentDetails, index);
        }
    }

    /**
     * Display path information between two intersections on the textual view, on the "Tour" tab for an already computed tour.
     * @param parentPanel parent panel
     * @param shortestPath the shortest path to display
     * @param segmentDetails whether details must be displayed or not
     */
    private void displayShortestPath(JPanel parentPanel, ShortestPath shortestPath, boolean segmentDetails) {
        JPanel pathPanel = new JPanel();
        pathPanel.setLayout(new BorderLayout());
        pathPanel.setBorder(BorderFactory.createMatteBorder(0,0,0,10, Constants.COLOR_4));

        JLabel arrowDown = new JLabel();
        try {
            arrowDown.setIcon(new ImageIcon(Constants.getImage("arrow-down").getScaledInstance(30, 30, Image.SCALE_DEFAULT)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        pathPanel.add(arrowDown, BorderLayout.LINE_START);

        addLine(pathPanel,"Length", String.format("%.1f",shortestPath.getPathLength() / (double) 1000) + " km", false, 12);

        if (!segmentDetails && tour.isTourComputed()) {
            JButton pathDetail = new JButton(PATH_DETAILS);
            try {
                window.setStyle(pathDetail);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pathDetail.setPreferredSize(new Dimension(pathDetail.getPreferredSize().width, 30));
            pathDetailsButtons.add(pathDetail);
            backgroundTourPanelButtons.add(pathDetail);
            pathDetail.addActionListener(buttonListener);
            pathPanel.add(pathDetail, BorderLayout.LINE_END);
        }

        pathPanel.setMaximumSize(new Dimension(getPreferredSize().width, 30));
        parentPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        parentPanel.add(pathPanel);
        parentPanel.add(Box.createRigidArea(new Dimension(0, gap)));
    }

    /**
     * Display segments (road name, length in meters) on textual view, on the details of a shortest path.
     * @param parentPanel parent panel
     * @param segments segments to display
     */
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
        segmentsPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        parentPanel.add(segmentsPanel);
    }

    private void displayAddRequest() {
        chooseAddressButtons.clear();
        timeFields.clear();
        JPanel addRequestContentPanel = new JPanel();
        addRequestContentPanel.setLayout(new BorderLayout());
        JPanel pickupAndDelivery = new JPanel();
        pickupAndDelivery.setLayout(new BoxLayout(pickupAndDelivery, BoxLayout.Y_AXIS));
        pickupAndDelivery.setBackground(Constants.COLOR_4);
        displayAddRequestPoint(pickupAndDelivery, "Pickup");
        displayAddRequestPoint(pickupAndDelivery, "Delivery");
        addRequestContentPanel.add(pickupAndDelivery);
        if (tour.getNewRequest() != null) {
            displayColorPanel(tour.getNewRequest().getColor(), addRequestContentPanel);
        }
        addRequestPanel.add(addRequestContentPanel);

        JPanel buttonsAddRequestPanel = new JPanel();
        buttonsAddRequestPanel.setLayout(new GridLayout(1,2));
        JButton cancelAddRequest = displayColoredButton(CANCEL, Constants.COLOR_2);
        addRequestButtons.add(cancelAddRequest);
        buttonsAddRequestPanel.add(cancelAddRequest);
        JButton validateAddRequest = displayColoredButton(CONTINUE_ADD_REQUEST, Constants.COLOR_12);
        addRequestButtons.add(validateAddRequest);
        buttonsAddRequestPanel.add(validateAddRequest);
        addRequestPanel.add(buttonsAddRequestPanel, BorderLayout.PAGE_END);
    }

    private void displayAddRequestPoint(JPanel parentPanel, String pointType) {
        JPanel addRequestPointPanel = new JPanel();
        addRequestPointPanel.setLayout(new BoxLayout(addRequestPointPanel, BoxLayout.Y_AXIS));

        JPanel chooseAddressPanel = new JPanel();
        chooseAddressPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        chooseAddressPanel.setBackground(Constants.COLOR_4);
        addLine(chooseAddressPanel, pointType, "", false, 14);
        JButton chooseAddress = new JButton(CHOOSE_ADDRESS);
        try {
            window.setStyle(chooseAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        chooseAddress.addActionListener(buttonListener);
        chooseAddressPanel.add(chooseAddress);
        chooseAddressButtons.add(chooseAddress);
        addRequestButtons.add(chooseAddress);
        addRequestPointPanel.add(chooseAddressPanel);

        JPanel addressPanel = new JPanel();
        addressPanel.setLayout((new FlowLayout(FlowLayout.LEFT)));
        addressPanel.setBackground(Constants.COLOR_4);
        String addressText = "No address selected";
        if (tour.getNewRequest() != null) {
            if (pointType.equals("Pickup") && tour.getNewRequest().getPickupAddress() != null) {
                addressText = tour.getNewRequest().getPickupAddress().getLatitude() + ", " + tour.getNewRequest().getPickupAddress().getLongitude();
            } else if (pointType.equals("Delivery") && tour.getNewRequest().getDeliveryAddress() != null) {
                addressText = tour.getNewRequest().getDeliveryAddress().getLatitude() + ", " + tour.getNewRequest().getDeliveryAddress().getLongitude();
            }
        }
        addLine(addressPanel, "Address", addressText, false, 12);
        addRequestPointPanel.add(addressPanel);

        JPanel chooseTimePanel = new JPanel();
        chooseTimePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        chooseTimePanel.setBackground(Constants.COLOR_4);
        addLine(chooseTimePanel, "Process time (minutes)", "", false, 12);
        JSpinner timeField = new JSpinner();
        timeField.setPreferredSize(new Dimension(60, 30));
        timeFields.add(timeField);
        chooseTimePanel.add(timeField);
        addRequestPointPanel.add(chooseTimePanel);

        addRequestPointPanel.setMaximumSize(new Dimension(getMaximumSize().width, 126));

        parentPanel.add(Box.createRigidArea(new Dimension(0, gap)));
        parentPanel.add(addRequestPointPanel);
        parentPanel.add(Box.createRigidArea(new Dimension(0, gap)));
    }

    private void displayDepotPanel(JPanel parentPanel, Map<String, String> informations) {
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new BorderLayout());

        displayColorPanel(Color.black, informationPanel);

        JPanel contentPanel = createContentPanel(informations, false);
        informationPanel.add(contentPanel);

        informationPanel.setMaximumSize(new Dimension(getPreferredSize().width, informations.size()*16 + gap));
        parentPanel.add(informationPanel);
    }

    private void displayRequestPanel(JPanel parentPanel, Map<String, String> informations, Color color, boolean selected) {
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new BorderLayout());
        displayColorPanel(color, informationPanel);
        JPanel contentWithButtonPanel = createContentWithButtonPanel(informations, selected, requestPanels, true);
        int maxHeight = informations.size()*21 + gap;
        if (!tour.getListShortestPaths().isEmpty() && tour.isTourComputed()) {
            maxHeight += 40;
            displayDeleteButton(contentWithButtonPanel, selected);
        }
        informationPanel.add(contentWithButtonPanel);
        informationPanel.setMaximumSize(new Dimension(getPreferredSize().width, maxHeight));
        parentPanel.add(informationPanel);
    }

    private void displayDeleteButton(JPanel contentWithButtonPanel, boolean selected) {
        JButton deleteRequest = new JButton(DELETE_REQUEST);
        try {
            window.setStyle(deleteRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (selected) deleteRequest.setBackground(Constants.COLOR_12);
        deleteRequestButtons.add(deleteRequest);
        deleteRequest.addActionListener(buttonListener);
        contentWithButtonPanel.add(deleteRequest, BorderLayout.PAGE_END);
    }

    private void displayTourIntersectionPanel(JPanel parentPanel, Map<String, String> informations, Color color, boolean selected, boolean segmentDetails, int index) {
        JPanel informationPanel = new JPanel();
        informationPanel.setLayout(new BorderLayout());
        displayColorPanel(color, informationPanel);
        JPanel contentWithButtonPanel = createContentWithButtonPanel(informations, selected, tourIntersectionsPanels, !segmentDetails);
        int maxHeight = informations.size()*21 + gap;
        if (!segmentDetails && tour.isTourComputed()) {
            displayMoveButtonsPanel(index, contentWithButtonPanel, selected);
            if (selected) {
                if (changeTimeMode) {
                    displayChangeTimePanel(contentWithButtonPanel);
                    maxHeight += 40;
                } else {
                    displayUpdateButtonsPanel(contentWithButtonPanel);
                }
                maxHeight += 40;
            }
        }
        informationPanel.add(contentWithButtonPanel);
        informationPanel.setMaximumSize(new Dimension(getPreferredSize().width, maxHeight));
        parentPanel.add(informationPanel);
    }

    private JPanel createContentWithButtonPanel(Map<String, String> informations, boolean selected, List<JPanel> listPanels, boolean showCursor) {
        JPanel contentWithButtonPanel = new JPanel();
        contentWithButtonPanel.setLayout(new BorderLayout());
        JPanel contentPanel = createContentPanel(informations, selected);
        listPanels.add(contentPanel);
        contentPanel.addMouseListener(mouseListener);
        if (showCursor) {
            contentPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        contentWithButtonPanel.add(contentPanel);
        return contentWithButtonPanel;
    }

    private JPanel createContentPanel(Map<String, String> informations, boolean selected) {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        for (Map.Entry<String, String> information : informations.entrySet()) {
            addLine(contentPanel, information.getKey(), information.getValue(), selected, 12);
        }
        return contentPanel;
    }

    private void displayMoveButtonsPanel(int index, JPanel pointPanel, boolean selected) {
        JPanel moveButtonsPanel = new JPanel();
        moveButtonsPanel.setLayout(new GridLayout(2,1, 0, 20));
        if (selected) {
            moveButtonsPanel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Constants.COLOR_2));
            moveButtonsPanel.setBackground(Constants.COLOR_2);
        } else {
            moveButtonsPanel.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, Constants.COLOR_4));
            moveButtonsPanel.setBackground(Constants.COLOR_4);
        }
        // check if intersection is first
        if (index != 0) {
            displayMoveButton(moveButtonsPanel, "move-up", goUpButtons, selected);
        } else {
            moveButtonsPanel.add(new JLabel());
        }

        // check if intersection is last
        if (index < tour.getListShortestPaths().size() - 2) {
            displayMoveButton(moveButtonsPanel, "move-down", goDownButtons, selected);
        }

        pointPanel.add(moveButtonsPanel, BorderLayout.LINE_END);
    }

    private void displayMoveButton(JPanel moveButtonsPanel, String iconName, List<JButton> listButtons, boolean selected) {
        JButton moveButton = new JButton();
        try {
            moveButton.setIcon(new ImageIcon(Constants.getImage(iconName)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        moveButton.setFocusable(false);
        moveButton.setFocusPainted(false);
        moveButton.setBorder(BorderFactory.createEmptyBorder());
        if (selected) moveButton.setBackground(Constants.COLOR_12);
        else moveButton.setBackground(Constants.COLOR_2);
        listButtons.add(moveButton);
        backgroundTourPanelButtons.add(moveButton);
        moveButton.addActionListener(buttonListener);
        moveButtonsPanel.add(moveButton);
    }

    private void displayChangeTimePanel(JPanel parentPanel) {
        JPanel changeTimePanel = new JPanel();
        changeTimePanel.setLayout(new GridLayout(2,1));
        changeTimePanel.setBackground(Constants.COLOR_2);

        JPanel chooseTimePanel = new JPanel();
        chooseTimePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        chooseTimePanel.setBackground(Constants.COLOR_2);
        addLine(chooseTimePanel, "New process time (minutes)", "", true, 12);
        changeTimeField = new JSpinner();
        changeTimeField.setPreferredSize(new Dimension(40, 30));
        chooseTimePanel.add(changeTimeField);
        changeTimePanel.add(chooseTimePanel);

        JPanel changeTimeButtons = new JPanel();
        changeTimeButtons.setLayout(new GridLayout(1,2));
        changeTimeButtons.setBackground(Constants.COLOR_2);

        JButton cancelButton = displayColoredButton(CANCEL, Constants.COLOR_2);
        changeTimeButtons.add(cancelButton);

        JButton continueButton = displayColoredButton(SAVE_TIME, Constants.COLOR_12);
        changeTimeButtons.add(continueButton);

        changeTimePanel.add(changeTimeButtons);

        parentPanel.add(changeTimePanel, BorderLayout.PAGE_END);
    }

    private void displayUpdateButtonsPanel(JPanel parentPanel) {
        JPanel updateButtonsPanel = new JPanel();
        updateButtonsPanel.setLayout(new GridLayout(1,2, 5, 0));
        updateButtonsPanel.setBackground(Constants.COLOR_2);

        JButton changeTimeButton = displayColoredButton(CHANGE_TIME, Constants.COLOR_12);
        updateButtonsPanel.add(changeTimeButton);
        backgroundTourPanelButtons.add(changeTimeButton);

        JButton changeAddressButton = displayColoredButton(CHANGE_ADDRESS, Constants.COLOR_12);
        updateButtonsPanel.add(changeAddressButton);
        backgroundTourPanelButtons.add(changeAddressButton);

        parentPanel.add(updateButtonsPanel, BorderLayout.PAGE_END);
    }

    private JButton displayColoredButton(String buttonName, Color color) {
        JButton button = new JButton(buttonName);
        try {
            window.setStyle(button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        button.setBackground(color);
        button.addActionListener(buttonListener);
        return button;
    }

    private void displayColorPanel(Color color, JPanel informationPanel) {
        JLabel colorInformation = new JLabel();
        colorInformation.setBackground(color);
        colorInformation.setOpaque(true);
        colorInformation.setPreferredSize(new Dimension(10, informationPanel.getPreferredSize().height));
        informationPanel.add(colorInformation, BorderLayout.LINE_START);
    }

    /**
     * Print text in a new line.
     * @param information parent panel
     * @param fieldName name of the field
     * @param fieldContent value of the field
     * @param selected whether the line is selected or not
     * @param fontSize font size
     */
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

    /**
     * Set font
     * @param label label concerned
     * @param fontName name of the new font
     * @param fontSize font size
     */
    private void setLabelStyle(JLabel label, String fontName, int fontSize) {
        try {
            label.setFont(Constants.getFont(fontName, fontSize));
        } catch (Exception e) {
            e.printStackTrace();
        }
        label.setForeground(Constants.COLOR_3);
    }

    /**
     * Method called by observable instances when the textual view must be updated.
     * @param o observable instance which send the notification
     *
     */
    @Override
    public void update(Observable o) {
        if (o.equals(tour)) {
            displayTextualView();
        }
    }

    /**
     * Switch buttons designs (background and borders) for "Tour" and "Requests"
     * @param previousButton previous button
     * @param nextButton clicked button
     */
    private void changeButton(JButton previousButton, JButton nextButton) {
        nextButton.setBorder(BorderFactory.createMatteBorder(0,0,5,0,Constants.COLOR_3));
        nextButton.setBackground(Constants.COLOR_4);
        previousButton.setBorder(BorderFactory.createEmptyBorder());
        previousButton.setBackground(Constants.COLOR_2);
    }

    /**
     * Switch to requests panel.
     */
    public void showRequestsPanel() {
        changeButton(tourHeader, requestsHeader);
        cardLayout.show(cardLayoutPanel, "requests");
    }

    /**
     * Switch to tour panel.
     */
    public void showTourPanel() {
        changeButton(requestsHeader, tourHeader);
        cardLayout.show(cardLayoutPanel, "tour");
    }

    public void showAddRequestPanel() {
        cardLayout.show(cardLayoutPanel, "addRequest");
    }

    /**
     * Disable or enable requests panels.
     * @param enabled whether panels must be enabled or not.
     */
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

    /**
     * Disable or enable tour panels.
     * @param enabled whether panels must be enabled or not.
     */
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

    /**
     * Change background color of a request when mouse enters its panel.
     * @param indexPanel index of the panel concerned
     */
    public void colorRequestPanelOnMouseEntered(int indexPanel) {
        for (Component childComponent : requestPanels.get(indexPanel).getComponents()) {
            JPanel childPanel = (JPanel) childComponent;
            childPanel.setBackground(Constants.COLOR_2);
            childPanel.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_2));
        }
    }

    /**
     * Change background color of an intersection when mouse enters its panel.
     * @param indexPanel index of the intersection concerned
     */
    public void colorTourIntersectionPanelOnMouseEntered(int indexPanel) {
        for (Component childComponent : tourIntersectionsPanels.get(indexPanel).getComponents()) {
            JPanel childPanel = (JPanel) childComponent;
            childPanel.setBackground(Constants.COLOR_2);
            childPanel.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_2));
        }
    }

    /**
     * Change background color of a request when mouse leaves its panel.
     * @param indexPanel index of the panel concerned
     */
    public void colorRequestPanelOnMouseExited(int indexPanel) {
        for (Component childComponent : requestPanels.get(indexPanel).getComponents()) {
            JPanel childPanel = (JPanel) childComponent;
            childPanel.setBackground(Constants.COLOR_4);
            childPanel.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_4));
        }
    }

    /**
     * Change background color of an intersection when mouse leaves its panel.
     * @param indexPanel index of the panel concerned
     */
    public void colorTourIntersectionPanelOnMouseExited(int indexPanel) {
        for (Component childComponent : tourIntersectionsPanels.get(indexPanel).getComponents()) {
            JPanel childPanel = (JPanel) childComponent;
            childPanel.setBackground(Constants.COLOR_4);
            childPanel.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Constants.COLOR_4));
        }
    }

    public void setEnabledAddRequestButtons(boolean enabled) {
        for (JButton button : addRequestButtons) {
            button.setEnabled(enabled);
        }
        for (JButton button : backgroundTourPanelButtons) {
            button.setEnabled(enabled);
        }
        setEnabledHeaderButtons(enabled);
    }

    public void setChangeTimeMode(boolean changeTimeMode) {
        this.changeTimeMode = changeTimeMode;
        displayTextualView();
        for (JButton button : backgroundTourPanelButtons) {
            button.setEnabled(!changeTimeMode);
        }
        setEnabledHeaderButtons(!changeTimeMode);
    }

    private void setEnabledHeaderButtons(boolean enable) {
        requestsHeader.setEnabled(enable);
        tourHeader.setEnabled(enable);
    }
}
