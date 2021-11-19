package view;

import javax.swing.*;
import java.awt.*;
import observer.Observable;
import observer.Observer;

/**
 * Textual element on the GUI.
 * Used to display requests.
 */
public class TextualView extends JPanel implements Observer {

    /**
     * Create a textual view in window
     * @param window the GUI
     */
    public TextualView(Window window){
        setBackground(Constants.COLOR_4);
        setBorder(BorderFactory.createMatteBorder(10,5,10,10,Constants.COLOR_1));
        window.getContentPane().add(this, BorderLayout.LINE_END);
    }

    /**
     * Update content of the view on the GUI.
     * Called by models using Observable design pattern.
     * @param observed
     * @param arg
     */
    @Override
    public void update(Observable observed, Object arg) {

    }
}
