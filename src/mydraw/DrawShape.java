package mydraw;

import java.awt.Graphics;
import java.awt.Point;

import mydraw.drawable.fillovalCommand;
import mydraw.drawable.fillrectangleCommand;
import mydraw.drawable.ovalCommand;
import mydraw.drawable.polyLineCommand;
import mydraw.drawable.rectangleCommand;
import mydraw.drawable.rhombusCommand;
import mydraw.drawable.triangleCommand;

/*
 * This class implements all API-Methods that display shapes.
 */
public class DrawShape {
    
    DrawGUI gui;

    public DrawShape(DrawGUI gui){
        this.gui = gui;
    }

    public void drawRectangle(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = gui.frontPanel.getGraphics();
        g.setColor(gui.fgColor);
        g.drawRect(x, y, width, height);
        g.dispose();

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.drawRect(x, y, width, height);
        g2.dispose();
        gui.commandQueue.add(new rectangleCommand(gui, upper_left.x, upper_left.y, lower_right.x, lower_right.y, gui.fgColor));
    }

    public void drawOval(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = gui.frontPanel.getGraphics();
        g.setColor(gui.fgColor);
        g.drawOval(x, y, width, height);
        g.dispose();

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.drawOval(x, y, width, height);
        g2.dispose();
        gui.commandQueue.add(new ovalCommand(gui, upper_left.x, upper_left.y, lower_right.x, lower_right.y, gui.fgColor));
    }

    public void drawPolyLine(java.util.List<Point> points) {
        Graphics g = gui.frontPanel.getGraphics();
        g.setPaintMode();
        g.setColor(gui.fgColor);

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.setPaintMode();

        for (int i = 1; i < points.size(); i++) {
            Point prevPoint = points.get(i - 1);
            Point currPoint = points.get(i);
            g.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
            g2.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
        }
        g.dispose();
        g2.dispose();
        gui.commandQueue.add(new polyLineCommand(gui, gui.fgColor, points));
    }

    public void drawTriangle(Point upper_left, Point lower_right) {
        Graphics g = gui.frontPanel.getGraphics();
        g.setColor(gui.fgColor);
        g.drawLine((lower_right.x + upper_left.x) / 2, upper_left.y, lower_right.x, lower_right.y);
        g.drawLine(upper_left.x, lower_right.y, lower_right.x, lower_right.y);
        g.drawLine(upper_left.x, lower_right.y, (lower_right.x + upper_left.x) / 2, upper_left.y);
        g.dispose();

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.drawLine((lower_right.x + upper_left.x) / 2, upper_left.y, lower_right.x, lower_right.y);
        g2.drawLine(upper_left.x, lower_right.y, lower_right.x, lower_right.y);
        g2.drawLine(upper_left.x, lower_right.y, (lower_right.x + upper_left.x) / 2, upper_left.y);
        g2.dispose();
        gui.commandQueue.add(new triangleCommand(gui, upper_left.x, upper_left.y, lower_right.x, lower_right.y, gui.fgColor));
    }

    public void drawRhombus(Point upper_left, Point lower_right) {
        Graphics g = gui.frontPanel.getGraphics();
        g.setColor(gui.fgColor);
        g.drawLine((upper_left.x + lower_right.x) / 2, upper_left.y, lower_right.x, (upper_left.y + lower_right.y) / 2);
        g.drawLine(lower_right.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2,
                lower_right.y);
        g.drawLine((upper_left.x + lower_right.x) / 2, lower_right.y, upper_left.x, (upper_left.y + lower_right.y) / 2);
        g.drawLine(upper_left.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2, upper_left.y);
        g.dispose();

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.drawLine((upper_left.x + lower_right.x) / 2, upper_left.y, lower_right.x,
                (upper_left.y + lower_right.y) / 2);
        g2.drawLine(lower_right.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2,
                lower_right.y);
        g2.drawLine((upper_left.x + lower_right.x) / 2, lower_right.y, upper_left.x,
                (upper_left.y + lower_right.y) / 2);
        g2.drawLine(upper_left.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2, upper_left.y);
        g2.dispose();
        gui.commandQueue.add(new rhombusCommand(gui, upper_left.x, upper_left.y, lower_right.x, lower_right.y, gui.fgColor));
    }

    public void drawFillRectangle(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = gui.frontPanel.getGraphics();
        g.setColor(gui.fgColor);
        g.drawRect(x, y, width, height);
        g.fillRect(x, y, width, height);
        g.dispose();

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.drawRect(x, y, width, height);
        g2.fillRect(x, y, width, height);
        g2.dispose();
        gui.commandQueue.add(new fillrectangleCommand(gui, upper_left.x, upper_left.y, lower_right.x, lower_right.y, gui.fgColor));
    }

    public void drawFillOval(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = gui.frontPanel.getGraphics();
        g.setColor(gui.fgColor);
        g.drawOval(x, y, width, height);
        g.fillOval(x, y, width, height);
        g.dispose();

        Graphics g2 = gui.buffImage.getGraphics();
        g2.setColor(gui.fgColor);
        g2.drawOval(x, y, width, height);
        g2.fillOval(x, y, width, height);
        g2.dispose();
        gui.commandQueue.add(new fillovalCommand(gui, upper_left.x, upper_left.y, lower_right.x, lower_right.y, gui.fgColor));
    }


}
