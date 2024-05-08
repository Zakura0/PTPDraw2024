package mydraw;

import mydraw.DrawGUI;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

class ColorItemListener implements ItemListener{

    // user selected new color => store new color in DrawGUIs
    public void itemStateChanged(ItemEvent e) {
        String color;
        color = e.getItem().toString();
        .setFGColor(color);
    }
}
