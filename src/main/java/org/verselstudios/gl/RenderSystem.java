package org.verselstudios.gl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL45;
import org.verselstudios.math.MatrixStack;
import org.verselstudios.math.Vector2d;
import org.verselstudios.math.Vector3d;
import org.verselstudios.math.Vector4d;
import org.verselstudios.shader.ShaderProgram;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL45.*;

public class RenderSystem {

    private final int vao;
    private final int vbo;
    private final RenderType type;
    private final ShaderProgram program;
    private int indices = 0;

    private static final int STRIDE = 18;

    private final ArrayList<Vertex> verticies = new ArrayList<>();

    private int state = 0;

    public RenderSystem(RenderType type, ShaderProgram program) {
        this.type = type;
        this.program = program;
        vao = glGenVertexArrays();
        vbo = glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        int stride = STRIDE * Float.BYTES;

        // position (location = 0)
        glVertexAttribPointer(0, 3, GL_INT, false, stride, 0);
        glEnableVertexAttribArray(0);

        // texCoord (location = 1)
        glVertexAttribPointer(1, 2, GL_FLOAT, false, stride, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        // color (location = 2)
        glVertexAttribPointer(2, 4, GL_FLOAT, false, stride, 5L * Float.BYTES);
        glEnableVertexAttribArray(2);

        // normal (location = 3)
        glVertexAttribPointer(3, 3, GL_FLOAT, false, stride, 9L * Float.BYTES);
        glEnableVertexAttribArray(3);

        // tangent (location = 4)
        glVertexAttribPointer(4, 3, GL_FLOAT, false, stride, 12L * Float.BYTES);
        glEnableVertexAttribArray(4);

        // bitangent (location = 5)
        glVertexAttribPointer(5, 3, GL_FLOAT, false, stride, 15L * Float.BYTES);
        glEnableVertexAttribArray(5);
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
        FloatBuffer buffer = BufferUtils.createFloatBuffer(verticies.size() * STRIDE);
        for (Vertex vertex : verticies) {
            put(buffer, (float) vertex.position.getX(), (float) vertex.position.getY(), (float) vertex.position.getZ(),
                    (float) vertex.texCoord.getX(), (float) vertex.texCoord.getY(),
                    (float) vertex.color.getX(), (float) vertex.color.getY(), (float) vertex.color.getZ(), (float) vertex.color.getW(),
                    (float) vertex.normal.getX(), (float) vertex.normal.getY(), (float) vertex.normal.getZ(),
                    (float) vertex.tangent.getX(), (float) vertex.tangent.getY(), (float) vertex.tangent.getZ(),
                    (float) vertex.bitangent.getX(), (float) vertex.bitangent.getY(), (float) vertex.bitangent.getZ());
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

    public record Vertex(Vector3d position, Vector2d texCoord, Vector4d color, Vector3d normal, Vector3d tangent, Vector3d bitangent) {
        public Vertex(Vector3d position, Vector2d texCoord, Vector4d color, Vector3d normal, Vector3d tangent, Vector3d bitangent) {
            this.position = position;
            this.texCoord = texCoord;
            this.color = color;
            this.normal = normal;
            this.tangent = tangent;
            this.bitangent = bitangent;
        }
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
