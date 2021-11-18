package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Window extends JFrame implements ComponentListener {

    // Titles of window buttons
    protected final static String LOAD_MAP = "Load a map";
    protected static final String LOAD_REQUEST = "Load a request planning";
    protected static final String COMPUTE_TOUR = "Compute the tour";
    private ArrayList<JButton> buttons;
    private JPanel header;
    private GraphicalView graphicalView;
    private TextualView textualView;
    private ButtonListener buttonListener;
    private MouseListener mouseListener;
    private KeyboardListener keyboardListener;
    private int windowWidth = 800;
    private int windowHeight = 550;

    private final String[] buttonTexts = new String[]{LOAD_MAP, LOAD_REQUEST, COMPUTE_TOUR};

    public Window(Controller controller) throws IOException, FontFormatException {
        //TODO: Add map when implemented
        //TODO: Add controller when implemented
        createHeader(controller);
        //TODO: Add intermediate JPanel before graphicalView and textualView
        graphicalView = new GraphicalView(this);
        textualView = new TextualView(this);
        //TODO: Add mouse listener
        //TODO: Add keyboard listener
        setMinimumSize(new Dimension(windowWidth, windowHeight));
        setWindowSize();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addComponentListener(this);
    }

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

    private void addAppName() throws IOException, FontFormatException {
        JLabel appNameLabel = new JLabel("Application Name");
        appNameLabel.setForeground(Constants.COLOR_3);
        appNameLabel.setFont(Constants.getFont("DMSans-Bold.ttf", 14));
        appNameLabel.setBorder(BorderFactory.createEmptyBorder(0,20,0,20));
        header.add(appNameLabel);
    }

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

    private void setWindowSize() {
        int headerHeight = 50;
        int textualViewWidth = 300;
        setPreferredSize(new Dimension(windowWidth, windowHeight));
        header.setPreferredSize(new Dimension(windowWidth, headerHeight));
        textualView.setPreferredSize(new Dimension(textualViewWidth, windowHeight - headerHeight));
        graphicalView.setPreferredSize(new Dimension(windowWidth - textualViewWidth, windowHeight - headerHeight));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        windowWidth = e.getComponent().getSize().width;
        windowHeight = e.getComponent().getSize().height;
        setWindowSize();
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    public void displayMap() {
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////

        // THIS SHOULD BE DONE IN THE CONTROLLER WHEN WE WANT TO LOAD A MAP

        // TODO: replace with intersections and segments coming from CityMap
        java.util.List<double[]> intersectionsTest = new ArrayList<>();
        intersectionsTest.add(new double[]{45.75406, 4.857418, 1});
        intersectionsTest.add(new double[]{45.75244, 4.862118, 2});
        intersectionsTest.add(new double[]{45.75466, 4.861418, 3});
        intersectionsTest.add(new double[]{45.74806, 4.872218, 4});
        intersectionsTest.add(new double[]{45.75606, 4.850841, 5});
        intersectionsTest.add(new double[]{45.74934, 4.864663, 6});
        graphicalView.initIntersectionViewList(intersectionsTest);

        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
    }
}
