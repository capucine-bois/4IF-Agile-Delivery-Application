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
    private final Controller controller;

    private PopUpView popUpView;

    /**
     * Complete controller taking application controller in parameter.
     * @param controller application controller
     */
    public ButtonListener(Controller controller){
        this.controller = controller;
        /*
          The window
         */
    }

    public void setPopUpView(PopUpView popUpView) {
        this.popUpView = popUpView;
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
            case Window.REDO -> controller.redo();
            case TextualView.ADD_REQUEST -> controller.addRequest();
            case TextualView.REQUESTS_HEADER -> controller.showRequestsPanel();
            case TextualView.TOUR_HEADER -> controller.showTourPanel();
            case TextualView.GO_BACK_TO_TOUR -> controller.goBackToTour();
            case TextualView.PATH_DETAILS -> controller.leftClickOnShortestPath(TextualView.pathDetailsButtons.indexOf((JButton) e.getSource()));
            case TextualView.DELETE_REQUEST -> controller.deleteRequest(TextualView.deleteRequestButtons.indexOf((JButton) e.getSource()));
            case TextualView.CANCEL -> controller.cancel();
            case TextualView.CHOOSE_ADDRESS -> controller.chooseAddress(TextualView.chooseAddressButtons.indexOf((JButton) e.getSource()));
            case TextualView.CHANGE_ADDRESS -> controller.changeAddress();
            case TextualView.CHANGE_TIME -> controller.changeTime();
            case TextualView.SAVE_TIME -> controller.saveTime(((JSpinner.DefaultEditor) TextualView.changeTimeField.getEditor()).getTextField().getText());
            case TextualView.CONTINUE_ADD_REQUEST -> {
                String pickupTime = ((JSpinner.DefaultEditor) TextualView.timeFields.get(0).getEditor()).getTextField().getText();
                String deliveryTime = ((JSpinner.DefaultEditor) TextualView.timeFields.get(1).getEditor()).getTextField().getText();
                controller.insertRequest(pickupTime, deliveryTime);
            }
            case "" -> {
                if (TextualView.goUpButtons.contains((JButton) e.getSource())) {
                    controller.moveIntersectionBefore(TextualView.goUpButtons.indexOf((JButton) e.getSource()) + 1);
                } else if (TextualView.goDownButtons.contains((JButton) e.getSource())) {
                    controller.moveIntersectionAfter(TextualView.goDownButtons.indexOf((JButton) e.getSource()));
                }
            }
            case PopUpView.CLOSE -> popUpView.setVisible(false);
        }
    }

}