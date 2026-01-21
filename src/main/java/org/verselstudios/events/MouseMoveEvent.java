package org.verselstudios.events;

import org.lwjgl.system.MemoryStack;
import org.verselstudios.math.Vector2d;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public record MouseMoveEvent(long window, double xpos, double ypos) {

    public Vector2d getWorldPos() {
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            glfwGetWindowSize(window, pWidth, pHeight);
            return new Vector2d(xpos * 2 - pWidth.get(), -(ypos * 2 - pHeight.get()));
        }
    }
}
