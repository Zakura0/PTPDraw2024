package mydraw;

import java.awt.Graphics;
import java.awt.Point;
import java.util.List;

import javax.swing.JOptionPane;

import mydraw.exceptions.ColorException;



public class DrawFunctions {

    DrawGUI gui;
    DrawShape shape;

    public DrawFunctions(DrawGUI gui, DrawShape shape){
        this.gui = gui;
        this.shape = shape;
    }


    public void clear() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the drawing? \n This action is irreversible.", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            gui.commandQueue.clear();
            Graphics g = gui.frontPanel.getGraphics();
            g.setColor(gui.bgColor);
            g.fillRect(0, 0, gui.frontPanel.getWidth(), gui.frontPanel.getHeight());
            g.dispose();

            Graphics g2 = gui.buffImage.getGraphics();
            g2.setColor(gui.bgColor);
            g2.fillRect(0, 0, gui.buffImage.getWidth(), gui.buffImage.getHeight());
            g2.dispose();
        }
    }
    
    public void undo() {
        if (gui.commandQueue.size() > 0) {
            gui.undoStack.add(gui.commandQueue.get(gui.commandQueue.size() - 1));
            gui.commandQueue.remove(gui.commandQueue.size() - 1);
            Graphics g = gui.frontPanel.getGraphics();
            gui.redraw(g);
        }
    }

    public void redo() {
        if (gui.undoStack.size() > 0) {
            gui.commandQueue.add(gui.undoStack.get(gui.undoStack.size() - 1));
            gui.undoStack.remove(gui.undoStack.size() - 1);
            Graphics g = gui.frontPanel.getGraphics();
            gui.redraw(g);
        }
    }

    public void autoDraw() {
        Point p1 = new Point(100, 200);
        Point p2 = new Point(200, 100);
        try {
            gui.setFGColor("red");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        shape.drawRectangle(p1, p2);
        Point p3 = new Point(300, 200);
        Point p4 = new Point(400, 100);
        try {
            gui.setFGColor("blue");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        shape.drawOval(p3, p4);
        Point pl1 = new Point(500, 200);
        Point pl2 = new Point(600, 100);
        Point pl3 = new Point(700, 200);
        try {
            gui.setFGColor("green");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        shape.drawPolyLine(List.of(pl1, pl2, pl3));
        Point p5 = new Point(100, 350);
        Point p6 = new Point(200, 250);
        shape.drawFillRectangle(p5, p6);
        Point p7 = new Point(300, 350);
        Point p8 = new Point(400, 250);
        shape.drawFillOval(p7, p8);
        Point p9 = new Point(500, 350);
        Point p10 = new Point(600, 250);
        shape.drawRhombus(p9, p10);
        Point p11 = new Point(600, 250);
        Point p12 = new Point(700, 350);
        shape.drawTriangle(p11, p12);
    }
    

}
