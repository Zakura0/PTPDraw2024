package mydraw;

import javax.swing.JPanel;

import java.awt.*;

/*
 * This class implements a paint component, allowing us to differenciate on what has been drawn.
 */
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
