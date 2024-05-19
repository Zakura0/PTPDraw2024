package mydraw;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mydraw.drawable.fillovalCommand;
import mydraw.drawable.fillrectangleCommand;
import mydraw.drawable.ovalCommand;
import mydraw.drawable.polyLineCommand;
import mydraw.drawable.rectangleCommand;
import mydraw.drawable.rhombusCommand;
import mydraw.drawable.triangleCommand;
import mydraw.exceptions.SizeException;
import mydraw.exceptions.TxtIOException;

public class DrawTextReader {

DrawGUI gui;

public DrawTextReader(DrawGUI gui){
    this.gui = gui;
}
    public void readText(String filePath) throws TxtIOException, IOException {
        gui.commandQueue.clear();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        if ((line = reader.readLine()) == null) {
            reader.close();
            throw new TxtIOException("No valid commands found.");
        }
        String[] size = line.split(";");
        int width = Integer.parseInt(size[1]);
        int height = Integer.parseInt(size[2]);
        try {
            gui.setWidth(width);
        } catch (SizeException e) {
            e.printStackTrace();
        }
        try {
            gui.setHeight(height);
        } catch (SizeException e) {
            e.printStackTrace();
        }
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String type = parts[0];
            if (type.equals("polyline")) {
                String points = parts[1];
                int color_rgb = Integer.parseInt(parts[2]);
                Color color = new Color(color_rgb, true);
                String[] pointStrings = points.split(":");
                List<Point> pointList = new ArrayList<>();
                for (String pointString : pointStrings) {
                    String[] coords = pointString.split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    pointList.add(new Point(x, y));
                }
                gui.commandQueue.add(new polyLineCommand(gui, color, pointList));
            } else {
                int x0 = Integer.parseInt(parts[1]);
                int y0 = Integer.parseInt(parts[2]);
                int x1 = Integer.parseInt(parts[3]);
                int y1 = Integer.parseInt(parts[4]);
                int color_rgb = Integer.parseInt(parts[5]);
                Color color = new Color(color_rgb, true);
                if (type.equals("rectangle")) {
                    gui.commandQueue.add(new rectangleCommand(gui, x0, y0, x1, y1, color));
                } else if (type.equals("oval")) {
                    gui.commandQueue.add(new ovalCommand(gui, x0, y0, x1, y1, color));
                } else if (type.equals("fillrectangle")) {
                    gui.commandQueue.add(new fillrectangleCommand(gui, x0, y0, x1, y1, color));
                } else if (type.equals("filloval")) {
                    gui.commandQueue.add(new fillovalCommand(gui, x0, y0, x1, y1, color));
                } else if (type.equals("rhombus")) {
                    gui.commandQueue.add(new rhombusCommand(gui, x0, y0, x1, y1, color));
                } else if (type.equals("triangle")) {
                    gui.commandQueue.add(new triangleCommand(gui, x0, y0, x1, y1, color));
                } else {
                    reader.close();
                    throw new TxtIOException("No valid commands found.");
                }
            } 

        }
        reader.close();

        gui.redraw(gui.frontPanel.getGraphics());
    }
}
