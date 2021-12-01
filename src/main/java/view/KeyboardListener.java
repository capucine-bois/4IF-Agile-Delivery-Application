package view;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import controller.Controller;

public class KeyboardListener extends KeyAdapter {

    private Controller controller;

    public KeyboardListener(Controller controller){
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Method called by the keyboard listener each time a key is pressed
        if (e.getKeyChar() == 'u')
            controller.undo();
    }

}
