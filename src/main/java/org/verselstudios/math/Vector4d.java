package org.verselstudios.math;

public class Vector4d {
    public static final Vector4d ONE = new Vector4d(1,1,1,1);
    private double x;
    private double y;
    private double z;
    private double w;

    public Vector4d() {
        this(0, 0, 0, 0);
    }

    public Vector4d(double x) {
        this(x, 0, 0, 0);
    }

    public Vector4d(double x, double y) {
        this(x, y, 0, 0);
    }

    public Vector4d(double x, double y, double z) {
        this(x, y, z, 0);
    }

    public Vector4d(Vector3d vec) {
        this(vec, 1);
    }

    public Vector4d(Vector3d vec, double w) {
        this.x = vec.getX();
        this.y = vec.getY();
        this.z = vec.getZ();
        this.w = w;
    }

    public Vector4d(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
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

    public double getW() {
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public Vector4d add(Vector4d b) {
        return new Vector4d(x + b.x, y + b.y, z + b.z, w + b.w);
    }

    public Vector4d negate() {
        return new Vector4d(-x, -y, -z, -w);
    }

    public Vector4d subtract(Vector4d b) {
        Vector4d c = b.negate();
        return new Vector4d(x + c.x, y + c.y, z + c.z, w + c.w);
    }

    public Vector4d multiply(double scalar) {
        return new Vector4d(x * scalar, y * scalar, z * scalar, w * scalar);
    }

    public Vector4d multiply(Vector4d b) {
        return new Vector4d(x * b.x, y * b.y, z * b.z, w * b.w);
    }

    public Vector4d divide(double scalar) {
        return new Vector4d(x / scalar, y / scalar, z / scalar, w / scalar);
    }

    public Vector4d divide(Vector4d b) {
        return new Vector4d(x / b.x, y / b.y, z / b.z, w / b.w);
    }

    public double dot(Vector4d b) {
        return x * b.x + y * b.y + z * b.z + w * b.w;
    }

    public double magnitude() {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Vector4d normalized() {
        double mag = magnitude();
        if (mag == 0.0) {
            // Return zero vector to avoid NaNs
            return new Vector4d(0, 0, 0, 0);
        }
        return new Vector4d(x / mag, y / mag, z / mag, w / mag);
    }

    public void normalize() {
        double mag = magnitude();
        if (mag == 0.0) {
            return; // leave as zero vector
        }
        x /= mag;
        y /= mag;
        z /= mag;
        w /= mag;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector4d other)) return false;

        final double EPSILON = 1e-9;

        return Math.abs(x - other.x) < EPSILON &&
                Math.abs(y - other.y) < EPSILON &&
                Math.abs(z - other.z) < EPSILON &&
                Math.abs(w - other.w) < EPSILON;
    }

    @Override
    public int hashCode() {
        final long lx = Double.doubleToLongBits(x);
        final long ly = Double.doubleToLongBits(y);
        final long lz = Double.doubleToLongBits(z);
        final long lw = Double.doubleToLongBits(w);

        int result = 17;
        result = 31 * result + Long.hashCode(lx);
        result = 31 * result + Long.hashCode(ly);
        result = 31 * result + Long.hashCode(lz);
        result = 31 * result + Long.hashCode(lw);
        return result;
    }

}
