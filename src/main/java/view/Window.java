package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Window extends JFrame {

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

    private final String[] buttonTexts = new String[]{LOAD_MAP, LOAD_REQUEST, COMPUTE_TOUR};

    public Window() throws IOException, FontFormatException {
        //TODO: Add map when implemented
        //TODO: Add controller when implemented
        createHeader();
        graphicalView = new GraphicalView(this);
        textualView = new TextualView(this);
        //TODO: Add mouse listener
        //TODO: Add keyboard listener
        setWindowSize();
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createHeader() throws IOException, FontFormatException {
        header = new JPanel();
        FlowLayout headerLayout = new FlowLayout(FlowLayout.LEFT);
        header.setLayout(headerLayout);
        header.setBackground(Constants.COLOR_1);
        addAppName();
        buttonListener = new ButtonListener();
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
        int windowWidth = 800;
        int windowHeight = 550;
        setMinimumSize(new Dimension(windowWidth, windowHeight));
        int headerHeight = 50;
        int textualViewWidth = 300;
        header.setPreferredSize(new Dimension(windowWidth, headerHeight));
        textualView.setPreferredSize(new Dimension(textualViewWidth, windowHeight - headerHeight));
    }

}
