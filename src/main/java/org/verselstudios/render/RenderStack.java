package org.verselstudios.render;

import org.verselstudios.events.*;
import org.verselstudios.math.Camera;
import org.verselstudios.math.Matrix4d;
import org.verselstudios.math.MatrixStack;

import java.util.ArrayList;

public class RenderStack {
    private static final ArrayList<Renderer> RENDERERS = new ArrayList<>();

    private static final MatrixStack MATRIX_STACK = new MatrixStack();

    private static final Camera camera = new Camera();

    public static void push(Renderer renderer) {
        RENDERERS.add(renderer);
    }

    public static Renderer pop() {
        return RENDERERS.removeLast();
    }

    public static void render() {
        MATRIX_STACK.push(camera.getTransform().getMatrix()); // Push view matrix
        for (Renderer renderer : RENDERERS) {
            renderer.render();
        }
        MATRIX_STACK.pop(); // Pop view matrix
        if (!MATRIX_STACK.isEmpty()) {
            throw new IllegalStateException("Matrix stack not empty at end of frame!");
        }
    }

    public static void onKeyPress(KeyEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onKeyPress(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public static void onMouseMove(MouseMoveEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onMouseMove(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public static void onMousePress(MousePressEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onMousePress(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public static void onCharacter(CharacterEvent event) {
        for (Renderer renderer : RENDERERS.reversed()) {
            ActionType actionType = renderer.onCharacter(event);
            if (actionType == ActionType.CONSUME) break;
        }
    }

    public static MatrixStack getMatrixStack() {
        return MATRIX_STACK;
    }
}
