package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;

import mydraw.DrawGUI;

public class triangleCommand implements Drawable {

private int x0, y0, x1, y1;
private Color color;

public triangleCommand(int x0, int y0, int x1, int y1, Color color) {
    this.x0 = x0;
    this.y0 = y0;
    this.x1 = x1;
    this.y1 = y1;
    this.color = color;
}
    @Override
    public void draw(Graphics g){
        g.setColor(color);
        g.drawLine((x1 + x0) / 2, y0, x1, y1);
        g.drawLine(x0, y1, x1, y1);
        g.drawLine(x0, y1, (x1 + x0) / 2, y0);
    }

    @Override
    public String toString(){
        String stringColor = DrawGUI.getKeyByValue(color);
        return "triangle;" + x0 + ";" + y0 + ";" + x1 + ";" + y1 + ";" + stringColor;
    }
}
