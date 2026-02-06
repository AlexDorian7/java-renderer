package org.verselstudios.math;

import org.joml.Matrix4d;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.verselstudios.physics.PhysXConvert;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;

public class Transform {

    private Vector3d position;
    private Quaterniond rotation;
    private Vector3d scale;

    public Transform() {
        this(0, 0, 0, 0, 0, 0, 1, 1, 1);
    }

    public Transform(double x, double y, double z, double rx, double ry, double rz, double sx, double sy, double sz) {
        this(new Vector3d(x, y, z), new Quaterniond().rotateX(rx).rotateY(ry).rotateZ(rz), new Vector3d(sx, sy, sz));
    }

    public Transform (Vector3d position, Quaterniond rotation, Vector3d scale) {
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

    public Quaterniond getRotation() {
        return rotation;
    }

    public void setRotation(Quaterniond rotation) {
        this.rotation = rotation;
    }

    public Vector3d getScale() {
        return scale;
    }

    public void setScale(Vector3d scale) {
        this.scale = scale;
    }

    public Matrix4d getViewMatrix() {
        Quaterniond inverse = new Quaterniond();
        rotation.invert(inverse);
        return new Matrix4d()
                .rotate(inverse)
                .translate(-position.x, -position.y, -position.z);
    }


    public Matrix4d getModelMatrix() {
        return new Matrix4d()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }


    public Matrix4d getFlatRotationMatrix() {
        // Extract yaw from the quaternion
        double yaw = Math.atan2(
                2.0 * (rotation.w * rotation.y + rotation.x * rotation.z),
                1.0 - 2.0 * (rotation.y * rotation.y + rotation.x * rotation.x)
        );

        return new Matrix4d().rotateY(yaw);
    }

    // PhysX does not support scale
    public PxTransform toPxTransform() {
        return new PxTransform(
                PhysXConvert.toPx(position),
                PhysXConvert.toPx(rotation)
        );
    }

    public PxTransform toPxTransform(PxTransform pxTransform) {
        pxTransform.setP(PhysXConvert.toPx(position));
        pxTransform.setQ(PhysXConvert.toPx(rotation));
        return pxTransform;
    }

    public void setFromPxTransform(PxTransform px) {
        PxVec3 p = px.getP();
        PxQuat q = px.getQ();

        position.set(p.getX(), p.getY(), p.getZ());
        rotation.set(q.getX(), q.getY(), q.getZ(), q.getW());
    }

    public static Transform fromPxTransform(PxTransform px) {
        PxVec3 p = px.getP();
        PxQuat q = px.getQ();

        return new Transform(
                new Vector3d(p.getX(), p.getY(), p.getZ()),
                new Quaterniond(q.getX(), q.getY(), q.getZ(), q.getW()),
                new Vector3d(1, 1, 1) // PhysX has no scale
        );
    }

    @Override
    public String toString() {
        return "Transform\n    Position: " + position + "\n    Rotation: " + rotation + "\n    Scale: " + scale;
    }

}
