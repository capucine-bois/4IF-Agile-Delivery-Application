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
    private GraphicalView graphicalView;
    private TextualView textualView;

    public MouseListener(Controller controller) {
        this.controller = controller;
    }

    public void setGraphicalView(GraphicalView graphicalView) {
        this.graphicalView = graphicalView;
    }

    public void setTextualView(TextualView textualView) {
        this.textualView = textualView;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            if (TextualView.requestPanels.contains((JPanel) e.getSource())) {
                controller.leftClickOnRequest(TextualView.requestPanels.indexOf((JPanel) e.getSource()));
            } else if (TextualView.tourIntersectionsPanels.contains((JPanel) e.getSource())) {
                controller.leftClickOnTourIntersection(TextualView.tourIntersectionsPanels.indexOf((JPanel) e.getSource()));
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (TextualView.requestPanels.contains((JPanel) e.getSource())) {
            textualView.setRequestPanelMouseEntered(TextualView.requestPanels.indexOf((JPanel) e.getSource()));
        } else if (TextualView.tourIntersectionsPanels.contains((JPanel) e.getSource())) {
           textualView.setTourIntersectionPanelMouseEntered(TextualView.tourIntersectionsPanels.indexOf((JPanel) e.getSource()));
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        if (TextualView.requestPanels.contains((JPanel) e.getSource())) {
            textualView.setRequestPanelMouseExited(TextualView.requestPanels.indexOf((JPanel) e.getSource()));
        } else if (TextualView.tourIntersectionsPanels.contains((JPanel) e.getSource())) {
            textualView.setTourIntersectionPanelMouseExited(TextualView.tourIntersectionsPanels.indexOf((JPanel) e.getSource()));
        }
    }

    /**
     * Method called each time the mouse wheel is moved
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getSource().equals(graphicalView)) {
            graphicalView.zoom(e.getX(), e.getY(), e.getWheelRotation());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getSource().equals(graphicalView)) {
            graphicalView.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getSource().equals(graphicalView)) {
            graphicalView.moveMap(e.getX(), e.getY());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getSource().equals(graphicalView)) {
            graphicalView.updatePrevious(e.getX(), e.getY());
            graphicalView.setCursor(new Cursor(Cursor.MOVE_CURSOR));
        }
    }
}
