package org.verselstudios.math;

public class Vector2d {
    public static final Vector2d ZERO = new Vector2d(0,0);
    private double x;
    private double y;

    public Vector2d() {
        this(0, 0);
    }

    public Vector2d(double x) {
        this(x, 0);
    }

    public Vector2d(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Vector2d add(Vector2d b) {
        return new Vector2d(x + b.x, y + b.y);
    }

    public Vector2d negate() {
        return new Vector2d(-x, -y);
    }

    public Vector2d subtract(Vector2d b) {
        Vector2d c = b.negate();
        return new Vector2d(x + c.x, y + c.y);
    }

    public Vector2d multiply(double scalar) {
        return new Vector2d(x * scalar, y * scalar);
    }

    public Vector2d multiply(Vector2d b) {
        return new Vector2d(x * b.x, y * b.y);
    }

    public Vector2d divide(double scalar) {
        return new Vector2d(x / scalar, y / scalar);
    }

    public Vector2d divide(Vector2d b) {
        return new Vector2d(x / b.x, y / b.y);
    }

    public double dot(Vector2d b) {
        return x * b.x + y * b.y;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2d normalized() {
        double mag = magnitude();
        if (mag == 0.0) {
            return new Vector2d(0, 0);
        }
        return new Vector2d(x / mag, y / mag);
    }

    public void normalize() {
        double mag = magnitude();
        if (mag == 0.0) return;

        x /= mag;
        y /= mag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector2d other)) return false;

        final double EPSILON = 1e-9;

        return Math.abs(x - other.x) < EPSILON &&
                Math.abs(y - other.y) < EPSILON;
    }

    @Override
    public int hashCode() {
        long lx = Double.doubleToLongBits(x);
        long ly = Double.doubleToLongBits(y);

        int result = 17;
        result = 31 * result + Long.hashCode(lx);
        result = 31 * result + Long.hashCode(ly);
        return result;
    }
}
