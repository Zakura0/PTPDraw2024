package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import mydraw.DrawGUI;

public class rhombusCommand implements Drawable {

int x0, y0, x1, y1;
DrawGUI window;
Point upper_left, lower_right;
Color color;

public rhombusCommand(DrawGUI window, int x0, int y0, int x1, int y1, Color color) {
    this.window = window;
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.color = color;

}
    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.drawLine((x0 + x1) / 2, y0, x1, (y0 + y1) / 2);
        g.drawLine(x1, (y0 + y1) / 2, (x0 + x1) / 2, y1);
        g.drawLine((x0 + x1) / 2, y1, x0, (y0 + y1) / 2);
        g.drawLine(x0, (y0 + y1) / 2, (x0 + x1) / 2, y0);
    }
}
