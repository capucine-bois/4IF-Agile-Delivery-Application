package view;

import controller.Controller;
import model.CityMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for the application.
 */
public class Window extends JFrame implements ComponentListener {

    // Titles of window buttons
    protected final static String LOAD_MAP = "Load a map";
    protected static final String LOAD_REQUEST = "Load a request planning";
    protected static final String COMPUTE_TOUR = "Compute the tour";
    private ArrayList<JButton> buttons;
    private JPanel header;
    private GraphicalView graphicalView;
    private TextualView textualView;

    // Listeners
    private ButtonListener buttonListener;
    private MouseListener mouseListener;
    private KeyboardListener keyboardListener;

    // Window size
    private int windowWidth = 800;
    private int windowHeight = 550;

    private final String[] buttonTexts = new String[]{LOAD_MAP, LOAD_REQUEST, COMPUTE_TOUR};

    /**
     * Complete constructor
     * @param cityMap the map to show
     * @param controller instance of application controller
     * @throws IOException raised if GUI listeners fail
     * @throws FontFormatException raised if text font can't be loaded
     */
    public Window(CityMap cityMap, Controller controller) throws IOException, FontFormatException {
        //TODO: Add map when implemented
        //TODO: Add controller when implemented
        createHeader(controller);
        //TODO: Add intermediate JPanel before graphicalView and textualView
        graphicalView = new GraphicalView(cityMap, this);
        textualView = new TextualView(this);
        //TODO: Add mouse listener
        //TODO: Add keyboard listener
        setMinimumSize(new Dimension(windowWidth, windowHeight));
        setWindowSize();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addComponentListener(this);
    }

    /**
     * Create header in GUI window with buttons for map loading, request loading, and computing tour.
     * @param controller application controller
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    private void createHeader(Controller controller) throws IOException, FontFormatException {
        header = new JPanel();
        FlowLayout headerLayout = new FlowLayout(FlowLayout.LEFT);
        header.setLayout(headerLayout);
        header.setBackground(Constants.COLOR_1);
        addAppName();
        buttonListener = new ButtonListener(controller);
        buttons = new ArrayList<>();
        for (String text : buttonTexts){
            JButton button = new JButton(text);
            buttons.add(button);
            setStyle(button);
            button.addActionListener(buttonListener);
            header.add(button);
        }
        getContentPane().add(header, BorderLayout.PAGE_START);
    }

    /**
     *
     * @throws IOException
     * @throws FontFormatException
     */
    private void addAppName() throws IOException, FontFormatException {
        JLabel appNameLabel = new JLabel("Application Name");
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
    private void setStyle(JButton button) throws IOException, FontFormatException {
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
    private void setWindowSize() {
        int headerHeight = 50;
        int textualViewWidth = 300;
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        header.setPreferredSize(new Dimension(windowWidth, headerHeight));
        textualView.setPreferredSize(new Dimension(textualViewWidth, windowHeight - headerHeight));
        graphicalView.setPreferredSize(new Dimension(windowWidth - textualViewWidth, windowHeight - headerHeight));
    }

    /**
     * Event triggered when the window is resized.
     * @param e event information
     */
    @Override
    public void componentResized(ComponentEvent e) {
        windowWidth = e.getComponent().getSize().width;
        windowHeight = e.getComponent().getSize().height;
        setWindowSize();
    }

    /**
     * Event triggered when the window is moved.
     * @param e event information
     */
    @Override
    public void componentMoved(ComponentEvent e) {

    }

    /**
     * Event triggers when the window shows up.
     * @param e event information
     */
    @Override
    public void componentShown(ComponentEvent e) {

    }

    /**
     * Event triggered when the window turns hidden.
     * @param e event information
     */
    @Override
    public void componentHidden(ComponentEvent e) {

    }
}
