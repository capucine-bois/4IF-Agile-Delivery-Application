package view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import controller.Controller;

/**
 * Listener to handle keyboard events.
 */
public class KeyboardListener extends KeyAdapter {

    /**
     * Application controller.
     */
    private final Controller controller;
    private PopUpView popUpView;

    public KeyboardListener(PopUpView popUpView, Controller controller){
        this.controller = controller;
        this.popUpView = popUpView;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (!popUpView.isVisible()) {
            if (e.getKeyCode() == 90 && e.isControlDown())
                controller.undo();

            if (e.getKeyCode() == 90 && e.isControlDown() && e.isShiftDown())
                controller.redo();

            if (e.getKeyCode() == KeyEvent.VK_UP) {
                controller.arrowKeyPressed(true);
            }

            if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                controller.arrowKeyPressed(false);
            }
        }

    }

}
