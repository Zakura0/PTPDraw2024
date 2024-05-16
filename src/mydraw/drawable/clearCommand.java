package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;

import mydraw.DrawGUI;

public class clearCommand implements Drawable {

    DrawGUI window;
    Color color;

    public clearCommand(DrawGUI window, Color color) {
        this.window = window;
        this.color = color;
    }

    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.fillRect(0, 0, window.getDrawPanel().getWidth(), window.getDrawPanel().getHeight());
    }

    @Override
    public String toString(){
        return "clear;" + color.getRGB();
    }
}
