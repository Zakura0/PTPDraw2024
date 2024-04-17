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

import static org.junit.jupiter.api.Assertions.*;

class DrawTests {

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
    void getBGColorTest() throws ColorException {
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
    void drawRectangleTest() {

    }

    @Test
    void drawOvalTest() {

    }

    @Test
    void drawPolylineTest() {

    }
}