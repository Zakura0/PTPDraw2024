package mydraw.test;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

import mydraw.Draw;
import mydraw.DrawFunctions;
import mydraw.DrawGUI;
import mydraw.DrawSaveImage;
import mydraw.DrawShape;
import mydraw.DrawTextReader;
import mydraw.DrawTextWriter;
import mydraw.drawable.Drawable;
import mydraw.exceptions.ColorException;
import mydraw.exceptions.SizeException;
import mydraw.exceptions.TxtIOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import javax.swing.SwingUtilities;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DrawTest {

    /*
     * Helper functions: toBufferedImage, imagesEqual, isFile,
     * createCircleMeasurements
     */
    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }

    boolean imagesEqual(BufferedImage expectedImage, BufferedImage actualImage) {
        if (expectedImage.getWidth() == actualImage.getWidth()
                && expectedImage.getHeight() == actualImage.getHeight()) {
            for (int x = 0; x < expectedImage.getWidth(); x++) {
                for (int y = 0; y < expectedImage.getHeight(); y++) {
                    if (expectedImage.getRGB(x, y) != actualImage.getRGB(x, y)) {
                        return false;
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    boolean isFile(String filepath) {
        File file = new File(filepath);
        return file.exists() && !file.isDirectory();
    }

    List<Point> createCircleMeasurements(List<Point> points) {
        List<Point> result = new ArrayList<>();
        int centerX, centerY, radiusX, radiusY;
        centerX = (points.get(0).x + points.get(1).x) / 2;
        centerY = (points.get(0).y + points.get(1).y) / 2;
        radiusX = Math.abs(points.get(1).x - points.get(0).x) / 2;
        radiusY = Math.abs(points.get(1).y - points.get(0).y) / 2;

        result.add(new Point(centerX, centerY - radiusY));
        result.add(new Point(centerX, centerY + radiusY));
        result.add(new Point(centerX + radiusX, centerY));
        result.add(new Point(centerX - radiusX, centerY));

        return result;
    }

    public String intToCol(int pixel) {
        int red = (pixel & 0xff0000) >> 16;
        int green = (pixel & 0x00ff00) >> 8;
        int blue = pixel & 0x0000ff;
        Color col = new Color(red, green, blue);
        return DrawGUI.getKeyByValue(col);
    }

    /*
     * Tests: for each API Method: Positive, Negative, Standard values, checks if
     * Execptions are thrown.
     * 
     * Standard values so far are: 800W (macOS sets it to 925 to fit all elements) x
     * 400H,
     * white background, black drawing color
     * 
     * To ensure easier debugging, there exists a test for each test case
     * In some cases, testing for standard values is not possible (e.g. writeImage)
     * or the negative test
     * correspodends to the exception test. in these cases, the standard and
     * exception test are omitted
     */

    private Draw draw;
    private DrawGUI window;
    private Graphics g;
    private DrawFunctions func;
    private DrawSaveImage save;
    private DrawTextReader read;
    private DrawTextWriter write;

    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            draw = new Draw();
            window = draw.getWindow();
            g = window.getGraphics();
            DrawShape shape = new DrawShape(window);
            this.func = new DrawFunctions(window, shape);
            this.save = new DrawSaveImage(window);
            this.read = new DrawTextReader(window);
            this.write = new DrawTextWriter(window);
        });
    }

    /*
     * Test for Width
     */

    @Test
    void WidthPositiveTest() throws SizeException {
        draw.setWidth(1000);
        assertEquals(1000, draw.getWidth());
    }

    @Test
    void WidthNegativeTest() throws SizeException {
        assertThrows(SizeException.class, () -> draw.setWidth(20));
    }

    @Test
    void WidthStandardTest() throws SizeException {
        assertEquals(800, draw.getWidth());
    }

    /*
     * Tests for Height
     */

    @Test
    void HeightPositiveTest() throws SizeException {
        draw.setHeight(1000);
        assertEquals(1000, draw.getHeight());
    }

    @Test
    void HeightNegativeTest() throws SizeException {
        assertThrows(SizeException.class, () -> draw.setHeight(20));
    }

    @Test
    void HeightStandardTest() throws SizeException {
        assertEquals(400, draw.getHeight());
    }

    /*
     * Tests for ForegroundColor (current drawing color)
     */

    @Test
    void FGColorPositiveTest() throws ColorException {
        draw.setFGColor("red");
        assertEquals("red", draw.getFGColor());
    }

    @Test
    void FGColorNegativeTest() throws ColorException {
        assertThrows(ColorException.class, () -> draw.setFGColor("colorful"));
    }

    @Test
    void FGColorStandardTest() throws ColorException {
        assertEquals("black", draw.getFGColor());
    }

    /*
     * Tests for BackgroundColor
     */

    @Test
    void BGColorPositiveTest() throws ColorException {
        draw.setFGColor("red");
        assertEquals("red", draw.getFGColor());
    }

    @Test
    void BGColorNegativeTest() throws ColorException {
        assertThrows(ColorException.class, () -> draw.setFGColor("colorful"));
    }

    @Test
    void BGColorStandardTest() throws ColorException {
        assertEquals("white", draw.getBGColor());
    }

    @Test
    void getDrawingPositiveTest() {
        assertInstanceOf(BufferedImage.class, draw.getDrawing());
    }

    @Test
    void getDrawingNegativeTest() {
        assertNotEquals(String.class, draw.getDrawing().getClass());
    }

    @Test
    void getDrawingStandardTest() {
        assertNotNull(draw.getDrawing());
    }

    @Test
    void writeImagePositiveTest() throws IOException {
        draw.writeImage(draw.getDrawing(), "writeImagePositive.bmp");
        assertTrue(isFile("writeImagePositive.bmp"));
    }

    @Test
    void writeImageNegativeeTest() throws IOException {
        assertThrows(FileNotFoundException.class, () -> draw.writeImage(draw.getDrawing(), ""));
    }

    @Test
    void writeImageThrowsTest() {
        assertThrows(NullPointerException.class, () -> save.writeImage(null, "writeImageThrows.bmp"));
    }

    @Test
    void readImagePositiveTest() throws IOException {
        save.writeImage(draw.getDrawing(), "imageReadPositive.bmp");
        assertTrue(imagesEqual(toBufferedImage(draw.getDrawing()),
                toBufferedImage(save.readImage("imageReadPositive.bmp"))));
    }

    @Test
    void readImageNegativeTest() throws IOException {
        save.writeImage(draw.getDrawing(), "imageReadPositive.bmp");
        assertThrows(FileNotFoundException.class, () -> save.readImage("notFound.bmp"));
    }

    @Test
    void readImageThrowsTest() throws IOException {
        assertThrows(NullPointerException.class, () -> save.readImage(null));
    }

    @Test
    void autoDrawPositiveTest() {
        draw.autoDraw();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("red", "blue", "green");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(intToCol(actualImage.getRGB(100, 200)));
        actualColors.add(intToCol(actualImage.getRGB(350, 200)));
        actualColors.add(intToCol(actualImage.getRGB(700, 350)));

        assertEquals(expectedColors, actualColors);
    }

    @Test
    void autoDrawNegativeTest() {
        draw.autoDraw();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("red", "blue", "green");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(intToCol(actualImage.getRGB(50, 200)));
        actualColors.add(intToCol(actualImage.getRGB(500, 200)));
        actualColors.add(intToCol(actualImage.getRGB(750, 350)));

        assertNotEquals(expectedColors, actualColors);
    }

    @Test
    void clearPositiveTest() {
        draw.autoDraw();
        draw.clear();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("white", "white", "white");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(intToCol(actualImage.getRGB(100, 200)));
        actualColors.add(intToCol(actualImage.getRGB(350, 200)));
        actualColors.add(intToCol(actualImage.getRGB(700, 350)));

        assertTrue(draw.getWindow().commandQueue.isEmpty());
        assertEquals(expectedColors, actualColors);
    }

    @Test
    void clearNegativeTest() {
        draw.autoDraw();
        draw.clear();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("red", "blue", "green");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(intToCol(actualImage.getRGB(100, 200)));
        actualColors.add(intToCol(actualImage.getRGB(350, 200)));
        actualColors.add(intToCol(actualImage.getRGB(700, 350)));

        assertTrue(draw.getWindow().commandQueue.isEmpty());
        assertNotEquals(expectedColors, actualColors);
    }

    @Test
    void drawRectanglePositiveTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200));

        draw.setFGColor("red");
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point pointx : points) {
            for (Point pointy : points) {
                String actual = intToCol(img.getRGB(pointx.x, pointy.y));
                assertEquals("red", actual);
            }
        }
    }

    @Test
    void drawRectangleNegativeTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(40, 40), new Point(230, 240));

        draw.setFGColor("red");
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point pointx : points) {
            for (Point pointy : points) {
                String actual = intToCol(img.getRGB(pointx.x, pointy.y));
                assertNotEquals("red", actual);
            }
        }
    }

    @Test
    void drawOvalPositiveTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200));

        draw.setFGColor("red");
        draw.drawOval(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        List<Point> circlePoints = createCircleMeasurements(points);

        for (Point point : circlePoints) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertEquals("red", actual);
        }
    }

    @Test
    void drawOvalNegativeeTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(40, 40), new Point(230, 240));

        draw.setFGColor("red");
        draw.drawOval(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        List<Point> circlePoints = createCircleMeasurements(points);

        for (Point point : circlePoints) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertNotEquals("red", actual);
        }
    }

    @Test
    void drawPolyLinePositiveTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200), new Point(100, 200));
        List<Point> polyline = Arrays.asList(new Point(100, 100), new Point(200, 200), new Point(100, 200));

        draw.setFGColor("red");
        draw.drawPolyLine(polyline);
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point point : points) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertEquals("red", actual);
        }
    }

    @Test
    void drawPolylineNegativeTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(120, 100), new Point(250, 200), new Point(100, 210));
        List<Point> polyline = Arrays.asList(new Point(100, 100), new Point(200, 200), new Point(100, 200));

        draw.setFGColor("red");
        draw.drawPolyLine(polyline);
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point point : points) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertNotEquals("red", actual);
        }
    }

    @Test
    void drawTrianglePositivetest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(150, 100), new Point(200, 200), new Point(100, 200));

        draw.setFGColor("red");
        draw.drawTriangle(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point point : points) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertEquals("red", actual);
        }
    }

    @Test
    void drawTriangleNegativetest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(150, 150), new Point(200, 100));

        draw.setFGColor("red");
        draw.drawTriangle(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point point : points) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertNotEquals("red", actual);
        }
    }

    @Test
    void drawRhombusPositiveTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200));

        draw.setFGColor("red");
        draw.drawRhombus(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        List<Point> rhombusPoints = createCircleMeasurements(points);

        for (Point point : rhombusPoints) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertEquals("red", actual);
        }
    }

    @Test
    void drawRhombusNegativeTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200));

        draw.setFGColor("red");
        draw.drawRhombus(new Point(40, 100), new Point(230, 240));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        List<Point> rhombusPoints = createCircleMeasurements(points);

        for (Point point : rhombusPoints) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertNotEquals("red", actual);
        }
    }

    @Test
    void drawFillRectanglePositiveTest() throws ColorException, InvocationTargetException, InterruptedException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200), new Point(150, 150));

        draw.setFGColor("red");
        draw.drawFillRectangle(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point pointx : points) {
            for (Point pointy : points) {
                String actual = intToCol(img.getRGB(pointx.x, pointy.y));
                assertEquals("red", actual);
            }
        }
    }

    @Test
    void drawFillRectangleNegativeTeest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(90, 90), new Point(210, 210), new Point(150, 95));

        draw.setFGColor("red");
        draw.drawFillRectangle(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point pointx : points) {
            for (Point pointy : points) {
                String actual = intToCol(img.getRGB(pointx.x, pointy.y));
                assertNotEquals("red", actual);
            }
        }
    }

    @Test
    void drawFillOvalPositiveTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(100, 100), new Point(200, 200));

        draw.setFGColor("red");
        draw.drawFillOval(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        List<Point> circlePoints = createCircleMeasurements(points);

        for (Point point : circlePoints) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertEquals("red", actual);
        }
        assertEquals("red", intToCol(img.getRGB(150, 150)));
    }

    @Test
    void drawFillOvalNegativeTest() throws ColorException {
        List<Point> points = Arrays.asList(new Point(90, 90), new Point(210, 210), new Point(150, 95));

        draw.setFGColor("red");
        draw.drawFillOval(new Point(100, 100), new Point(200, 200));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        List<Point> circlePoints = createCircleMeasurements(points);

        for (Point point : circlePoints) {
            String actual = intToCol(img.getRGB(point.x, point.y));
            assertNotEquals("red", actual);
        }
    }

    @Test
    void undoPositiveTest() {
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        String expectedColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        func.undo();
        String actualColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertNotEquals(expectedColor, actualColor);
    }

    @Test
    void undoNegativeeTest() {
        String expectedColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        func.undo();
        String actualColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertEquals(expectedColor, actualColor);
    }

    @Test
    void redoPositiveTest() {
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        String expectedColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        func.undo();
        func.redo();
        String actualColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertEquals(expectedColor, actualColor);
    }

    @Test
    void redoNegativeeTest() {
        String expectedColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        func.redo();
        String actualColor = intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertNotEquals(expectedColor, actualColor);
    }

    @Test
    void redrawPositiveTest() throws ColorException {
        List<String> expectedColors = Arrays.asList("red", "red", "red", "red");
        List<String> actualColors = new ArrayList<>();

        draw.setFGColor("red");
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        draw.drawOval(new Point(200, 100), new Point(300, 200));
        window.redraw(g);
        BufferedImage img = toBufferedImage(draw.getDrawing());

        actualColors.add(intToCol(img.getRGB(100, 100)));
        actualColors.add(intToCol(img.getRGB(200, 200)));
        actualColors.add(intToCol(img.getRGB(250, 100)));
        actualColors.add(intToCol(img.getRGB(300, 150)));

        assertEquals(expectedColors, actualColors);
    }

    @Test
    void redrawNegativeTest() throws ColorException {
        List<String> expectedColors = Arrays.asList("red", "red", "red", "red");
        List<String> actualColors = new ArrayList<>();

        draw.setFGColor("green");
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        draw.drawOval(new Point(200, 100), new Point(300, 200));
        window.redraw(g);
        BufferedImage img = toBufferedImage(draw.getDrawing());

        actualColors.add(intToCol(img.getRGB(100, 100)));
        actualColors.add(intToCol(img.getRGB(200, 200)));
        actualColors.add(intToCol(img.getRGB(250, 100)));
        actualColors.add(intToCol(img.getRGB(300, 150)));

        assertNotEquals(expectedColors, actualColors);
    }

    @Test
    void redrawStandardTest() {
        List<String> expectedColors = Arrays.asList("white", "white", "white", "white");
        List<String> actualColors = new ArrayList<>();

        draw.clear();
        window.redraw(g);
        BufferedImage img = toBufferedImage(draw.getDrawing());

        actualColors.add(intToCol(img.getRGB(100, 100)));
        actualColors.add(intToCol(img.getRGB(200, 200)));
        actualColors.add(intToCol(img.getRGB(250, 100)));
        actualColors.add(intToCol(img.getRGB(300, 150)));

        assertEquals(expectedColors, actualColors);
    }

    @Test
    void writeTextPositiveTest() throws TxtIOException {
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));

        String expectedCommand = "size;800;400;\nrectangle;100;100;200;200;black\n";
        String actualCommand = write.writeText();

        assertEquals(expectedCommand, actualCommand);
    }

    @Test
    void writeTextNegativeTest() throws TxtIOException {
        draw.drawOval(new Point(100, 100), new Point(200, 200));

        String expectedCommand = "rectangle;100;100;200;200;black\n";
        String actualCommand = write.writeText();

        assertNotEquals(expectedCommand, actualCommand);
    }

    @Test
    void writeTextThrowsTest() throws ColorException {
        draw.setFGColor("green");
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        draw.clear();
        assertThrows(TxtIOException.class, () -> write.writeText());
    }

    @Test
    void readTextPositiveTest() throws TxtIOException, IOException {
        List<Drawable> expectedCommandQueue;
        List<Drawable> actualCommandQueue;

        draw.drawRectangle(new Point(100, 100), new Point(200, 200));

        expectedCommandQueue = window.commandQueue;
        String drawingData = write.writeText();
        File textFile = new File("readTextPositive.txt");
        FileWriter fileWriter = new FileWriter(textFile);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print(drawingData);
        printWriter.close();
        read.readText("readTextPositive.txt");
        actualCommandQueue = window.commandQueue;

        assertEquals(expectedCommandQueue, actualCommandQueue);
    }

    @Test
    void readTextNegativeTest() throws IOException {
        assertThrows(FileNotFoundException.class, () -> read.readText(""));
    }

    @Test
    void readTextThrowsTest() throws TxtIOException, IOException {
        File textFile = new File("readTextThrows.txt");
        FileWriter fileWriter = new FileWriter(textFile);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("");
        printWriter.close();
        assertThrows(TxtIOException.class, () -> read.readText("readTextThrows.txt"));
    }

    @AfterAll
    static void cleanup() {
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".bmp") || name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}