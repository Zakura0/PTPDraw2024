package mydraw;

import mydraw.drawable.Drawable;
import mydraw.exceptions.TxtIOException;

/*
 * This class implements the function of saving the commands in the command queue in a .txt file
 */
public class DrawTextWriter {

    DrawGUI gui;

    public DrawTextWriter(DrawGUI gui){
        this.gui = gui;
    }

    public String writeText() throws TxtIOException {
        StringBuilder drawingData = new StringBuilder();

        if (gui.commandQueue.isEmpty()) {
            throw new TxtIOException("No valid commands found.");
        }

        drawingData.append("size;" + gui.getWidth() + ";" + gui.getHeight() + ";\n");
        for (Drawable drawable : gui.commandQueue) {
            drawingData.append(drawable.toString()).append("\n");
        }
        return drawingData.toString();
    }
}
