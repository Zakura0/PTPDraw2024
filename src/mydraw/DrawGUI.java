package mydraw;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import mydraw.exceptions.*;
import mydraw.drawable.*;
import mydraw.listener.*;


/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

public class DrawGUI extends JFrame {
    Draw app; // A reference to the application, to send commands to.
    Color fgColor; // A referenec to the current foreground color (drawing color)
    Color bgColor; // A reference to the current background color
    DrawPanel frontPanel; // A reference to the GUI panel
    JPanel backPanel; // A reference to the Control panel
    BufferedImage buffImage; // A reference to the drawing panel (used to save the drawing)

    public Hashtable<String, Color> colors;
    public List<Drawable> commandQueue;
    public List<Drawable> undoStack;

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
        undoStack = new ArrayList<>();
        // Initializes the drawing panel
        doubleBuffering();
        setupGUI();
        redraw(frontPanel.getGraphics());
    }

    private void setupGUI() {
        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Menu");
        JMenuItem save = new JMenuItem("Save");
        JMenuItem saveText = new JMenuItem("Save Text");
        JMenuItem readText = new JMenuItem("Read Text");
        JMenuItem quit = new JMenuItem("Quit");
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

        JComboBox<String> bgColor_chooser = new JComboBox<>();
        bgColor_chooser.addItem("White");
        bgColor_chooser.addItem("Black");
        bgColor_chooser.addItem("Green");
        bgColor_chooser.addItem("Red");
        bgColor_chooser.addItem("Blue");

        // Create three buttons
        JButton clear = new JButton("Clear");
        JButton auto = new JButton("Auto");
        JButton undo = new JButton("Undo");
        JButton redo = new JButton("Redo");

        // Set a LayoutManager, and add the choosers and buttons to the window.
        backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        backPanel.add(new JLabel("Shape:"));
        backPanel.add(shape_chooser);
        backPanel.add(new JLabel("Color:"));
        backPanel.add(color_chooser);
        backPanel.add(new JLabel("BGColor:"));
        backPanel.add(bgColor_chooser);
        backPanel.add(clear);
        backPanel.add(auto);
        backPanel.add(undo);
        backPanel.add(redo);

        // Initializes the GUI front panel
        frontPanel = new DrawPanel(this);
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
        undo.addActionListener(new DrawActionListener("undo", app));
        redo.addActionListener(new DrawActionListener("redo", app));
        saveText.addActionListener(new DrawActionListener("save text", app));
        readText.addActionListener(new DrawActionListener("read text", app));

        fileMenu.add(save);
        fileMenu.add(saveText);
        fileMenu.add(readText);
        fileMenu.add(quit);
        menuBar.add(fileMenu);
        this.setJMenuBar(menuBar);

        // vorher ShapeManager hier!

        shape_chooser.addItemListener(new ShapeManager(this));

        color_chooser.addItemListener(new ColorItemListener(this, false));

        bgColor_chooser.addItemListener(new ColorItemListener(this, true));

        // Handle the window close request similarly
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                doCommand("quit");
            }
        });

        // Finally, set the size of the window, and pop it up
        this.frontPanel.setPreferredSize(new Dimension(800, 400));
        this.pack();
        this.setResizable(true);
        this.setVisible(true);
        Graphics g = frontPanel.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
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
        } else if (command.equals("undo")) {
            undo();
        } else if (command.equals("redo")) {
            redo();
        } else if (command.equals("save text")) {
            try {
                openSaveText();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("read text")) {
            openReadText();
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

    private void openSaveText() throws IOException {
        String drawingData = writeText();
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Text");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Textfile (*.txt)", "txt"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }
            File textFile = new File(filePath);
            FileWriter fileWriter = new FileWriter(textFile);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(drawingData);
            printWriter.close();
        }
    }

    private void openReadText() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Text");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Textfile (*.txt)", "txt"));
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            String filePath = fileToOpen.getAbsolutePath();
            try {
                readText(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void redraw(Graphics g) {
        buffImage = new BufferedImage(frontPanel.getWidth(), frontPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        g.setColor(bgColor);
        g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(bgColor);
        g2.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
        for (Drawable drawable : commandQueue) {
            drawable.draw(g);
            drawable.draw(g2);
        }
        g.dispose();
    }

    void undo() {
        if (commandQueue.size() > 0) {
            undoStack.add(commandQueue.get(commandQueue.size() - 1));
            commandQueue.remove(commandQueue.size() - 1);
            Graphics g = this.frontPanel.getGraphics();
            redraw(g);
        }
    }

    void redo() {
        if (undoStack.size() > 0) {
            commandQueue.add(undoStack.get(undoStack.size() - 1));
            undoStack.remove(undoStack.size() - 1);
            Graphics g = this.frontPanel.getGraphics();
            redraw(g);
        }
    }

    public String writeText() throws IOException {
        StringBuilder drawingData = new StringBuilder();

        for (Drawable drawable : commandQueue) {
            drawingData.append(drawable.toString()).append("\n");
        }
        return drawingData.toString();
    }

    public void readText(String filePath) throws IOException {
        commandQueue.clear();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String type = parts[0];
            if (type.equals("polyline")) {
                String points = parts[1];
                int color_rgb = Integer.parseInt(parts[2]);
                Color color = new Color(color_rgb, true);
                String[] pointStrings = points.split(":");
                List<Point> pointList = new ArrayList<>();
                for (String pointString : pointStrings) {
                    String[] coords = pointString.split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    pointList.add(new Point(x, y));
                }
                commandQueue.add(new polyLineCommand(this, color, pointList));
            } else {
                int x0 = Integer.parseInt(parts[1]);
                int y0 = Integer.parseInt(parts[2]);
                int x1 = Integer.parseInt(parts[3]);
                int y1 = Integer.parseInt(parts[4]);
                int color_rgb = Integer.parseInt(parts[5]);
                Color color = new Color(color_rgb, true);
                if (type.equals("rectangle")) {
                    commandQueue.add(new rectangleCommand(this, x0, y0, x1, y1, color));
                } else if (type.equals("oval")) {
                    commandQueue.add(new ovalCommand(this, x0, y0, x1, y1, color));
                } else if (type.equals("fillrectangle")) {
                    commandQueue.add(new fillrectangleCommand(this, x0, y0, x1, y1, color));
                } else if (type.equals("filloval")) {
                    commandQueue.add(new fillovalCommand(this, x0, y0, x1, y1, color));
                } else if (type.equals("rhombus")) {
                    commandQueue.add(new rhombusCommand(this, x0, y0, x1, y1, color));
                } else if (type.equals("triangle")) {
                    commandQueue.add(new triangleCommand(this, x0, y0, x1, y1, color));
                }
            }

        }
        reader.close();

        redraw(frontPanel.getGraphics());
    }

    public String getFGColor() {
        for (String key : colors.keySet()) {
            if (colors.get(key).equals(fgColor)) {
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
        if (width < 925) {
            throw new SizeException("Width must be at least 925 pixels.");
        }
        this.frontPanel.setPreferredSize(new Dimension(width, getHeight()));
        this.pack();
        this.buffImage = new BufferedImage(width, getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public void setHeight(int height) throws SizeException {
        if (height < 400) {
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
        redraw(frontPanel.getGraphics());
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
        commandQueue.add(new rectangleCommand(this, upper_left.x, upper_left.y, lower_right.x, lower_right.y, fgColor));
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
        commandQueue.add(new ovalCommand(this, upper_left.x, upper_left.y, lower_right.x, lower_right.y, fgColor));
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
        commandQueue.add(new polyLineCommand(this, fgColor, points));
    }

    public void drawTriangle(Point upper_left, Point lower_right) {
        Graphics g = this.frontPanel.getGraphics();
        g.setColor(this.fgColor);
        g.drawLine((lower_right.x + upper_left.x) / 2, upper_left.y, lower_right.x, lower_right.y);
        g.drawLine(upper_left.x, lower_right.y, lower_right.x, lower_right.y);
        g.drawLine(upper_left.x, lower_right.y, (lower_right.x + upper_left.x) / 2, upper_left.y);
        g.dispose();

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.drawLine((lower_right.x + upper_left.x) / 2, upper_left.y, lower_right.x, lower_right.y);
        g2.drawLine(upper_left.x, lower_right.y, lower_right.x, lower_right.y);
        g2.drawLine(upper_left.x, lower_right.y, (lower_right.x + upper_left.x) / 2, upper_left.y);
        g2.dispose();
        commandQueue.add(new triangleCommand(this, upper_left.x, upper_left.y, lower_right.x, lower_right.y, fgColor));
    }

    public void drawRhombus(Point upper_left, Point lower_right) {
        Graphics g = this.frontPanel.getGraphics();
        g.setColor(this.fgColor);
        g.drawLine((upper_left.x + lower_right.x) / 2, upper_left.y, lower_right.x, (upper_left.y + lower_right.y) / 2);
        g.drawLine(lower_right.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2,
                lower_right.y);
        g.drawLine((upper_left.x + lower_right.x) / 2, lower_right.y, upper_left.x, (upper_left.y + lower_right.y) / 2);
        g.drawLine(upper_left.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2, upper_left.y);
        g.dispose();

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.drawLine((upper_left.x + lower_right.x) / 2, upper_left.y, lower_right.x,
                (upper_left.y + lower_right.y) / 2);
        g2.drawLine(lower_right.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2,
                lower_right.y);
        g2.drawLine((upper_left.x + lower_right.x) / 2, lower_right.y, upper_left.x,
                (upper_left.y + lower_right.y) / 2);
        g2.drawLine(upper_left.x, (upper_left.y + lower_right.y) / 2, (upper_left.x + lower_right.x) / 2, upper_left.y);
        g2.dispose();
        commandQueue.add(new rhombusCommand(this, upper_left.x, upper_left.y, lower_right.x, lower_right.y, fgColor));
    }

    public void drawFillRectangle(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = this.frontPanel.getGraphics();
        g.setColor(this.fgColor);
        g.drawRect(x, y, width, height);
        g.fillRect(x, y, width, height);
        g.dispose();

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.drawRect(x, y, width, height);
        g2.fillRect(x, y, width, height);
        g2.dispose();
        commandQueue
                .add(new fillrectangleCommand(this, upper_left.x, upper_left.y, lower_right.x, lower_right.y, fgColor));
    }

    public void drawFillOval(Point upper_left, Point lower_right) {
        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x);
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = this.frontPanel.getGraphics();
        g.setColor(this.fgColor);
        g.drawOval(x, y, width, height);
        g.fillOval(x, y, width, height);
        g.dispose();

        Graphics g2 = this.buffImage.getGraphics();
        g2.setColor(this.fgColor);
        g2.drawOval(x, y, width, height);
        g2.fillOval(x, y, width, height);
        g2.dispose();
        commandQueue.add(new fillovalCommand(this, upper_left.x, upper_left.y, lower_right.x, lower_right.y, fgColor));
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

    public DrawPanel getDrawPanel() {
        return frontPanel;
    }

    public void clear() {
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear the drawing? \n This action is irreversible.", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            commandQueue.clear();
            Graphics g = frontPanel.getGraphics();
            g.setColor(bgColor);
            g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
            g.dispose();

            Graphics g2 = buffImage.getGraphics();
            g2.setColor(bgColor);
            g2.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
            g2.dispose();
        }
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
        Point p5 = new Point(100, 350);
        Point p6 = new Point(200, 250);
        drawFillRectangle(p5, p6);
        Point p7 = new Point(300, 350);
        Point p8 = new Point(400, 250);
        drawFillOval(p7, p8);
        Point p9 = new Point(500, 350);
        Point p10 = new Point(600, 250);
        drawRhombus(p9, p10);
        Point p11 = new Point(600, 250);
        Point p12 = new Point(700, 350);
        drawTriangle(p11, p12);
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

    /*
     * Helper Method: used to test clear without mocking the user confirmation
     * (assuming the user agreed for all test cases)
     */

    public void clearHelper() {
        commandQueue.clear();
        Graphics g = frontPanel.getGraphics();
        g.setColor(bgColor);
        g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
        g.dispose();

        Graphics g2 = buffImage.getGraphics();
        g2.setColor(bgColor);
        g2.fillRect(0, 0, buffImage.getWidth(), buffImage.getHeight());
        g2.dispose();
    }
}