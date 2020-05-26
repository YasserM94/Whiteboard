package whiteboard;

import java.awt.Color;

public class Shape {

    private int type;
    private int x;
    private int y;
    private Color color;

    public Shape(int type, int x, int y, Color color) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }



}
