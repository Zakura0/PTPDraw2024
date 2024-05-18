package mydraw.test;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

import mydraw.Draw;
import mydraw.exceptions.ColorException;
import mydraw.exceptions.SizeException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DrawTest {

    /*
     * Helper functions: toBufferedImage, imagesEqual, isFile, createCircleMeasurements
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

    List<Integer> createCircleMeasurements(List<Point> points) {
        List<Integer> result = new ArrayList<>();
        int centerX = (points.get(0).x + points.get(1).x) / 2;
        int centerY = (points.get(0).y + points.get(1).y) / 2;
        int radiusX = Math.abs(points.get(1).x - points.get(0).x) / 2;
        int radiusY = Math.abs(points.get(1).y - points.get(0).y) / 2;

        result = Arrays.asList(centerX, centerY, radiusX, radiusY);
        return result;
    }

    /*
     * Tests: for each API Method: Positive, Negative, Standard values, checks if Execptions are thrown.
     * 
     * Standard values so far are: 800W (macOS sets it to 925 to fit all elements) x 400H, 
     * white background, black drawing color
     * 
     * To ensure easier debugging, there exists a test for each test case
     * In some cases, testing for standard values is not possible (e.g. writeImage) or the negative test
     * correspodends to the exception test. in these cases, the standard and exception test are omitted
     */

    private Draw draw;

    @BeforeEach
    void setUp() {
        draw = new Draw();
    }
    
    /*
     * Tests for Width
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
        assertEquals(925, draw.getWidth());
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
        assertThrows(ColorException.class, () -> draw.setFGColor("yellow"));
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
        assertThrows(ColorException.class, () -> draw.setFGColor("yellow"));
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
        assertThrows(NullPointerException.class, () -> draw.writeImage(null, "writeImageThrows.bmp"));
    }




    @Test
    void readImagePositiveTest() throws IOException {
        draw.writeImage(draw.getDrawing(), "imageReadPositive.bmp");
        assertTrue(imagesEqual(toBufferedImage(draw.getDrawing()), toBufferedImage(draw.readImage("imageReadPositive.bmp"))));
    }

    @Test
    void readImageNegativeTest() throws IOException {
        draw.writeImage(draw.getDrawing(), "imageReadPositive.bmp");
        assertThrows(FileNotFoundException.class, () -> draw.readImage("notFound.bmp"));
    }

    @Test
    void readImageThrowsTest() throws IOException {
        assertThrows(NullPointerException.class, () -> draw.readImage(null));
    }





    @Test
    void autoDrawPositiveTest() {
        draw.autoDraw();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("red", "blue", "green");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(draw.intToCol(actualImage.getRGB(100, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(350, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(700, 350)));

        assertEquals(expectedColors, actualColors);
    }

    @Test
    void autoDrawNegativeTest() {
        draw.autoDraw();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("red", "blue", "green");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(draw.intToCol(actualImage.getRGB(50, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(500, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(750, 350)));

        assertNotEquals(expectedColors, actualColors);
    }




    @Test
    void clearPositiveTest() {
        draw.autoDraw();
        draw.clear();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("white", "white", "white");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(draw.intToCol(actualImage.getRGB(100, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(350, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(700, 350)));

        assertEquals(expectedColors, actualColors);
    }

    @Test
    void clearNegativeTest() {
        draw.autoDraw();
        draw.clear();
        BufferedImage actualImage = toBufferedImage(draw.getDrawing());
        List<String> expectedColors = Arrays.asList("red", "blue", "green");
        List<String> actualColors = new ArrayList<>();

        actualColors.add(draw.intToCol(actualImage.getRGB(100, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(350, 200)));
        actualColors.add(draw.intToCol(actualImage.getRGB(700, 350)));

        assertNotEquals(expectedColors, actualColors);
    }




    @Test
    void compareImagePositiveTest() throws IOException {
        draw.autoDraw();
        Image reference = draw.getDrawing();
        draw.writeImage(reference, "reference.bmp");
        draw.clear();

        Point p1 = new Point(100, 200);
        Point p2 = new Point(200, 100);
        try {
            draw.setFGColor("red");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        draw.drawRectangle(p1, p2);
        Point p3 = new Point(300, 200);
        Point p4 = new Point(400, 100);
        try {
            draw.setFGColor("blue");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        draw.drawOval(p3, p4);
        Point pl1 = new Point(500, 200);
        Point pl2 = new Point(600, 100);
        Point pl3 = new Point(700, 200);
        try {
            draw.setFGColor("green");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        draw.drawPolyLine(List.of(pl1, pl2, pl3));
        Point p5 = new Point(100, 350);
        Point p6 = new Point(200, 250);
        draw.drawFillRectangle(p5, p6);
        Point p7 = new Point(300, 350);
        Point p8 = new Point(400, 250);
        draw.drawFillOval(p7, p8);
        Point p9 = new Point(500, 350);
        Point p10 = new Point(600, 250);
        draw.drawRhombus(p9, p10);
        Point p11 = new Point(600, 250);
        Point p12 = new Point(700, 350);
        draw.drawTriangle(p11, p12);

        Image actual = draw.getDrawing();
        draw.writeImage(actual, "actual.bmp");

        BufferedImage expectedImage = toBufferedImage(draw.readImage("reference.bmp"));
        BufferedImage actualImage = toBufferedImage(draw.readImage("actual.bmp"));

        assertTrue(imagesEqual(expectedImage, actualImage));

    }

    @Test
    void compareImageNegativeTest() throws IOException {
        draw.autoDraw();
        Image reference = draw.getDrawing();
        draw.writeImage(reference, "reference.bmp");
        draw.clear();

        Point p1 = new Point(100, 200);
        Point p2 = new Point(200, 100);
        try {
            draw.setFGColor("red");
        } catch (ColorException e) {
            System.err.println("Color Exception: " + e.getMessage());
        }
        draw.drawRectangle(p1, p2);

        Image actual = draw.getDrawing();
        draw.writeImage(actual, "actual.bmp");

        BufferedImage expectedImage = toBufferedImage(draw.readImage("reference.bmp"));
        BufferedImage actualImage = toBufferedImage(draw.readImage("actual.bmp"));

        assertFalse(imagesEqual(expectedImage, actualImage));

    }


    @Test
    void drawRectangleTest() throws ColorException {
        List<Point> pointsPos = new ArrayList<>();
        List<Point> pointsNeg = new ArrayList<>();

        pointsPos.add(new Point(100, 100));
        pointsPos.add(new Point(200, 200));

        pointsNeg.add(new Point(40, 40));
        pointsNeg.add(new Point(230, 340));

        draw.setFGColor("red");
        String expectedColor = draw.getFGColor();
        draw.drawRectangle(pointsPos.get(0), pointsPos.get(1));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point pointx : pointsPos) {
            for (Point pointy : pointsPos) {
                String actual = draw.intToCol(img.getRGB(pointx.x, pointy.y));
                assertEquals(expectedColor, actual);
            }
        }
        for (Point pointx : pointsNeg) {
            for (Point pointy : pointsNeg) {
                String actual = draw.intToCol(img.getRGB(pointx.x, pointy.y));
                assertNotEquals(expectedColor, actual);
            }
        }
        assertThrows(ColorException.class, () -> {
            draw.setFGColor("yellow");
        });
    }




    @Test
    void drawOvalTest() throws ColorException {
        List<Point> pointsPos = new ArrayList<>();
        List<Point> pointsNeg = new ArrayList<>();
        List<String> actualColorsPos = new ArrayList<>();
        List<String> actualColorsNeg = new ArrayList<>();
        List<Integer> measures;
        int centerX;
        int centerY;
        int radiusX;
        int radiusY;

        pointsPos.add(new Point(100, 100));
        pointsPos.add(new Point(200, 200));

        pointsNeg.add(new Point(40, 40));
        pointsNeg.add(new Point(230, 340));

        draw.setFGColor("black");

        String expectedColor = draw.getFGColor();

        draw.drawOval(pointsPos.get(0), pointsPos.get(1));

        BufferedImage actual = toBufferedImage(draw.getDrawing());

        measures = createCircleMeasurements(pointsPos);

        centerX = measures.get(0);
        centerY = measures.get(1);
        radiusX = measures.get(2);
        radiusY = measures.get(3);

        actualColorsPos.add(draw.intToCol(actual.getRGB(centerX, centerY - radiusY)));
        actualColorsPos.add(draw.intToCol(actual.getRGB(centerX, centerY + radiusY)));
        actualColorsPos.add(draw.intToCol(actual.getRGB(centerX + radiusX, centerY)));
        actualColorsPos.add(draw.intToCol(actual.getRGB(centerX - radiusX, centerY)));

        for (String color : actualColorsPos) {
            assertEquals(expectedColor, color);
        }

        measures = createCircleMeasurements(pointsNeg);

        centerX = measures.get(0);
        centerY = measures.get(1);
        radiusX = measures.get(2);
        radiusY = measures.get(3);

        actualColorsNeg.add(draw.intToCol(actual.getRGB(centerX, centerY - radiusY)));
        actualColorsNeg.add(draw.intToCol(actual.getRGB(centerX, centerY + radiusY)));
        actualColorsNeg.add(draw.intToCol(actual.getRGB(centerX + radiusX, centerY)));
        actualColorsNeg.add(draw.intToCol(actual.getRGB(centerX - radiusX, centerY)));

        for (String color : actualColorsNeg) {
            assertNotEquals(expectedColor, color);
        }

        assertThrows(ColorException.class, () -> {
            draw.setFGColor("yellow");
        });
    }




    @Test
    void drawPolylineTest() throws ColorException {
        List<Point> pointsPos = new ArrayList<>();
        List<Point> pointsNeg = new ArrayList<>();

        pointsPos.add(new Point(100, 100));
        pointsPos.add(new Point(150, 100));
        pointsPos.add(new Point(150, 150));
        pointsPos.add(new Point(200, 250));

        pointsNeg.add(new Point(40, 40));
        pointsNeg.add(new Point(23, 34));
        pointsNeg.add(new Point(15, 9));
        pointsNeg.add(new Point(230, 0));

        draw.setFGColor("black");

        String expectedColor = draw.getFGColor();

        draw.drawPolyLine(pointsPos);

        BufferedImage buff = toBufferedImage(draw.getDrawing());

        for (Point point : pointsPos) {
            String actual = draw.intToCol(buff.getRGB(point.x, point.y));
            assertEquals(expectedColor, actual);
        }

        for (Point point : pointsNeg) {
            String actual = draw.intToCol(buff.getRGB(point.x, point.y));
            assertNotEquals(expectedColor, actual);
        }

        assertThrows(ColorException.class, () -> {
            draw.setFGColor("yellow");
        });
    }




    @Test
    void undoPositiveTest() {
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        String expectedColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        draw.undo();
        String actualColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertNotEquals(expectedColor, actualColor);
    }

    @Test
    void undoNegativeeTest() {
        String expectedColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        draw.undo();
        String actualColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertEquals(expectedColor, actualColor);
    }




    @Test
    void redoPositiveTest() {
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        String expectedColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        draw.undo();
        draw.redo();
        String actualColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertEquals(expectedColor, actualColor);
    }

    @Test
    void redoNegativeeTest() {
        String expectedColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));
        draw.drawRectangle(new Point(100, 100), new Point(200, 200));
        draw.redo();
        String actualColor = draw.intToCol(toBufferedImage(draw.getDrawing()).getRGB(100, 100));

        assertNotEquals(expectedColor, actualColor);
    }




    @AfterAll
    static void cleanup() {
        File directory = new File(".");
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".bmp"));
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }
}