package org.verselstudios.render;

import org.verselstudios.events.ActionType;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.events.MousePressEvent;
import org.verselstudios.math.Rectangle;
import org.verselstudios.math.Vector2d;

public class DraggableWindow extends Window {

    private boolean dragging = false;
    private Vector2d lastPos;
    private Vector2d offset;

    public DraggableWindow(Rectangle bounds) {
        super(bounds);
    }

    @Override
    public ActionType onKeyPress(KeyEvent event) {
        return ActionType.PASS;
    }

    @Override
    public ActionType onMouseMove(MouseMoveEvent event) {
        lastPos = event.getWorldPos();
        if (dragging) {
            getBounds().setPos(lastPos.subtract(offset));
        }
        return ActionType.PASS;
    }

    @Override
    public ActionType onMousePress(MousePressEvent event) {
        if (lastPos == null || !getBounds().contains(lastPos)) return ActionType.PASS;
        dragging = event.isPressed() && event.button() == 0;
        offset = lastPos.subtract(getBounds().getPos());
        return dragging ? ActionType.CONSUME : ActionType.PASS;
    }
}
