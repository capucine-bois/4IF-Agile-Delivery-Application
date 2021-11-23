package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.Controller;

/**
 * Listener for buttons.
 * Specify actions when buttons are pressed.
 */
public class ButtonListener implements ActionListener {
    private  Controller controller;

    /**
     * Complete controller taking application controller in parameter.
     * @param controller application controller
     */
    public ButtonListener(Controller controller){
        this.controller = controller;
    }

    /**
     * Event triggered when button is pressed.
     * The action depends on the button text value (to differentiate them).
     * @param e event information
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println(e.getActionCommand());
        switch (e.getActionCommand()) {
            case Window.LOAD_MAP -> controller.loadMap();
            case Window.LOAD_REQUEST -> controller.loadRequests();
            case Window.COMPUTE_TOUR -> controller.computeTour();
        }
    }
}
