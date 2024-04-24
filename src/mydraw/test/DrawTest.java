package mydraw.test;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402
 */

import mydraw.ColorException;
import mydraw.Draw;

import mydraw.SizeException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DrawTest {

    Color strToCol(String colorsign) throws ColorException {
        return switch (colorsign.toLowerCase()) {
            case "black" -> Color.black;
            case "green" -> Color.green;
            case "red" -> Color.red;
            case "blue" -> Color.blue;
            case "white" -> Color.white;
            default -> throw new ColorException("Invalid color!");
        };
    }

    Color intToCol(int pixel) {
        int red = (pixel & 0x00ff0000) >> 16;
        int green = (pixel & 0x0000ff00) >> 8;
        int blue = pixel & 0x000000ff;
        return new Color(red, green, blue);
    }

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

    Draw draw = new Draw();

    @Test
    void WidthTest() throws SizeException {
        int expectedWidth, actualWidth;

        expectedWidth = 600;
        draw.setWidth(expectedWidth);
        actualWidth = draw.getWidth();

        assertEquals(expectedWidth, actualWidth);
        assertThrows(SizeException.class, () -> draw.setWidth(20));
    }

    @Test
    void HeightTest() throws SizeException {
        int expectedHeight, actualHeight;

        expectedHeight = 100;
        draw.setHeight(expectedHeight);
        actualHeight = draw.getHeight();

        assertEquals(expectedHeight, actualHeight);
        assertThrows(SizeException.class, () -> draw.setHeight(60));
    }

    @Test
    void FGColorTest() throws ColorException {
        String expectedColor, actualColor;

        expectedColor = "red";
        draw.setFGColor(expectedColor);
        actualColor = draw.getFGColor();

        assertEquals(expectedColor, actualColor);
        assertThrows(ColorException.class, () -> draw.setFGColor("yellow"));
    }

    @Test
    void BGColorTest() throws ColorException {
        String expectedColor = "red";
        draw.setBGColor(expectedColor);
        String actualColor = draw.getBGColor();

        assertEquals(expectedColor, actualColor);
        assertThrows(ColorException.class, () -> draw.setBGColor("yellow"));
    }

    @Test
    void getDrawingTest() {
        assertInstanceOf(BufferedImage.class, draw.getDrawing());
        assertNotNull(draw.getDrawing());
    }

    @Test
    void writeImageTest() throws IOException {
        Image image = draw.getDrawing();
        String filepath = "test.bmp";

        draw.writeImage(image, filepath);

        assertTrue(isFile(filepath));
        assertThrows(NullPointerException.class, () -> draw.writeImage(null, filepath));
    }

    @Test
    void readImageTest() throws IOException {
        Image expectedImage = draw.getDrawing();
        Image actualImage;
        String filepath = "test.bmp";

        draw.writeImage(expectedImage, filepath);
        actualImage = draw.readImage(filepath);

        assertTrue(imagesEqual(toBufferedImage(expectedImage), toBufferedImage(actualImage)));
        assertThrows(NullPointerException.class, () -> draw.readImage(null));
    }

    @Test
    void autoDrawTest() throws IOException {
        Image expectedImage;
        Image actualImage;
        draw.autoDraw();
        draw.writeImage(draw.getDrawing(), "autoDrawTest.bmp");
        expectedImage = draw.readImage("empty.bmp");
        actualImage = draw.readImage("autoDrawTest.bmp");
        assertFalse(imagesEqual(toBufferedImage(expectedImage), toBufferedImage(actualImage)));
    }

    @Test
    void clearTest() throws IOException {
        Image expectedImage;
        Image actualImage;
        draw.autoDraw();
        draw.writeImage(draw.getDrawing(), "image.bmp");
        draw.clear();
        draw.writeImage(draw.getDrawing(), "clearTest.bmp");       

        expectedImage = draw.readImage("image.bmp");
        actualImage = draw.readImage("clearTest.bmp");

        assertFalse(imagesEqual(toBufferedImage(expectedImage), toBufferedImage(actualImage)));
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
        Color expectedColor = strToCol(draw.getFGColor());
        draw.drawRectangle(pointsPos.get(0), pointsPos.get(1));
        BufferedImage img = toBufferedImage(draw.getDrawing());

        for (Point pointx : pointsPos) {
            for (Point pointy : pointsPos) {
                Color actual = intToCol(img.getRGB(pointx.x, pointy.y));
                assertEquals(expectedColor, actual);
            }
        }
        for (Point pointx : pointsNeg) {
            for (Point pointy : pointsNeg) {
                Color actual = intToCol(img.getRGB(pointx.x, pointy.y));
                assertNotEquals(expectedColor, actual);
            }
        }
        assertThrows(ColorException.class, () -> {
            draw.setFGColor("yellow");
            strToCol(draw.getFGColor());
        });
    }

    @Test
    void drawOvalTest() throws ColorException {
        List<Point> pointsPos = new ArrayList<>();
        List<Point> pointsNeg = new ArrayList<>();
        List<Color> actualColorsPos = new ArrayList<>();
        List<Color> actualColorsNeg = new ArrayList<>();
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

        Color expectedColor = strToCol(draw.getFGColor());

        draw.drawOval(pointsPos.get(0), pointsPos.get(1));

        BufferedImage actual = toBufferedImage(draw.getDrawing());

        measures = createCircleMeasurements(pointsPos);

        centerX = measures.get(0);
        centerY = measures.get(1);
        radiusX = measures.get(2);
        radiusY = measures.get(3);

        actualColorsPos.add(intToCol(actual.getRGB(centerX, centerY - radiusY)));
        actualColorsPos.add(intToCol(actual.getRGB(centerX, centerY + radiusY)));
        actualColorsPos.add(intToCol(actual.getRGB(centerX + radiusX, centerY)));
        actualColorsPos.add(intToCol(actual.getRGB(centerX - radiusX, centerY)));

        for (Color color : actualColorsPos) {
            assertEquals(expectedColor, color);
        }

        measures = createCircleMeasurements(pointsNeg);

        centerX = measures.get(0);
        centerY = measures.get(1);
        radiusX = measures.get(2);
        radiusY = measures.get(3);

        actualColorsNeg.add(intToCol(actual.getRGB(centerX, centerY - radiusY)));
        actualColorsNeg.add(intToCol(actual.getRGB(centerX, centerY + radiusY)));
        actualColorsNeg.add(intToCol(actual.getRGB(centerX + radiusX, centerY)));
        actualColorsNeg.add(intToCol(actual.getRGB(centerX - radiusX, centerY)));

        for (Color color : actualColorsNeg) {
            assertNotEquals(expectedColor, color);
        }

        assertThrows(ColorException.class, () -> {
            draw.setFGColor("yellow");
            strToCol(draw.getFGColor());
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

        Color expectedColor = strToCol(draw.getFGColor());

        draw.drawPolyLine(pointsPos);

        BufferedImage buff = toBufferedImage(draw.getDrawing());

        for (Point point : pointsPos) {
            Color actual = intToCol(buff.getRGB(point.x, point.y));
            assertEquals(expectedColor, actual);
        }

        for (Point point : pointsNeg) {
            Color actual = intToCol(buff.getRGB(point.x, point.y));
            assertNotEquals(expectedColor, actual);
        }

        assertThrows(ColorException.class, () -> {
            draw.setFGColor("yellow");
            strToCol(draw.getFGColor());
        });
    }

    @Test
    void compareImagesTest() throws IOException {
        Image expectedImage;
        Image actualImage;
        draw.writeImage(draw.getDrawing(), "empty.bmp");
        draw.autoDraw();
        draw.writeImage(draw.getDrawing(), "image.bmp");
        draw.writeImage(draw.getDrawing(), "autoDrawTest.bmp");
        expectedImage = draw.readImage("autoDrawTest.bmp");
        actualImage = draw.readImage("image.bmp");

        assertTrue(imagesEqual(toBufferedImage(expectedImage), toBufferedImage(actualImage)));

        expectedImage = draw.readImage("empty.bmp");

        assertFalse(imagesEqual(toBufferedImage(expectedImage), toBufferedImage(actualImage)));

        assertThrows(IOException.class, () -> {
            draw.readImage("image.png");
        });
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