package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;

import mydraw.DrawGUI;

public class polyLineCommand implements Drawable {

DrawGUI window;
int nPoints;
int pointsX [];
int pointsY [];
Color color;

public polyLineCommand(DrawGUI window,Color color, int pointsX [], int pointsY [], int nPoints){
    this.window = window;
    this.pointsX = pointsX;
    this.pointsY = pointsY;
    this.color = color;
    this.nPoints = nPoints;
}
    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.drawPolyline(pointsX, pointsY, nPoints);
    }
}
