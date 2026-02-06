package org.verselstudios.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.physics.Physical;
import org.verselstudios.physics.PhysicsWorld;
import physx.physics.PxRigidActor;

public abstract class PhysicsObject extends DepthObject implements Physical {

    private static final Logger LOGGER = LogManager.getLogger(PhysicsObject.class);

    private PxRigidActor rigidBody;
    protected PhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, PxRigidActor rigidActor) {
        super(modelTransform, renderSystem, texture);
        this.rigidBody = rigidActor;
        PhysicsWorld.getInstance().getScene().addActor(rigidActor);
    }

    /**
     * Sets the physics transform to the model transform
     */
    public void teleport() {
        LOGGER.debug("Teleporting");
//        rigidBody.getGlobalPose().destroy();
        rigidBody.setGlobalPose(modelTransform.toPxTransform());

    }


    @Override
    public void onRemove() {
        super.onRemove();
        clean();
    }

    /**
     * This should be called before the object is removed
     */
    public void clean() {
        rigidBody.release();
    }

    @Override
    public void updatePhysics() {
        modelTransform.setFromPxTransform(rigidBody.getGlobalPose());
    }
}
