package org.verselstudios.render;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.verselstudios.events.ActionType;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.math.Camera;
import org.verselstudios.math.Transform;

import org.verselstudios.physics.PhysXConvert;
import org.verselstudios.physics.Physical;
import org.verselstudios.physics.PhysicsWorld;
import physx.common.*;
import physx.extensions.PxRigidBodyExt;
import physx.geometry.*;
import physx.physics.*;

import java.util.HashSet;
import java.util.Set;

public class PhysicsCameraControlRenderer implements Renderer, Physical {

    private final Camera camera;
    private final PxRigidDynamic body;

    private static final float MOVE_SPEED = 6f;
    private static final float JUMP_FORCE = 250f;
    private static final double LOOK_SENSITIVITY = 0.002;

    private final Set<Integer> keysDown = new HashSet<>();

    private double pitch = 0.0;
    private double yaw   = 0.0;
    private final float radius = 0.3f;
    private final float height = 1.4f;

    // -------------------------------------------------------
    // CONSTRUCTOR
    // -------------------------------------------------------
    public PhysicsCameraControlRenderer(
            Camera camera,
            PxMaterial material
    ) {
        this.camera = camera;

        this.body = createRigidBody(PhysicsWorld.getInstance().getPhysics(), material, camera.getTransform());
        PhysicsWorld.getInstance().getScene().addActor(body);
    }

    // -------------------------------------------------------
    // CREATE RIGID BODY
    // -------------------------------------------------------
    private PxRigidDynamic createRigidBody(
            PxPhysics physics,
            PxMaterial material,
            Transform startTransform
    ) {

        PxTransform pose = startTransform.toPxTransform();
        PxRigidDynamic body = physics.createRigidDynamic(pose);

        // Capsule collider (FPS style)

        PxCapsuleGeometry geom = new PxCapsuleGeometry(radius, height / 2f);
        PxShape shape = physics.createShape(geom, material, true);

        PxFilterData filterData = new PxFilterData(1, 1, 0, 0);
        shape.setSimulationFilterData(filterData);
        shape.setQueryFilterData(filterData);

        // Rotate capsule upright
        PxTransform localPose = new Transform(0,0,0, 0,0,Math.PI/2, 1, 1, 1).toPxTransform();
        shape.setLocalPose(localPose);

        body.attachShape(shape);

        // Mass / inertia
        PxRigidBodyExt.updateMassAndInertia(body, 80f);

        // Damping
        body.setLinearDamping(0f);
        body.setAngularDamping(0f);

        // Prevent tipping over
        body.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_X, true);
        body.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Y, true);
        body.setRigidDynamicLockFlag(PxRigidDynamicLockFlagEnum.eLOCK_ANGULAR_Z, true);

        return body;
    }

    // -------------------------------------------------------
    // MAIN UPDATE
    // -------------------------------------------------------
    @Override
    public void updatePhysics() {
        applyMovement();
        syncTransformFromPhysics();
    }

    @Override
    public void render() {

    }

    // -------------------------------------------------------
    // MOVEMENT
    // -------------------------------------------------------
    private void applyMovement() {

        Vector3d forward = new Vector3d(0, 0, -1);
        Vector3d right   = new Vector3d(1, 0, 0);

        camera.getTransform().getRotation().transform(forward);
        camera.getTransform().getRotation().transform(right);

        forward.y = 0;
        right.y = 0;
        forward.normalize();
        right.normalize();

        Vector3d wishDir = new Vector3d();

        if (keysDown.contains(GLFW.GLFW_KEY_W)) wishDir.add(forward);
        if (keysDown.contains(GLFW.GLFW_KEY_S)) wishDir.sub(forward);
        if (keysDown.contains(GLFW.GLFW_KEY_D)) wishDir.add(right);
        if (keysDown.contains(GLFW.GLFW_KEY_A)) wishDir.sub(right);

        if (wishDir.lengthSquared() > 0)
            wishDir.normalize().mul(MOVE_SPEED);

        PxVec3 vel = body.getLinearVelocity();
        float yVel = vel.getY();

        vel.setX((float) wishDir.x);
        vel.setZ((float) wishDir.z);
        vel.setY(yVel);

        body.setLinearVelocity(vel);

        if (keysDown.contains(GLFW.GLFW_KEY_SPACE) && isGrounded()) {
            keysDown.remove(GLFW.GLFW_KEY_SPACE);
            body.addForce(new PxVec3(0, JUMP_FORCE, 0), PxForceModeEnum.eIMPULSE);
        }
    }

    // -------------------------------------------------------
    // SYNC PHYSICS → TRANSFORM
    // -------------------------------------------------------
    private void syncTransformFromPhysics() {
        PxTransform pose = body.getGlobalPose();
        Vector3d pos = PhysXConvert.toJoml(pose.getQ().getBasisVector1()).mul(height / 2).add(PhysXConvert.toJoml(pose.getP()));
        camera.getTransform().setPosition(pos);
    }

    // -------------------------------------------------------
    // INPUT
    // -------------------------------------------------------
    @Override
    public ActionType onKeyPress(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_SPACE) { // Do not allow space to be held
            if (event.isPress())
                keysDown.add(event.key());
            else
                keysDown.remove(event.key());
            return ActionType.CONSUME;
        }
        if (event.isPressed())
            keysDown.add(event.key());
        else
            keysDown.remove(event.key());

        return ActionType.CONSUME;
    }

    @Override
    public ActionType onMouseMove(MouseMoveEvent event) {

        double dx = event.dx();
        double dy = event.dy();

        yaw   -= dx * LOOK_SENSITIVITY;
        pitch -= dy * LOOK_SENSITIVITY;

        double limit = Math.toRadians(89);
        pitch = Math.max(-limit, Math.min(limit, pitch));

        Quaterniond q = new Quaterniond()
                .rotateY(yaw)
                .rotateX(pitch);

        camera.getTransform().getRotation().set(q);

        return ActionType.CONSUME;
    }

    // -------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------
    private boolean isGrounded() {
        return true; // Replace with raycast later
    }

    // -------------------------------------------------------
    // CLEANUP
    // -------------------------------------------------------
    public void destroy() {
        PhysicsWorld.getInstance().getScene().removeActor(body);
        body.release();
    }

    @Override
    public void onRemove() {
        Renderer.super.onRemove();
        destroy();
    }
}
