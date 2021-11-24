package view;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ComponentListener extends ComponentAdapter {

    private Window window;
    private GraphicalView graphicalView;

    public ComponentListener(Window window, GraphicalView graphicalView) {
        this.window = window;
        this.graphicalView = graphicalView;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        window.setWindowSize(e.getComponent().getSize().width, e.getComponent().getSize().height);
        graphicalView.repaint();
    }
}
