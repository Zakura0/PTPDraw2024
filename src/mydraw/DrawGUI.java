package mydraw;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

public class DrawGUI extends JFrame {
    Draw app; // A reference to the application, to send commands to.
    Color fgColor; // A referenec to the current foreground color (drawing color)
    Color bgColor; // A reference to the current background color
    JPanel frontPanel; // A reference to the GUI panel
    BufferedImage buffImage; // A reference to the drawing panel (used to save the drawing)
    Graphics g;

    public Hashtable<String, Color> colors;
    List<Drawable> commandQueue;

    /**
     * The GUI constructor does all the work of creating the GUI and setting
     * up event listeners. Note the use of local and anonymous classes.
     */
    public DrawGUI(Draw application) {
        super("Draw"); // Create the window
        app = application; // Remember the application reference
        colors = new Hashtable<>();
        colors.put("black", Color.BLACK);
        colors.put("green", Color.GREEN);
        colors.put("red", Color.RED);
        colors.put("blue", Color.BLUE);
        colors.put("white", Color.WHITE);
        fgColor = Color.BLACK;
        bgColor = Color.WHITE;
        commandQueue = new ArrayList<>();

        // Initializes the drawing panel
        doubleBuffering();
        setupGUI();
    }

    private void setupGUI() {
        // selector for drawing modes
        JComboBox<String> shape_chooser = new JComboBox<>();
        shape_chooser.addItem("Scribble");
        shape_chooser.addItem("Rectangle");
        shape_chooser.addItem("Oval");
        shape_chooser.addItem("Triangle");
        shape_chooser.addItem("Rhombus");
        shape_chooser.addItem("Fill Rectangle");
        shape_chooser.addItem("Fill Oval");

        // selector for drawing colors
        JComboBox<String> color_chooser = new JComboBox<>();
        color_chooser.addItem("Black");
        color_chooser.addItem("Green");
        color_chooser.addItem("Red");
        color_chooser.addItem("Blue");

        // Create three buttons
        JButton clear = new JButton("Clear");
        JButton quit = new JButton("Quit");
        JButton save = new JButton("Save");
        JButton auto = new JButton("Auto");

        // Set a LayoutManager, and add the choosers and buttons to the window.
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        backPanel.add(new JLabel("Shape:"));
        backPanel.add(shape_chooser);
        backPanel.add(new JLabel("Color:"));
        backPanel.add(color_chooser);
        backPanel.add(clear);
        backPanel.add(quit);
        backPanel.add(save);
        backPanel.add(auto);

        // Initializes the GUI front panel
        frontPanel = new JPanel();
        frontPanel.setBackground(bgColor);

        // Sets up the different layers/panels
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(backPanel, BorderLayout.NORTH);
        contentPane.add(frontPanel, BorderLayout.CENTER);

        // Define action listener adapters that connect the buttons to the app
        clear.addActionListener(new DrawActionListener("clear", app));
        quit.addActionListener(new DrawActionListener("quit", app));
        save.addActionListener(new DrawActionListener("save", app));
        auto.addActionListener(new DrawActionListener("auto", app));

        // vorher ShapeManager hier!

        shape_chooser.addItemListener(new ShapeManager(this));

        color_chooser.addItemListener(new ColorItemListener(this));

        // Handle the window close request similarly
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doCommand("quit");
            }
        });

        // Finally, set the size of the window, and pop it up
        this.frontPanel.setPreferredSize(new Dimension(800, 400));
        this.pack();
        this.frontPanel.setBackground(bgColor);
        this.setResizable(true);
        this.setVisible(true);
    }

    public void doCommand(String command) {
        if (command.equals("clear")) {
            clear();
        } else if (command.equals("quit")) {
            this.dispose();
            System.exit(0);
        } else if (command.equals("auto")) {
            autoDraw();
        } else if (command.equals("save")) {
            openSaveDialog();
        }

    }

    private void openSaveDialog() {
        Image ImgToSave = getDrawing();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Bitmap Image (*.bmp)", "bmp"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".bmp")) {
                filePath += ".bmp";
            }
            try {
                writeImage(ImgToSave, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void undo(){        
        if (commandQueue.size() > 0) {
            commandQueue.remove(commandQueue.size() - 1);
            clear();
            for (Drawable command : commandQueue) {
                command.draw(g);
            }
        }
    }

    public String getFGColor() {
        for (String key : colors.keySet()) {
            if (colors.get(key).equals(bgColor)) {
                return key;
            }
        }
        return null;
    }

    public void setFGColor(String new_color) throws ColorException {
        if (colors.containsKey(new_color.toLowerCase())) {
            fgColor = colors.get(new_color.toLowerCase());
        } else {
            throw new ColorException("Invalid color: " + new_color);
        }

    }

    public int getWidth() {
        return this.frontPanel.getSize().width;

    }

    public int getHeight() {
        return this.frontPanel.getSize().height;
    }

    public void setWidth(int width) throws SizeException {
        if (width < 750) {
            throw new SizeException("Width must be at least 600 pixels.");
        }
        this.frontPanel.setPreferredSize(new Dimension(width, getHeight()));
        this.pack();
        this.buffImage = new BufferedImage(width, getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public void setHeight(int height) throws SizeException {
        if (height < 70) {
            throw new SizeException("Height must be at least 70 pixels.");
        }
        this.frontPanel.setPreferredSize(new Dimension(getWidth(), height));
        this.pack();
        this.buffImage = new BufferedImage(getWidth(), height, BufferedImage.TYPE_INT_RGB);
    }

    public void setBGColor(String new_color) throws ColorException {
        if (colors.containsKey(new_color.toLowerCase())) {
            bgColor = colors.get(new_color.toLowerCase());
        } else {
            throw new ColorException("Invalid color: " + new_color);
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

    public String getBGColor() {
        for (String key : colors.keySet()) {
            if (colors.get(key).equals(bgColor)) {
                return key;
            }
        }
        return null;
    }

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
     **/

    public Image getDrawing() {
        return this.buffImage;
    }

    public void writeImage(Image img, String filename) throws IOException {
        MyBMPFile.write(filename, (BufferedImage) img);
    }

    public Image readImage(String filename) throws IOException {
        return MyBMPFile.read(filename);
    }

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
    }

    /**
     * Helper Method: Sets up a BufferedImage to save the drawings on an extra pane
     **/
    private void doubleBuffering() {
        buffImage = new BufferedImage(800, 400, BufferedImage.TYPE_INT_RGB);
        Graphics g = buffImage.createGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
        g.dispose();
    }

    public String intToCol(int pixel) {
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        Color col = new Color(red, green, blue);
        for (String key : colors.keySet()) {
            if (colors.get(key).equals(col)) {
                return key;
            }
        }
        return null;
    }

}