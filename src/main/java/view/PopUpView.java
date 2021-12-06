package view;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Popup message on the GUI
 */
public class PopUpView {

    protected final static String CLOSE = "Close";

    private final JPanel popUpPanel;
    private JLabel message;
    private JButton button;
    private final Window window;

    private final ButtonListener buttonListener;

    /**
     * Constructor
     * @param window the window
     * @param mouseListener
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    public PopUpView(Window window, MouseListener mouseListener, ButtonListener buttonListener) throws IOException, FontFormatException {
        this.window = window;
        this.buttonListener = buttonListener;
        buttonListener.setPopUpView(this);
        popUpPanel = (JPanel) window.getGlassPane();
        popUpPanel.setLayout(new BorderLayout());
        popUpPanel.addMouseListener(mouseListener);

        JPanel backgroundPopUp = new JPanel();
        backgroundPopUp.setLayout(new GridBagLayout());
        backgroundPopUp.setBackground(new Color(0,0,0,115));
        popUpPanel.add(backgroundPopUp);

        JPanel popUp = new JPanel();
        popUp.setLayout(new BorderLayout());
        popUp.setPreferredSize(new Dimension(360, 180));
        popUp.setBackground(Constants.COLOR_1);
        backgroundPopUp.add(popUp, new GridBagConstraints());

        createMessagePanel(popUp);
        createButtonPanel(popUp);
    }

    private void createMessagePanel(JPanel parentPanel) throws IOException, FontFormatException {
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());
        message = new JLabel();
        message.setPreferredSize(new Dimension(320, 100));
        message.setFont(Constants.getFont("DMSans-Medium.ttf", 14));
        message.setForeground(Constants.COLOR_3);
        messagePanel.add(message, new GridBagConstraints());
        parentPanel.add(messagePanel, BorderLayout.CENTER);
    }

    private void createButtonPanel(JPanel parentPanel) throws IOException, FontFormatException {
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        button = new JButton(CLOSE);
        button.addActionListener(buttonListener);
        window.setStyle(button);
        buttonPanel.add(button, new GridBagConstraints());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        parentPanel.add(buttonPanel, BorderLayout.PAGE_END);
    }

    /**
     * Shows error
     * @param messageContent error message to display
     */
    public void showError(String messageContent) {
        button.setVisible(true);
        message.setText("<html><p style='text-align: center;'>" + messageContent + "</p></html>");
        message.setHorizontalAlignment(SwingConstants.CENTER);
        popUpPanel.setVisible(true);
    }

    public void setVisible(boolean visible) {
        popUpPanel.setVisible(visible);
    }
}
