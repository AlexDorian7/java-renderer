package org.verselstudios.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector3d;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;
import org.verselstudios.Main;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;
import org.verselstudios.shader.Vao;
import org.verselstudios.shader.Vertex;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL45.*;

public class RenderSystem {
    private static final Logger LOGGER = LogManager.getLogger(RenderSystem.class);

    private final int vao;
    private final int vbo;
    private final RenderType type;
    private final ShaderProgram program;
    private int indices = 0;

    private final int stride;

    private final ArrayList<Vertex> verticies = new ArrayList<>();

    private int state = 0;

    public RenderSystem(RenderType type, ShaderProgram program) {
        this.type = type;
        this.program = program;

        this.stride = program.getVaoBuilder().getStride();

        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        int stride1 = program.getVaoBuilder().getStride() * Float.BYTES;

        long ptr = 0;
        for (int i=0; i<program.getVaoBuilder().getVAOs().size(); i++) {
            Vao vaoObj = program.getVaoBuilder().getVAOs().get(i);
            glVertexAttribPointer(i, vaoObj.amount(), GL_FLOAT, vaoObj.normalized(), stride1, ptr * Float.BYTES);
            glEnableVertexAttribArray(i);
            ptr += vaoObj.amount();
        }
    }

    public RenderSystem begin() {
        if (state != 0) {
            throw new IllegalStateException("Render system is in state " + state + " expected 0.");
        }
        state = 1;
        return this;
    }

    public RenderSystem addVertex(Vertex vertex) {
        if (state != 1) {
            throw new IllegalStateException("Render system is in state " + state + " expected 1.");
        }
        if (!vertex.getVaoBuilder().equals(program.getVaoBuilder())) throw new IllegalArgumentException("VAO builder of vertex does not match VAO builder of shader program");
        verticies.add(vertex);
        return this;
    }

    public RenderSystem end() {
        if (state != 1) {
            throw new IllegalStateException("Render system is in state " + state + " expected 1.");
        }
        state = 2;
        if (verticies.isEmpty()) {
            throw new IllegalStateException("No vertices in system.");
        }
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = BufferUtils.createFloatBuffer(verticies.size() * stride);
        for (Vertex vertex : verticies) {
            for (float f : vertex.getData()) {
                buffer.put(f);
            }

        }
        buffer.flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        indices = verticies.size();
        verticies.clear();
        return this;
    }

    public void draw(MatrixStack matrixStack) {
        if (state != 2) {
            throw new IllegalStateException("Render system is in state " + state + " expected 2.");
        }
        program.use();
        program.setModelMatrix(matrixStack.matrix());
        program.setViewMatrix(Main.getRenderManager().getRenderStack().getCamera().getTransform().getViewMatrix());
        program.setProjectionMatrix(ShaderRegister.PROJECTION_MATRIX);
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glDrawArrays(type.type, 0, indices);
        glBindVertexArray(0);
    }


    public void destroy() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }

    public final ShaderProgram getProgram() {
        return program;
    }

    protected int getState() {
        return state;
    }

    protected int getVao() {
        return vao;
    }

    protected int getVbo() {
        return vbo;
    }

    protected RenderType getType() {
        return type;
    }

    protected int getIndices() {
        return indices;
    }

    public enum RenderType {

        GL_POINTS(GL45.GL_POINTS),
        GL_LINES(GL45.GL_LINES),
        GL_LINE_LOOP(GL45.GL_LINE_LOOP),
        GL_LINE_STRIP(GL45.GL_LINE_STRIP),
        GL_TRIANGLES(GL45.GL_TRIANGLES),
        GL_TRIANGLE_STRIP(GL45.GL_TRIANGLE_STRIP),
        GL_TRIANGLE_FAN(GL45.GL_TRIANGLE_FAN),
        @Deprecated
        GL_QUADS(GL45.GL_QUADS),
        @Deprecated
        GL_QUAD_STRIP(GL45.GL_QUAD_STRIP),
        @Deprecated
        GL_POLYGON(GL45.GL_POLYGON);

        public final int type;

        RenderType(int type) {
            this.type = type;
        }
    }
}
