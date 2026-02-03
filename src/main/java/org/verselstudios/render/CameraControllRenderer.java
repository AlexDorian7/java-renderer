package org.verselstudios.render;

import org.verselstudios.events.*;
import org.verselstudios.math.*;

import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.glfw.GLFW.*;

public class CameraControllRenderer implements Renderer {

    private final Camera camera;

    // Movement tuning
    private static final double MOVE_SPEED = 5.0;
    private static final double LOOK_SENSITIVITY = 0.002;

    // Input state
    private final Set<Integer> keysDown = new HashSet<>();
    private double windowWidth = 1, windowHeight = 1;
    private double centerX = 0, centerY = 0;

    // Store yaw/pitch internally
    private double yaw = 0;
    private double pitch = 0;

    public CameraControllRenderer(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void render() {
        double dt = Time.deltaTime();

        Transform transform = camera.getTransform();
        Matrix4d basis = transform.getFlatRotationMatrix();

        Vector3d forward = basis.getForwardVector().negate(); // Camera looks down -Z
        Vector3d right   = basis.getRightVector();
        Vector3d up      = basis.getUpVector();

        Vector3d movement = Vector3d.ZERO;
        if (keysDown.contains(GLFW_KEY_W)) movement = movement.add(forward);
        if (keysDown.contains(GLFW_KEY_S)) movement = movement.subtract(forward);
        if (keysDown.contains(GLFW_KEY_D)) movement = movement.add(right);
        if (keysDown.contains(GLFW_KEY_A)) movement = movement.subtract(right);
        if (keysDown.contains(GLFW_KEY_SPACE)) movement = movement.add(up);
        if (keysDown.contains(GLFW_KEY_LEFT_SHIFT)) movement = movement.subtract(up);

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
    public ActionType onMouseMove(MouseMoveEvent event) {
        long window = event.window();

        // Initialize window size if not set
        if (windowWidth <= 1 || windowHeight <= 1) {
            int[] w = new int[1], h = new int[1];
            glfwGetWindowSize(window, w, h);
            windowWidth = w[0];
            windowHeight = h[0];
            centerX = windowWidth / 2.0;
            centerY = windowHeight / 2.0;

            // Lock mouse to center initially
            glfwSetCursorPos(window, centerX, centerY);
            glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

            // Initialize yaw/pitch from current camera rotation
            Vector3d rot = camera.getTransform().getRotation();
            pitch = rot.getX();
            yaw = rot.getY();
        }

        // Get delta from center
        double dx = event.xpos() - centerX;
        double dy = event.ypos() - centerY;

        // Update angles
        yaw   -= dx * LOOK_SENSITIVITY;
        pitch -= dy * LOOK_SENSITIVITY;

        // Clamp pitch
        pitch = Math.max(-Math.PI / 2 + 0.01, Math.min(Math.PI / 2 - 0.01, pitch));

        // Apply rotation
        camera.getTransform().setRotation(new Vector3d(pitch, yaw, 0));

        // Reset mouse to center
        glfwSetCursorPos(window, centerX, centerY);

        return ActionType.CONSUME;
    }

    @Override
    public ActionType onMousePress(MousePressEvent event) {
        return ActionType.PASS; // No longer needed for look
    }

    @Override
    public ActionType onCharacter(CharacterEvent event) {
        return ActionType.PASS;
    }
}
