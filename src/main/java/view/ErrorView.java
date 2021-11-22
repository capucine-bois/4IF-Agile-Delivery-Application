package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * Error message on the GUI
 */
public class ErrorView implements ActionListener {
    private final JPanel errorPanel;
    private final JLabel message;
    private final JButton button;
    private final Window window;

    /**
     * Constructor
     * @param window the window
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    public ErrorView(Window window) throws IOException, FontFormatException {
        errorPanel = (JPanel) window.getGlassPane();
        errorPanel.setLayout(new BorderLayout());

        JPanel backgroundError = new JPanel();
        backgroundError.setLayout(new GridBagLayout());
        backgroundError.setBackground(new Color(0,0,0,115));
        errorPanel.add(backgroundError);

        JPanel error = new JPanel();
        error.setLayout(new BorderLayout());
        error.setPreferredSize(new Dimension(360, 180));
        error.setBackground(Constants.COLOR_1);
        backgroundError.add(error, new GridBagConstraints());

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());
        message = new JLabel();
        message.setPreferredSize(new Dimension(320, 100));
        message.setFont(Constants.getFont("DMSans-Medium.ttf", 14));
        message.setForeground(Constants.COLOR_3);
        messagePanel.add(message, new GridBagConstraints());
        error.add(messagePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        button = new JButton("Close");
        button.addActionListener(this);
        window.setStyle(button);
        buttonPanel.add(button, new GridBagConstraints());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        error.add(buttonPanel, BorderLayout.PAGE_END);

        this.window = window;
    }

    /**
     * Hides error panel when button is pressed.
     * @param e event information
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == button) {
            window.resetDefaultButtonStates();
            errorPanel.setVisible(false);
        }
    }

    /**
     * Shows error panel
     * @param messageContent error message to display
     */
    public void showError(String messageContent) {
        message.setText("<html><p style='text-align: center;'>" + messageContent + "</p></html>");
        message.setHorizontalAlignment(SwingConstants.CENTER);
        errorPanel.setVisible(true);
    }
}
