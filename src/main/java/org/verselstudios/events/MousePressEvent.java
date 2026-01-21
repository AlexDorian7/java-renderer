package org.verselstudios.events;

import static org.lwjgl.glfw.GLFW.*;

public record MousePressEvent(long window, int button, int action, int mods) {

    public boolean isShiftPressed() {
        return (mods & GLFW_MOD_SHIFT) != 0;
    }

    public boolean isControlPressed() {
        return (mods & GLFW_MOD_CONTROL) != 0;
    }

    public boolean isAltPressed() {
        return (mods & GLFW_MOD_ALT) != 0;
    }

    public boolean isSuperPressed() {
        return (mods & GLFW_MOD_SUPER) != 0;
    }

    public boolean isPressed() {
        return action == 1; // pressed is 1, released is 0
    }

    public boolean isReleased() {
        return !isPressed();
    }
}
