package mydraw;

import java.awt.Graphics;
import java.awt.Point;

public class rectangleCommand implements Drawable {

DrawGUI window;
Point upper_left, lower_right;

public rectangleCommand(DrawGUI window, Point upper_left, Point lower_right) {
    this.window = window;
    this.upper_left = upper_left;
    this.lower_right = lower_right;
    window.drawRectangle(upper_left, lower_right);
}
    @Override
    public void draw(Graphics g){
        new rectangleCommand(window, upper_left, lower_right);
    }
}
