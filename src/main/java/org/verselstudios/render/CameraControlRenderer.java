package org.verselstudios.render;

import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.lwjgl.glfw.GLFW;
import org.verselstudios.events.*;
import org.verselstudios.math.Camera;
import org.verselstudios.math.Time;
import org.verselstudios.math.Transform;

import java.util.HashSet;
import java.util.Set;

public class CameraControlRenderer implements Renderer {

    private final Camera camera;

    private static final double MOVE_SPEED = 5.0;
    private static final double LOOK_SENSITIVITY = 0.002;
    private static final double MAX_MOUSE_DELTA = 50.0; // clamp extreme deltas

    private final Set<Integer> keysDown = new HashSet<>();
    private boolean mouseLocked = true;

    private double pitch = 0.0;
    private double yaw   = 0.0;

    public CameraControlRenderer(Camera camera) {
        this.camera = camera;
    }

    // -------------------------------------------------------
    // Movement
    // -------------------------------------------------------
    @Override
    public void render() {
        double dt = Time.deltaTime();

        Transform t = camera.getTransform();
        Vector3d movement = new Vector3d();

        Vector3d forward = new Vector3d(0,0,-1);
        Vector3d right   = new Vector3d(1,0,0);
        Vector3d up      = new Vector3d(0,1,0);

        t.getRotation().transform(forward);
        t.getRotation().transform(right);
        t.getRotation().transform(up);

        if (keysDown.contains(GLFW.GLFW_KEY_W)) movement.add(forward);
        if (keysDown.contains(GLFW.GLFW_KEY_S)) movement.sub(forward);
        if (keysDown.contains(GLFW.GLFW_KEY_D)) movement.add(right);
        if (keysDown.contains(GLFW.GLFW_KEY_A)) movement.sub(right);
        if (keysDown.contains(GLFW.GLFW_KEY_SPACE)) movement.add(up);
        if (keysDown.contains(GLFW.GLFW_KEY_LEFT_SHIFT)) movement.sub(up);

        if (movement.lengthSquared() > 0) {
            movement.normalize().mul(MOVE_SPEED * dt);
            t.getPosition().add(movement);
        }
    }

    // -------------------------------------------------------
    // Key Input
    // -------------------------------------------------------
    @Override
    public ActionType onKeyPress(KeyEvent event) {
        if (event.isPressed())
            keysDown.add(event.key());
        else
            keysDown.remove(event.key());

        return ActionType.CONSUME;
    }

    // -------------------------------------------------------
    // Mouse Look
    // -------------------------------------------------------
    @Override
    public ActionType onMouseMove(MouseMoveEvent event) {
        if (!mouseLocked) return ActionType.PASS;

        // Clamp deltas
        double dx = event.dx();
        double dy = event.dy();

        yaw   -= dx * LOOK_SENSITIVITY;
        pitch -= dy * LOOK_SENSITIVITY;

        double limit = Math.toRadians(89);
        pitch = Math.max(-limit, Math.min(limit, pitch));

        Quaterniond q = new Quaterniond()
                .rotateY(yaw)
                .rotateX(pitch);

        camera.getTransform().getRotation().set(q);
        return ActionType.CONSUME;
    }
}
