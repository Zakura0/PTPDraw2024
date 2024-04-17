package test;
/*
* @authors Giahung Bui 7557640 , Ben Woller 7740402
*/

import mydraw.ColorException;
import mydraw.Draw;

import mydraw.SizeException;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DrawTests {

    DrawTests() {
    }

    Color strToCol(String colorsign) throws ColorException {
        return switch(colorsign.toLowerCase()) {
            case "black" -> Color.black;
            case "green" -> Color.green;
            case "red" -> Color.red;
            case  "blue" -> Color.blue;
            case "white" ->Color.white;
            default -> throw new ColorException("Invalid color!");
        };
    }

    Draw draw = new Draw();

    @Test
    void WidthTest() throws SizeException {
        int expectedWidth, actualWidth;

        expectedWidth = 100;
        draw.setWidth(expectedWidth);
        actualWidth = draw.getWidth();
        assertEquals(expectedWidth, actualWidth);

        assertThrows(SizeException.class, () ->
                draw.setWidth(20));
    }
    @Test
    void HeightTest() throws SizeException {
        int expectedHeight, actualHeight;

        expectedHeight = 100;
        draw.setHeight(expectedHeight);
        actualHeight = draw.getHeight();
        assertEquals(expectedHeight, actualHeight);

        assertThrows(SizeException.class, () ->
                draw.setHeight(20));
    }

    @Test
    void FGColorTest() throws ColorException {
        String expectedColor, actualColor;

        expectedColor = "red";
        draw.setFGColor(expectedColor);
        actualColor = draw.getFGColor();
        assertEquals(expectedColor, actualColor);

        assertThrows(ColorException.class, () ->
                draw.setFGColor("yellow"));
    }

    @Test
    void BGColorTest() throws ColorException {
        String expectedColor = "red";
        draw.setBGColor(expectedColor);
        String actualColor = draw.getBGColor();
        assertEquals(expectedColor, actualColor);

        assertThrows(ColorException.class, () ->
                draw.setBGColor("yellow"));
    }

    @Test
    void  getDrawingTest() {

    }

    @Test
    void writeImageTest() throws IOException {

    }

    @Test
    void readImageTest() throws IOException {

    }

    @Test
    void clearTest() {

    }

    @Test
    void autoDrawTest() {

    }

    @Test
    void drawRectangleTest() throws AWTException, ColorException {
        Point ptone, pttwo;

        ptone = new Point(100, 100);
        pttwo = new Point(200, 200);

        draw.setFGColor("black");

        Color expectedColor = strToCol(draw.getFGColor());

        draw.drawRectangle(ptone, pttwo);
        Color actualone = new Robot().getPixelColor(ptone.x, ptone.y);
        Color actualtwo = new Robot().getPixelColor(pttwo.x, pttwo.y);
        Color actualthree = new Robot().getPixelColor(pttwo.x, ptone.y);
        Color actualfour = new Robot().getPixelColor(ptone.x, pttwo.y);

        assertEquals(expectedColor, actualone);
        assertEquals(expectedColor, actualtwo);
        assertEquals(expectedColor, actualthree);
        assertEquals(expectedColor, actualfour);
    }

    @Test
    void drawOvalTest() throws AWTException, ColorException {
        Point ptone, pttwo;

        ptone = new Point(100, 100);
        pttwo = new Point(200, 200);

        draw.setFGColor("black");

        Color expectedColor = strToCol(draw.getFGColor());

        draw.drawOval(ptone, pttwo);
        Color actualone = new Robot().getPixelColor((int) (ptone.x*1.5), ptone.y);
        System.out.println(actualone.toString());
        Color actualtwo = new Robot().getPixelColor(ptone.x, (int) (ptone.y*1.5));
        Color actualthree = new Robot().getPixelColor(pttwo.x/2, pttwo.y);
        Color actualfour = new Robot().getPixelColor(pttwo.x, pttwo.y/2);

        System.out.println(actualone);
        System.out.println(actualtwo);
        System.out.println(actualthree);
        System.out.println(actualfour);

        assertEquals(expectedColor, actualone);
        assertEquals(expectedColor, actualtwo);
        assertEquals(expectedColor, actualthree);
        assertEquals(expectedColor, actualfour);
    }

    @Test
    void drawPolylineTest() throws ColorException, AWTException {
        Point ptone, pttwo, ptthree, ptfour;

        ptone = new Point(100, 100);
        pttwo = new Point(150, 100);
        ptthree = new Point(150, 150);
        ptfour = new Point(200, 250);

        List<Point> points = new ArrayList<>();

        points.add(ptone);
        points.add(pttwo);
        points.add(ptthree);
        points.add(ptfour);

        draw.setFGColor("black");

        Color expectedColor = strToCol(draw.getFGColor());

        draw.drawPolyLine(points);
        Color actualone = new Robot().getPixelColor(points.getFirst().x, points.getFirst().y);
        Color actualtwo = new Robot().getPixelColor(points.get(1).x, points.get(1).y);
        Color actualthree = new Robot().getPixelColor(points.get(2).x, points.get(2).y);
        Color actualfour = new Robot().getPixelColor(points.getLast().x, points.getLast().y);

        System.out.println(actualone);
        System.out.println(actualtwo);
        System.out.println(actualthree);
        System.out.println(actualfour);

        assertEquals(expectedColor, actualone);
        assertEquals(expectedColor, actualtwo);
        assertEquals(expectedColor, actualthree);
        assertEquals(expectedColor, actualfour);
    }
}