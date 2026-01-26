package org.verselstudios.render;

import org.verselstudios.events.*;

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

    default ActionType onCharacter(CharacterEvent event) {
        return ActionType.PASS;
    }
}
