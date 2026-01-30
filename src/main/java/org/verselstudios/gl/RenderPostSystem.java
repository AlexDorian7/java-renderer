package org.verselstudios.gl;

import org.verselstudios.math.MatrixStack;
import org.verselstudios.render.RenderManager;
import org.verselstudios.shader.ShaderPostProgram;
import org.verselstudios.shader.ShaderProgram;
import org.verselstudios.shader.ShaderRegister;

import static org.lwjgl.opengl.GL45.*;

public class RenderPostSystem extends RenderSystem {
    public RenderPostSystem(RenderType type, ShaderProgram program) {
        super(type, program);
    }


    public void draw(ShaderPostProgram postProgram, int colorTex, int depthTex) {
        if (getState() != 2) {
            throw new IllegalStateException("Render system is in state " + getState() + " expected 2.");
        }
        postProgram.use();
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, colorTex);
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, depthTex);
        postProgram.setUniformi(postProgram.getUniformLocation("color"), 0);
        postProgram.setUniformi(postProgram.getUniformLocation("depth"), 1);
        glBindVertexArray(getVao());
        glBindBuffer(GL_ARRAY_BUFFER, getVbo());
        glDrawArrays(getType().type, 0, getIndices());
        glBindVertexArray(0);
    }
}
