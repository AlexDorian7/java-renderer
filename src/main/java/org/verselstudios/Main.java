package org.verselstudios;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.lwjgl.Version;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;
import org.verselstudios.Image.Texture;
import org.verselstudios.events.CharacterEvent;
import org.verselstudios.events.KeyEvent;
import org.verselstudios.events.MouseMoveEvent;
import org.verselstudios.events.MousePressEvent;
import org.verselstudios.math.Transform;
import org.verselstudios.physics.PhysicsWorld;
import org.verselstudios.physics.material.PhysicsMaterials;
import org.verselstudios.render.*;
import org.verselstudios.shader.material.Material;
import org.verselstudios.voxel.Chunk;
import org.verselstudios.world.objects.ChunkRenderer;
import org.verselstudios.world.objects.DynamicCylinder;
import org.verselstudios.world.objects.FallingDynamicBox;
import org.verselstudios.world.objects.StaticBox;

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

        PhysicsWorld ignored = PhysicsWorld.getInstance();// Init Physics

        renderManager = new RenderManager();
        Texture groundTexture = new Texture("assets/textures/ground_white.png");
        Texture crateTexture = new Texture("assets/textures/crate.png");
        Texture crateSpecularTexture = new Texture("assets/textures/crate_specular.png");
        Texture brickNormalTexture = new Texture("assets/textures/brick_normal.png");
        Texture crateNormalTexture = new Texture("assets/textures/crate_normal.png");
        Texture brickHeightTexture = new Texture("assets/textures/brick_height.png");
        Texture crateHeightTexture = new Texture("assets/textures/crate_height.png");

        Material groundMaterial = new Material(groundTexture, groundTexture, brickNormalTexture, brickHeightTexture, 256);
        Material crateMaterial = new Material(crateTexture, crateSpecularTexture, crateNormalTexture, crateHeightTexture, 16);

        Chunk chunk = new Chunk(new Vector3i(0, 0, 0));
        Chunk chunk1 = new Chunk(new Vector3i(-1, 0, 0));
        Chunk chunk2 = new Chunk(new Vector3i(0, 0, -1));
        Chunk chunk3 = new Chunk(new Vector3i(-1, 0, -1));
        for (int x=0; x<16; x++) {
            for (int z=0; z<16; z++) {
                chunk.getStorage()[x][0][z] = "crate";
                chunk1.getStorage()[x][0][z] = "ground";
                chunk2.getStorage()[x][0][z] = "ground";
                chunk3.getStorage()[x][0][z] = "crate";
            }
        }

        ChunkRenderer chunkRenderer = new ChunkRenderer(chunk, crateMaterial);
        ChunkRenderer chunkRenderer1 = new ChunkRenderer(chunk1, crateMaterial);
        ChunkRenderer chunkRenderer2 = new ChunkRenderer(chunk2, crateMaterial);
        ChunkRenderer chunkRenderer3 = new ChunkRenderer(chunk3, crateMaterial);
        renderManager.getRenderStack().push(chunkRenderer);
        renderManager.getRenderStack().push(chunkRenderer1);
        renderManager.getRenderStack().push(chunkRenderer2);
        renderManager.getRenderStack().push(chunkRenderer3);

//        renderManager.getRenderStack().push(new StaticBox(new Transform(0,-10,0, Math.PI/4,0,0, 1,1,1), new Vector3d(20,1,20), groundTexture, groundMaterial, PhysicsMaterials.DEFAULT)); // Slope
//        renderManager.getRenderStack().push(new StaticBox(new Transform(0,-20,0, 0,0,0, 1,1,1), new Vector3d(50,1,50), groundTexture, groundMaterial, PhysicsMaterials.DEFAULT)); // Ground
//        for (int x=-10; x<=10; x++) {
//            for (int y=0; y<2; y++) {
//                for (int z = -10; z <= 10; z++) {
//                    renderManager.getRenderStack().push(new FallingDynamicBox(new Transform(new Vector3d(x * 5, y*5+10, z * 5), new Quaterniond(), new Vector3d(1)), new Vector3d(0.5), crateTexture, crateMaterial, PhysicsMaterials.DEFAULT)); // FallingBox
//                }
//            }
//        }


//        renderManager.getRenderStack().push(new PhysicsCameraControlRenderer(renderManager.getRenderStack().getCamera(), PhysicsMaterials.CHARACTER));
        renderManager.getRenderStack().push(new CameraControlRenderer(renderManager.getRenderStack().getCamera()));

//        renderManager.getRenderStack().push(new AxisRenderer(true));
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

        glfwWindowHint(GLFW_SAMPLES, 4); // tell glfw that we want to use MSAA (Anti aliasing)
        glEnable(GL_MULTISAMPLE); // Tell OpenGL to do the same
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