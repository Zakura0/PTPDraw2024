package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import mydraw.DrawGUI;

public class polyLineCommand implements Drawable {

private List<Point> points;
private Color color;

public polyLineCommand(Color color, List<Point> points){
    this.color = color;
    this.points = points;
}
    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.setPaintMode();
        for (int i = 1; i < points.size(); i++) {
            Point prevPoint = points.get(i - 1);
            Point nextPoint = points.get(i);
            g.drawLine(prevPoint.x, prevPoint.y, nextPoint.x, nextPoint.y);
        }
    }
    @Override
    public String toString(){
        String stringColor = DrawGUI.getKeyByValue(color);
        String points_line = "";
        for (Point point : points) {
            points_line += point.x + "," + point.y + ":";
        }
        return "polyline;" + points_line + ";"  + stringColor;
    }
}
