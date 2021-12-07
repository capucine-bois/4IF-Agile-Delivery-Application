package view;

import controller.Controller;
import model.CityMap;
import model.Tour;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * GUI for the application.
 */
public class Window extends JFrame {

    // Titles of window buttons
    protected final static String LOAD_MAP = "Load a map";
    protected static final String LOAD_REQUEST = "Load a request planning";
    protected static final String COMPUTE_TOUR = "Compute the tour";
    protected static final String STOP_COMPUTATION = "Stop the computation";
    protected static final String CANCEL_SELECTION = "Cancel";
    protected static final String UNDO = "Undo";
    protected static final String REDO = "Redo";


    private ArrayList<JButton> buttons;
    private JPanel header;
    private JPanel graphicalPanel;
    private JPanel popUpGraphicalView;
    private JLabel popUpGraphicalViewMessage;
    private JButton popUpGraphicalViewButton;
    private final GraphicalView graphicalView;
    private final TextualView textualView;
    private final PopUpView popUpView;

    // Listeners
    private final ButtonListener buttonListener;

    private final String[] buttonTexts = new String[]{LOAD_MAP, LOAD_REQUEST, COMPUTE_TOUR, UNDO, REDO};
    private boolean[] defaultButtonStates = new boolean[]{true, false, false, false, false};

    /**
     * Complete constructor
     * @param cityMap the map to show
     * @param tour the tour
     * @param controller instance of application controller
     * @throws IOException raised if GUI listeners fail
     * @throws FontFormatException raised if text font can't be loaded
     */
    public Window(CityMap cityMap, Tour tour, Controller controller) throws IOException, FontFormatException {
        MouseListener mouseListener = new MouseListener(controller);
        buttonListener = new ButtonListener(controller);
        graphicalView = new GraphicalView(cityMap, tour, mouseListener);
        textualView = new TextualView(tour, this, mouseListener, buttonListener);
        popUpView = new PopUpView(this, mouseListener, buttonListener);
        KeyboardListener keyboardListener = new KeyboardListener(popUpView, controller);
        addKeyListener(keyboardListener);
        createHeader();
        createGraphicalView();
        int minimumWindowWidth = 800;
        int minimumWindowHeight = 550;
        setMinimumSize(new Dimension(minimumWindowWidth, minimumWindowHeight));
        setWindowSize(minimumWindowWidth, minimumWindowHeight);
        setTitle("Deliver' IF");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     * Initiate graphical view.
     * @throws IOException
     * @throws FontFormatException
     */
    private void createGraphicalView() throws IOException, FontFormatException {
        graphicalPanel = new JPanel();
        graphicalPanel.setLayout(new BorderLayout());

        popUpGraphicalView = new JPanel();
        popUpGraphicalView.setLayout(new BorderLayout());
        popUpGraphicalView.setBackground(Constants.COLOR_4);
        popUpGraphicalView.setBorder(BorderFactory.createMatteBorder(10, 10, 0, 10, Constants.COLOR_1));

        popUpGraphicalViewMessage = new JLabel();
        popUpGraphicalViewMessage.setFont(Constants.getFont("DMSans-Medium.ttf", 12));
        popUpGraphicalViewMessage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        popUpGraphicalViewMessage.setForeground(Constants.COLOR_3);
        popUpGraphicalView.add(popUpGraphicalViewMessage);
        popUpGraphicalViewButton = new JButton();
        setStyle(popUpGraphicalViewButton);
        popUpGraphicalViewButton.addActionListener(buttonListener);
        popUpGraphicalView.add(popUpGraphicalViewButton, BorderLayout.LINE_END);

        graphicalPanel.add(graphicalView);
        getContentPane().add(graphicalPanel);
    }

    private void setPopUpGraphicalViewForComputing() {
        popUpGraphicalViewMessage.setText("<html><p>Computation of best tour in progress...<br/>You can stop the computation and continue with the best tour currently found.</p></html>");
        popUpGraphicalViewButton.setText(STOP_COMPUTATION);
        popUpGraphicalViewButton.setPreferredSize(new Dimension(160, popUpGraphicalViewButton.getPreferredSize().height));
    }

    private void setPopUpGraphicalViewForSelection() {
        popUpGraphicalViewMessage.setText("<html><p>Choose an address by clicking on a red point on the map.</p></html>");
        popUpGraphicalViewButton.setText(CANCEL_SELECTION);
        popUpGraphicalViewButton.setPreferredSize(new Dimension(80, popUpGraphicalViewButton.getPreferredSize().height));
    }

    /**
     * Create header in GUI window with buttons for map loading, request loading, and computing tour.
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    private void createHeader() throws IOException, FontFormatException {
        header = new JPanel();
        FlowLayout headerLayout = new FlowLayout(FlowLayout.LEFT);
        header.setLayout(headerLayout);
        header.setBackground(Constants.COLOR_1);
        addAppName();
        buttons = new ArrayList<>();
        for (String text : buttonTexts){
            JButton button = new JButton(text);
            buttons.add(button);
            setStyle(button);
            button.addActionListener(buttonListener);
            header.add(button);
        }
        getContentPane().add(header, BorderLayout.PAGE_START);
        resetComponentsState();
    }

    /**
     * Set the application name to display in the top left corner
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    private void addAppName() throws IOException, FontFormatException {
        JLabel appNameLabel = new JLabel("Deliver' IF");
        appNameLabel.setForeground(Constants.COLOR_3);
        appNameLabel.setFont(Constants.getFont("DMSans-Bold.ttf", 14));
        appNameLabel.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));
        header.add(appNameLabel);
    }

    /**
     * Set style (font, borders, colors...) to given button
     * @param button the button to apply style
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    public void setStyle(JButton button) throws IOException, FontFormatException {
        int buttonHeight = 40;
        button.setPreferredSize(new Dimension(button.getPreferredSize().width, buttonHeight));
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setBackground(Constants.COLOR_2);
        button.setForeground(Constants.COLOR_3);
        button.setFont(Constants.getFont("DMSans-Medium.ttf", 12));
        button.setFocusable(false);
        button.setFocusPainted(false);
    }

    /**
     * Set window size using windowWidth and windowHeight attributes.
     */
    public void setWindowSize(int windowWidth, int windowHeight) {
        int headerHeight = 50;
        int textualViewWidth = 320;
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        header.setPreferredSize(new Dimension(windowWidth, headerHeight));
        textualView.setPreferredSize(new Dimension(textualViewWidth, windowHeight - headerHeight));
        graphicalView.setPreferredSize(new Dimension(windowWidth - textualViewWidth, windowHeight - headerHeight));
    }

    /**
     * Displays an error message on the window.
     * Disable other buttons (load XML files and compute a tour).
     * @param message error message
     */
    public void displayErrorMessage(String message) {
        popUpView.showError(message);
    }

    /**
     * Reset buttons state corresponding to current state.
     */
    public void resetComponentsState() {
        for (int i=0; i<buttons.size(); i++) {
            if (i < defaultButtonStates.length) {
                buttons.get(i).setEnabled(defaultButtonStates[i]);
            }
        }
    }

    /**
     * Setter for defaultButtonStates attribute.
     * @param defaultButtonStates wanted value for defaultButtonStates attribute.
     */
    public void setDefaultButtonStates(boolean[] defaultButtonStates) {
        this.defaultButtonStates = defaultButtonStates;
        resetComponentsState();
    }

    /**
     * Show requests panel on textual view.
     */
    public void showRequestsPanel() {
        textualView.showRequestsPanel();
    }

    /**
     * Show tour panel on textual view.
     */
    public void showTourPanel() {
        textualView.showTourPanel();
    }

    public void showAddRequestPanel() {
        textualView.showAddRequestPanel();
    }

    /**
     * Disable or enable requests panels.
     * @param enabled whether panels must be enabled or not.
     */
    public void setEnabledRequests(boolean enabled) {
        textualView.setEnabledRequests(enabled);
    }

    /**
     * Disable or enable tour panels.
     * @param enabled whether panels must be enabled or not.
     */
    public void setEnabledTour(boolean enabled) {
        textualView.setEnabledTour(enabled);
    }

    /**
     * Change background color of a request when mouse enters its panel.
     * @param indexRequest index of the panel concerned
     */
    public void colorRequestPanelOnMouseEntered(int indexRequest) {
        textualView.colorRequestPanelOnMouseEntered(indexRequest);
    }

    /**
     * Change background color of a request when mouse leaves its panel.
     * @param indexRequest index of the panel concerned
     */
    public void colorRequestPanelOnMouseExited(int indexRequest) {
        textualView.colorRequestPanelOnMouseExited(indexRequest);
    }

    /**
     * Change background color of an intersection when mouse enters its panel.
     * @param indexIntersection index of the panel concerned
     */
    public void colorTourIntersectionPanelOnMouseEntered(int indexIntersection) {
        textualView.colorTourIntersectionPanelOnMouseEntered(indexIntersection);
    }

    /**
     * Change background color of an intersection when mouse leaves its panel.
     * @param indexIntersection index of the panel concerned
     */
    public void colorTourIntersectionPanelOnMouseExited(int indexIntersection) {
        textualView.colorTourIntersectionPanelOnMouseExited(indexIntersection);
    }

    /**
     * Change state of the "Undo" button
     * @param state new state
     */
    public void setUndoButtonState(boolean state) {
        buttons.get(buttons.size()-2).setEnabled(state);
    }

    /**
     * Change state of the "Redo" button
     * @param state new state
     */
    public void setRedoButtonState(boolean state) { buttons.get(buttons.size()-1).setEnabled(state);}

    public void showComputingPanel() {
        setPopUpGraphicalViewForComputing();
        graphicalPanel.add(popUpGraphicalView, BorderLayout.PAGE_START);
        revalidate();
    }

    public void hideComputingPanel() {
        graphicalPanel.remove(popUpGraphicalView);
        revalidate();
    }

    public void enterSelectionMode() {
        setPopUpGraphicalViewForSelection();
        graphicalPanel.add(popUpGraphicalView, BorderLayout.PAGE_START);
        revalidate();
        graphicalView.enterSelectionMode();
        textualView.setEnabledAddRequestButtons(false);
    }

    public void exitSelectionMode() {
        graphicalPanel.remove(popUpGraphicalView);
        revalidate();
        graphicalView.exitSelectionMode();
        textualView.setEnabledAddRequestButtons(true);
    }

    public void enterChangeTimeMode() {
        textualView.setChangeTimeMode(true);
    }

    public void exitChangeTimeMode() {
        textualView.setChangeTimeMode(false);
        requestFocusInWindow();
    }

}
