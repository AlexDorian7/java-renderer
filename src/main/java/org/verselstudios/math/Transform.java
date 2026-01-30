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

    public Matrix4d getViewMatrix() {
        Matrix4d view = new Matrix4d();

        // Inverse rotation (note the minus signs)
        view = Matrix4d.rotationZ(-rotation.getZ())
                .multiply(view);
        view = Matrix4d.rotationY(-rotation.getY())
                .multiply(view);
        view = Matrix4d.rotationX(-rotation.getX())
                .multiply(view);

        // Inverse translation
        view = Matrix4d.translation(
                -position.getX(),
                -position.getY(),
                -position.getZ()
        ).multiply(view);

        return view;
    }

    public Matrix4d getModelMatrix() {
        Matrix4d model = new Matrix4d();

        // Translation (last applied, first multiplied)
        model = Matrix4d.translation(
                position.getX(),
                position.getY(),
                position.getZ()
        ).multiply(model);

        // Rotation
        model = Matrix4d.rotationZ(rotation.getZ()).multiply(model);
        model = Matrix4d.rotationY(rotation.getY()).multiply(model);
        model = Matrix4d.rotationX(rotation.getX()).multiply(model);

        // Scale (first applied)
        model = Matrix4d.scale(
                scale.getX(),
                scale.getY(),
                scale.getZ()
        ).multiply(model);

        return model;
    }

    public Matrix4d getFlatRotationMatrix() {
        return Matrix4d.rotationY(rotation.getY());
    }

}
