package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import controller.Controller;

public class ButtonListener implements ActionListener {
    private  Controller controller;

    public ButtonListener(Controller controller){
        this.controller = controller;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case Window.LOAD_MAP -> controller.loadMap();
            case Window.LOAD_REQUEST -> controller.loadRequests();
        }
    }
}
