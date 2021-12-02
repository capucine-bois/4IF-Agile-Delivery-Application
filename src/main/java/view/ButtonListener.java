package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.Controller;

import javax.swing.*;

/**
 * Listener for buttons.
 * Specify actions when buttons are pressed.
 */
public class ButtonListener implements ActionListener {

    /* ATTRIBUTES */

    /**
     * The application controller
     */
    private Controller controller;

    /**
     * The window
     */
    private Window window;

    /**
     * Complete controller taking application controller in parameter.
     * @param controller application controller
     */
    public ButtonListener(Controller controller, Window window){
        this.controller = controller;
        this.window = window;
    }

    /**
     * Event triggered when button is pressed.
     * The action depends on the button text value (to differentiate them).
     * @param e event information
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case Window.LOAD_MAP -> controller.loadMap();
            case Window.LOAD_REQUEST -> controller.loadRequests();
            case Window.COMPUTE_TOUR -> controller.computeTour();
            case Window.STOP_COMPUTATION -> controller.stopTourComputation();
            case Window.UNDO -> controller.undo();
            case TextualView.ADD_REQUEST -> controller.insertRequest();
            case TextualView.REQUESTS_HEADER -> controller.showRequestsPanel();
            case TextualView.TOUR_HEADER -> controller.showTourPanel();
            case TextualView.GO_BACK_TO_TOUR -> controller.goBackToTour();
            case TextualView.PATH_DETAILS -> controller.leftClickOnShortestPath(TextualView.pathDetailsButtons.indexOf(e.getSource()));
            case TextualView.DELETE_REQUEST -> controller.deleteRequest(TextualView.deleteRequestButtons.indexOf(e.getSource()));
            case TextualView.GO_UP -> controller.moveIntersectionBefore(TextualView.goUpButtons.indexOf(e.getSource()) + 1);
            case TextualView.GO_DOWN -> controller.moveIntersectionAfter(TextualView.goDownButtons.indexOf(e.getSource()));
        }
    }

}
