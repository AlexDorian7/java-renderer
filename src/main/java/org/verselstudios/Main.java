package org.verselstudios;

import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.verselstudios.events.CharacterEvent;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.events.MousePressEvent;
import org.verselstudios.math.Matrix4d;
import org.verselstudios.math.Rectangle;
import org.verselstudios.render.*;
import org.verselstudios.shader.ShaderRegister;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    // The window handle
    private long window;

    // The RenderManager
    private static RenderManager renderManager;

    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

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

        renderManager.getRenderStack().push(new CameraControllRenderer(renderManager.getRenderStack().getCamera()));

//        RenderStack.push(new TextWindow("Hello World", "Hello World", new Rectangle(1, 1)));
        renderManager.getRenderStack().push(new DebugWorldTextRenderer());
        renderManager.getRenderStack().push(new DebugHeightMapRenderer());
        renderManager.getRenderStack().push(new AxisRenderer(true));
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(600, 600, "Work In Progress", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
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

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            renderManager.getRenderStack().onMouseMove(new MouseMoveEvent(window, xpos, ypos));
        });



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