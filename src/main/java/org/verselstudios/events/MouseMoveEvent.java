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
//            return new Vector2d(xpos / pWidth.get(), ypos / pHeight.get());
            int width = pWidth.get();
            int height = pHeight.get();
            Vector2d normalized = new Vector2d(xpos * 2 / width - 1, -(ypos * 2 / height - 1));
            double aspect = (double) width / height;
            return normalized.multiply(new Vector2d(aspect, 1));
        }
    }

    public Vector2d getPos() {
        return new Vector2d(xpos, ypos);
    }
}
