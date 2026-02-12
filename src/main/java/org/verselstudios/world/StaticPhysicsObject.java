package org.verselstudios.world;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.physics.PhysicsWorld;
import physx.common.PxTransform;
import physx.physics.PxRigidStatic;
import physx.physics.PxShape;

import java.util.List;

public class StaticPhysicsObject extends PhysicsObject {
    protected StaticPhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, PxShape shape) {
        super(modelTransform, renderSystem, texture, getRigidBody(shape, modelTransform));
    }

    protected StaticPhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, List<PxShape> shapes) {
        super(modelTransform, renderSystem, texture, getRigidBody(shapes, modelTransform));
    }

    private static PxRigidStatic getRigidBody(PxShape shape, Transform transform) {
        PxTransform tmpPose = transform.toPxTransform();
        PxRigidStatic rigidBody = PhysicsWorld.getInstance().getPhysics().createRigidStatic(tmpPose);
        rigidBody.attachShape(shape);
        shape.release();
        tmpPose.destroy();
        return rigidBody;
    }

    private static PxRigidStatic getRigidBody(List<PxShape> shapes, Transform transform) {
        PxTransform tmpPose = transform.toPxTransform();
        PxRigidStatic rigidBody = PhysicsWorld.getInstance().getPhysics().createRigidStatic(tmpPose);
        for (PxShape shape : shapes) {
            rigidBody.attachShape(shape);
            shape.release();
        }
        tmpPose.destroy();
        return rigidBody;
    }
}
