package mydraw;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

public class clearCommand implements Drawable {

    DrawGUI window;
    Color color;
    JPanel frontPanel;

    public clearCommand(DrawGUI window, Color color, JPanel frontPanel) {
        this.window = window;
        this.color = color;
        this.frontPanel = frontPanel;
    }

    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
    }
}
