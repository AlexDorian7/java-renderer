package org.verselstudios.render;

import org.lwjgl.glfw.GLFW;
import org.verselstudios.events.*;
import org.verselstudios.math.*;

import java.util.HashSet;
import java.util.Set;

public class CameraControllRenderer implements Renderer {

    private final Camera camera;

    // Movement tuning
    private static final double MOVE_SPEED = 5.0;
    private static final double LOOK_SENSITIVITY = 0.002;

    // Input state
    private final Set<Integer> keysDown = new HashSet<>();
    private Vector2d lastMousePos = null;
    private boolean mouseCaptured = false;

    public CameraControllRenderer(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void render() {
        double dt = Time.deltaTime(); // assume you have this

        Vector3d movement = Vector3d.ZERO;

        Transform transform = camera.getTransform();
        Matrix4d basis = transform.getFlatRotationMatrix();

        Vector3d forward = basis.getForwardVector().negate(); // Camera faces down -Z
        Vector3d right   = basis.getRightVector();
        Vector3d up      = basis.getUpVector();


        if (keysDown.contains(GLFW.GLFW_KEY_W)) movement = movement.add(forward);
        if (keysDown.contains(GLFW.GLFW_KEY_S)) movement = movement.subtract(forward);
        if (keysDown.contains(GLFW.GLFW_KEY_D)) movement = movement.add(right);
        if (keysDown.contains(GLFW.GLFW_KEY_A)) movement = movement.subtract(right);
        if (keysDown.contains(GLFW.GLFW_KEY_SPACE)) movement = movement.add(up);
        if (keysDown.contains(GLFW.GLFW_KEY_LEFT_SHIFT)) movement = movement.subtract(up);


        if (!movement.isZero()) {
            movement = movement.normalize().multiply(MOVE_SPEED * dt);
            transform.setPosition(transform.getPosition().add(movement));
        }
    }

    @Override
    public ActionType onKeyPress(KeyEvent event) {
        if (event.isPressed()) {
            keysDown.add(event.key());
        } else {
            keysDown.remove(event.key());
        }
        return ActionType.CONSUME;
    }

    @Override
    public ActionType onMousePress(MousePressEvent event) {
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            mouseCaptured = event.isPressed();
            lastMousePos = null;
            return ActionType.CONSUME;
        }
        return ActionType.PASS;
    }

    @Override
    public ActionType onMouseMove(MouseMoveEvent event) {
        if (!mouseCaptured) return ActionType.PASS;
        if (lastMousePos == null) {
            lastMousePos = event.getPos();
            return ActionType.CONSUME;
        }

        Vector2d delta = event.getPos().subtract(lastMousePos);
        lastMousePos = event.getPos();

        Vector3d rot = camera.getTransform().getRotation();

        double yaw   = rot.getY() - delta.getX() * LOOK_SENSITIVITY;
        double pitch = rot.getX() - delta.getY() * LOOK_SENSITIVITY;

        // Clamp pitch (no flips)
        pitch = Math.max(-Math.PI / 2 + 0.01, Math.min(Math.PI / 2 - 0.01, pitch));

        camera.getTransform().setRotation(new Vector3d(pitch, yaw, 0));
        return ActionType.CONSUME;
    }
}
