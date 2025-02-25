package mydraw.drawable;

import java.awt.Color;
import java.awt.Graphics;

import mydraw.DrawGUI;

public class fillovalCommand implements Drawable {

    private int x0, y0, x1, y1;
    private Color color;
    
    public fillovalCommand(int x0, int y0, int x1, int y1, Color color) {
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
            g.fillOval(x, y, w, h);
        }
        
        @Override
        public String toString(){
        String stringColor = DrawGUI.getKeyByValue(color);
        return "filloval;" + x0 + ";" + y0 + ";" + x1 + ";" + y1 + ";" + stringColor;
    }
    }
