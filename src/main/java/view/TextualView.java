package view;

import javax.swing.*;
import java.awt.*;

public class TextualView extends JPanel {

    /**
     * Create a textual view in window
     * @param window the window
     */
    public TextualView(Window window){
        setBackground(Constants.COLOR_4);
        setBorder(BorderFactory.createMatteBorder(10,5,10,10,Constants.COLOR_1));
        window.getContentPane().add(this, BorderLayout.LINE_END);
    }
}
