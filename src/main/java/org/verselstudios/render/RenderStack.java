package org.verselstudios.render;

import org.verselstudios.events.*;
import org.verselstudios.math.Camera;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.math.Time;
import org.verselstudios.physics.Physical;

import java.util.ArrayList;

public class RenderStack {

    RenderStack() {} // Package Private

    private final ArrayList<Renderer> RENDERERS = new ArrayList<>();

    private final MatrixStack MATRIX_STACK = new MatrixStack();

    private final Camera camera = new Camera();

    public void push(Renderer renderer) {
        RENDERERS.add(renderer);
    }

    public Renderer pop() {
        Renderer renderer = RENDERERS.removeLast();
        renderer.onRemove();
        return renderer;
    }

    public void render() {
        MATRIX_STACK.pushView(camera.getTransform().getViewMatrix()); // Push view matrix
        for (Renderer renderer : RENDERERS) {
            if (renderer instanceof Physical physical) {
                physical.updatePhysics();
            }
            renderer.render();
        }
        MATRIX_STACK.pop(); // Pop view matrix
        if (!MATRIX_STACK.isEmpty()) {
            throw new IllegalStateException("Matrix stack not empty at end of frame!");
        }
    }

    public void onKeyPress(KeyEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onKeyPress(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public void onMouseMove(MouseMoveEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onMouseMove(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public  void onMousePress(MousePressEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onMousePress(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public void onCharacter(CharacterEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onCharacter(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public MatrixStack getMatrixStack() {
        return MATRIX_STACK;
    }

    public Camera getCamera() {
        return camera;
    }
}
