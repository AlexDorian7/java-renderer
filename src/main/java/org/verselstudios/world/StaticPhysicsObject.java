package org.verselstudios.world;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.physics.PhysicsWorld;
import physx.common.PxTransform;
import physx.physics.PxRigidStatic;
import physx.physics.PxShape;

public class StaticPhysicsObject extends PhysicsObject {
    protected StaticPhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, PxShape shape) {
        super(modelTransform, renderSystem, texture, getRigidBody(shape, modelTransform));
    }

    private static PxRigidStatic getRigidBody(PxShape shape, Transform transform) {
        PxTransform tmpPose = transform.toPxTransform();
        PxRigidStatic rigidBody = PhysicsWorld.getInstance().getPhysics().createRigidStatic(tmpPose);
        rigidBody.attachShape(shape);
        shape.release();
        tmpPose.destroy();
        return rigidBody;
    }
}
