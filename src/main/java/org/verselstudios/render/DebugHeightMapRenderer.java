package org.verselstudios.render;

import org.verselstudios.Main;
import org.verselstudios.surface.NoiseHeightmap;
import org.verselstudios.surface.SurfaceRenderSystemNew;

public class DebugHeightMapRenderer implements Renderer {

    private final SurfaceRenderSystemNew system = new SurfaceRenderSystemNew(new NoiseHeightmap(), 128, 32);

    @Override
    public void render() {
        system.draw(Main.getRenderManager().getRenderStack().getMatrixStack());
    }
}
