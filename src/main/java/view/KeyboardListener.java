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

    public KeyboardListener(Controller controller){
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Method called by the keyboard listener each time a key is pressed
        if (e.getKeyCode() == 90 && e.isControlDown())
            controller.undo();

        if (e.getKeyCode() == 90 && e.isControlDown() && e.isShiftDown())
            controller.redo();

    }

}
