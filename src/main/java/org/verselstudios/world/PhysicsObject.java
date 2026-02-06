package org.verselstudios.world;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.physics.Physical;
import org.verselstudios.physics.PhysicsWorld;
import physx.common.PxVec3;
import physx.physics.PxForceModeEnum;
import physx.physics.PxRigidActor;
import physx.physics.PxRigidBody;
import physx.physics.PxRigidDynamic;

public abstract class PhysicsObject extends DepthObject implements Physical {

    private static final Logger LOGGER = LogManager.getLogger(PhysicsObject.class);

    private PxRigidActor rigidActor;
    protected PhysicsObject(Transform modelTransform, RenderSystem renderSystem, Texture texture, PxRigidActor rigidActor) {
        super(modelTransform, renderSystem, texture);
        this.rigidActor = rigidActor;
        PhysicsWorld.getInstance().getScene().addActor(rigidActor);
    }

    /**
     * Sets the physics transform to the model transform and clears motion.
     */
    public void teleport() {
        LOGGER.debug("Teleporting");

        // Move instantly
        rigidActor.setGlobalPose(modelTransform.toPxTransform());

        if (rigidActor instanceof PxRigidDynamic rigidBody) {

            // Stop all motion
            rigidBody.setLinearVelocity(new PxVec3(0,0,0));
            rigidBody.setAngularVelocity(new PxVec3(0,0,0));

            // Optional but good practice
            rigidBody.clearForce(PxForceModeEnum.eFORCE);
            rigidBody.clearTorque(PxForceModeEnum.eFORCE);

            // Make sure the body is awake after teleport
        }
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
        rigidActor.release();
    }

    @Override
    public void updatePhysics() {
        modelTransform.setFromPxTransform(rigidActor.getGlobalPose());
    }
}
