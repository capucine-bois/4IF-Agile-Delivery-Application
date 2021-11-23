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
            if (e.getSource() instanceof JPanel && textualView.getRequestPanels().contains((JPanel) e.getSource())) {
                controller.leftClickOnRequest(textualView.getRequestPanels().indexOf((JPanel) e.getSource()));
            } else if (e.getSource() instanceof JPanel && textualView.getShortestPathsPanels().contains((JPanel) e.getSource())) {
                controller.leftClickOnShortestPath(textualView.getShortestPathsPanels().indexOf((JPanel) e.getSource()));
            } else if (e.getSource() instanceof JLabel && textualView.getBackToTour() == e.getSource()) {
                controller.goBackToTour();
            }
        }
    }
}
