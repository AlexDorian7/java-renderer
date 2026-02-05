package org.verselstudios;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.verselstudios.events.CharacterEvent;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.events.MousePressEvent;
import org.verselstudios.render.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class);

    private static final double MAX_MOUSE_DELTA = 50.0; // clamp extreme deltas

    private double lastX = -1;
    private double lastY = -1;

    public static void main(String[] args) {
        new Main().run();
    }

    // The window handle
    private long window;

    // The RenderManager
    private static RenderManager renderManager;

    public void run() {
        LOGGER.info("Hello LWJGL " + Version.getVersion() + "!");

        init();
        registerInternals();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void registerInternals() {

        renderManager = new RenderManager();

        renderManager.getRenderStack().push(new MovementTestRenderer());

        renderManager.getRenderStack().push(new CameraControlRenderer(renderManager.getRenderStack().getCamera()));

        renderManager.getRenderStack().push(new AxisRenderer(true));
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(1000, 1000, "Work In Progress", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
            renderManager.getRenderStack().onKeyPress(new KeyEvent(window, key, scancode, action, mods));
        });
        glfwSetCharCallback(window, (window, codepoint) -> {
            char character = (char) codepoint;
            renderManager.getRenderStack().onCharacter(new CharacterEvent(window, character));
        });

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            resize(width, height);
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            renderManager.getRenderStack().onMousePress(new MousePressEvent(window, button, action, mods));
        });

        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
        }

        initMouse(window);


        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            resize(pWidth.get(), pHeight.get());
        } // the stack frame is popped automatically
    }

    // Call once after window creation
    private void initMouse(long window) {
        // Hide cursor
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_HIDDEN);

        // Enable raw motion if available
        if (GLFW.glfwRawMouseMotionSupported()) {
            GLFW.glfwSetInputMode(window, GLFW.GLFW_RAW_MOUSE_MOTION, GLFW.GLFW_TRUE);
        }

        // Start cursor at window center
        int[] width = new int[1];
        int[] height = new int[1];
        GLFW.glfwGetWindowSize(window, width, height);
        GLFW.glfwSetCursorPos(window, width[0]/2.0, height[0]/2.0);
        lastX = width[0]/2.0;
        lastY = height[0]/2.0;

        // Set callback
        GLFW.glfwSetCursorPosCallback(window, (win, xpos, ypos) -> {
            double dx = xpos - lastX;
            double dy = ypos - lastY;

            // Clamp extreme deltas
            dx = Math.max(-MAX_MOUSE_DELTA, Math.min(MAX_MOUSE_DELTA, dx));
            dy = Math.max(-MAX_MOUSE_DELTA, Math.min(MAX_MOUSE_DELTA, dy));

            lastX = xpos;
            lastY = ypos;

            renderManager.getRenderStack().onMouseMove(new MouseMoveEvent(window, xpos, ypos, dx, dy));

            // Warp cursor back to center every frame to avoid leaving window
            GLFW.glfwSetCursorPos(win, width[0]/2.0, height[0]/2.0);
            lastX = width[0]/2.0;
            lastY = height[0]/2.0;
        });
    }


    private static void resize(int width, int height) {
        glViewport(0, 0, width, height);
    }

    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.5f, 1.0f, 0.0f);
        glClearDepth(2.0f);


        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            // Get Window Size
            try ( MemoryStack stack = stackPush() ) {
                IntBuffer pWidth = stack.mallocInt(1); // int*
                IntBuffer pHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window, pWidth, pHeight);

                renderManager.render(pWidth.get(), pHeight.get());

            } catch (Exception e) {
                throw new RuntimeException("Failed to render frame", e);
            } // the stack frame is popped automatically



            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public static RenderManager getRenderManager() {
        return renderManager;
    }
}