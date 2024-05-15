package mydraw.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import mydraw.DrawGUI;
import mydraw.exceptions.ColorException;

public class ColorItemListener implements ItemListener {

    DrawGUI window;
    Boolean background;

    public ColorItemListener(DrawGUI window, Boolean background) {
        this.window = window;
        this.background = background;
    }

    // user selected new color => store new color in DrawGUIs
    public void itemStateChanged(ItemEvent e) {
        String color;
        color = e.getItem().toString();
        try {
            if (background) {
                window.setBGColor(color);
            } else {
                window.setFGColor(color);
            }
        } catch (ColorException e1) {
            e1.printStackTrace();
        }

    }
}
