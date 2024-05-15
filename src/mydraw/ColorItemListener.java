package mydraw;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class ColorItemListener implements ItemListener{

    DrawGUI window;

    public ColorItemListener(DrawGUI window) {
        this.window = window;
    }

    // user selected new color => store new color in DrawGUIs
    public void itemStateChanged(ItemEvent e) {
        String color;
        color = e.getItem().toString();
        try {
            window.setFGColor(color);
        } catch (ColorException e1) {
            e1.printStackTrace();
        }
        
    }
}
