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
import org.verselstudios.render.*;
import org.verselstudios.shader.ShaderRegister;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

    private long frames = 0;

    public static void main(String[] args) {
        new Main().run();
    }

    // The window handle
    private long window;

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
        ShaderRegister.CORE.use();
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
            RenderStack.onKeyPress(new KeyEvent(window, key, scancode, action, mods));
        });
        glfwSetCharCallback(window, (window, codepoint) -> {
            char character = (char) codepoint;
            RenderStack.onCharacter(new CharacterEvent(window, character));
        });

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            resize(width, height);
        });

        glfwSetMouseButtonCallback(window, (window, button, action, mods) -> {
            RenderStack.onMousePress(new MousePressEvent(window, button, action, mods));
        });

        glfwSetCursorPosCallback(window, (window, xpos, ypos) -> {
            RenderStack.onMouseMove(new MouseMoveEvent(window, xpos, ypos));
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
        GL20.glViewport(0, 0, width, height);

        ShaderRegister.PROJECTION_MATRIX = Matrix4d.ortho(-width, width, -height, height, -1, 1);
        ShaderRegister.CORE.setProjectionMatrix(ShaderRegister.PROJECTION_MATRIX);
        
//        GL20.glMatrixMode(GL20.GL_PROJECTION);
//        GL20.glLoadIdentity();
//        GL20.glOrtho(-width, width, -height, height, -1, 1);
//
//        GL20.glMatrixMode(GL20.GL_MODELVIEW);
    }

    private void loop() {
        // Set the clear color
        glClearColor(0.0f, 0.5f, 1.0f, 0.0f);

        GL20.glEnable(GL20.GL_TEXTURE_2D);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            RenderStack.render();

            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            frames++;
        }
    }
}