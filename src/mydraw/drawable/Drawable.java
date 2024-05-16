package mydraw.drawable;

import java.awt.Graphics;

//interface which allows a graphics object to be drawn, 
//without the type being known.
public interface Drawable {

    public void draw(Graphics g);
    public String toString();
}

