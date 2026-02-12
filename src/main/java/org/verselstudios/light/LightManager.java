package org.verselstudios.light;

import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.List;

public class LightManager {
    private static final DirectionalLight SUN = new DirectionalLight(new Vector3d(1).normalize(), new Vector3d(0), new Vector3d(0), new Vector3d(0));

    private static final ArrayList<PointLight> LIGHTS = new ArrayList<>();

    static {
//        LIGHTS.add(new PointLight(new Vector3d(), new Vector3d(0), new Vector3d(1), new Vector3d(1), new Vector3d(1, 0.022, 0.0019)));
        LIGHTS.add(new PointLight(new Vector3d(-10, -15, 0), new Vector3d(0.125,0,0), new Vector3d(1,0,0), new Vector3d(1,0,0), new Vector3d(1, 0.022, 0.0019)));
        LIGHTS.add(new PointLight(new Vector3d(0, -15, 0), new Vector3d(0,0.125,0), new Vector3d(0,1,0), new Vector3d(0,1,0), new Vector3d(1, 0.022, 0.0019)));
        LIGHTS.add(new PointLight(new Vector3d(10, -15, 0), new Vector3d(0,0,0.125), new Vector3d(0,0,1), new Vector3d(0,0,1), new Vector3d(1, 0.022, 0.0019)));
    }

    public static DirectionalLight getDirectionalLight() {
        return SUN;
    }

    public static ArrayList<PointLight> getLights() {
        return LIGHTS;
    }

    public static List<PointLight> getClosestLights(Vector3d position) {

        // No lights
        if (LIGHTS.isEmpty()) {
            return List.of();
        }

        // If <= 16 just return a copy
        if (LIGHTS.size() <= 16) {
            return new ArrayList<>(LIGHTS);
        }

        // Copy so we don't mutate the original list
        ArrayList<PointLight> sorted = new ArrayList<>(LIGHTS);

        // Sort by squared distance
        sorted.sort((a, b) -> {
            double da = a.position().distanceSquared(position);
            double db = b.position().distanceSquared(position);
            return Double.compare(da, db);
        });

        // Return first 16
        return sorted.subList(0, 16);
    }


}
