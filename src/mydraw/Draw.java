package mydraw;

import java.awt.*;
import java.io.IOException;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

import mydraw.exceptions.ColorException;
import mydraw.exceptions.SizeException;

public class Draw {
    protected DrawGUI window;

    public static void main(String[] args) {
        new Draw();
    }

    public Draw() {
        window = new DrawGUI(this);
    }

    /**
     * API Method: gets the current window
     * Return type: DrawGUI
     **/
    public DrawGUI getWindow() {
        return window;
    }

    /**
     * API Method: retrieves current height of the window
     * Return type: int
     **/
    public int getHeight() {
        return window.getHeight();
    }

    /**
     * API Method: sets current window height.
     * Params: int height
     * Throws a SizeException if the height is negative
     **/
    public void setHeight(int height) throws SizeException {
        window.setHeight(height);
    }

    /**
     * API Method: retrieves current width of the window
     * Return type: int
     **/
    public int getWidth() {
        return window.getWidth();
    }

    /**
     * API Method: sets current window width
     * Params: int width
     * Throws a SizeException if the width is smaller than 750 (due to MacOS
     * incompability)
     **/
    public void setWidth(int width) throws SizeException {
        window.setWidth(width);
    }

    /**
     * API Method: sets current foreground color.
     * Params: String new_color
     * Available Colors: "black", "green", "red", "blue"
     * Throws an ColorException if the color to be set is not recognized
     **/
    public void setFGColor(String new_color) throws ColorException {
        window.setFGColor(new_color);
    }

    /**
     * API Method: retrieves current foreground color
     * Return type: String
     **/
    public String getFGColor() {
        return window.getFGColor();
    }

    /**
     * API Method: sets current background color.
     * Params: String new_color
     * Available Colors: "black", "green", "red", "blue", "white"
     * Throws an ColorException if the color to be set is not recognized
     **/
    public void setBGColor(String new_color) throws ColorException {
        window.setBGColor(new_color);
    }

    /**
     * API Method: retrieves current background color
     * Return type: String
     **/
    public String getBGColor() {
        return window.getBGColor();
    }

    /**
     * API Method: retrieves the current drawing as a BufferedImage
     * Return type: BufferedImage
     **/
    public Image getDrawing() {
        return window.getDrawing();
    }

    /**
     * API Method: saves an BufferedImage to a file and saves it under a given
     * name in the current directory.
     * Params: Image img, String filename
     * Throws an IOException if image cant be saved
     **/
    public void writeImage(Image img, String filename) throws IOException {
        window.writeImage(img, filename);
    }

    /**
     * API Method: reads a file and gets the content of the BMP file as
     * a buffered image
     * Returns: BufferedImage
     * Throws an IOException if filename cant be found
     **/
    public Image readImage(String filename) throws IOException {
        return window.readImage(filename);
    }

    /**
     * API Method: clears the drawing pane and sets the color to the current
     * bgColor
     **/
    public void clear() {
        window.clear();
    }

    /**
     * API Method: script, that draws different shapes automatically on
     * the drawing pane. Saves them afterward as an BMP image.
     **/
    public void autoDraw() {
        window.autoDraw();
        // paint your testimage now using API methods
    }

    /**
     * API Method: draws a rectangle on the front panel and drawing panel, where two
     * points are used
     * to calculate the overall width and height of the rectangle
     * Prams: Point upper_left, Point lower_right
     **/
    public void drawRectangle(Point upper_left, Point lower_right) {
        window.drawRectangle(upper_left, lower_right);
    }

    /**
     * API Method: draws a circle/ellipse on the front panel and drawing panel,
     * where two points are used
     * to calculate the overall width and height of the circle/ellipse
     * Prams: Point upper_left, Point lower_right
     **/
    public void drawOval(Point upper_left, Point lower_right) {
        window.drawOval(upper_left, lower_right);
    }

    /**
     * API Method: draws a polyline on the front panel and drawing panel, where
     * multiple points are used
     * to draw lines from one point to another: e.g. p1 - p2 - p3
     * Prams: List<Point> points
     **/
    public void drawPolyLine(java.util.List<Point> points) {
        window.drawPolyLine(points);
    }

    public void drawTriangle(Point upper_left, Point lower_right) {
        window.drawTriangle(upper_left, lower_right);
    }

    public void drawRhombus(Point upper_left, Point lower_right) {
        window.drawRhombus(upper_left, lower_right);
    }

    public void drawFillRectangle(Point upper_left, Point lower_right) {
        window.drawFillRectangle(upper_left, lower_right);
    }

    public void drawFillOval(Point upper_left, Point lower_right) {
        window.drawFillOval(upper_left, lower_right);
    }

    public String intToCol(int pixel) {
        return window.intToCol(pixel);
    }
}