package org.verselstudios.math;

public final class Time {

    private static double lastTime = getTime();
    private static double deltaTime = 0.0;

    public static void update() {
        double now = getTime();
        deltaTime = now - lastTime;
        lastTime = now;
    }

    public static double deltaTime() {
        return deltaTime;
    }

    private static double getTime() {
        return org.lwjgl.glfw.GLFW.glfwGetTime();
    }
}
