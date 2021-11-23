package view;

import controller.Controller;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Listener for mouse events.
 */
public class MouseListener extends MouseAdapter {

    private Controller controller;
    private TextualView textualView;

    public MouseListener(Controller controller, TextualView textualView) {
        this.controller = controller;
        this.textualView = textualView;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            controller.leftClick(textualView.getRequestPanels().indexOf((JPanel) e.getSource()));
        }
    }
}
