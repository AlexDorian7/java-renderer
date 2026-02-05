package org.verselstudios.world;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;

import static org.lwjgl.opengl.GL45.*;

public abstract class TexturedObject extends WorldObject {

    protected Texture texture;

    protected TexturedObject(Transform modelTransform, RenderSystem renderSystem, Texture texture) {
        super(modelTransform, renderSystem);
        this.texture = texture;
    }

    @Override
    protected void preRender() {
        glEnable(GL_TEXTURE_2D);
        texture.bind(renderSystem.getProgram());
    }

    @Override
    protected void postRender() {
        glDisable(GL_TEXTURE_2D);
    }
}
