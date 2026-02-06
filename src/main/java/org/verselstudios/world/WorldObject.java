package org.verselstudios.world;

import org.jetbrains.annotations.Nullable;
import org.verselstudios.Image.Texture;
import org.verselstudios.Main;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.model.TransformRenderSystem;
import org.verselstudios.render.Renderer;

import static org.lwjgl.opengl.GL45.*;

public abstract class WorldObject implements Renderer {

    protected Transform modelTransform;
    protected RenderSystem renderSystem;

    protected boolean renderDebug = false;

    protected WorldObject(Transform modelTransform, RenderSystem renderSystem) {
        this.modelTransform = modelTransform;
        this.renderSystem = renderSystem;
    }


    protected void preRender() {}
    protected void postRender() {}

    @Override
    public void render() {
        preRender();
        Main.getRenderManager().getRenderStack().getMatrixStack().push(modelTransform.getModelMatrix());
        renderSystem.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
        Main.getRenderManager().getRenderStack().getMatrixStack().pop();
        postRender();

        if (renderDebug) {
            glEnable(GL_DEPTH_TEST);
            TransformRenderSystem dbgSys = new TransformRenderSystem(modelTransform);
            dbgSys.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
            glDisable(GL_DEPTH_TEST);
        }
    }
}
