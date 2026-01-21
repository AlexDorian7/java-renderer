package org.verselstudios.render;

import org.lwjgl.system.MemoryStack;
import org.verselstudios.events.ActionType;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.events.MousePressEvent;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.render.font.Font;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.system.MemoryStack.stackPush;

public class DebugRenderer implements Renderer {

    KeyEvent keyEvent;
    MouseMoveEvent mouseMoveEvent;
    MousePressEvent mousePressEvent;

    @Override
    public void render() {
        if (mouseMoveEvent == null) return;
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            glfwGetWindowSize(mouseMoveEvent.window(), pWidth, pHeight);

            Vector3d position = new Vector3d(mouseMoveEvent.xpos() * 2 - pWidth.get(), -(mouseMoveEvent.ypos() * 2 - pHeight.get()) - (40*3), 0);
            if (keyEvent != null) {
                char c = (char) keyEvent.key();
                Font.DEFAULT.renderString(position, c + " - " + keyEvent.key() + " - " + keyEvent.action());
            }
            if (mousePressEvent != null) {
                Font.DEFAULT.renderString(position.add(new Vector3d(0, 40, 0)), mousePressEvent.button() + " - " + mousePressEvent.isPressed());
            }
            Font.DEFAULT.renderString(position.add(new Vector3d(0, 80, 0)), "[" + mouseMoveEvent.xpos() + ", " + mouseMoveEvent.ypos() + "]");

        } catch (Exception e) {}

    }

    @Override
    public ActionType onKeyPress(KeyEvent event) {
        keyEvent = event;
        return ActionType.PASS;
    }

    @Override
    public ActionType onMouseMove(MouseMoveEvent event) {
        mouseMoveEvent = event;
        return ActionType.PASS;
    }

    @Override
    public ActionType onMousePress(MousePressEvent event) {
        mousePressEvent = event;
        return ActionType.PASS;
    }
}
