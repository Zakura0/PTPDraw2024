package mydraw;

import javax.swing.JPanel;

import mydraw.drawable.Drawable;

import java.awt.*;

public class DrawPanel extends JPanel {

    DrawGUI gui;

    public DrawPanel(DrawGUI gui) {
        super();
        this.gui = gui;
    }
    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(gui.bgColor);
        g.fillRect(0, 0, gui.frontPanel.getWidth(), gui.frontPanel.getHeight());
        for (Drawable drawable : gui.commandQueue)
        {
            drawable.draw(g);
        }
        g.dispose();
    }
}
