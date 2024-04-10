package mydraw;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;

/*
 * @authors Giahung Bui 7557640 , Ben Woller 
 */

public  class Draw {
    public static void main(String[] args) {
        Draw app = new Draw();
        
        DrawGUI gui = app.window;

        try {
            gui.setBGColor("white");
            gui.setFGColor("red");
            gui.setSize(800,400);
            Point upperLeft = new Point(250,200);
            Point lowerRight = new Point(500,100);
            app.drawRectangle(upperLeft, lowerRight);
            gui.setVisible(true);
        }
        catch (ColorException e){
            System.err.println("invalid color");
        } 
        //drawRectangle funktioniert nicht wie gedacht??
    }

    public Draw() {
        window = new DrawGUI(this);
    }

    protected DrawGUI window;

    public void doCommand(String command) {
        if (command.equals("clear")) {
            Graphics g = window.getGraphics();
            g.setColor(window.getBackground());
            g.fillRect(0, 0, window.getSize().width, window.getSize().height);
        } else if (command.equals("quit")) {
            window.dispose();
            System.exit(0);
        }
    }
    
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
            Choice shape_chooser = new Choice();
            shape_chooser.add("Scribble");
            shape_chooser.add("Rectangle");
            shape_chooser.add("Oval");
    
            // selector for drawing colors
            Choice color_chooser = new Choice();
            color_chooser.add("Black");
            color_chooser.add("Green");
            color_chooser.add("Red");
            color_chooser.add("Blue");
    
            // Create two buttons
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
    
        /** API method: get fg color ...*/
        public String getFGColor() {
            if (color == Color.black) {
                return "Black";
            } else if (color == Color.green) {
                return "Green";
            } else if (color == Color.red) {
                return "Red";
            } else if (color == Color.blue) {
                return "Blue";
            } else {
                return null;
            }
        }
    
        /**
         * @param new_color
         * @throws ColorException
         */
        public void setFGColor(String new_color) throws ColorException{
            switch(new_color.toLowerCase()) {
                case "black":
                    color = Color.black;
                    break;
                case "green":
                color = Color.green;
                    break;
                case "red":
                    color = Color.red;
                    break;
                case  "blue":
                    color = Color.blue;
                    break;
                default:
                    throw new ColorException("Invalid color!");
            }
        }
    
        public int getWidth() {
            return this.getSize().width;
        }
    
        public int getHeight() {
            return this.getSize().height;
        }
    
        public void setWidth(int width) throws SizeException{
            this.setSize(new Dimension(width, this.getSize().height));
        }
    
        public void setHeight(int height) throws SizeException {
            this.setSize(new Dimension(this.getSize().width, height));
        }

        public void setBGColor(String new_color) throws ColorException{
            switch(new_color.toLowerCase()) {
                case "black":
                    this.setBackground(Color.black);;
                    break;
                case "green":
                this.setBackground(Color.green);
                    break;
                case "red":
                    this.setBackground(Color.red);
                    break;
                case  "blue":
                    this.setBackground(Color.blue);
                case "white":
                    this.setBackground(Color.white);
                    break;
                default:
                    throw new ColorException("Invalid color!");
            }
        }
    
            public String getBGColor(){
                Color bgColor = this.getBackground();
                if (bgColor == null){
                    return null;
                }
                String [] colorSign = {"Black", "Green", "Red", "Blue", "default (white)"};
                Color [] colors = {Color.BLACK, Color.GREEN, Color.RED, Color.BLUE, Color.WHITE};
                for (int i = 0; i < colors.length; i++) {
                    if (bgColor.equals(colors[i])){
                        return colorSign[i];
                    }
                }
                return null;
            }
        }

        public void drawRectangle(Point upper_left, Point lower_right){
            String  fgColorString = window.getFGColor();
            Color fgColor = Color.decode(fgColorString);

            int x = Math.min(upper_left.x, lower_right.x);
            int y = Math.min(upper_left.y, lower_right.y);
            int width = Math.abs(lower_right.x - upper_left.x);
            int height = Math.abs(lower_right.y - upper_left.y);

            Graphics g = window.getGraphics();
            g.setPaintMode();
            g.setColor(fgColor);
            g.drawRect(x, y, width, height);
        }

        public void drawOval(Point upper_left, Point lower_right){
            String  fgColorString = window.getFGColor();
            Color fgColor = Color.decode(fgColorString);

            int x = Math.min(upper_left.x, lower_right.x);
            int y = Math.min(upper_left.y, lower_right.y);
            int width = Math.abs(lower_right.x - upper_left.x);
            int height = Math.abs(lower_right.y - upper_left.y);

            Graphics g = window.getGraphics();
            g.setPaintMode();
            g.setColor(fgColor);
            g.drawOval(x, y, width, height);
        }
        
        public void drawPolyLine(java.util.List<Point> points){
            String  fgColorString = window.getFGColor();
            Color fgColor = Color.decode(fgColorString);
            Graphics g = window.getGraphics();
            g.setPaintMode();
            g.setColor(fgColor);
            
                for (int i = 1; i < points.size(); i++) {
                    Point prevPoint = points.get(i - 1);
                    Point currPoint = points.get(i);
                    g.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
                }
            }
        

        
        public Image getDrawing(){
            BufferedImage image = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            window.paint(g);
            g.dispose();
            return image;
        }

        public void clear() {
            window.getContentPane().removeAll();
            window.repaint();
         }

         public void autoDraw() {
            
         }        
}