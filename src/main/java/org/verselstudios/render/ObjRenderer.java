package org.verselstudios.render;

import org.verselstudios.Image.Texture;
import org.verselstudios.Main;
import org.verselstudios.model.ObjRenderSystem;

import static org.lwjgl.opengl.GL45.*;

public class ObjRenderer implements Renderer {

    private final ObjRenderSystem renderSystem;
    private final Texture texture;

    public ObjRenderer(String resourcePath, Texture texture) {
        renderSystem = new ObjRenderSystem("assets/models/" + resourcePath + ".obj");
        this.texture = texture;
    }

    @Override
    public void render() {
        texture.bind(renderSystem.getProgram());
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        renderSystem.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_DEPTH_TEST);
    }
}
