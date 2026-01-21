package org.verselstudios.events;

import static org.lwjgl.glfw.GLFW.*;

public record KeyEvent(long window, int key, int scancode, int action, int mods) {

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
        return action > 0;
    }

    public boolean isPress() {
        return action == GLFW_PRESS; // pressed is 1, released is 0, 2 is held
    }

    public boolean isHeld() {
        return action == 2;
    }

    public boolean isReleased() {
        return action == GLFW_RELEASE;
    }
}
