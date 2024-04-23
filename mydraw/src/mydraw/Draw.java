package mydraw;

import java.awt.*;
import java.io.IOException;


/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402
 */

public class Draw {
    protected DrawGUI window;
    Color fgColor;
    Color bgColor;
    int height;
    int width;
    public static void main(String[] args) {
       new Draw();
    }

    public Draw() {
        width = 800;
        height = 400;
        fgColor = Color.BLACK;
        bgColor = Color.WHITE;
        window = new DrawGUI(this);
        
    }

    public void doCommand(String command) {
        if (command.equals("clear")) {
            clear();
        } else if (command.equals("quit")) {
            window.dispose();
            System.exit(0);
        } else if (command.equals("auto")){
            autoDraw();
        }

    }

    /**
     * API method: get height ...
     * more details here ...
     */

    public int getHeight() {
        return window.getHeight();
    }

    /** API method: set height ... */
    public void setHeight(int height) throws SizeException {
        window.setHeight(height);
    }

    /** API method: get width ... */
    public int getWidth() {
        return window.getWidth();
    }

    /** API method: set width ... */
    public void setWidth(int width) throws SizeException {
        window.setWidth(width);
    }

    /** API method: set fg color ... */
    public void setFGColor(String new_color) throws ColorException {
        window.setFGColor(new_color);
    }

    /** API method: get fg color ... */
    public String getFGColor() {
        return window.getFGColor();
    }

    /** API method: set bg color ... */
    public void setBGColor(String new_color) throws ColorException {
        window.setBGColor(new_color);
    }

    /** API method: get bg color ... */
    public String getBGColor() {
        return window.getBGColor();
    }

    /** API method: get drawing ... */
    public Image getDrawing() {
        return window.getDrawing();
    }

    /** API method: writeImage ... */
    public void writeImage(Image img, String filename) throws IOException {
        window.writeImage(img, filename);
    }

    /** API method: readImage ... */
    public Image readImage(String filename) throws IOException {
        return window.readImage(filename);
    }

    /** API method: clear ... */
    public void clear() {
        window.clear();
    }

    /** API - test method: paint every shape ... */
    public void autoDraw() {
        window.autoDraw();
        // paint your testimage now using API methods
    }

    /** API: paint a rectangle ... */
    public void drawRectangle(Point upper_left, Point lower_right) {
        window.drawRectangle(upper_left, lower_right);
    }

    /** API: paint an oval ... */
    public void drawOval(Point upper_left, Point lower_right) {
        window.drawOval(upper_left, lower_right);
    }

    /** API: paint a polyline/scribble ... */
    public void drawPolyLine(java.util.List<Point> points) {
        window.drawPolyLine(points);
    }

}