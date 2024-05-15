package mydraw;

import java.awt.Graphics;
import java.awt.Point;

public class polyLineCommand implements Drawable {

DrawGUI window;
java.util.List<Point> points;

public polyLineCommand(DrawGUI window, java.util.List<Point> points){
    this.window = window;
    this.points = points;
    window.drawPolyLine(points);
}
    @Override
    public void draw(Graphics g){
        new polyLineCommand(window,points);
    }
}
