package whiteboard;

import java.awt.Color;
import java.io.Serializable;

public class ComplexObject implements Serializable {

    int id;
    int num;
    int type;
    int xPoint;
    int yPoint;
    Color color;
    String message;
    boolean trueOrFalse;

    public ComplexObject(int id, int num, int type, int xPoint, int yPoint, Color color, String message, boolean isTrue) {
        this.id = id;
        this.num = num;
        this.type = type;
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.color = color;
        this.message = message;
        this.trueOrFalse = isTrue;
    }

    public ComplexObject(int id, int type, int xPoint, int yPoint, Color color) {
        this.id = id;
        this.type = type;
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.color = color;
    }

    public ComplexObject(int id, int num, boolean trueOrFalse) {
        this.id = id;
        this.num = num;
        this.trueOrFalse = trueOrFalse;
    }

    public ComplexObject(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public ComplexObject(int id, boolean trueOrFalse) {
        this.id = id;
        this.trueOrFalse = trueOrFalse;
    }

    public ComplexObject(int id, int num) {
        this.id = id;
        this.num = num;
    }

    public ComplexObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getxPoint() {
        return xPoint;
    }

    public void setxPoint(int xPoint) {
        this.xPoint = xPoint;
    }

    public int getyPoint() {
        return yPoint;
    }

    public void setyPoint(int yPoint) {
        this.yPoint = yPoint;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isTrueOrFalse() {
        return trueOrFalse;
    }

    public void setTrueOrFalse(boolean trueOrFalse) {
        this.trueOrFalse = trueOrFalse;
    }

}
