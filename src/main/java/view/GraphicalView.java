package view;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;

public class GraphicalView extends JPanel {

    private int viewHeight;
    private int viewWidth;
    private Graphics g;

    /**
     * Create the graphical view
     * @param w the window
     */
    public GraphicalView(Window w) {
        setLayout(null);
        setBackground(Constants.COLOR_5);
        setBorder(new CompoundBorder(BorderFactory.createMatteBorder(10,10,10,5,Constants.COLOR_1),BorderFactory.createLineBorder(Constants.COLOR_4, 2)));
        w.getContentPane().add(this, BorderLayout.CENTER);
    }

}
