package org.verselstudios.render;

import org.joml.Matrix4d;
import org.verselstudios.math.Time;
import org.verselstudios.model.QuadRenderSystem;
import org.verselstudios.model.RenderPostSystem;
import org.verselstudios.physics.PhysicsWorld;
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

    // -------------------
    // Scene FBO (MSAA + HDR)
    // -------------------
    private int sceneFbo;
    private int sceneColorTexMS;
    private int sceneDepthTexMS;

    private int sceneResolveFbo;
    private int sceneColorTex; // resolved single-sample for post-processing

    // -------------------
    // Post-processing ping-pong
    // -------------------
    private int postFboA;
    private int postFboB;
    private int postTexA;
    private int postTexB;

    private static final int MSAA_SAMPLES = 4;

    public RenderManager() {
        renderStack = new RenderStack();
        postStack = new PostProcessStack();

        postStack.push(ShaderRegister.loadPostProgram("blit")); // final blit to screen
        postStack.push(ShaderRegister.loadPostProgram("toneMap"));
        postStack.push(ShaderRegister.loadPostProgram("gammaCorrect"));
        postSystem = QuadRenderSystem.makePostQuad();
    }

    // ============================================================
    // Main render function
    // ============================================================
    public void render(int windowWidth, int windowHeight) {
        if (windowWidth <= 0 || windowHeight <= 0) return;

        resizeIfNeeded(windowWidth, windowHeight);
        Time.update();

        PhysicsWorld.getInstance().simulate();

        double aspect = (double) windowWidth / windowHeight;
        if (ShaderRegister.PROJECTION_MATRIX == null) {
            ShaderRegister.PROJECTION_MATRIX = new Matrix4d().perspective(Math.PI / 2, aspect, 0.1, 100);
        } else {
            ShaderRegister.PROJECTION_MATRIX.identity().perspective(Math.PI / 2, aspect, 0.1, 100);
        }

        // -----------------------------
        // 1. Render scene to MSAA HDR FBO
        // -----------------------------
        glBindFramebuffer(GL_FRAMEBUFFER, sceneFbo);
        glViewport(0, 0, windowWidth, windowHeight);

        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_BACK);

        renderStack.render();

        // -----------------------------
        // 2. Resolve MSAA → single-sample HDR
        // -----------------------------
        glBindFramebuffer(GL_READ_FRAMEBUFFER, sceneFbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, sceneResolveFbo);
        glBlitFramebuffer(
                0, 0, windowWidth, windowHeight,
                0, 0, windowWidth, windowHeight,
                GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT,
                GL_NEAREST
        );

        // -----------------------------
        // 3. Post-processing passes
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

            postSystem.draw(postStack.getShaders().get(i), readTex, sceneDepthTexMS);
            readTex = writeTex;
        }

        // -----------------------------
        // 4. Final pass → screen
        // -----------------------------
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, windowWidth, windowHeight);
        glClear(GL_COLOR_BUFFER_BIT);
        postSystem.draw(postStack.getShaders().get(0), readTex, sceneDepthTexMS);
    }

    // ============================================================
    // Resize / FBO creation
    // ============================================================
    private void resizeIfNeeded(int width, int height) {
        if (width == fbWidth && height == fbHeight) return;

        fbWidth = width;
        fbHeight = height;

        // --- Scene MSAA HDR ---
        if (sceneFbo != 0) {
            glDeleteFramebuffers(sceneFbo);
            glDeleteTextures(sceneColorTexMS);
            glDeleteTextures(sceneDepthTexMS);
            glDeleteFramebuffers(sceneResolveFbo);
            glDeleteTextures(sceneColorTex);
        }

        sceneFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, sceneFbo);

        sceneColorTexMS = createColorTextureMultisample(width, height, true);
        sceneDepthTexMS = createDepthTextureMultisample(width, height);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, sceneColorTexMS, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, sceneDepthTexMS, 0);

        checkFbo();

        // Single-sample resolved texture for post
        sceneResolveFbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, sceneResolveFbo);

        sceneColorTex = createColorTextureFloat(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, sceneColorTex, 0);

        checkFbo();

        // --- Post-processing ping-pong ---
        postFboA = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, postFboA);
        postTexA = createColorTextureFloat(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, postTexA, 0);
        checkFbo();

        postFboB = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, postFboB);
        postTexB = createColorTextureFloat(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, postTexB, 0);
        checkFbo();

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    // ----------------------------
    // Helpers
    // ----------------------------
    private int createColorTextureMultisample(int width, int height, boolean hdr) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, tex);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, MSAA_SAMPLES, hdr ? GL_RGBA16F : GL_RGBA8, width, height, true);
        return tex;
    }

    private int createDepthTextureMultisample(int width, int height) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, tex);
        glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, MSAA_SAMPLES, GL_DEPTH_COMPONENT24, width, height, true);
        return tex;
    }

    private int createColorTextureFloat(int width, int height) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA16F, width, height, 0, GL_RGBA, GL_FLOAT, (ByteBuffer) null);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        return tex;
    }

    private void checkFbo() {
        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer incomplete!");
        }
    }

    // ----------------------------
    // Accessors
    // ----------------------------
    public RenderStack getRenderStack() {
        return renderStack;
    }

    public PostProcessStack getPostProcessStack() {
        return postStack;
    }
}
