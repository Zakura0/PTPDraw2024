package mydraw;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.*;

public class DrawGUI extends JFrame {
        Draw app; // A reference to the application, to send commands to.
        Color color;

        /**
         * The GUI constructor does all the work of creating the GUI and setting
         * up event listeners. Note the use of local and anonymous classes.
         */
        public DrawGUI(Draw application) {
            super("Draw"); // Create the window
            app = application; // Remember the application reference
            color = Color.black; // the current drawing color
            // selector for drawing modes

            JComboBox<String> shape_chooser = new JComboBox<>();
            shape_chooser.addItem("Scribble");
            shape_chooser.addItem("Rectangle");
            shape_chooser.addItem("Oval");

            // selector for drawing colors
            JComboBox<String> color_chooser = new JComboBox<>();
            color_chooser.addItem("Black");
            color_chooser.addItem("Green");
            color_chooser.addItem("Red");
            color_chooser.addItem("Blue");

            // Create three buttons
            JButton clear = new JButton("Clear");
            JButton quit = new JButton("Quit");
            JButton auto = new JButton("auto");

            // Set a LayoutManager, and add the choosers and buttons to the window.
            this.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 5));
            this.add(new JLabel("Shape:"));
            this.add(shape_chooser);
            this.add(new JLabel("Color:"));
            this.add(color_chooser);
            this.add(clear);
            this.add(quit);
            this.add(auto);

            // Here's a local class used for action listeners for the buttons
            class DrawActionListener implements ActionListener {
                private String command;

                public DrawActionListener(String cmd) {
                    command = cmd;
                }

                public void actionPerformed(ActionEvent e) {
                    app.doCommand(command);
                }
            }

            // Define action listener adapters that connect the buttons to the app
            clear.addActionListener(new DrawActionListener("clear"));
            quit.addActionListener(new DrawActionListener("quit"));
            auto.addActionListener(new DrawActionListener("auto"));

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

                    public void mousePressed(MouseEvent e) {
                        lastx = e.getX();
                        lasty = e.getY();
                    }

                    public void mouseDragged(MouseEvent e) {
                        Graphics g = gui.getGraphics();
                        int x = e.getX(), y = e.getY();
                        g.setColor(gui.color);
                        g.setPaintMode();
                        g.drawLine(lastx, lasty, x, y);
                        lastx = x;
                        lasty = y;
                    }
                }

                // if this class is active, rectangles are drawn
                class RectangleDrawer extends ShapeDrawer {
                    int pressx, pressy;
                    int lastx = -1, lasty = -1;

                    // mouse pressed => fix first corner of rectangle
                    public void mousePressed(MouseEvent e) {
                        pressx = e.getX();
                        pressy = e.getY();
                    }

                    // mouse released => fix second corner of rectangle
                    // and draw the resulting shape
                    public void mouseReleased(MouseEvent e) {
                        Graphics g = gui.getGraphics();
                        if (lastx != -1) {
                            // first undraw a rubber rect
                            g.setXORMode(gui.color);
                            g.setColor(gui.getBackground());
                            doDraw(pressx, pressy, lastx, lasty, g);
                            lastx = -1;
                            lasty = -1;
                        }
                        // these commands finish the rubberband mode
                        g.setPaintMode();
                        g.setColor(gui.color);
                        // draw the finel rectangle
                        doDraw(pressx, pressy, e.getX(), e.getY(), g);
                    }

                    // mouse released => temporarily set second corner of rectangle
                    // draw the resulting shape in "rubber-band mode"
                    public void mouseDragged(MouseEvent e) {
                        Graphics g = gui.getGraphics();
                        // these commands set the rubberband mode
                        g.setXORMode(gui.color);
                        g.setColor(gui.getBackground());
                        if (lastx != -1) {
                            // first undraw previous rubber rect
                            doDraw(pressx, pressy, lastx, lasty, g);

                        }
                        lastx = e.getX();
                        lasty = e.getY();
                        // draw new rubber rect
                        doDraw(pressx, pressy, lastx, lasty, g);
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

                ScribbleDrawer scribbleDrawer = new ScribbleDrawer();
                RectangleDrawer rectDrawer = new RectangleDrawer();
                OvalDrawer ovalDrawer = new OvalDrawer();
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
                    }
                }
            }

            shape_chooser.addItemListener(new ShapeManager(this));

            class ColorItemListener implements ItemListener {

                // user selected new color => store new color in DrawGUIs
                public void itemStateChanged(ItemEvent e) {
                    if (e.getItem().equals("Black")) {
                        color = Color.black;
                    } else if (e.getItem().equals("Green")) {
                        color = Color.green;
                    } else if (e.getItem().equals("Red")) {
                        color = Color.red;
                    } else if (e.getItem().equals("Blue")) {
                        color = Color.blue;
                    }
                }
            }

            color_chooser.addItemListener(new ColorItemListener());

            // Handle the window close request similarly
            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    app.doCommand("quit");
                }
            });

            // Finally, set the size of the window, and pop it up
            this.setSize(800, 400);
            this.setBackground(Color.white);
            // this.show(); //awt
            this.setVisible(true); // ++
        }

        /** API method: get fg color ... */
        public String getFGColor() {
            if (color == Color.black) {
                return "black";
            } else if (color == Color.green) {
                return "green";
            } else if (color == Color.red) {
                return "red";
            } else if (color == Color.blue) {
                return "blue";
            } else {
                return null;
            }
        }

        /**
         * @param new_color
         * @throws ColorException
         */
        public void setFGColor(String new_color) throws ColorException {
            switch (new_color.toLowerCase()) {
                case "black":
                    color = Color.black;
                    break;
                case "green":
                    color = Color.green;
                    break;
                case "red":
                    color = Color.red;
                    break;
                case "blue":
                    color = Color.blue;
                    break;
                default:
                    throw new ColorException("Invalid color!");
            }
        }

        public int getWidth() {
            if (this != null) {
                return this.getSize().width;
            }
            return 0;

        }

        public int getHeight() {
            if (this != null) {
                return this.getSize().height;
            }
            return 0;
        }

        public void setWidth(int width) throws SizeException {
            if (width < 50) {
                throw new SizeException();
            }
            this.setSize(new Dimension(width, this.getSize().height));
        }

        public void setHeight(int height) throws SizeException {
            if (height < 50) {
                throw new SizeException();
            }
            this.setSize(new Dimension(this.getSize().width, height));
        }

        public void setBGColor(String new_color) throws ColorException {
            switch (new_color.toLowerCase()) {
                case "black":
                    this.setBackground(Color.black);
                    ;
                    break;
                case "green":
                    this.setBackground(Color.green);
                    break;
                case "red":
                    this.setBackground(Color.red);
                    break;
                case "blue":
                    this.setBackground(Color.blue);
                case "white":
                    this.setBackground(Color.white);
                    break;
                default:
                    throw new ColorException("Invalid color!");
            }
        }

        public String getBGColor() {
            Color bgColor = this.getBackground();
            if (bgColor == null) {
                return null;
            }
            String[] colorSign = { "black", "green", "red", "blue", "default (white)" };
            Color[] colors = { Color.black, Color.green, Color.red, Color.blue, Color.white };
            for (int i = 0; i < colors.length; i++) {
                if (bgColor.equals(colors[i])) {
                    return colorSign[i];
                }
            }
            return null;
        }

        public void drawRectangle(Point upper_left, Point lower_right) {
            String fgColor = this.getFGColor();
            Color c = Color.getColor(fgColor);

            int x = Math.min(upper_left.x, lower_right.x);
            int y = Math.min(upper_left.y, lower_right.y);
            int width = Math.abs(lower_right.x - upper_left.x);
            int height = Math.abs(lower_right.y - upper_left.y);

            Graphics g = this.getGraphics();
            g.setColor(c);
            g.drawRect(x, y, width, height);
        }

        public void drawOval(Point upper_left, Point lower_right) {
            String fgColor = this.getFGColor();
            Color c = Color.getColor(fgColor);

            int x = Math.min(upper_left.x, lower_right.x);
            int y = Math.min(upper_left.y, lower_right.y);
            int width = Math.abs(lower_right.x - upper_left.x);
            int height = Math.abs(lower_right.y - upper_left.y);

            Graphics g = this.getGraphics();
            g.setColor(c);
            g.drawOval(x, y, width, height);
        }

        public void drawPolyLine(java.util.List<Point> points) {
            String fgColor = this.getFGColor();
            Color c = Color.getColor(fgColor);

            Graphics g = this.getGraphics();
            g.setPaintMode();
            g.setColor(c);

            for (int i = 1; i < points.size(); i++) {
                Point prevPoint = points.get(i - 1);
                Point currPoint = points.get(i);
                g.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
            }
        }

        public Image getDrawing() {
                BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics();
                paint(g);
                g.dispose();
                return image;
        }

        public void writeImage(Image img, String filename) throws IOException {
            MyBMPFile.write(filename, (BufferedImage) img);
        }

        public Image readImage(String filename) throws IOException {
            return MyBMPFile.read(filename);
        }

        public void clear() {
            this.getContentPane().getGraphics().clearRect(0, 0, this.getWidth(), this.getHeight());
            this.repaint();
        }

        public void autoDraw() {
            Point p1 = new Point(100, 200);
            Point p2 = new Point(200, 100);
            drawRectangle(p1, p2);
            Point p3 = new Point(300, 200);
            Point p4 = new Point(400, 100);
            drawOval(p3, p4);
            java.util.List<Point> points = new ArrayList<>();
            points.add(new Point(500, 200));
            points.add(new Point(600, 100));
            points.add(new Point(700, 200));
            drawPolyLine(points);
            Image image = this.getDrawing();
            try {
                this.writeImage(image, "test.bmp");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }