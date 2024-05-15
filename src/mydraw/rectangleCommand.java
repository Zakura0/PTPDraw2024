package mydraw;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

public class rectangleCommand implements Drawable {

int x, y, w, h;
DrawGUI window;
Point upper_left, lower_right;
Color color;

public rectangleCommand(DrawGUI window, int x, int y, int w, int h, Color color) {
    this.window = window;
    this.x = x;
    this.y = y;
    this.w = w;
    this.h = h;
    this.color = color;

}
    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.drawRect(x, y, w, h);
    }
}
