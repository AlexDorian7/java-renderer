package org.verselstudios.render;

import org.verselstudios.events.*;

import java.util.ArrayList;

public class RenderStack {
    private static final ArrayList<Renderer> RENDERERS = new ArrayList<>();

    public static void push(Renderer renderer) {
        RENDERERS.add(renderer);
    }

    public static Renderer pop() {
        return RENDERERS.removeLast();
    }

    public static void render() {
        for (Renderer renderer : RENDERERS) {
            renderer.render();
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

}
