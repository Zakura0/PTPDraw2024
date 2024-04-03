package mydraw;

import java.awt.Graphics;

public class Draw {
    public static void main(String[] args) {
        new Draw();
    }

    public Draw() {
        window = new DrawGUI(this);
    }

    protected DrawGUI window;

    public void doCommand(String command) {
        if (command.equals("clear")) {
            Graphics g = window.getGraphics();
            g.setColor(window.getBackground());
            g.fillRect(0, 0, window.getSize().width, window.getSize().height);
        } else if (command.equals("quit")) {
            window.dispose();
            System.exit(0);
        }
    }
}