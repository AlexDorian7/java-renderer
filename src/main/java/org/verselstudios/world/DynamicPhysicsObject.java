package org.verselstudios.world;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.physics.PhysicsWorld;
import physx.common.PxTransform;
import physx.physics.PxRigidDynamic;
import physx.physics.PxShape;

import java.util.List;

public class DynamicPhysicsObject extends PhysicsObject {
    protected DynamicPhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, PxShape shape) {
        super(modelTransform, renderSystem, texture, getRigidBody(shape, modelTransform));
    }

    protected DynamicPhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, List<PxShape> shapes) {
        super(modelTransform, renderSystem, texture, getRigidBody(shapes, modelTransform));
    }

    private static PxRigidDynamic getRigidBody(PxShape shape, Transform transform) {
        PxTransform tmpPose = transform.toPxTransform();
        PxRigidDynamic rigidBody = PhysicsWorld.getInstance().getPhysics().createRigidDynamic(tmpPose);
        rigidBody.attachShape(shape);
        shape.release();
        tmpPose.destroy();
        return rigidBody;
    }

    private static PxRigidDynamic getRigidBody(List<PxShape> shapes, Transform transform) {
        PxTransform tmpPose = transform.toPxTransform();
        PxRigidDynamic rigidBody = PhysicsWorld.getInstance().getPhysics().createRigidDynamic(tmpPose);
        for (PxShape shape : shapes) {
            rigidBody.attachShape(shape);
            shape.release();
        }
        tmpPose.destroy();
        return rigidBody;
    }
}
