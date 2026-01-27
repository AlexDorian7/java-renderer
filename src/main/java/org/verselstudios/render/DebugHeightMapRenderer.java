package org.verselstudios.render;

import org.verselstudios.surface.NoiseHeightmap;
import org.verselstudios.surface.SurfaceRenderSystem;

public class DebugHeightMapRenderer implements Renderer {

    private final SurfaceRenderSystem system = new SurfaceRenderSystem(new NoiseHeightmap(), 256);

    @Override
    public void render() {
        system.draw(RenderStack.getMatrixStack());
    }
}
