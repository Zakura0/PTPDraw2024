package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import mydraw.DrawGUI;

public class fillrectangleCommand implements Drawable {

int x0, y0, x1, y1;
DrawGUI window;
Point upper_left, lower_right;
Color color;

public fillrectangleCommand(DrawGUI window, int x0, int y0, int x1, int y1, Color color) {
    this.window = window;
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.color = color;

}
    @Override
    public void draw(Graphics g){
        int x = Math.min(x0, x1);
        int y = Math.min(y0, y1);
        int w = Math.abs(x1 - x0);
        int h = Math.abs(y1 - y0);
        g.setColor(color);
        g.fillRect(x, y, w, h);
    }
}
