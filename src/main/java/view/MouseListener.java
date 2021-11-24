package view;

import controller.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * Listener for mouse events.
 */
public class MouseListener extends MouseAdapter {

    private Controller controller;
    private TextualView textualView;
    private GraphicalView graphicalView;
    private Window window;

    public MouseListener(Controller controller, Window window) {
        this.controller = controller;
        this.window = window;
    }

    public void setTextualView(TextualView textualView) {
        this.textualView = textualView;
    }

    public void setGraphicalView(GraphicalView graphicalView) {
        this.graphicalView = graphicalView;
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


    /**
     * Method called each time the mouse wheel is moved
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getSource() instanceof GraphicalView) {
            graphicalView.zoom(e.getX(), e.getY(), e.getWheelRotation());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource() instanceof GraphicalView) {
            graphicalView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource() instanceof GraphicalView) {
            graphicalView.moveMap(e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource() instanceof GraphicalView) {
            graphicalView.updatePrevious(e.getX(), e.getY());
            graphicalView.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }
    }
}
