package mydraw;

import java.awt.*;
import java.io.IOException;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

import mydraw.exceptions.ColorException;
import mydraw.exceptions.SizeException;
import mydraw.exceptions.TxtIOException;

public class Draw {
    private DrawGUI window;
    private DrawShape shape;
    private DrawSaveImage save;
    private DrawFunctions func;
    private DrawTextReader read;
    public static void main(String[] args) {
        new Draw();
    }

    public Draw() {
        window = new DrawGUI(this);
    }

    /**
     * API Method: gets the current window
     * @return type: DrawGUI
     **/
    public DrawGUI getWindow() {
        return window;
    }

    /**
     * API Method: retrieves current height of the window
     * @return type: int
     **/
    public int getHeight() {
        return window.getHeight();
    }

    /**
     * API Method: sets current window height.
     * @param: int height
     * @throws SizeException if the height is smaller than 400
     **/
    public void setHeight(int height) throws SizeException {
        window.setHeight(height);
    }

    /**
     * API Method: retrieves current width of the window
     * @return type: int
     **/
    public int getWidth() {
        return window.getWidth();
    }

    /**
     * API Method: sets current window width
     * @param int width
     * @throws SizeException if the width is smaller than 925 (due to MacOS
     * incompability)
     **/
    public void setWidth(int width) throws SizeException {
        window.setWidth(width);
    }

    /**
     * API Method: sets current foreground color.
     * Available Colors: "black", "green", "red", "blue"
     * @throws ColorException if the color to be set is not recognized
     * @param String new_color
     **/
    public void setFGColor(String new_color) throws ColorException {
        window.setFGColor(new_color);
    }

    /**
     * API Method: retrieves current foreground color
     * @return String
     **/
    public String getFGColor() {
        return window.getFGColor();
    }

    /**
     * API Method: sets current background color.
     * Available Colors: "black", "green", "red", "blue", "white"
     * @param String new_color
     * @throws ColorException if the color to be set is not recognized
     **/
    public void setBGColor(String new_color) throws ColorException {
        window.setBGColor(new_color);
    }

    /**
     * API Method: retrieves current background color
     * @return String
     **/
    public String getBGColor() {
        return window.getBGColor();
    }

    /**
     * API Method: retrieves the current drawing as a BufferedImage
     * @return BufferedImage
     **/
    public Image getDrawing() {
        return window.getDrawing();
    }

    /**
     * API Method: saves an BufferedImage to a file and saves it under a given
     * name in the current directory.
     * @param Image img, 
     * @param String filename
     * @throws IOException if image cant be saved
     **/
    public void writeImage(Image img, String filename) throws IOException {
        save.writeImage(img, filename);
    }

    /**
     * API Method: reads a file and gets the content of the BMP file as
     * a buffered image
     * @return BufferedImage
     * @throws IOException if filename cant be found
     **/
    public Image readImage(String filename) throws IOException {
        return save.readImage(filename);
    }

    /**
     * API Method: clears the drawing pane by replacing it with a rectangle in the size of the front panel in the current fgColor.
     * Also clears the command queue, not allowing any undo()'s.
     **/
    public void clear(DrawFunctions func) {
        func.clear(true);
    }

    /**
     * API Method: script, that draws different shapes automatically on
     * the drawing pane. Saves them afterward as an BMP image.
     **/
    public void autoDraw() {
        func.autoDraw();
        // paint your testimage now using API methods
    }

    /**
     * API Method: draws a rectangle on the front panel and drawing panel, where two
     * points are used
     * to calculate the overall width and height of the rectangle
     * @param Point upper_left 
     * @param Point lower_right
     **/
    public void drawRectangle(Point upper_left, Point lower_right) {
        shape.drawRectangle(upper_left, lower_right);
    }

    /**
     * API Method: draws a circle/ellipse on the front panel and drawing panel,
     * where two points are used
     * to calculate the overall width and height of the circle/ellipse
     * @param Point upper_left 
     * @param Point lower_right
     **/
    public void drawOval(Point upper_left, Point lower_right) {
        shape.drawOval(upper_left, lower_right);
    }

    /**
     * API Method: draws a polyline on the front panel and drawing panel, where
     * multiple points are used
     * to draw lines from one point to another: e.g. p1 - p2 - p3
     * @param List<Point> points
     **/
    public void drawPolyLine(java.util.List<Point> points) {
        shape.drawPolyLine(points);
    }
    /**
     * API Method: draws a triangle on the front panel and drawing panel, where two points are used
     * to calculate the overall width and height of the rectangle
     * @param Point upper_left 
     * @param Point lower_right
     */
    public void drawTriangle(Point upper_left, Point lower_right) {
        shape.drawTriangle(upper_left, lower_right);
    }

    /**
     * API Method: draws a rhombus on the front panel and drawing panel,
     * where two points are used
     * to calculate the overall width and height of the rhombus
     * @param Point upper_left 
     * @param Point lower_right
     **/
    public void drawRhombus(Point upper_left, Point lower_right) {
        shape.drawRhombus(upper_left, lower_right);
    }
    /**
     * API Method: draws a filled rectangle with a the selected fgColor on the front panel and drawing panel,
     * where two points are used
     * to calculate the overall width and height of the rectangle
     * @param Point upper_left 
     * @param Point lower_right
     **/
    public void drawFillRectangle(Point upper_left, Point lower_right) {
        shape.drawFillRectangle(upper_left, lower_right);
    }
    /**
     * API Method: draws a filled Oval with a the selected fgColor on the front panel and drawing panel,
     * where two points are used
     * to calculate the overall width and height of the Oval
     * @param Point upper_left 
     * @param Point lower_right
     **/
    public void drawFillOval(Point upper_left, Point lower_right) {
        shape.drawFillOval(upper_left, lower_right);
    }
    /*
     * API Method: undo's the last graphic object in the command queue
     */
    public void undo() {
        func.undo();
    }
    /*
     * API Method: redo's the last graphic object in the command queue
     */
    public void redo() {
        func.redo();
    }
    /*
    * API Method: reads a .txt file containing command queue commands and draws
    * the graphic objects on the front panel and drawing panel.
    * @param String filePath
    */
    public void readText(String filePath) throws TxtIOException, IOException {
        try {
            read.readText(filePath);
        } catch (TxtIOException e) {
            e.printStackTrace();
        }
    }
    /*
    * API Method: writes the comamnds in the command queue on a .txt file
    * 
    */
    public void writeText() throws IOException {
        try {
            window.openSaveText();
        } catch (TxtIOException e) {
            e.printStackTrace();
        }
    }
}