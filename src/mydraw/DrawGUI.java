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
import java.util.List;

import javax.swing.*;

/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402
 */

public class DrawGUI extends JFrame {
    Draw app; // A reference to the application, to send commands to.
    Color fgColor; // A referenec to the current foreground color (drawing color)
    Color bgColor; // A reference to the current background color
    int width;  // A reference to the current window width
    int height; // A reference to the current window height
    JPanel frontPanel; // A reference to the GUI panel
    BufferedImage buffImage; // A reference to the drawing panel (used to save the drawing)

    /**
     * The GUI constructor does all the work of creating the GUI and setting
     * up event listeners. Note the use of local and anonymous classes.
     */
    public DrawGUI(Draw application) {
        super("Draw"); // Create the window
        app = application; // Remember the application reference
        fgColor = app.fgColor;
        bgColor = app.bgColor;
        width = app.width;
        height = app.height;

        // Initializes the drawing panel
        doubleBuffering();

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
        JPanel backPanel = new JPanel();
        backPanel.setLayout(new FlowLayout());
        backPanel.add(new JLabel("Shape:"));
        backPanel.add(shape_chooser);
        backPanel.add(new JLabel("Color:"));
        backPanel.add(color_chooser);
        backPanel.add(clear);
        backPanel.add(quit);
        backPanel.add(auto);

        // Initializes the GUI front panel
        frontPanel = new JPanel();
        frontPanel.setBackground(Color.WHITE);

        // Sets up the different layers/panels
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(backPanel, BorderLayout.NORTH);
        contentPane.add(frontPanel, BorderLayout.CENTER);

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
                    Point p = gui.frontPanel.getMousePosition();
                    if (p != null) {
                        lastx = p.x;
                        lasty = p.y;
                    }
                }

                public void mouseDragged(MouseEvent e) {
                    Graphics g = gui.frontPanel.getGraphics();
                    Graphics g2 = gui.buffImage.getGraphics();
                    Point p = gui.frontPanel.getMousePosition();
                    if (p != null) {
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
                    fgColor = Color.black;
                } else if (e.getItem().equals("Green")) {
                    fgColor = Color.green;
                } else if (e.getItem().equals("Red")) {
                    fgColor = Color.red;
                } else if (e.getItem().equals("Blue")) {
                    fgColor = Color.blue;
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
        this.frontPanel.setPreferredSize(new Dimension(app.width, app.height));
        this.pack();
        this.frontPanel.setBackground(app.bgColor);
        this.setSize(800, 400);
        this.setBackground(Color.white);
        this.setResizable(false);
        // this.show(); //awt
        this.setVisible(true); // ++
    }

    /**
     * API Method: retrieves current foreground color
     * Return type: String
     * **/
    public String getFGColor() {
        if (fgColor == Color.black) {
            return "black";
        } else if (fgColor == Color.green) {
            return "green";
        } else if (fgColor == Color.red) {
            return "red";
        } else if (fgColor == Color.blue) {
            return "blue";
        } else {
            return null;
        }
    }

    /**
     * API Method: sets current foreground color.
     * Params: String new_color
     * Available Colors: "black", "green", "red", "blue"
     * Throws an ColorException if the color to be set is not recognized
     * **/
    public void setFGColor(String new_color) throws ColorException {
        switch (new_color.toLowerCase()) {
            case "black":
                fgColor = Color.black;
                break;
            case "green":
                fgColor = Color.green;
                break;
            case "red":
                fgColor = Color.red;
                break;
            case "blue":
                fgColor = Color.blue;
                break;
            default:
                throw new ColorException("Invalid color!");
        }
    }

    /**
     * API Method: retrieves current width of the window
     * Return type: int
     * **/
    public int getWidth() {
        return width;

    }

    /**
     * API Method: retrieves current height of the window
     * Return type: int
     * **/
    public int getHeight() {
        return height;
    }

    /**
     * API Method: sets current window width
     * Params: int width
     * Throws a SizeException if the width is negative
     * **/
    public void setWidth(int width) throws SizeException {
        this.frontPanel.setPreferredSize(new Dimension(width, getHeight()));
        this.pack();
        this.buffImage = new BufferedImage(width, getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    /**
     * API Method: sets current window height.
     * Params: int height
     * Throws a SizeException if the height is negative
     * **/
    public void setHeight(int height) throws SizeException {
        this.frontPanel.setPreferredSize(new Dimension(getWidth(), height));
        this.pack();
        this.buffImage = new BufferedImage(getWidth(), height, BufferedImage.TYPE_INT_RGB);
    }

    /**
     * API Method: sets current background color.
     * Params: String new_color
     * Available Colors: "black", "green", "red", "blue", "white"
     * Throws an ColorException if the color to be set is not recognized
     * **/
    public void setBGColor(String new_color) throws ColorException {
        switch (new_color.toLowerCase()) {
            case "black":
                bgColor = Color.black;
                break;
            case "green":
                bgColor = Color.green;
                break;
            case "red":
                bgColor = Color.red;
                break;
            case "blue":
                bgColor = Color.blue;
                break;
            case "white":
                bgColor = Color.white;
                break;
            default:
                throw new ColorException("Invalid color!");
        }
        Graphics g = this.frontPanel.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, this.frontPanel.getWidth(), this.frontPanel.getHeight());
        g.dispose();

        Graphics g2 = this.buffImage.createGraphics();
        g2.setColor(bgColor);
        g2.fillRect(0, 0, this.buffImage.getWidth(), this.buffImage.getHeight());
        g2.dispose();
    }

    /**
     * API Method: retrieves current background color
     * Return type: String
     * **/
    public String getBGColor() {
        if (bgColor == Color.black) {
            return "black";
        } else if (bgColor == Color.green) {
            return "green";
        } else if (bgColor == Color.red) {
            return "red";
        } else if (bgColor == Color.blue) {
            return "blue";
        } else if (bgColor == Color.white){
            return "white";
        } else {
            return null;
        }
    }

    /**
     * API Method: draws a rectangle on the front panel and drawing panel, where two points are used
     * to calculate the overall width and height of the rectangle
     * Prams: Point upper_left, Point lower_right
     * **/
    public void drawRectangle(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = this.frontPanel.getGraphics();
        g.setColor(this.fgColor);
        g.drawRect(x, y, width, height);
        g.dispose();

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.drawRect(x, y, width, height);
        g2.dispose();
    }

    /**
     * API Method: draws a circle/ellipse on the front panel and drawing panel, where two points are used
     * to calculate the overall width and height of the circle/ellipse
     * Prams: Point upper_left, Point lower_right
     * **/
    public void drawOval(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = this.frontPanel.getGraphics();
        g.setColor(this.fgColor);
        g.drawOval(x, y, width, height);
        g.dispose();

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.drawOval(x, y, width, height);
        g2.dispose();
    }

    /**
     * API Method: draws a polyline on the front panel and drawing panel, where multiple points are used
     * to draw lines from one point to another: e.g. p1 - p2 - p3
     * Prams: List<Point> points
     * **/
    public void drawPolyLine(java.util.List<Point> points) {
        Graphics g = this.frontPanel.getGraphics();
        g.setPaintMode();
        g.setColor(this.fgColor);

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.setPaintMode();

        for (int i = 1; i < points.size(); i++) {
            Point prevPoint = points.get(i - 1);
            Point currPoint = points.get(i);
            g.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
            g2.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
        }
        g.dispose();
        g2.dispose();
    }

    /**
     * API Method: retrieves the current drawing as a BufferedImage
     * Return type: BufferedImage
     * **/

    public Image getDrawing() {
        return this.buffImage;
    }

    /**
     * API Method: saves an BufferedImage to a file and saves it under a given
     * name in the current directory.
     * Params: Image img, String filename
     * Throws an IOException if image cant be saved
     * **/

    public void writeImage(Image img, String filename) throws IOException {
        MyBMPFile.write(filename, (BufferedImage) img);
    }

    /**
     * API Method: reads a file and gets the content of the BMP file as
     * a buffered image
     * Returns: BufferedImage
     * Throws an IOException if filename cant be found
     * **/

    public Image readImage(String filename) throws IOException {
        return MyBMPFile.read(filename);
    }

    /**
     * API Method: clears the drawing pane and sets the color to the current
     * bgColor
     * **/
    public void clear() {
        Graphics g = frontPanel.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
        g.dispose();

        Graphics g2 = buffImage.getGraphics();
        g2.setColor(bgColor);
        g2.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
        g2.dispose();
    }

    /**
     * API Method: script, that draws different shapes automatically on
     * the drawing pane. Saves them afterward as an BMP image.
     * **/
    public void autoDraw() {
        Point p1 = new Point(100, 200);
        Point p2 = new Point(200, 100);
        try {
            setFGColor("red");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        drawRectangle(p1, p2);
        Point p3 = new Point(300, 200);
        Point p4 = new Point(400, 100);
        try {
            setFGColor("blue");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        drawOval(p3, p4);
        Point pl1 = new Point(500, 200);
        Point pl2 = new Point(600, 100);
        Point pl3 = new Point(700, 200);
        try {
            setFGColor("green");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        drawPolyLine(List.of(pl1, pl2, pl3));
        try {
            writeImage(buffImage, "output.bmp");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper Method: Sets up a BufferedImage to save the drawings on an extra pane
     * **/
    private void doubleBuffering() {
        buffImage = new BufferedImage(app.width, app.height, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImage.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
        g.dispose();
    }

}