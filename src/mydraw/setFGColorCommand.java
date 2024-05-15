package mydraw;

import java.awt.Graphics;

public class setFGColorCommand implements Drawable{
    private String new_color;
    DrawGUI window;

    public setFGColorCommand(DrawGUI window, String new_color){
        this.window = window;
        this.new_color = new_color;
    }
    @Override
    public void draw(Graphics g){
        if (window.colors.containsKey(new_color.toLowerCase())) {
            window.fgColor = window.colors.get(new_color.toLowerCase());
        } else {
            throw new ColorException("Invalid color: " + new_color);
        } // Wie machen wir das mit der ColorException?
            //TODO
    }

}
