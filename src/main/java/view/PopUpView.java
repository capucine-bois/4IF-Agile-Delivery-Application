package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Popup message on the GUI
 */
public class PopUpView implements ActionListener {
    private final JPanel popUpPanel;
    private final JLabel message;
    private final JButton button;
    private final Window window;

    /**
     * Constructor
     * @param window the window
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    public PopUpView(Window window) throws IOException, FontFormatException {
        popUpPanel = (JPanel) window.getGlassPane();
        popUpPanel.setLayout(new BorderLayout());

        JPanel backgroundPopUp = new JPanel();
        backgroundPopUp.setLayout(new GridBagLayout());
        backgroundPopUp.setBackground(new Color(0,0,0,115));
        popUpPanel.add(backgroundPopUp);

        JPanel popUp = new JPanel();
        popUp.setLayout(new BorderLayout());
        popUp.setPreferredSize(new Dimension(360, 180));
        popUp.setBackground(Constants.COLOR_1);
        backgroundPopUp.add(popUp, new GridBagConstraints());

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());
        message = new JLabel();
        message.setPreferredSize(new Dimension(320, 100));
        message.setFont(Constants.getFont("DMSans-Medium.ttf", 14));
        message.setForeground(Constants.COLOR_3);
        messagePanel.add(message, new GridBagConstraints());
        popUp.add(messagePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        button = new JButton("Close");
        button.addActionListener(this);
        window.setStyle(button);
        buttonPanel.add(button, new GridBagConstraints());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        popUp.add(buttonPanel, BorderLayout.PAGE_END);

        this.window = window;
    }

    /**
     * Hides error when button is pressed.
     * @param e event information
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            window.resetComponentsState();
            popUpPanel.setVisible(false);
        }
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
}
