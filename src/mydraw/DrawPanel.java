package mydraw;

import javax.swing.JPanel;

import java.awt.*;

public class DrawPanel extends JPanel {

    DrawGUI gui;

    public DrawPanel(DrawGUI gui) {
        super();
        this.gui = gui;
    }
    @Override
    protected void paintComponent(Graphics g) {
        gui.redraw(g);
    }
}
