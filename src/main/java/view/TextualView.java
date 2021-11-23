package view;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import controller.Controller;
import model.Request;
import model.Tour;
import observer.Observable;
import observer.Observer;

/**
 * Textual element on the GUI.
 * Used to display requests.
 */
public class TextualView extends JPanel implements Observer {

    private Tour tour;
    private final int gap = 20;
    private final int colorWidth = 10;
    private final int border = 10;
    private MouseListener mouseListener;
    private List<JPanel> requestPanels;

    /**
     * Create a textual view in window
     * @param w the GUI
     */
    public TextualView(Tour tour, Window w, Controller controller){
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_4);
        setBorder(BorderFactory.createMatteBorder(border,border,border,0,Constants.COLOR_1));
        w.getContentPane().add(this, BorderLayout.LINE_START);
        tour.addObserver(this);
        this.tour = tour;
        requestPanels = new ArrayList<>();
        mouseListener = new MouseListener(controller, this);
    }

    private void displayTextualRequests() {
        removeAll();
        if (!tour.getPlanningRequests().isEmpty()) {
            createScrollPane();
        }
        revalidate();
    }

    private void createScrollPane() {
        JPanel mainPanel = createMainPanel();
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(null);
        add(scrollPane);
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createMatteBorder(gap,0,0,0,Constants.COLOR_4));
        mainPanel.setBackground(Constants.COLOR_4);
        displayDepotInformation(mainPanel);
        displayRequestsInformation(mainPanel);
        return mainPanel;
    }

    private void displayDepotInformation(JPanel mainPanel) {
        Map<String, String> depotInformation = new HashMap<>();
        String depotCoordinates = tour.getDepotAddress().getLatitude() + ", " + tour.getDepotAddress().getLongitude();
        depotInformation.put("Depot address", depotCoordinates);
        depotInformation.put("Departure time", tour.getDepartureTime());
        displayInformation(mainPanel, depotInformation, Color.black, null);
    }

    private void displayRequestsInformation(JPanel mainPanel) {
        float[] hsv = new float[3];
        Color initialColor = Color.red;
        Color.RGBtoHSB(initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue(), hsv);
        double goldenRatioConjugate = 0.618033988749895;
        requestPanels.clear();
        for (Request request : tour.getPlanningRequests()) {
            Color requestColor = Color.getHSBColor(hsv[0], hsv[1], hsv[2]);
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
            displayInformation(mainPanel, requestInformation, requestColor, request);
            hsv[0] += goldenRatioConjugate;
            hsv[0] %= 1;
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
}
