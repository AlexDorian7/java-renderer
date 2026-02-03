package org.verselstudios.math;

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
        this(vec.getX(), vec.getY(), vec.getZ(), vec.getW());
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
        return (point.getX() >= pos.getX() && point.getX() <= getBound().getX() && point.getY() >= pos.getY() && point.getY() <= getBound().getY());
    }

    public Transform getTransform() {
        return new Transform(pos.getX(), pos.getY(), 0, 0, 0, 0, size.getX(), size.getY(), 1);
    }
}
