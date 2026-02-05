package org.verselstudios.world;

import org.verselstudios.Image.Texture;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;

import static org.lwjgl.opengl.GL45.*;

public class DepthObject extends TexturedObject {
    protected DepthObject(Transform modelTransform, RenderSystem renderSystem, Texture texture) {
        super(modelTransform, renderSystem, texture);
    }

    @Override
    protected void preRender() {
        super.preRender();
        glEnable(GL_DEPTH_TEST);
    }

    @Override
    protected void postRender() {
        super.postRender();
        glDisable(GL_DEPTH_TEST);
    }
}
