package org.verselstudios.render;

import org.verselstudios.model.QuadRenderSystem;
import org.verselstudios.model.RenderPostSystem;
import org.verselstudios.math.Matrix4d;
import org.verselstudios.shader.PostProcessStack;
import org.verselstudios.shader.ShaderRegister;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL45.*;

public class RenderManager {

    private int fbWidth = -1;
    private int fbHeight = -1;


    private final RenderStack renderStack;
    private final PostProcessStack postStack;

    private final RenderPostSystem postSystem;

    // Scene framebuffer
    private final int sceneFbo;
    private final int sceneColorTex;
    private final int sceneDepthTex;

    // Ping-pong framebuffers
    private final int postFboA;
    private final int postFboB;
    private final int postTexA;
    private final int postTexB;

    public RenderManager() {

        renderStack = new RenderStack();
        postStack = new PostProcessStack();

        // Example stack (last shader outputs to screen)
        postStack.push(ShaderRegister.loadPostProgram("blit"));
//        postStack.push(ShaderRegister.loadPostProgram("depth"));

        postSystem = QuadRenderSystem.makePostQuad();

        // -----------------------------
        // Scene framebuffer
        // -----------------------------
        sceneFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, sceneFbo);

        sceneColorTex = createColorTexture(16, 16); // This will be rebuilt
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D,
                sceneColorTex,
                0
        );

        sceneDepthTex = createDepthTexture(16, 16); // This will be rebuilt
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_DEPTH_ATTACHMENT,
                GL_TEXTURE_2D,
                sceneDepthTex,
                0
        );

        checkFbo();

        // -----------------------------
        // Post-process ping-pong A
        // -----------------------------
        postFboA = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, postFboA);

        postTexA = createColorTexture(16, 16); // This will be rebuilt
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D,
                postTexA,
                0
        );

        checkFbo();

        // -----------------------------
        // Post-process ping-pong B
        // -----------------------------
        postFboB = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, postFboB);

        postTexB = createColorTexture(16, 16); // This will be rebuilt
        glFramebufferTexture2D(
                GL_FRAMEBUFFER,
                GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D,
                postTexB,
                0
        );

        checkFbo();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    // ============================================================
    // Render
    // ============================================================

    public void render(int windowWidth, int windowHeight) {

        resizeIfNeeded(windowWidth, windowHeight);

        // Create Projection Matrix
        ShaderRegister.PROJECTION_MATRIX = Matrix4d.perspective(90, 0.1, 100, windowWidth, windowHeight);

        // -----------------------------
        // 1. Geometry pass
        // -----------------------------
        glBindFramebuffer(GL_FRAMEBUFFER, sceneFbo);
        glViewport(0, 0, windowWidth, windowHeight);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDepthFunc(GL_LESS);
        glCullFace(GL_BACK);
        glEnable(GL_CULL_FACE);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        renderStack.render();

        // -----------------------------
        // 2. Post-processing passes
        // -----------------------------
        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);

        int readTex = sceneColorTex;

        for (int i = postStack.getShaders().size() - 1; i > 0; i--) {

            boolean even = ((postStack.getShaders().size() - i) & 1) == 0;
            int writeFbo = even ? postFboA : postFboB;
            int writeTex = even ? postTexA : postTexB;

            glBindFramebuffer(GL_FRAMEBUFFER, writeFbo);
            glClear(GL_COLOR_BUFFER_BIT);

            postSystem.draw(
                    postStack.getShaders().get(i),
                    readTex,
                    sceneDepthTex
            );

            readTex = writeTex;
        }

        // -----------------------------
        // 3. Final pass → screen
        // -----------------------------
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, windowWidth, windowHeight);

        glClear(GL_COLOR_BUFFER_BIT);

        postSystem.draw(
                postStack.getShaders().getFirst(),
                readTex,
                sceneDepthTex
        );
    }

    // ============================================================
    // Helpers
    // ============================================================

    private void resizeIfNeeded(int width, int height) {
        if (width == fbWidth && height == fbHeight) return;

        fbWidth = width;
        fbHeight = height;

        // ----- Scene FBO -----
        glBindFramebuffer(GL_FRAMEBUFFER, sceneFbo);

        glDeleteTextures(sceneColorTex);
        glDeleteTextures(sceneDepthTex);

        int color = createColorTexture(width, height);
        int depth = createDepthTexture(width, height);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, color, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depth, 0);

        checkFbo();

        // ----- Post A -----
        glBindFramebuffer(GL_FRAMEBUFFER, postFboA);
        glDeleteTextures(postTexA);

        int a = createColorTexture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, a, 0);
        checkFbo();

        // ----- Post B -----
        glBindFramebuffer(GL_FRAMEBUFFER, postFboB);
        glDeleteTextures(postTexB);

        int b = createColorTexture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, b, 0);
        checkFbo();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }


    private int createColorTexture(int width, int height) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_RGBA8,
                width,
                height,
                0,
                GL_RGBA,
                GL_UNSIGNED_BYTE,
                (ByteBuffer) null
        );

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        return tex;
    }

    private int createDepthTexture(int width, int height) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);

        glTexImage2D(
                GL_TEXTURE_2D,
                0,
                GL_DEPTH_COMPONENT24,
                width,
                height,
                0,
                GL_DEPTH_COMPONENT,
                GL_FLOAT,
                (ByteBuffer) null
        );

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_NONE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        return tex;
    }

    private void checkFbo() {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer incomplete!");
        }
    }

    // ============================================================
    // Accessors
    // ============================================================

    public RenderStack getRenderStack() {
        return renderStack;
    }

    public PostProcessStack getPostProcessStack() {
        return postStack;
    }
}
