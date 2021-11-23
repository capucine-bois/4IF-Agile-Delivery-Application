package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * Loader on the GUI.
 */
public class LoaderView {
    private final JPanel loaderPanel;
    private final JLabel message;
    private final Window window;

    /**
     * Constructor
     * @param window the window
     * @throws IOException raised if font file can't be found
     * @throws FontFormatException raised if font can't be loaded
     */
    public LoaderView(Window window) throws IOException, FontFormatException {
        loaderPanel = (JPanel) window.getGlassPane();
        loaderPanel.setLayout(new BorderLayout());

        JPanel backgroundLoader = new JPanel();
        backgroundLoader.setLayout(new GridBagLayout());
        backgroundLoader.setBackground(new Color(0,0,0,115));
        loaderPanel.add(backgroundLoader);

        JPanel loader = new JPanel();
        loader.setLayout(new BorderLayout());
        loader.setPreferredSize(new Dimension(360, 180));
        loader.setBackground(Constants.COLOR_1);
        backgroundLoader.add(loader, new GridBagConstraints());

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new GridBagLayout());
        message = new JLabel();
        message.setPreferredSize(new Dimension(320, 100));
        message.setFont(Constants.getFont("DMSans-Medium.ttf", 14));
        message.setForeground(Constants.COLOR_3);
        messagePanel.add(message, new GridBagConstraints());
        loader.add(messagePanel, BorderLayout.CENTER);

        message.setText("<html><p style='text-align: center;'>" + "Loading..." + "</p></html>");
        message.setHorizontalAlignment(SwingConstants.CENTER);

        this.window = window;
    }


    public void setVisibility(boolean state) {
        loaderPanel.setVisible(state);
    }
}
