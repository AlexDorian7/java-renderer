package org.verselstudios.physics;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import org.verselstudios.math.Transform;

/**
 * Utility class for converting between JOML / engine math types and PhysX JNI types.
 *
 * All conversions are float-based when going into PhysX.
 */
public final class PhysXConvert {

    private PhysXConvert() {}

    // ------------------------------------------------------------------------
    // Vector3
    // ------------------------------------------------------------------------

    // JOML → PhysX
    public static PxVec3 toPx(Vector3d v) {
        return new PxVec3((float) v.x, (float) v.y, (float) v.z);
    }

    public static PxVec3 toPx(Vector3f v) {
        return new PxVec3(v.x, v.y, v.z);
    }

    // PhysX → JOML
    public static Vector3d toJoml(PxVec3 v) {
        return new Vector3d(v.getX(), v.getY(), v.getZ());
    }

    // PhysX → existing JOML (no allocation)
    public static void toJoml(PxVec3 v, Vector3d out) {
        out.set(v.getX(), v.getY(), v.getZ());
    }

    // ------------------------------------------------------------------------
    // Quaternion
    // ------------------------------------------------------------------------

    // JOML → PhysX
    public static PxQuat toPx(Quaterniond q) {
        return new PxQuat(
                (float) q.x,
                (float) q.y,
                (float) q.z,
                (float) q.w
        );
    }

    // PhysX → JOML
    public static Quaterniond toJoml(PxQuat q) {
        return new Quaterniond(
                q.getX(),
                q.getY(),
                q.getZ(),
                q.getW()
        );
    }

    // PhysX → existing JOML
    public static void toJoml(PxQuat q, Quaterniond out) {
        out.set(q.getX(), q.getY(), q.getZ(), q.getW());
    }

    // ------------------------------------------------------------------------
    // Transform (Position + Rotation)
    // ------------------------------------------------------------------------

    // JOML → PhysX
    public static PxTransform toPx(Vector3d position, Quaterniond rotation) {
        return new PxTransform(
                toPx(position),
                toPx(rotation)
        );
    }

    // Engine Transform → PhysX
    public static PxTransform toPx(Transform t) {
        return new PxTransform(
                toPx(t.getPosition()),
                toPx(t.getRotation())
        );
    }

    // PhysX → Engine Transform (no allocations)
    public static void toJoml(PxTransform px, Transform out) {
        toJoml(px.getP(), out.getPosition());
        toJoml(px.getQ(), out.getRotation());
    }

    // PhysX → JOML components
    public static void toJoml(PxTransform px, Vector3d posOut, Quaterniond rotOut) {
        toJoml(px.getP(), posOut);
        toJoml(px.getQ(), rotOut);
    }


}
