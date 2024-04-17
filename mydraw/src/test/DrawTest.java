package test;

import mydraw.ColorException;
import mydraw.Draw;

import mydraw.SizeException;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DrawTests {

    Draw draw = new Draw();

    Color strToCol(String colorSign) {
        return switch(colorSign.toLowerCase()) {
            case "black" -> Color.black;
            case "green" -> Color.green;
            case "red" -> Color.red;
            case "blue" -> Color.blue;
            case "white" -> Color.white;
            default -> null;
        };
    }

    String colToStr(Color color) {
        if (color == Color.black) {
            return "black";
        }
        if (color == Color.green) {
            return "green";
        }
        if (color == Color.red) {
            return "red";
        }
        if (color == Color.blue) {
            return "blue";
        }
        if (color == Color.white) {
            return "white";
        }
        else {
            return null;
        }
    }

    @Test
    void getWidthTest() {
        int expectedWidth, actualWidth;

        expectedWidth = 100;
        Draw.DrawGUI.setSize(expectedWidth, 200);
        actualWidth = draw.getWidth();
        assertEquals(expectedWidth, actualWidth);

        expectedWidth = 0;
        window.setSize(expectedWidth, 200);
        actualWidth = draw.getWidth();
        assertEquals(expectedWidth, actualWidth);

        expectedWidth = -10;
        window.setSize(expectedWidth, 200);
        actualWidth = window.getWidth();
        assertEquals(expectedWidth, actualWidth);
    }

    @Test
    void getHeightTest() {
        int expectedHeight, actualHeight;

        expectedHeight = 200;
        window.setSize(100, expectedHeight);
        actualHeight = window.getHeight();
        assertEquals(expectedHeight, actualHeight);

        expectedHeight = 0;
        window.setSize(100, expectedHeight);
        actualHeight = window.getHeight();
        assertEquals(expectedHeight, actualHeight);

        expectedHeight = -10;
        window.setSize(100, expectedHeight);
        actualHeight = window.getHeight();
        assertEquals(expectedHeight, actualHeight);
    }

    @Test
    void setWidthTest() throws SizeException {
        int expectedWidth = 300;
        window.setWidth(expectedWidth);
        int actualWidth = window.getSize().width;
        assertEquals(expectedWidth, actualWidth);
    }

    @Test
    void setHeightTest() throws SizeException {
        int expectedHeight = 400;
        window.setHeight(expectedHeight);
        int actualHeight = window.getSize().height;
        assertEquals(expectedHeight, actualHeight);
    }

    @Test
    void getFGColorTest() throws ColorException {
        String expectedColor = "red";
        window.setForeground(strToCol(expectedColor));
        String actualColor = window.getFGColor();
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void setFGColorTest() throws ColorException {
        String expectedColor = "blue";
        window.setFGColor(expectedColor);
        String actualColor = colToStr(window.getBackground());
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void getBGColorTest() throws ColorException {
        String expectedColor = "red";
        window.setBackground(strToCol(expectedColor));
        String actualColor = window.getBGColor();
        assertEquals(expectedColor, actualColor);
    }

    @Test
    void setBGColorTest() throws ColorException {
        String expectedColor = "blue";
        window.setBGColor(expectedColor);
        String actualColor = colToStr(window.getBackground());
        assertEquals(expectedColor, actualColor);
        // TODO: test fails --> vermutlich wegen
        //       Unterschieden in getFG und getBG
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