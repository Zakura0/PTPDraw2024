package mydraw;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.Window;

public class setWidthCommand {

BufferedImage buffImage;
int width;
DrawGUI window;

public setWidthCommand(DrawGUI window, int width) {
    this.window = window;
    this.width = width;
}

public void draw(Graphics g) {
    if (width < 600) {
        throw new SizeException("Width must be at least 600 pixels."); //TODO Exceptions?
    }
    window.frontPanel.setPreferredSize(new Dimension(width, window.getHeight()));
    window.pack();
    window.buffImage = new BufferedImage(width, window.getHeight(), BufferedImage.TYPE_INT_RGB);
}

}