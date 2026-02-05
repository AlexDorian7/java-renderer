package org.verselstudios.math;

import org.joml.Vector2d;
import org.joml.Vector4d;

public class Rectangle {

    private Vector2d pos;
    private Vector2d size;

    public Rectangle(double w, double h) {
        this(0, 0, w, h);
    }

    public Rectangle(double x, double y, double w,  double h) {
        this.pos = new Vector2d(x, y);
        this.size = new Vector2d(w, h);
    }

    public Rectangle(Vector2d pos, Vector2d size) {
        this.pos = pos;
        this.size = size;
    }

    public Rectangle(Vector4d vec) {
        this(vec.x, vec.y, vec.z, vec.w);
    }

    public Vector2d getPos() {
        return pos;
    }

    public void setPos(Vector2d pos) {
        this.pos = pos;
    }

    public Vector2d getSize() {
        return size;
    }

    public void setSize(Vector2d size) {
        this.size = size;
    }

    public Vector2d getBound() {
        return this.pos.add(this.size);
    }

    public boolean contains(Vector2d point) {
        return (point.x >= pos.x && point.x <= getBound().x && point.y >= pos.y && point.y <= getBound().y);
    }

    public Transform getTransform() {
        return new Transform(pos.x, pos.y, 0, 0, 0, 0, size.x, size.y, 1);
    }
}
