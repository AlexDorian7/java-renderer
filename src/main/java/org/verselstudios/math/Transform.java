package org.verselstudios.math;

public class Transform {

    private Vector3d position;
    private Vector3d rotation;
    private Vector3d scale;

    public Transform() {
        this(0, 0, 0, 0, 0, 0, 1, 1, 1);
    }

    public Transform(double x, double y, double z, double rx, double ry, double rz, double sx, double sy, double sz) {
        this(new Vector3d(x, y, z), new Vector3d(rx, ry, rz), new Vector3d(sx, sy, sz));
    }

    public Transform (Vector3d position, Vector3d rotation, Vector3d scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Vector3d getPosition() {
        return position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
    }

    public Vector3d getScale() {
        return scale;
    }

    public void setScale(Vector3d scale) {
        this.scale = scale;
    }

    public Matrix4d getMatrix() {
        return Matrix4d.translation(position.getX(), position.getY(), position.getZ())
                .multiply(Matrix4d.rotationXYZ(rotation.getX(), rotation.getY(), rotation.getZ())
                        .multiply(Matrix4d.scale(scale.getX(), scale.getY(), scale.getZ())));
    }

    public Matrix4d getInverseMatrix() {
        return Matrix4d.scale(1/scale.getX(), 1/scale.getY(), 1/scale.getZ())
                .multiply(Matrix4d.rotationZYX(-rotation.getX(), -rotation.getY(), -rotation.getZ())
                        .multiply(Matrix4d.translation(-position.getX(), -position.getY(), -position.getZ())));
    }
}
