package org.verselstudios.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.Vao;
import org.verselstudios.shader.Vertex;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL45.*;

public class RenderSystem {

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
        program.setModelViewMatrix(matrixStack.matrix());
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glDrawArrays(type.type, 0, indices);
        glBindVertexArray(0);
    }


    public void destroy() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }

    private void put(FloatBuffer buf, float x, float y, float z, float u, float v, float r, float g, float b, float a, float nx, float ny, float nz, float tx, float ty, float tz, float bx, float by, float bz) {
        buf.put(x).put(y).put(z);           // position
        buf.put(u).put(v);                  // texcoord
        buf.put(r).put(g).put(b).put(a);    // color
        buf.put(nx).put(ny).put(nz);        // normal
        buf.put(tx).put(ty).put(tz);        // tangent
        buf.put(bx).put(by).put(bz);        // bitangent
    }

    public final ShaderProgram getProgram() {
        return program;
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
