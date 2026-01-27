package org.verselstudios.math;

public class Camera {
    private Transform transform;

    public Camera() {
        this(new Transform());
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
