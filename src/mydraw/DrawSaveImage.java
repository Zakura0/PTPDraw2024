package mydraw;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * this class saves the drawn image on the drawing pane as a .bmp file 
 */
public class DrawSaveImage {

    DrawGUI gui;

    public DrawSaveImage(DrawGUI gui){
        this.gui = gui;
    }

    public void writeImage(Image img, String filename) throws IOException {
        MyBMPFile.write(filename, (BufferedImage) img);
    }

    public Image readImage(String filename) throws IOException {
        return MyBMPFile.read(filename);
    }


}
