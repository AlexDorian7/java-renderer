package org.verselstudios.render;

import org.lwjgl.glfw.GLFW;
import org.verselstudios.events.*;
import org.verselstudios.math.Camera;
import org.verselstudios.math.Vector3d;

public class CameraControllRenderer implements Renderer {

    private final Camera camera;

    private static final double SPEED = 0.01;

    public CameraControllRenderer(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void render() {

    }

    @Override
    public ActionType onKeyPress(KeyEvent event) {
        if (event.isPressed()) {
            if (event.key() == GLFW.GLFW_KEY_W) {
                camera.getTransform().setPosition(camera.getTransform().getPosition().add(camera.getTransform().getMatrix().getForwardVector().multiply(SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_S) {
                camera.getTransform().setPosition(camera.getTransform().getPosition().add(camera.getTransform().getMatrix().getForwardVector().multiply(-SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_D) {
                camera.getTransform().setPosition(camera.getTransform().getPosition().add(camera.getTransform().getMatrix().getRightVector().multiply(SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_A) {
                camera.getTransform().setPosition(camera.getTransform().getPosition().add(camera.getTransform().getMatrix().getRightVector().multiply(-SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_Q) {
                camera.getTransform().setPosition(camera.getTransform().getPosition().add(camera.getTransform().getMatrix().getUpVector().multiply(SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_E) {
                camera.getTransform().setPosition(camera.getTransform().getPosition().add(camera.getTransform().getMatrix().getUpVector().multiply(-SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_UP) {
                camera.getTransform().setRotation(camera.getTransform().getRotation().add(new Vector3d(1,0,0).multiply(SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_DOWN) {
                camera.getTransform().setRotation(camera.getTransform().getRotation().add(new Vector3d(1,0,0).multiply(-SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_RIGHT) {
                camera.getTransform().setRotation(camera.getTransform().getRotation().add(new Vector3d(0,1,0).multiply(SPEED)));
                return ActionType.CONSUME;
            } else if (event.key() == GLFW.GLFW_KEY_LEFT) {
                camera.getTransform().setRotation(camera.getTransform().getRotation().add(new Vector3d(0,1,0).multiply(-SPEED)));
                return ActionType.CONSUME;
            }
        }

        return ActionType.PASS;

    }

    @Override
    public ActionType onMouseMove(MouseMoveEvent event) {
        return Renderer.super.onMouseMove(event);
    }

    @Override
    public ActionType onMousePress(MousePressEvent event) {
        return Renderer.super.onMousePress(event);
    }

    @Override
    public ActionType onCharacter(CharacterEvent event) {
        return Renderer.super.onCharacter(event);
    }
}
