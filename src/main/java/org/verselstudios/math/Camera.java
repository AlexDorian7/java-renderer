package org.verselstudios.math;

import org.joml.Quaterniond;
import org.joml.Vector3d;

public class Camera {
    private Transform transform;

    public Camera() {
        this(new Transform(new Vector3d(0, 0, 0), new Quaterniond(), new Vector3d(1)));
    }

    public Camera(Transform transform) {
        this.transform = transform;
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform = transform;
    }
}
