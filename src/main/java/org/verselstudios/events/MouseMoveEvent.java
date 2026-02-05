package org.verselstudios.events;

public class MouseMoveEvent {
    private final long window;
    private final double dx;
    private final double dy;

    // Optional: track absolute position if you want
    private final double xpos;
    private final double ypos;

    public MouseMoveEvent(long window, double xpos, double ypos, double dx, double dy) {
        this.window = window;
        this.xpos = xpos;
        this.ypos = ypos;
        this.dx = dx;
        this.dy = dy;
    }

    public long window() { return window; }
    public double dx() { return dx; }
    public double dy() { return dy; }
    public double xpos() { return xpos; }
    public double ypos() { return ypos; }
}