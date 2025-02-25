package mydraw;

import java.awt.event.MouseMotionListener;

import mydraw.drawable.fillovalCommand;
import mydraw.drawable.fillrectangleCommand;
import mydraw.drawable.ovalCommand;
import mydraw.drawable.polyLineCommand;
import mydraw.drawable.rectangleCommand;
import mydraw.drawable.rhombusCommand;
import mydraw.drawable.triangleCommand;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// this class determines how mouse events are to be interpreted,
// depending on the shape mode currently set
class ShapeManager implements ItemListener {
    DrawGUI gui;

    @SuppressWarnings("unused")
    abstract class ShapeDrawer extends MouseAdapter implements MouseMotionListener {
        public void mouseMoved(MouseEvent e) {
            /* ignore */ }
    }

    // if this class is active, the mouse is interpreted as a pen
    class ScribbleDrawer extends ShapeDrawer {
        int lastx, lasty;
        List<Point> points = new ArrayList<Point>();

        public void mousePressed(MouseEvent e) {
            Point p = gui.frontPanel.getMousePosition();
            if (p != null) {
                lastx = p.x;
                lasty = p.y;
                points.add(p);
            }
        }

        public void mouseDragged(MouseEvent e) {
            Graphics g = gui.frontPanel.getGraphics();
            Graphics g2 = gui.buffImage.getGraphics();
            Point p = gui.frontPanel.getMousePosition();
            if (p != null) {
                points.add(p);
                int x = p.x;
                int y = p.y;
                g.setColor(gui.fgColor);
                g2.setColor(gui.fgColor);
                g.setPaintMode();
                g2.setPaintMode();
                g.drawLine(lastx, lasty, x, y);
                g2.drawLine(lastx, lasty, x, y);
                lastx = x;
                lasty = y;
            }
        }
        
        public void mouseReleased(MouseEvent e) {
            List<Point> final_points = new ArrayList<Point>(this.points);
            gui.commandQueue.add(new polyLineCommand(gui.fgColor, final_points));
            points.clear();
            gui.undoStack.clear(); 
        }
    }

    // if this class is active, rectangles are drawn
    class RectangleDrawer extends ShapeDrawer {
        int pressx, pressy;
        int lastx = -1, lasty = -1;

        // mouse pressed => fix first corner of rectangle
        public void mousePressed(MouseEvent e) {
            Point p = gui.frontPanel.getMousePosition();
            if (p != null) {
                int x = p.x;
                int y = p.y;
                pressx = x;
                pressy = y;
            }
        }

        // mouse released => fix second corner of rectangle
        // and draw the resulting shape
        public void mouseReleased(MouseEvent e) {
            Graphics g = gui.frontPanel.getGraphics();
            Graphics g2 = gui.buffImage.getGraphics();
            if (lastx != -1) {
                // first undraw a rubber rect
                g.setXORMode(gui.fgColor);
                g.setColor(gui.getBackground());
                doDraw(pressx, pressy, lastx, lasty, g);
                lastx = -1;
                lasty = -1;
            }
            // these commands finish the rubberband mode
            g.setPaintMode();
            g2.setPaintMode();
            g.setColor(gui.fgColor);
            g2.setColor(gui.fgColor);
            // draw the finel rectangle
            Point p = gui.frontPanel.getMousePosition();
            if (p != null) {
                int x = p.x;
                int y = p.y;
                doDraw(pressx, pressy, x, y, g);
                doDraw(pressx, pressy, x, y, g2);
                if (this instanceof OvalDrawer)
                {
                    gui.commandQueue.add(new ovalCommand(pressx, pressy, x, y, gui.fgColor));
                }
                else  if (this instanceof TriangleDrawer)
                {
                    gui.commandQueue.add(new triangleCommand(pressx, pressy, x, y, gui.fgColor));
                }
                else if (this instanceof RhombusDrawer)
                {
                    gui.commandQueue.add(new rhombusCommand(pressx, pressy, x, y, gui.fgColor));
                }
                else if (this instanceof RectangleFillDrawer)
                {
                    gui.commandQueue.add(new fillrectangleCommand(pressx, pressy, x, y, gui.fgColor));
                }
                else if (this instanceof OvalFillDrawer)
                {
                    gui.commandQueue.add(new fillovalCommand(pressx, pressy, x, y, gui.fgColor));
                }
                else {
                    gui.commandQueue.add(new rectangleCommand(pressx, pressy, x, y, gui.fgColor));
                }
                gui.undoStack.clear();           
            }
        }

        // mouse released => temporarily set second corner of rectangle
        // draw the resulting shape in "rubber-band mode"
        public void mouseDragged(MouseEvent e) {
            Graphics g = gui.frontPanel.getGraphics();
            // these commands set the rubberband mode
            g.setXORMode(gui.fgColor);
            g.setColor(gui.getBackground());
            if (lastx != -1) {
                // first undraw previous rubber rect
                doDraw(pressx, pressy, lastx, lasty, g);

            }
            Point p = gui.frontPanel.getMousePosition();
            if (p != null) {
                int x = p.x;
                int y = p.y;
                lastx = x;
                lasty = y;
                // draw new rubber rect
                doDraw(pressx, pressy, lastx, lasty, g);
            }
        }

        public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
            // calculate upperleft and width/height of rectangle
            int x = Math.min(x0, x1);
            int y = Math.min(y0, y1);
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);

            // draw rectangle
            g.drawRect(x, y, w, h);
        }
    }

    // if this class is active, ovals are drawn
    class OvalDrawer extends RectangleDrawer {
        public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
            int x = Math.min(x0, x1);
            int y = Math.min(y0, y1);
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);
            // draw oval instead of rectangle
            g.drawOval(x, y, w, h);
        }
    }

    class TriangleDrawer extends RectangleDrawer {
        public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
            // draw triangle instead of rectangle
            g.drawLine((x1 + x0) / 2, y0, x1, y1);
            g.drawLine(x0, y1, x1, y1);
            g.drawLine(x0, y1, (x1 + x0) / 2, y0);
        }
    }

    class RhombusDrawer extends RectangleDrawer {
        public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
            // draw rhoumbus instead of rectangle
            g.drawLine((x0 + x1) / 2, y0, x1, (y0 + y1) / 2);
            g.drawLine(x1, (y0 + y1) / 2, (x0 + x1) / 2, y1);
            g.drawLine((x0 + x1) / 2, y1, x0, (y0 + y1) / 2);
            g.drawLine(x0, (y0 + y1) / 2, (x0 + x1) / 2, y0);
        }
    }

    class RectangleFillDrawer extends RectangleDrawer {
        public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
            int x = Math.min(x0, x1);
            int y = Math.min(y0, y1);
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);
            // draw filled rectangle
            g.fillRect(x, y, w, h);
        }
    }

    class OvalFillDrawer extends RectangleDrawer {
        public void doDraw(int x0, int y0, int x1, int y1, Graphics g) {
            int x = Math.min(x0, x1);
            int y = Math.min(y0, y1);
            int w = Math.abs(x1 - x0);
            int h = Math.abs(y1 - y0);
            // draw filled rectangle
            g.fillOval(x, y, w, h);
        }
    }

    ScribbleDrawer scribbleDrawer = new ScribbleDrawer();
    RectangleDrawer rectDrawer = new RectangleDrawer();
    OvalDrawer ovalDrawer = new OvalDrawer();
    TriangleDrawer triangleDrawer = new TriangleDrawer();
    RhombusDrawer rhombusDrawer = new RhombusDrawer();
    RectangleFillDrawer rectFillDrawer = new RectangleFillDrawer();
    OvalFillDrawer ovalFillDrawer = new OvalFillDrawer();
    ShapeDrawer currentDrawer;

    public ShapeManager(DrawGUI itsGui) {
        gui = itsGui;
        // default: activate scribble mode
        currentDrawer = scribbleDrawer;
        gui.addMouseListener(currentDrawer);
        gui.addMouseMotionListener(currentDrawer);
    }

    // reset the shape drawer
    public void setCurrentDrawer(ShapeDrawer l) {
        if (currentDrawer == l)
            return;

        // activate new drawer
        gui.removeMouseListener(currentDrawer);
        gui.removeMouseMotionListener(currentDrawer);
        currentDrawer = l;
        gui.addMouseListener(currentDrawer);
        gui.addMouseMotionListener(currentDrawer);
    }

    // user selected new shape => reset the shape mode
    public void itemStateChanged(ItemEvent e) {
        if (e.getItem().equals("Scribble")) {
            setCurrentDrawer(scribbleDrawer);
        } else if (e.getItem().equals("Rectangle")) {
            setCurrentDrawer(rectDrawer);
        } else if (e.getItem().equals("Oval")) {
            setCurrentDrawer(ovalDrawer);
        } else if (e.getItem().equals("Triangle")) {
            setCurrentDrawer(triangleDrawer);
        } else if (e.getItem().equals("Rhombus")) {
            setCurrentDrawer(rhombusDrawer);
        } else if (e.getItem().equals("Fill Rectangle")) {
            setCurrentDrawer(rectFillDrawer);
        } else if (e.getItem().equals("Fill Oval")) {
            setCurrentDrawer(ovalFillDrawer);
        }
    }
}