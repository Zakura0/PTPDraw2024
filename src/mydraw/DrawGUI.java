package mydraw;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
    public static Hashtable<String, Color> colors;
    public List<Drawable> commandQueue;
    public List<Drawable> undoStack;
    public DrawShape shape;
    public DrawTextReader read;
    public DrawTextWriter write;
    public DrawSaveImage save;
    public DrawFunctions func;

    /**
     * The GUI constructor does all the work of creating the GUI and setting
     * up event listeners. Note the use of local and anonymous classes.
     */
    public DrawGUI(Draw application) {
        super("Draw"); // Create the window
        app = application; // Remember the application reference
        DrawShape shape = new DrawShape(this);
        this.shape = shape;
        DrawTextReader read = new DrawTextReader(this);
        this.read = read;
        DrawTextWriter write = new DrawTextWriter(this);
        this.write = write;
        DrawSaveImage save = new DrawSaveImage(this);
        this.save = save;
        DrawFunctions func = new DrawFunctions(this, shape);
        this.func = func;
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

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                redraw(frontPanel.getGraphics());
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
            int dialogResult = JOptionPane.showConfirmDialog(null,
                    "Are you sure you want to clear the drawing? \n This action is irreversible.", "Confirmation",
                    JOptionPane.YES_NO_OPTION);
            boolean dialogResultBool = dialogResult == JOptionPane.YES_OPTION;
            func.clear(dialogResultBool);
        } else if (command.equals("quit")) {
            this.dispose();
            System.exit(0);
        } else if (command.equals("auto")) {
            func.autoDraw();
        } else if (command.equals("save")) {
            openSaveDialog();
        } else if (command.equals("undo")) {
            func.undo();
        } else if (command.equals("redo")) {
            func.redo();
        } else if (command.equals("save text")) {
            try {
                openSaveText();
            } catch (IOException | TxtIOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("read text")) {
            try {
                openReadText();
            } catch (TxtIOException e) {
                e.printStackTrace();
            }
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
                save.writeImage(ImgToSave, filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void openSaveText() throws TxtIOException, IOException {
        String drawingData = write.writeText();
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

    private void openReadText() throws TxtIOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Text");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Textfile (*.txt)", "txt"));
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToOpen = fileChooser.getSelectedFile();
            String filePath = fileToOpen.getAbsolutePath();
            try {
                read.readText(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void redraw(Graphics g) {
        buffImage = new BufferedImage(frontPanel.getWidth(), frontPanel.getHeight(), BufferedImage.TYPE_INT_RGB);
        g.setColor(bgColor);
        g.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
        Graphics g2 = buffImage.getGraphics();
        g2.setColor(bgColor);
        g2.fillRect(0, 0, frontPanel.getWidth(), frontPanel.getHeight());
        for (Drawable drawable : commandQueue) {
            drawable.draw(g);
            drawable.draw(g2);
        }
        g.dispose();
        g2.dispose();
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
        if (width < 800) {
            throw new SizeException("Width must be at least 800 pixels.");
        }
        this.frontPanel.setPreferredSize(new Dimension(width, getHeight()));
        this.pack();
        this.buffImage = new BufferedImage(width, getHeight(), BufferedImage.TYPE_INT_RGB);
    }

    public void setHeight(int height) throws SizeException {
        if (height < 400) {
            throw new SizeException("Height must be at least 400 pixels.");
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

    /**
     * API Method: retrieves the current drawing as a BufferedImage
     * Return type: BufferedImage
     **/

    public Image getDrawing() {
        return this.buffImage;
    }

    public DrawPanel getDrawPanel() {
        return frontPanel;
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

    public static String getKeyByValue(Color value) {
        for (Map.Entry<String, Color> entry : colors.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return "black"; // Falls kein Key gefunden wird
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