package org.verselstudios.render;

import org.verselstudios.events.ActionType;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.events.MousePressEvent;

public interface Renderer {

    void render();

    default ActionType onKeyPress(KeyEvent event) {
        return ActionType.PASS;
    }

    default ActionType onMouseMove(MouseMoveEvent event) {
        return ActionType.PASS;
    }

    default ActionType onMousePress(MousePressEvent event) {
        return ActionType.PASS;
    }
}
