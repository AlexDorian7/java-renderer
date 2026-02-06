package org.verselstudios.physics.material;

import org.verselstudios.physics.PhysicsWorld;
import physx.physics.PxMaterial;

public class PhysicsMaterials {
    public static final PxMaterial DEFAULT = PhysicsWorld.getInstance().getPhysics().createMaterial(0.5f, 0.5f, 0.5f);
}
