package org.verselstudios.world.objects;

import org.joml.Vector3d;
import org.verselstudios.Image.Texture;
import org.verselstudios.math.Time;
import org.verselstudios.math.Transform;
import org.verselstudios.model.BoxRenderSystem;
import org.verselstudios.physics.PhysicsWorld;
import org.verselstudios.world.DynamicPhysicsObject;
import physx.geometry.PxBoxGeometry;
import physx.physics.*;

public class FallingDynamicBox extends DynamicPhysicsObject {

    private static final PxShapeFlags SHAPE_FLAGS = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value | PxShapeFlagEnum.eSIMULATION_SHAPE.value));
    private static final PxFilterData FILTER_DATA = new PxFilterData(1, 1, 0, 0);
    private double time = 0;

    private final Transform spawn;

    public FallingDynamicBox(Transform modelTransform, Vector3d radius, Texture texture, PxMaterial material) {
        super(modelTransform, new BoxRenderSystem(radius), texture, createShape(radius, material));
        spawn = new Transform(modelTransform);
    }

    private static PxShape createShape(Vector3d radius, PxMaterial material) {
        PxBoxGeometry boxGeometry = new PxBoxGeometry((float) radius.x, (float) radius.y, (float) radius.z);
        PxShape shape = PhysicsWorld.getInstance().getPhysics().createShape(boxGeometry, material, true, SHAPE_FLAGS);
        boxGeometry.destroy();
        shape.setSimulationFilterData(FILTER_DATA);
        return shape;
    }

    @Override
    protected void postRender() {
        super.postRender();
        time += Time.deltaTime();
        if (time >= 5) {
            time = 0;
            modelTransform.getPosition().set(spawn.getPosition());
            modelTransform.getRotation().set(spawn.getRotation());
            teleport();
        }
    }
}
