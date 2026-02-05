package org.verselstudios.world;

import org.jetbrains.annotations.Nullable;
import org.verselstudios.Image.Texture;
import org.verselstudios.Main;
import org.verselstudios.math.Transform;
import org.verselstudios.model.RenderSystem;
import org.verselstudios.render.Renderer;

public abstract class WorldObject implements Renderer {

    protected Transform modelTransform;

    protected RenderSystem renderSystem;

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
    }
}
