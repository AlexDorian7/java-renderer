package org.verselstudios.render;

import org.verselstudios.Main;
import org.verselstudios.model.verselModel.VerselModel;

import static org.lwjgl.opengl.GL45.*;

@Deprecated
public class VerselModelRenderer implements Renderer {

    private final VerselModel renderSystem;

    public VerselModelRenderer(String resourcePath) {
        renderSystem = VerselModel.load(resourcePath);
    }

    @Override
    public void render() {
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        renderSystem.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
    }
}
