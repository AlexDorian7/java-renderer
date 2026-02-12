package org.verselstudios.world.objects;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.CylinderRenderSystem;
import org.verselstudios.physics.PhysicsWorld;
import org.verselstudios.shader.material.Material;
import org.verselstudios.world.DynamicPhysicsObject;
import physx.common.PxQuat;
import physx.common.PxTransform;
import physx.common.PxVec3;
import physx.geometry.*;
import physx.physics.*;

import java.util.ArrayList;
import java.util.List;

public class DynamicCylinder extends DynamicPhysicsObject {

    private static final PxShapeFlags SHAPE_FLAGS = new PxShapeFlags((byte) (PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value | PxShapeFlagEnum.eSIMULATION_SHAPE.value));
    private static final PxFilterData FILTER_DATA = new PxFilterData(1, 1, 0, 0);

    public DynamicCylinder(Transform modelTransform, double radius, double halfHeight, int steps, Texture texture, Material material, PxMaterial pxMaterial) {
        super(modelTransform, new CylinderRenderSystem(radius, halfHeight, steps, material), texture, createShapes((float) radius, (float) halfHeight, steps, pxMaterial));
    }

    public static List<PxShape> createShapes(float radius, float halfHeight, int steps, PxMaterial material) {
        List<PxShape> shapes = new ArrayList<>(steps);

        double angleStep = 2 * Math.PI / steps;

        for (int i = 0; i < steps; i++) {
            double angle = i * angleStep;

            // Chord length along the circumference for this segment
            float boxWidth = (float)(2 * radius * Math.tan(Math.PI / steps));
            float boxLength = radius; // from center to edge

            PxBoxGeometry geom = new PxBoxGeometry(
                    boxLength,    // along radius
                    halfHeight,   // vertical
                    boxWidth * 0.5f  // along tangent
            );

            PxShape shape = PhysicsWorld.getInstance().getPhysics().createShape(geom, material, true, SHAPE_FLAGS);
            shape.setSimulationFilterData(FILTER_DATA);

            // Orientation of wedge around Y axis
            PxQuat rot = new PxQuat(0, (float)Math.sin(angle/2), 0, (float)Math.cos(angle/2));

            // Offset outward so box stretches from center → edge
            float x = (float)(Math.cos(angle) * boxLength * 0.5);
            float z = (float)(Math.sin(angle) * boxLength * 0.5);

            PxTransform localPose = new PxTransform(new PxVec3(x, 0, z), rot);
            shape.setLocalPose(localPose);

            shapes.add(shape);
        }

        return shapes;
    }

}
