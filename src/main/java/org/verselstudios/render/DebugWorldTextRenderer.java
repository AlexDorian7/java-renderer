package org.verselstudios.render;

import org.verselstudios.Main;
import org.verselstudios.model.FontRenderSystem;
import org.verselstudios.math.Matrix4d;
import org.verselstudios.math.Transform;
import org.verselstudios.render.font.Font;

import static org.lwjgl.opengl.GL45.*;

public class DebugWorldTextRenderer implements Renderer {
    private final FontRenderSystem text = Font.DEFAULT.createFontRenderSystem("I am a very long string that should word wrap!", Font.FontStyle.DEFAULT, 16);

    private final Transform transform = new Transform(0,10,0,0,0,0,1,1,1);

    @Override
    public void render() {
        Matrix4d modelMatrix = transform.getModelMatrix();
        Main.getRenderManager().getRenderStack().getMatrixStack().push(modelMatrix);
        glEnable(GL_DEPTH);
        Font.renderFontSystem(text, Main.getRenderManager().getRenderStack().getMatrixStack());
        glDisable(GL_DEPTH);
        Main.getRenderManager().getRenderStack().getMatrixStack().pop();
    }
}
