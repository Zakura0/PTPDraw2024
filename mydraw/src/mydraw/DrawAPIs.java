package mydraw;
import java.awt.*;
import java.awt.image.BufferedImage;


/**
 * @author Giahung
 *
 */

public class DrawAPIs extends Draw{
    Color color;
    DrawGUI window;

    public DrawAPIs() {
    }

    /** API method: get fg color ...*/
    public String getFGColor() {
        if (color == Color.black) {
            return "black";
        } else if (color == Color.green) {
            return "green";
        } else if (color == Color.red) {
            return "red";
        } else if (color == Color.blue) {
            return "blue";
        } else {
            return null;
        }
    }

    /**
     * @param new_color
     * @throws ColorException
     */
    public void setFGColor(String new_color) throws ColorException{
        switch(new_color.toLowerCase()) {
            case "black":
                color = Color.black;
                break;
            case "green":
            color = Color.green;
                break;
            case "red":
                color = Color.red;
                break;
            case  "blue":
                color = Color.blue;
                break;
            default:
                throw new ColorException("Invalid color!");
        }
    }

    public int getWidth() {
        return window.getSize().width;
    }

    public int getHeight() {
        return window.getSize().height;
    }

    public void setWidth(int width) throws SizeException{
        window.setSize(new Dimension(width, window.getSize().height));
    }

    public void setHeight(int height) throws SizeException {
        window.setSize(new Dimension(window.getSize().width, height));
    }

    public void setBGColor(String new_color) throws ColorException{
        switch(new_color.toLowerCase()) {
            case "black":
                window.setBackground(Color.black);;
                break;
            case "green":
                window.setBackground(Color.green);
                break;
            case "red":
                window.setBackground(Color.red);
                break;
            case  "blue":
                window.setBackground(Color.blue);
            case "white":
                window.setBackground(Color.white);
                break;
            default:
                throw new ColorException("Invalid color!");
        }
    }

        public String getBGColor(){
            Color bgColor = window.getBackground();
            if (bgColor == null){
                return null;
            }
            String [] colorSign = {"black", "green", "red", "blue", "default (white)"};
            Color [] colors = {Color.black, Color.green, Color.red, Color.blue, Color.white};
            for (int i = 0; i < colors.length; i++) {
                if (bgColor.equals(colors[i])){
                    return colorSign[i];
                }
            }
            return null;
        }
        
    public void drawRectangle(Point upper_left, Point lower_right){
        String fgColor = this.getFGColor();
        Color c = Color.getColor(fgColor);

        int x = Math.min(upper_left.x, lower_right.x);
        int y = Math.min(upper_left.y, lower_right.y);
        int width = Math.abs(lower_right.x - upper_left.x); 
        int height = Math.abs(lower_right.y - upper_left.y);

        Graphics g = window.getGraphics();
        g.setColor(c);
        g.drawRect(x, y, width, height);
    }


    public void drawOval(Point upper_left, Point lower_right){
        //TODO
    }
    
    public void drawPolyLine(java.util.List<Point> points){
        String fgColor = this.getFGColor();
        Color c = Color.getColor(fgColor);

        Graphics g = window.getGraphics();
        g.setPaintMode();
        g.setColor(c);
        
            for (int i = 1; i < points.size(); i++) {
                Point prevPoint = points.get(i - 1);
                Point currPoint = points.get(i);
                g.drawLine(prevPoint.x, prevPoint.y, currPoint.x, currPoint.y);
            }
        }
    
    public Image getDrawing(){
        BufferedImage image = new BufferedImage(window.getWidth(), window.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        window.paint(g);
        g.dispose();
        return image;
    }

    public void clear() {
        window.getContentPane().removeAll();
        window.repaint();
     }

     public void autoDraw() {
        
     }
}
