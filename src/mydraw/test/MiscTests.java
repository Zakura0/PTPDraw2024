package mydraw.test;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

import mydraw.Draw;
import mydraw.DrawGUI;
import mydraw.DrawSaveImage;
import mydraw.exceptions.ColorException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.*;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 7740402, Simon Kazemi 7621942
 */

public class MiscTests {

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

    private Draw draw;
    private DrawGUI window;
    private DrawSaveImage save;
    
    @BeforeEach
    void setUp() throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            draw = new Draw();
            window = draw.getWindow();
            this.save = new DrawSaveImage(window);
        });
    }

    @Test
    void compareImagePositiveTest() throws IOException, InterruptedException {
        draw.autoDraw();
        Image reference = draw.getDrawing();
        save.writeImage(reference, "reference.bmp");
        Thread.sleep(1000);
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
        save.writeImage(actual, "actual.bmp");
        Thread.sleep(1000);

        BufferedImage expectedImage = toBufferedImage(save.readImage("reference.bmp"));
        BufferedImage actualImage = toBufferedImage(save.readImage("actual.bmp"));

        assertTrue(imagesEqual(expectedImage, actualImage));
    }

    @Test
    void compareImageNegativeTest() throws IOException {
        draw.autoDraw();
        Image reference = draw.getDrawing();
        save.writeImage(reference, "reference.bmp");
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
        save.writeImage(actual, "actual.bmp");

        BufferedImage expectedImage = toBufferedImage(save.readImage("reference.bmp"));
        BufferedImage actualImage = toBufferedImage(save.readImage("actual.bmp"));

        assertFalse(imagesEqual(expectedImage, actualImage));
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
