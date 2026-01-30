package org.verselstudios.math;

import java.nio.FloatBuffer;

public class Matrix4d {

    private final double[] m = new double[16];

    /* =========================
       Constructors
       ========================= */

    public Matrix4d() {
        identity();
    }

    public Matrix4d(double[] values) {
        if (values.length != 16)
            throw new IllegalArgumentException("Matrix4d requires 16 values");
        System.arraycopy(values, 0, m, 0, 16);
    }

    /* =========================
       Basic matrices
       ========================= */

    public Matrix4d identity() {
        for (int i = 0; i < 16; i++) m[i] = 0;
        m[0] = m[5] = m[10] = m[15] = 1;
        return this;
    }

    public static Matrix4d identityMatrix() {
        return new Matrix4d();
    }

    /* =========================
       Accessors
       ========================= */

    public double get(int row, int col) {
        return m[col * 4 + row];
    }

    public void set(int row, int col, double value) {
        m[col * 4 + row] = value;
    }

    public double[] raw() {
        return m;
    }

    /* =========================
       Matrix operations
       ========================= */

    public Matrix4d multiply(Matrix4d b) {
        Matrix4d result = new Matrix4d();
        result.identity();

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                double sum = 0;
                for (int i = 0; i < 4; i++) {
                    sum += get(row, i) * b.get(i, col);
                }
                result.set(row, col, sum);
            }
        }
        return result;
    }

    public Vector4d multiply(Vector4d v) {
        return new Vector4d(
                get(0,0)*v.getX() + get(0,1)*v.getY() + get(0,2)*v.getZ() + get(0,3)*v.getW(),
                get(1,0)*v.getX() + get(1,1)*v.getY() + get(1,2)*v.getZ() + get(1,3)*v.getW(),
                get(2,0)*v.getX() + get(2,1)*v.getY() + get(2,2)*v.getZ() + get(2,3)*v.getW(),
                get(3,0)*v.getX() + get(3,1)*v.getY() + get(3,2)*v.getZ() + get(3,3)*v.getW()
        );
    }

    /* =========================
       Transform builders
       ========================= */

    public static Matrix4d translation(double x, double y, double z) {
        Matrix4d mat = new Matrix4d();
        mat.set(0, 3, x);
        mat.set(1, 3, y);
        mat.set(2, 3, z);
        return mat;
    }

    public static Matrix4d scale(double x, double y, double z) {
        Matrix4d mat = new Matrix4d();
        mat.set(0, 0, x);
        mat.set(1, 1, y);
        mat.set(2, 2, z);
        return mat;
    }

    public static Matrix4d rotationX(double radians) {
        Matrix4d mat = new Matrix4d();
        double c = Math.cos(radians);
        double s = Math.sin(radians);

        mat.set(1, 1,  c);
        mat.set(2, 1,  s);
        mat.set(1, 2, -s);
        mat.set(2, 2,  c);

        return mat;
    }

    public static Matrix4d rotationY(double radians) {
        Matrix4d mat = new Matrix4d();
        double c = Math.cos(radians);
        double s = Math.sin(radians);

        mat.set(0, 0,  c);
        mat.set(2, 0, -s);
        mat.set(0, 2,  s);
        mat.set(2, 2,  c);

        return mat;
    }

    public static Matrix4d rotationZ(double radians) {
        Matrix4d mat = new Matrix4d();
        double c = Math.cos(radians);
        double s = Math.sin(radians);

        mat.set(0, 0,  c);
        mat.set(1, 0,  s);
        mat.set(0, 1, -s);
        mat.set(1, 1,  c);

        return mat;
    }


    public static Matrix4d rotationYXZ(double radiansX, double radiansY, double radiansZ) {
        Matrix4d matX = rotationX(radiansX);
        Matrix4d matY = rotationY(radiansY);
        Matrix4d matZ = rotationZ(radiansZ);

        Matrix4d m1 = matY.multiply(matX);
        return m1.multiply(matZ);
    }

    public static Matrix4d rotationXYZ(double radiansX, double radiansY, double radiansZ) {
        Matrix4d matX = rotationX(radiansX);
        Matrix4d matY = rotationY(radiansY);
        Matrix4d matZ = rotationZ(radiansZ);

        Matrix4d m1 = matX.multiply(matY);
        return m1.multiply(matZ);
    }

    public static Matrix4d rotationZYX(double radiansX, double radiansY, double radiansZ) {
        Matrix4d matX = rotationX(radiansX);
        Matrix4d matY = rotationY(radiansY);
        Matrix4d matZ = rotationZ(radiansZ);

        Matrix4d m1 = matZ.multiply(matY);
        return m1.multiply(matX);
    }

    /* =========================
       Projection matrices
       ========================= */
    public static Matrix4d ortho(
            double left, double right,
            double bottom, double top,
            double near, double far
    ) {
        Matrix4d mat = new Matrix4d();

        mat.set(0, 0, 2.0 / (right - left));
        mat.set(1, 1, 2.0 / (top - bottom));
        mat.set(2, 2, -2.0 / (far - near));

        mat.set(0, 3, -(right + left) / (right - left));
        mat.set(1, 3, -(top + bottom) / (top - bottom));
        mat.set(2, 3, -(far + near) / (far - near));

        return mat;
    }

    public static Matrix4d perspective(double fov, double near, double far, double width, double height) {
        double aspect = width / height;
        double f = 1.0 / Math.tan(Math.toRadians(fov) / 2.0);

        Matrix4d mat = new Matrix4d();
        mat.set(0, 0, f / aspect);
        mat.set(1, 1, f);
        mat.set(2, 2, (far + near) / (near - far));
        mat.set(2, 3, (2 * far * near) / (near - far));
        mat.set(3, 2, -1);
        mat.set(3, 3, 0);

        return mat;
    }

    /* =========================
       Utilities
       ========================= */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Matrix4d other)) return false;
        for (int i = 0; i < 16; i++) {
            if (Double.compare(m[i], other.m[i]) != 0)
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Matrix4d:\n");
        for (int row = 0; row < 4; row++) {
            sb.append("[ ");
            for (int col = 0; col < 4; col++) {
                sb.append(String.format("%8.3f ", get(row, col)));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public void store(FloatBuffer buffer) {
        // Column-major order (OpenGL expects this)
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                buffer.put((float) get(row, col));
            }
        }
    }

    public Vector3d getRightVector() {
        return new Vector3d(get(0, 0), get(1, 0), get(2, 0)).normalize();
    }

    public Vector3d getUpVector() {
        return new Vector3d(get(0, 1), get(1, 1), get(2, 1)).normalize();
    }

    public Vector3d getForwardVector() {
        return new Vector3d(get(0, 2), get(1, 2), get(2, 2)).normalize();
    }
}
