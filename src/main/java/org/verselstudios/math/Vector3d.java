package org.verselstudios.math;

public class Vector3d {
    private double x;
    private double y;
    private double z;

    public Vector3d() {
        this(0, 0, 0);
    }

    public Vector3d(double x) {
        this(x, 0, 0);
    }

    public Vector3d(double x, double y) {
        this(x, y, 0);
    }

    public Vector3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
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

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3d add(Vector3d b) {
        return new Vector3d(x + b.x, y + b.y, z + b.z);
    }

    public Vector3d negate() {
        return new Vector3d(-x, -y, -z);
    }

    public Vector3d subtract(Vector3d b) {
        Vector3d c = b.negate();
        return new Vector3d(x + c.x, y + c.y, z + c.z);
    }

    public Vector3d multiply(double scalar) {
        return new Vector3d(x * scalar, y * scalar, z * scalar);
    }

    public Vector3d multiply(Vector3d b) {
        return new Vector3d(x * b.x, y * b.y, z * b.z);
    }

    public Vector3d divide(double scalar) {
        return new Vector3d(x / scalar, y / scalar, z / scalar);
    }

    public Vector3d divide(Vector3d b) {
        return new Vector3d(x / b.x, y / b.y, z / b.z);
    }

    public double dot(Vector3d b) {
        return x*b.x + y*b.y + z*b.z;
    }

    public double magnitude() {
        return Math.sqrt(x*x+y*y+z*z);
    }

    public Vector3d cross(Vector3d b) {
        return new Vector3d(
                y * b.z - z * b.y,
                z * b.x - x * b.z,
                x * b.y - y * b.x
        );
    }

    public Vector3d normalized() {
        double mag = magnitude();
        if (mag == 0.0) {
            return new Vector3d(0, 0, 0);
        }
        return new Vector3d(x / mag, y / mag, z / mag);
    }

    public void normalize() {
        double mag = magnitude();
        if (mag == 0.0) return;

        x /= mag;
        y /= mag;
        z /= mag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector3d other)) return false;

        final double EPSILON = 1e-9;

        return Math.abs(x - other.x) < EPSILON &&
                Math.abs(y - other.y) < EPSILON &&
                Math.abs(z - other.z) < EPSILON;
    }

    @Override
    public int hashCode() {
        long lx = Double.doubleToLongBits(x);
        long ly = Double.doubleToLongBits(y);
        long lz = Double.doubleToLongBits(z);

        int result = 17;
        result = 31 * result + Long.hashCode(lx);
        result = 31 * result + Long.hashCode(ly);
        result = 31 * result + Long.hashCode(lz);
        return result;
    }
}
